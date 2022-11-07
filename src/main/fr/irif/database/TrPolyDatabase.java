package fr.irif.database;

import fr.irif.events.EventData;
import fr.irif.events.ReadTransactionalEvent;
import fr.irif.events.TransactionalEvent;
import fr.irif.events.WriteTransactionalEvent;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.util.Pair;

import java.util.ArrayList;

public class TrPolyDatabase extends TrDatabase{

    public TrPolyDatabase(Config config) {
        super(config);
    }

    @Override
    public void backtrackDatabase() {

        if(isMockAccess()){
            if(mockPath != null) {
                int n = timesPathExecuted.get(mockPath);
                if (n == -1) timesPathExecuted.remove(mockPath);
                else timesPathExecuted.put(mockPath, n - 1);
                mockPath = null;
            }
            return;
        }
        var tr = events.get(events.size() - 1);
        TransactionalEvent e = tr.getLast();
        switch (e.getType()) {
            case READ:
                ReadTransactionalEvent r = (ReadTransactionalEvent) e;
                WriteTransactionalEvent w = ((ReadTransactionalEvent) e).getWriteEvent();

                var localRead = w.getTransactionId() == r.getTransactionId();

                if (!swapped(e) && w.getWriteIndex() != 0 && !isGuided() && !localRead) {
                    WriteTransactionalEvent nw = writeEventsPerVariable.get(e.getVariable()).get(w.getWriteIndex() - 1);
                    changeWriteRead(nw, r);
                    guideInfo.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.READ);
                    return;

                } else if (!swapped(e) || isGuided()) {
                    readEventsPerVariable.get(e.getVariable()).remove(readEventsPerVariable.get(e.getVariable()).size() - 1);
                    eraseWriteRead(r);
                    if(!isGuided()) {
                        maximalWriteEventIndexes.remove(r.getEventData());
                    }
                    else if(!deletedOnSwap.isEmpty() && deletedOnSwap.getLast() == r) {
                        maximalWriteEventIndexes.remove(r.getEventData());
                        deletedOnSwap.removeLast();
                    }

                } else {
                    guideInfo.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.RESTORE);
                    return;
                }
                break;
            case WRITE:
                ArrayList<WriteTransactionalEvent> writeEvents = writeEventsPerVariable.get(e.getVariable());

                if(!writeEvents.isEmpty() && writeEvents.get(writeEvents.size()-1).getTransactionId() == e.getTransactionId()){
                    writeEvents.remove(writeEvents.size() -1);

                }
                history.removeWrite(e.getVariable(), e.getTransactionId());
                break;
            case COMMIT:
                if(getDatabaseBacktrackMode() == GuideInfo.BacktrackTypes.SWAP &&
                        guideInfo.getDeleted().contains(tr.getFirst())) {
                    for(var ei : tr){
                        if (ei.getType() == TransactionalEvent.Type.WRITE) {
                            backtrackPoints.remove(ei.getEventData());
                        }
                    }
                    break;
                }
                else if(isGuided()){
                    break;
                }

                Pair<WriteTransactionalEvent, ReadTransactionalEvent> p = nextSwap();
                if (p == null) {
                    guideInfo.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.JPF);
                    //remove all backtrack points
                    for(var ei : tr){
                        if (ei.getType() == TransactionalEvent.Type.WRITE) {
                            backtrackPoints.remove(ei.getEventData());
                        }
                    }
                }
                else {
                    guideInfo.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.SWAP);
                    WriteTransactionalEvent wSwap = p._1;
                    ReadTransactionalEvent rSwap = p._2;
                    generateBacktrackPath(wSwap, rSwap);

                    rSwap.setBacktrackEvent(events.get(rSwap.getTransactionId()-1).getLast().getEventData());

                    return;
                }

                break;
            case ABORT:
                //If T is not longer aborted, the writes have to be added to the history: if a read change its wr, it
                //may be committed.
                for(var ea: events.get(events.size() - 1)){
                    if(ea.getType() == TransactionalEvent.Type.WRITE) {

                        var wa = (WriteTransactionalEvent) ea;
                        var writes = writeEventsPerVariable.get(ea.getVariable());

                        //Transactions have at most one visible write per variable.
                        if (!writes.isEmpty() && writes.get(writes.size() - 1).getTransactionId() == ea.getTransactionId()) {
                            writes.remove(writes.size() - 1);
                        }
                        writes.add(wa);
                        history.addWrite(wa.getVariable(), wa.getTransactionId());
                    }
                }
                break;
            case BEGIN:
                history.removeLastTransaction();
                sessionOrder.get(e.getThreadId()).remove(sessionOrder.get(e.getThreadId()).size() - 1);
                break;

        }

        trueHistory = null;

        if(!isGuided()) {
            guideInfo.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.JPF);
        }
        programExtendedOrder.put(e.getThreadId(), programExtendedOrder.get(e.getThreadId()) - 1);


        EventData ed = e.getEventData();

        instructionsMapped.remove(ed);
        int n = timesPathExecuted.get(ed.getPath());
        if(n == -1) timesPathExecuted.remove(ed.getPath());
        else timesPathExecuted.put(ed.getPath(), n-1);

        tr.removeLast();
        if(tr.isEmpty()) events.remove(events.size() - 1);
        if(e.getType() == TransactionalEvent.Type.WRITE){
            var newWE = tr.getLastWriteEvent(e.getVariable());
            if(newWE != null){
                writeEventsPerVariable.get(e.getVariable()).add(newWE);
            }
        }

    }


}
