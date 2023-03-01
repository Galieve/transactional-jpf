package country.lab.database;

import country.lab.events.*;
import country.lab.events.*;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class TrDatabase extends Database{

    public TrDatabase(Config config) {
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
                    }

                } else {
                    generateRestorePath(r);
                    guideInfo.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.RESTORE);
                    return;
                }
                break;
            case WRITE:
                ArrayList<WriteTransactionalEvent> writeEvents = writeEventsPerVariable.get(e.getVariable());

                if(!writeEvents.isEmpty() && writeEvents.get(writeEvents.size()-1).getTransactionId() == e.getTransactionId()){
                    writeEvents.remove(writeEvents.size() -1);

                }
                if(!deletedOnSwap.isEmpty() && deletedOnSwap.getLast() == e) {
                    backtrackPoints.remove(e.getEventData());
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

        if(!deletedOnSwap.isEmpty() && deletedOnSwap.getLast() == e) {
            maximalWriteEventIndexes.remove(e.getEventData());
            deletedOnSwap.removeLast();
        }

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

    @Override
    public void addEvent(TransactionalEvent t){

        EventData ed = t.getEventData();
        timesPathExecuted.put(ed.getPath(), ed.getTime()); //before mockAccess, we need it to check path of alternatives

        if(isMockAccess()){
            mockPath = ed.getPath();
            return;
        }

        instructionsMapped.put(ed, t);
        timesPathExecuted.put(ed.getPath(), ed.getTime());


        switch (t.getType()) {
            case WRITE:
                if(!isGuided()) {
                    readEventsPerVariable.putIfAbsent(t.getVariable(), new ArrayList<>());
                }
                writeEventsPerVariable.putIfAbsent(t.getVariable(), new ArrayList<>());


                var writesOfX = writeEventsPerVariable.get(t.getVariable());
                if(!writesOfX.isEmpty() && writesOfX.get(writesOfX.size()-1).getTransactionId() == t.getTransactionId()){
                    writesOfX.remove(writesOfX.size() -1);

                }
                writesOfX.add((WriteTransactionalEvent) t);

                history.addWrite(t.getVariable(),t.getTransactionId());

                break;
            case READ:
                ArrayList<WriteTransactionalEvent> writeEvents = writeEventsPerVariable.get(t.getVariable());
                ReadTransactionalEvent r = (ReadTransactionalEvent) t;
                WriteTransactionalEvent w = null;

                if(writeEventsPerVariable.get(t.getVariable()) == null){
                    //If the event does not exist we create a new event out
                    //of the blue that writes null and that would be theoretically
                    //placed just before the read.
                    var edw = new EventData(ed.getPath()+"---"+"FAKE WRITE EVENT", ed.getTime(), ed.getBeginEvent());
                    w = new WriteTransactionalEvent(edw,
                            new ArrayList<>(Arrays.asList(r.getVariable(), "null")),
                            0, r.getObservationSequenceIndex(),
                            r.getThreadId(), r.getTransactionId(),
                            r.getTransactionalSessionId(), r.getPoId()-1);
                    instructionsMapped.put(edw, w);

                }
                else {
                    w = writeEvents.get(writeEvents.size() - 1);
                }
                r.setWriteEvent(w);
                setWriteRead(r);
                readEventsPerVariable.putIfAbsent(t.getVariable(), new ArrayList<>());
                readEventsPerVariable.get(t.getVariable()).add((ReadTransactionalEvent) t);
                break;
            case BEGIN:
                sessionOrder.putIfAbsent(t.getThreadId(), new ArrayList<>());
                history.addTransaction(t.getTransactionId(), t.getThreadId(), sessionOrder.get(t.getThreadId()));
                sessionOrder.get(t.getThreadId()).add(t.getTransactionId());
                programExtendedOrder.putIfAbsent(t.getThreadId(), 0);
                oracle.addBegin(ed);
                events.add(new Transaction(new LinkedList<>()));
                break;
            case COMMIT:
                if(getDatabaseBacktrackMode() != GuideInfo.BacktrackTypes.SWAP) {
                    var begin = events.get(events.size() -1).getFirst();
                    oracle.addCommit(begin.getEventData(), ed);
                    for(var e: events.get(events.size()-1)){
                        if (e.getType() == TransactionalEvent.Type.WRITE) {
                            var writes = writeEventsPerVariable.get(e.getVariable());
                            if(writes.get(writes.size() - 1).getTransactionId() == e.getObservationSequenceIndex()) {
                                backtrackPoints.putIfAbsent(e.getEventData(), readEventsPerVariable.get(e.getVariable()).size() - 1);
                            }
                            else{
                                //TODO: remove this
                                backtrackPoints.putIfAbsent(e.getEventData(), -1);
                            }
                        }
                    }
                }
                break;
            case ABORT:
                for(var e: events.get(events.size() - 1)){
                    if(e.getType() == TransactionalEvent.Type.WRITE) {
                        var writes = writeEventsPerVariable.get(e.getVariable());
                        if (!writes.isEmpty() && writes.get(writes.size() - 1).getTransactionId() == e.getTransactionId()) {
                            writes.remove(writes.size() - 1);
                        }
                        history.removeWrite(e.getVariable(), e.getTransactionId());
                    }
                }
                break;
        }

        trueHistory = null;

        events.get(events.size()-1).addEvent(t);
        programExtendedOrder.put(t.getThreadId(), programExtendedOrder.get(t.getThreadId()) + 1);

    }


}
