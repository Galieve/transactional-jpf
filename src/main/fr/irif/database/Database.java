package fr.irif.database;

import com.rits.cloning.Cloner;
import fr.irif.events.*;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.util.Pair;
import gov.nasa.jpf.vm.Transition;

import java.awt.geom.IllegalPathStateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class Database {

    protected Oracle oracle;

    protected ArrayList<TransactionalEvent> events;

    protected ArrayList<TransactionalEvent> beginEvents;

    protected HashMap<EventData, Integer> backtrackPoints;

    //It points to the event w in writeEventsPerVariable.get(e.getVariable()) such that
    //when e it was added, w was the last event in that list.
    protected HashMap<EventData, EventData> maximalWriteEventIndexes;

    protected HashMap<String, ArrayList<WriteTransactionalEvent>> writeEventsPerVariable;

    protected HashMap<String, ArrayList<ReadTransactionalEvent>> readEventsPerVariable;

    protected HashMap<Integer, ArrayList<Integer>> sessionOrder;

    protected HashMap<Integer, Integer> programExtendedOrder;

    protected HashMap<EventData,TransactionalEvent> instructionsMapped;

    protected HashMap<String, Integer> timesPathExecuted;

    protected boolean mockAccess;

    protected GuideInfo guideInfo;

    protected History history;

    protected static Database databaseInstance;

    protected String mockPath;

    private Database(Config config) {
        events = new ArrayList<>();
        backtrackPoints = new HashMap<>();
        writeEventsPerVariable = new HashMap<>();
        readEventsPerVariable = new HashMap<>();
        history = config.getEssentialInstance("db.database_model.class", History.class,
                new Class[]{Config.class},
                new Object[]{config});
        guideInfo = new GuideInfo();
        sessionOrder = new HashMap<>();
        instructionsMapped = new HashMap<>();
        oracle = Oracle.getOracle();
        maximalWriteEventIndexes = new HashMap<>();
        mockAccess = true;
        programExtendedOrder = new HashMap<>();
        timesPathExecuted = new HashMap<>();
        beginEvents = new ArrayList<>();
    }

    public static Database getDatabase(Config config) {
        if (databaseInstance == null) {
            databaseInstance = new Database(config);
        }
        return databaseInstance;
    }


    public static Database getDatabase() {
        if (databaseInstance == null) {
            throw new IllegalPathStateException();
        }
        return databaseInstance;
    }

    //Use it carefully.
    public Database cloneDatabase(){
        Cloner cloner = new Cloner();
        var clone = cloner.deepClone(this);
        //This variable has to be shared.
        clone.backtrackPoints = backtrackPoints;
        return clone;
    }

    public void setDatabaseInstance(Database db){
        databaseInstance = db;
    }
    
    public boolean isGuided(){
        return guideInfo.getDatabaseBacktrackMode() == GuideInfo.BacktrackTypes.SWAP ||
                guideInfo.getDatabaseBacktrackMode() == GuideInfo.BacktrackTypes.RESTORE;
    }


    public void addEvent(TransactionalEvent t){

        EventData ed = t.getEventData();
        timesPathExecuted.put(ed.getPath(), ed.getTime()); //before mockAccess, we need it to check path of alternatives

        if(isMockAccess()){
            mockPath = ed.getPath();
            return;
        }

        instructionsMapped.put(ed, t);
        timesPathExecuted.put(ed.getPath(), ed.getTime());
        events.add(t);

        switch (t.getType()) {
            case WRITE:
                //TODO: check constraint size
                if(t.getTransactionId() != 0 && writeEventsPerVariable.get(t.getVariable()) == null){
                    throw new IllegalStateException("Initial transaction does not contain variable "+t.getVariable());
                }
                if(!isGuided()) {
                    readEventsPerVariable.putIfAbsent(t.getVariable(), new ArrayList<>());
                }
                writeEventsPerVariable.putIfAbsent(t.getVariable(), new ArrayList<>());
                writeEventsPerVariable.get(t.getVariable()).add((WriteTransactionalEvent) t);
                history.addWrite(t.getVariable(),t.getTransactionId());

                break;
            case READ:
                ArrayList<WriteTransactionalEvent> writeEvents = writeEventsPerVariable.get(t.getVariable());
                if(writeEventsPerVariable.get(t.getVariable()) == null){
                    throw new IllegalStateException("Initial transaction does not contain variable " + t.getVariable());
                }                //maximalWriteEventIndexes.put(t, writeEvents.size() - 1);

                WriteTransactionalEvent w = writeEvents.get(writeEvents.size() - 1);
                ReadTransactionalEvent r = (ReadTransactionalEvent) t;
                r.setWriteEvent(w);
                setWriteRead(r);
                readEventsPerVariable.putIfAbsent(t.getVariable(), new ArrayList<>());
                readEventsPerVariable.get(t.getVariable()).add((ReadTransactionalEvent) t);
                break;
            case BEGIN:
                //TODO
                sessionOrder.putIfAbsent(t.getThreadId(), new ArrayList<>());
                history.addTransaction(t.getTransactionId(), t.getThreadId(), sessionOrder.get(t.getThreadId()));
                sessionOrder.get(t.getThreadId()).add(t.getTransactionId());
                programExtendedOrder.putIfAbsent(t.getThreadId(), 0);
                oracle.addBegin(ed);
                beginEvents.add(t);
                break;
            case END:
                if(getDatabaseBacktrackMode() != GuideInfo.BacktrackTypes.SWAP) {

                    oracle.addEnd(ed);
                    for (int i = events.size() - 2; i >= 0; --i) {
                        TransactionalEvent e = events.get(i);
                        if (e.getTransactionId() != t.getTransactionId()) break;
                        else if (e.getType() == TransactionalEvent.Type.WRITE) {
                            backtrackPoints.putIfAbsent(e.getEventData(), readEventsPerVariable.get(e.getVariable()).size() - 1);
                        }
                    }
                }
                break;
        }
        programExtendedOrder.put(t.getThreadId(), programExtendedOrder.get(t.getThreadId()) + 1);

    }

    public boolean isLastEventReadBacktrackable(){
        var e = getLastEvent();
        if(e == null) return false;
        if(e.getType() != TransactionalEvent.Type.READ) return false;
        WriteTransactionalEvent w = ((ReadTransactionalEvent) e).getWriteEvent();
        return !swapped(e) && w.getWriteIndex() != 0;
    }

    public boolean isExecutingTransactionalEvent(Transition t, TransactionalEvent e){
        if(events.isEmpty() || e == null) return false;
        String s = TrEventRegister.getEventRegister().getStackTrace(t.getThreadInfo());
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(s.split("\\n")));
        if(lines.size() >= 2) {
            lines.remove(lines.size() - 1);
            lines.remove(lines.size() - 1); //This is lines.size() - 2 in the original array.
        }//There is one level of extra nesting before the end of a transaction.
        s = String.join("\n", lines);

        EventData ed = e.getEventData();
        return ed.equals(new EventData(s, timesPathExecuted.get(ed.getPath()), ed.getBeginEvent()));
       }

    protected void generateRestorePath(ReadTransactionalEvent r){
        EventData backEvent = r.getBacktrackEvent();
        TransactionalEvent e = instructionsMapped.get(backEvent);
        TransactionalEvent w = r.getWriteEvent();
        LinkedList<Transaction> restorePath = new LinkedList<>();

        int pi_idx = e.getObservationSequenceIndex()+1;
        EventData orac_idx = new EventData("", -1, null);
        orac_idx = oracle.getNextData(orac_idx);
        while(true){
            if(!instructionsMapped.containsKey(orac_idx)){
                EventData endData = oracle.getEnd(orac_idx);
                restorePath.add(new Transaction(new UnknownEvent(orac_idx), new UnknownEvent(endData)));
            }
            else{
                TransactionalEvent begin = instructionsMapped.get(orac_idx);
                TransactionalEvent end = instructionsMapped.get(oracle.getEnd(orac_idx));
                LinkedList<TransactionalEvent> trans = new LinkedList<>();
                if(begin.getTransactionId() == r.getTransactionId()){
                   for(int i = begin.getObservationSequenceIndex(); i < r.getObservationSequenceIndex(); ++i){
                       trans.add(events.get(i));
                   }
                   var wMCW = instructionsMapped.get(maximalWriteEventIndexes.get(r.getEventData()));
                   r.setWriteEvent((WriteTransactionalEvent) wMCW);
                   trans.add(r);
                   var endData = oracle.getEnd(begin.getEventData());
                   trans.add(new UnknownEvent(endData));
                   //trans.add(end); r is swapped, so end is null.
                   restorePath.add(new Transaction(trans));
                }
                else if(pi_idx <= end.getObservationSequenceIndex()) {
                    for (int j = pi_idx; j <= end.getObservationSequenceIndex(); ++j) {
                        TransactionalEvent t = events.get(j);
                        trans.add(t);
                        if(t.getType() == TransactionalEvent.Type.END) {
                            restorePath.add(new Transaction(trans));
                            trans = new LinkedList<>();
                        }
                    }
                    pi_idx = end.getObservationSequenceIndex() + 1;
                }
                //e.equals(w)?
                //end will also have the same trId, but begin is always not null
                if(begin.getTransactionId() == w.getTransactionId()){
                    break;
                }
            }
            orac_idx = oracle.getNextData(orac_idx);

        }
        guideInfo.addGuide(restorePath, e, null);
    }


    protected void generateBacktrackPath(WriteTransactionalEvent w, ReadTransactionalEvent r){
        LinkedList<Transaction> backtrackPath = new LinkedList<>();
        LinkedList<TransactionalEvent> transaction = new LinkedList<>();
        int i = r.getObservationSequenceIndex();

        //We need to start in a complete transaction, i.e. after the read.
        while(events.get(i).getType() != TransactionalEvent.Type.BEGIN){
            ++i;
        }
        while(i < events.size()){
            if(history.
                    areWRSO_starRelated(events.get(i).getTransactionId(), w.getTransactionId())){
                while(i < events.size()) {
                    transaction.add(events.get(i));
                    if(events.get(i).getType() == TransactionalEvent.Type.END) break;
                    ++i;
                }

                backtrackPath.add(new Transaction(transaction));
                transaction = new LinkedList<>();
            }
            ++i;
        }
        i = r.getObservationSequenceIndex();
        while(i >= 0 && events.get(i).getTransactionId() == r.getTransactionId()){
            transaction.addFirst(events.get(i));
            --i;
        }
        //++trIndex; //we don't want to backtrack the end of the last transaction.
        backtrackPath.add(new Transaction(transaction));
        guideInfo.addGuide(backtrackPath, events.get(i), w);

        //TODO: call DatabaseRelations to refactor the relations.
    }

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
        TransactionalEvent e = events.get(events.size() - 1);
        switch (e.getType()) {
            case READ:
                ReadTransactionalEvent r = (ReadTransactionalEvent) e;
                WriteTransactionalEvent w = ((ReadTransactionalEvent) e).getWriteEvent();

                if (!swapped(e) && w.getWriteIndex() != 0 && !isGuided()) {
                    WriteTransactionalEvent nw = writeEventsPerVariable.get(e.getVariable()).get(w.getWriteIndex() - 1);
                    changeWriteRead(nw, r);
                    guideInfo.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.READ);
                    return;

                } else if (!swapped(e) || isGuided()) {
                    readEventsPerVariable.get(e.getVariable()).remove(readEventsPerVariable.get(e.getVariable()).size() - 1);
                    eraseWriteRead(r);
                    if(!isGuided())
                        maximalWriteEventIndexes.remove(r.getEventData());

                } else {
                    generateRestorePath(r);
                    guideInfo.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.RESTORE);
                    return;
                }
                break;
            case WRITE:
                ArrayList<WriteTransactionalEvent> writeEvents = writeEventsPerVariable.get(e.getVariable());
                writeEvents.remove(writeEvents.size() - 1);
                history.removeWrite(e.getVariable(), e.getTransactionId());
                break;
            case END:
                if(isGuided()) break;

                Pair<WriteTransactionalEvent, ReadTransactionalEvent> p = nextSwap();
                if (p == null) {
                    guideInfo.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.JPF);
                    //remove all backtrack points
                    for(int i = events.size() - 2; i >= 0; --i){
                        TransactionalEvent ei = events.get(i);
                        if (ei.getTransactionId() != e.getTransactionId()) break;
                        else if (ei.getType() == TransactionalEvent.Type.WRITE) {
                            backtrackPoints.remove(ei.getEventData());
                        }
                    }
                }
                else {
                    guideInfo.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.SWAP);
                    WriteTransactionalEvent wSwap = p._1;
                    ReadTransactionalEvent rSwap = p._2;
                    generateBacktrackPath(wSwap, rSwap);

                    for (int i = rSwap.getObservationSequenceIndex() - 1; i >= 0; --i) {
                        //rSwap won't be in the first transaction.
                        if (events.get(i).getType() == TransactionalEvent.Type.END) {
                            rSwap.setBacktrackEvent(events.get(i).getEventData());
                            break;
                        }
                    }
                    return;
                }

                break;
            case BEGIN:
                history.removeLastTransaction();
                sessionOrder.get(e.getThreadId()).remove(sessionOrder.get(e.getThreadId()).size() - 1);
                beginEvents.remove(beginEvents.size()-1);
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
        events.remove(events.size() - 1);

    }

    public int getTimesPathExecuted(String s){
        Integer n = timesPathExecuted.get(s);
        if(n == null) return -1;
        else return n;
    }

    public int getNumberEvents() {
        return events.size();

    }

    public Integer getNumberOfWrites(String variable) {
        return writeEventsPerVariable.getOrDefault(variable, new ArrayList<>()).size();

    }

    public boolean swapped(TransactionalEvent e) {
        if (e.getType() != TransactionalEvent.Type.READ) return false;
        ReadTransactionalEvent r = (ReadTransactionalEvent) e;
        WriteTransactionalEvent w = r.getWriteEvent();
        if (oracle.compareEvents(r, w) > 0) return false;

        for(int i = 0; i < r.getTransactionId(); ++i){
            if(history.areWRSO_plusRelated(w.getTransactionId(),i))
                return false;
        }
        //optimize with a segement tree
        for(int i = e.getObservationSequenceIndex() - 1; i >= 0; --i){
            TransactionalEvent ei = events.get(i);
            if(ei.getTransactionId() != e.getTransactionId())
                break;
            else if(ei.getType() == TransactionalEvent.Type.READ &&
                    ((ReadTransactionalEvent) ei).getWriteEvent().getTransactionId() == w.getTransactionId()){
                return false;
            }
        }
        return true;
        //return databaseRelations.numberWR(w.getTransactionId(), e.getTransactionId()) == 1;

    }

    /*//TODO: check if this is true for all our models.
    public boolean maximumConsistentWrite(TransactionalEvent w, int n) {
        ArrayList<WriteTransactionalEvent> writeEvents = writeEventsPerVariable.get(w.getVariable());
        return writeEvents.get(n) == w;
    }

     */

    public boolean isMaximallyAdded(TransactionalEvent t) {
        if (t.getType() == TransactionalEvent.Type.READ) {
            var r = (ReadTransactionalEvent) t;
            var w = maximalWriteEventIndexes.get(r.getEventData());
            return !swapped(r) && r.getWriteEvent().getEventData().equals(w);
        }
        else return true;
    }

    //write, read
    public Pair<WriteTransactionalEvent, ReadTransactionalEvent> nextSwap(){
        for(int i = events.size()-2; i >= 0; --i){
            TransactionalEvent e = events.get(i);
            switch (e.getType()){
                case BEGIN:
                    //There is no more write events in this transaction, we cannot swap.
                    return null;
                case WRITE:
                    WriteTransactionalEvent w = (WriteTransactionalEvent) e;
                    for (int j = backtrackPoints.get(w.getEventData()); j >= 0; --j) {
                        //TODO: revisar
                        ReadTransactionalEvent r = readEventsPerVariable.get(w.getVariable()).get(j);
                        if(history.areWRSO_starRelated(r.getTransactionId(), w.getTransactionId())) continue;
                        if(!isMaximallyAdded(r)) continue;
                        boolean delIMA = true;
                        for(int k = r.getObservationSequenceIndex() + 1;k < events.size() && delIMA; ++k){
                            TransactionalEvent d = events.get(k);
                            if(!history.areWRSO_starRelated(d.getTransactionId(),w.getTransactionId())
                                && !isMaximallyAdded(d)){
                                delIMA = false;
                            }
                        }

                        if(delIMA){
                            backtrackPoints.put(e.getEventData(),j-1);
                            return new Pair<>(w, r);
                        }
                    }
                    break;

                default:
                    //do nothing
            }
        }
        return null;
    }

    public GuideInfo.BacktrackTypes getDatabaseBacktrackMode() {
        return guideInfo.getDatabaseBacktrackMode();
    }

    public void setDatabaseBacktrackMode(GuideInfo.BacktrackTypes databaseBacktrackMode) {
        guideInfo.setDatabaseBacktrackMode(databaseBacktrackMode);
    }

    public GuideInfo getGuideInfo() {
        return guideInfo;
    }

    public void resetGuidedInfo() {
      guideInfo.resetPath();
    }

    public TransactionalEvent getEventFromEventData(EventData eventData) {
        return instructionsMapped.get(eventData);
    }

    protected void setWriteRead(ReadTransactionalEvent r){
        WriteTransactionalEvent w = r.getWriteEvent();
        history.setWR(r.getVariable(), w.getTransactionId(), r.getTransactionId());
    }

    public void changeWriteRead(WriteTransactionalEvent w, ReadTransactionalEvent r){
        eraseWriteRead(r);
        r.setWriteEvent(w);
        setWriteRead(r);
    }

    protected void eraseWriteRead(ReadTransactionalEvent r){
        WriteTransactionalEvent w = r.getWriteEvent();
        history.removeWR(r.getVariable(), w.getTransactionId(), r.getTransactionId());
    }

    public boolean isMockAccess() {
        return mockAccess;
    }

    public void setMockAccess(boolean mockAccess) {
        this.mockAccess = mockAccess;
    }

    public boolean isAssertionViolated(){
        if(events.isEmpty()) return false;
        TransactionalEvent t = getLastEvent();
        if(t.getType()== TransactionalEvent.Type.ASSERT){
            return  !((AssertTransactionalEvent) t).checkAssertion();
        }
        else return false;
    }

    public boolean isConsistent(){
        return !isAssertionViolated() && history.isConsistent();
    }

    public void confirmMaximumConsistency(){
        if(events.isEmpty()) return;
        TransactionalEvent t = events.get(events.size() - 1);
        if(t.getType() == TransactionalEvent.Type.READ){
            ReadTransactionalEvent r = (ReadTransactionalEvent) t;
            maximalWriteEventIndexes.putIfAbsent(r.getEventData(), r.getWriteEvent().getEventData());
        }
    }

    public int getTransactionalId() {
        if(events.isEmpty()) return -1;
        return events.get(events.size() -1).getTransactionId();
    }

    public int getTransactionalS0Id(int threadId){
        if(!sessionOrder.containsKey(threadId)) return -1;
        return sessionOrder.get(threadId).size() - 1;
    }

    public int getPOId(int threadId){
        if(!programExtendedOrder.containsKey(threadId)) return 0;
        return programExtendedOrder.get(threadId);
    }

    public TransactionalEvent getLastEvent(){
        return getEvent(events.size()-1);
    }

    protected TransactionalEvent getEvent(int idx){
        if(idx < 0 || idx >= events.size()) return null;
        else return events.get(idx);
    }

    //Only for printing reasons. Hack.
    public WriteTransactionalEvent getWriteEvent(String var, int idx){
        return writeEventsPerVariable.get(var).get(idx);
    }

    public WriteTransactionalEvent getLastWriteEvent(String var){
        if(!writeEventsPerVariable.containsKey(var) ||
                writeEventsPerVariable.get(var).isEmpty()) return null;
        else return writeEventsPerVariable.get(var).get(writeEventsPerVariable.get(var).size() - 1);
    }

    public EventData getLastBeginData(){
        if(beginEvents.isEmpty()) return null;
        else return beginEvents.get(beginEvents.size()-1).getEventData();
    }

    public String getDatabaseState(){
        StringBuilder sb = new StringBuilder();
        for(var e: events){
            sb.append(e.toWRString()).append("\n");
        }
        sb.append("Completeness: ").append(!isAssertionViolated()).append("\n");
        return sb.toString();
    }

}
