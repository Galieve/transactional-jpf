package fr.irif.database;

import fr.irif.events.*;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.util.Pair;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.Transition;

import java.awt.geom.IllegalPathStateException;
import java.util.*;

public class Database {

    protected Oracle oracle;

    protected ArrayList<TransactionalEvent> events;

    protected HashMap<Instruction, Integer> backtrackPoints;

    //It points to the event w in writeEventsPerVariable.get(e.getVariable()) such that
    //when e it was added, w was the last event in that list.
    protected HashMap<TransactionalEvent, Integer> maximalWriteEventIndexes;

    protected HashMap<String, ArrayList<WriteTransactionalEvent>> writeEventsPerVariable;

    protected HashMap<String, ArrayList<ReadTransactionalEvent>> readEventsPerVariable;

    protected HashMap<Integer, ArrayList<Integer>> sessionOrder;

    protected HashMap<Integer, Integer> programExtendedOrder;

    protected HashMap<Instruction,TransactionalEvent> instructionsMapped;

    protected boolean mockAccess;

    protected GuideInfo guideInfo;

    protected History history;

    protected static Database databaseInstance;

    private Database(Config config) {
        events = new ArrayList<>();
        backtrackPoints = new HashMap<>();
        writeEventsPerVariable = new HashMap<>();
        readEventsPerVariable = new HashMap<>();
        history = config.getEssentialInstance("db.database_model.class", History.class);
        guideInfo = new GuideInfo();
        sessionOrder = new HashMap<>();
        instructionsMapped = new HashMap<>();
        oracle = Oracle.getOracle();
        maximalWriteEventIndexes = new HashMap<>();
        mockAccess = true;
        programExtendedOrder = new HashMap<>();
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
    
    public boolean isGuided(){
        return guideInfo.getDatabaseBacktrackMode() == GuideInfo.BacktrackTypes.SWAP ||
                guideInfo.getDatabaseBacktrackMode() == GuideInfo.BacktrackTypes.RESTORE;
    }


    public void addEvent(TransactionalEvent t){
        if(isMockAccess()) return;
        assert oracle.getInstrucionIndex(t.getInstruction()) != null;

        instructionsMapped.put(t.getInstruction(), t);
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
                }
                WriteTransactionalEvent w = writeEvents.get(writeEvents.size() - 1);
                ReadTransactionalEvent r = (ReadTransactionalEvent) t;
                r.changeWriteEvent(w);
                setWriteRead(r);
                readEventsPerVariable.putIfAbsent(t.getVariable(), new ArrayList<>());
                readEventsPerVariable.get(t.getVariable()).add((ReadTransactionalEvent) t);
                maximalWriteEventIndexes.put(t, writeEvents.size() - 1);

                break;
            case BEGIN:
                //TODO
                sessionOrder.putIfAbsent(t.getThreadId(), new ArrayList<>());
                history.addTransaction(t.getTransactionId(), t.getThreadId(), sessionOrder.get(t.getThreadId()));
                sessionOrder.get(t.getThreadId()).add(t.getTransactionId());
                programExtendedOrder.putIfAbsent(t.getThreadId(), 0);
                break;
            case END:
                if(getDatabaseBacktrackMode() != GuideInfo.BacktrackTypes.SWAP) {
                    for (int i = events.size() - 2; i >= 0; --i) {
                        TransactionalEvent e = events.get(i);
                        if (e.getTransactionId() != t.getTransactionId()) break;
                        else if (e.getType() == TransactionalEvent.Type.WRITE) {
                            backtrackPoints.putIfAbsent(e.getInstruction(), readEventsPerVariable.get(e.getVariable()).size() - 1);
                        }
                    }
                }
                break;
        }
        programExtendedOrder.put(t.getThreadId(), programExtendedOrder.get(t.getThreadId()) + 1);

    }

    public boolean isExecutingTransactionalEvent(Transition t, TransactionalEvent e){
        if(events.isEmpty() || e == null) return false;
        switch (e.getType()){
            case READ:
                if(t.getStepCount() < 5) return false;
                else return t.getStep(t.getStepCount() - 4).getInstruction() == e.getInstruction();
            case WRITE:
                if(t.getStepCount() < 5) return false;
                else return t.getStep(t.getStepCount() - 5).getInstruction() == e.getInstruction();
            case BEGIN:
            case END:
                if(t.getStepCount() < 3) return false;
                else return t.getStep(t.getStepCount() - 3).getInstruction() == e.getInstruction();
            case UNKNOWN:
                if (t.getStepCount() < 3) return false;
                else if (t.getStepCount() < 5) return t.getStep(t.getStepCount() - 3).getInstruction() == e.getInstruction();
                else return t.getStep(t.getStepCount() - 3).getInstruction() == e.getInstruction() ||
                            t.getStep(t.getStepCount() - 5).getInstruction() == e.getInstruction();
            default:
                //There is no default
                return false;

        }
    }

    protected void generateRestorePath(ReadTransactionalEvent r){
        Instruction backInst = r.getBacktrackInstruction();
        TransactionalEvent e = instructionsMapped.get(backInst);
        TransactionalEvent w = r.getWriteEvent();
        LinkedList<TransactionalEvent> restorePath = new LinkedList<>();

        boolean end = false;

        int pi_idx = e.getObservationSequenceIndex()+1;
        OracleData orac_idx = new OracleData(0,0);
        orac_idx = oracle.getNextData(orac_idx);
        while(!end){
            Instruction i = oracle.getInstruction(orac_idx);
            if(!instructionsMapped.containsKey(i)){
                restorePath.add(new UnknownEvent(i, orac_idx));
            }
            else{
                TransactionalEvent t = instructionsMapped.get(i);
                if(t.getTransactionId() == r.getTransactionId()){
                    restorePath.add(t);
                }
                else if(t.getObservationSequenceIndex() > e.getObservationSequenceIndex()) {
                    if(pi_idx <= t.getObservationSequenceIndex()) {
                        for (int j = pi_idx; j <= t.getObservationSequenceIndex(); ++j) {
                            restorePath.add(events.get(j));
                        }
                        pi_idx = t.getObservationSequenceIndex() + 1;
                    }
                }
                if(t.getTransactionId() == w.getTransactionId() && t.getType() == TransactionalEvent.Type.END){
                    end = true;
                }
            }
            orac_idx = oracle.getNextData(orac_idx);

        }
        guideInfo.addGuide(restorePath, e, null);
    }

    protected void generateBacktrackPath(WriteTransactionalEvent w, ReadTransactionalEvent r){
        LinkedList<TransactionalEvent> backtrackPath = new LinkedList<>();

        for(int i = r.getObservationSequenceIndex(); i < events.size(); ++i){
            if(history.
                    areWRSO_starRelated(events.get(i).getTransactionId(), w.getTransactionId())){
                backtrackPath.add(events.get(i));
            }
        }
        int trIndex = r.getObservationSequenceIndex();
        while(trIndex >= 0 && events.get(trIndex).getTransactionId() == r.getTransactionId()){
            --trIndex;
        }
        //++trIndex; //we don't want to backtrack the end of the last transaction.
        for(int i = trIndex + 1; i <= r.getObservationSequenceIndex(); ++i ){
            backtrackPath.add(events.get(i));
        }
        guideInfo.addGuide(backtrackPath, events.get(trIndex), w);

        //TODO: call DatabaseRelations to refactor the relations.
    }

    public void backtrackDatabase() {
        if(isMockAccess()) return;
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
                    maximalWriteEventIndexes.remove(e);

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
                            backtrackPoints.remove(ei.getInstruction());
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
                            rSwap.setBacktrackInstruction(events.get(i).getInstruction());
                            break;
                        }
                    }
                    return;
                }

                break;
            case BEGIN:
                history.removeLastTransaction();
                sessionOrder.get(e.getThreadId()).remove(sessionOrder.get(e.getThreadId()).size() - 1);
                programExtendedOrder.put(e.getThreadId(), programExtendedOrder.get(e.getThreadId()) - 1);

                break;

        }

        if(!isGuided()) {
            guideInfo.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.JPF);
        }
        instructionsMapped.remove(e.getInstruction());
        events.remove(events.size() - 1);

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
        if (r.getOracleOrder().compareTo(w.getOracleOrder()) > 0) return false;

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

    //TODO: check if this is true for all our models.
    public boolean maximumConsistentWrite(TransactionalEvent w, int n) {
        ArrayList<WriteTransactionalEvent> writeEvents = writeEventsPerVariable.get(w.getVariable());
        return writeEvents.get(n) == w;
    }

    public boolean isMaximallyAdded(TransactionalEvent r) {
        if (r.getType() == TransactionalEvent.Type.READ) {
            int n = maximalWriteEventIndexes.get(r);
            return !swapped(r) && maximumConsistentWrite(((ReadTransactionalEvent) r).getWriteEvent(), n);
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
                    for (int j = backtrackPoints.get(w.getInstruction()); j >= 0; --j) {
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
                            backtrackPoints.put(e.getInstruction(),j-1);
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

    public TransactionalEvent getEventFromInstruction(Instruction i) {
        return instructionsMapped.get(i);
    }

    protected void setWriteRead(ReadTransactionalEvent r){
        WriteTransactionalEvent w = r.getWriteEvent();
        history.setWR(r.getVariable(), w.getTransactionId(), r.getTransactionId());
    }

    public void changeWriteRead(WriteTransactionalEvent w, ReadTransactionalEvent r){
        eraseWriteRead(r);
        r.changeWriteEvent(w);
        setWriteRead(r);
    }

    protected void eraseWriteRead(ReadTransactionalEvent r){
        WriteTransactionalEvent w = r.getWriteEvent();
        history.removeWR(r.getVariable(), w.getTransactionId(), r.getTransactionId());
    }

    public OracleData getOrAddOraclePosition(Instruction i){
        if(isMockAccess()) return null;
        oracle.addInstructionIfAbsent(i);
        return oracle.getInstrucionIndex(i);
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
        return events.get(idx);
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


}
