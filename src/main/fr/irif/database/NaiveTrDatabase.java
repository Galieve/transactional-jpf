package fr.irif.database;

import fr.irif.events.EventData;
import fr.irif.events.ReadTransactionalEvent;
import fr.irif.events.TransactionalEvent;
import fr.irif.events.WriteTransactionalEvent;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.util.Pair;

import java.util.ArrayList;

public class NaiveTrDatabase extends Database{

    public NaiveTrDatabase(Config config){
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
            guideInfo.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.MOCK);
            return;
        }
        var tr = events.get(events.size() - 1);
        TransactionalEvent e = tr.getLast();
        switch (e.getType()) {
            case READ:
                ReadTransactionalEvent r = (ReadTransactionalEvent) e;
                WriteTransactionalEvent w = ((ReadTransactionalEvent) e).getWriteEvent();

                var localRead = w.getTransactionId() == r.getTransactionId();

                if (w.getWriteIndex() != 0 && !localRead) {
                    WriteTransactionalEvent nw = writeEventsPerVariable.get(e.getVariable()).get(w.getWriteIndex() - 1);
                    changeWriteRead(nw, r);
                    guideInfo.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.READ);
                    return;

                } else {
                    readEventsPerVariable.get(e.getVariable()).remove(
                            readEventsPerVariable.get(e.getVariable()).size() - 1);
                    eraseWriteRead(r);
                    maximalWriteEventIndexes.remove(r.getEventData());

                }
                break;
            case WRITE:
                ArrayList<WriteTransactionalEvent> writeEvents = writeEventsPerVariable.get(e.getVariable());
                writeEvents.remove(writeEvents.size() - 1);
                history.removeWrite(e.getVariable(), e.getTransactionId());
                break;
            case END:
                guideInfo.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.JPF);
                //remove all backtrack points
                for(var ei : tr){
                    if (ei.getType() == TransactionalEvent.Type.WRITE) {
                        backtrackPoints.remove(ei.getEventData());
                    }
                }

                break;
            case BEGIN:
                history.removeLastTransaction();
                sessionOrder.get(e.getThreadId()).remove(sessionOrder.get(e.getThreadId()).size() - 1);
                break;

        }

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
