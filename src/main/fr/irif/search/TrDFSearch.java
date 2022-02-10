package fr.irif.search;

import fr.irif.database.Database;
import fr.irif.database.GuideInfo;
import fr.irif.events.*;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.search.DFSearch;
import gov.nasa.jpf.util.Pair;
import gov.nasa.jpf.vm.Transition;
import gov.nasa.jpf.vm.VM;

import java.util.LinkedList;


public class TrDFSearch extends DFSearch {

    protected TrEventRegister trEventRegister;

    protected Database database;

    protected String msgListener;

    public TrDFSearch(Config config, VM vm) {
        super(config, vm);
        database = Database.getDatabase(config);
        trEventRegister = TrEventRegister.getEventRegister();
        msgListener = null;
    }

    protected void backtrackWithPath(LinkedList<TransactionalEvent> guidePath, TransactionalEvent end, WriteTransactionalEvent wSwap) {

        msgListener = "Starting "+database.getDatabaseBacktrackMode()+" mode.";
        notifyStateProcessed();

        Transition lastTransition = vm.getLastTransition();
        while(lastTransition != null && !database.isExecutingTransactionalEvent(lastTransition, end)){
            backtrack();

            depth--;
            notifyStateBacktracked();
            lastTransition = vm.getLastTransition();
        }
        trEventRegister.setChoiceGeneratorShared(true);
        //vm.getChoiceGenerator().reset();

        TransactionalEvent prev = end;
        while(!guidePath.isEmpty()){
            TransactionalEvent e = guidePath.getFirst();


            Pair<Boolean, Integer> p = computeStepsEvent(e);
            applyResetJumps(p);

            if(p._2 != null){
                if (e.getType() == TransactionalEvent.Type.READ) {
                    trEventRegister.setFakeRead(true);
                }
                forward();
                trEventRegister.setFakeRead(false);


                if(database.isExecutingTransactionalEvent(getTransition(), e)) {
                    if (e.getType() == TransactionalEvent.Type.READ) {
                        ReadTransactionalEvent rPast = (ReadTransactionalEvent) e;
                        ReadTransactionalEvent r = (ReadTransactionalEvent)
                                database.getEventFromInstruction(rPast.getInstruction());
                        WriteTransactionalEvent w = (WriteTransactionalEvent)
                                database.getEventFromInstruction(rPast.getWriteEvent().getInstruction());
                        if (w != null && guidePath.size() != 1) {
                            database.changeWriteRead(w, r);
                        }
                        else if(guidePath.size() != 1){
                            //r was the swapped event, r.getWriteEvent() is its IMA event.
                            //ONLY for notifying purposes
                            database.changeWriteRead(r.getWriteEvent(),r);
                        }
                        //If rPast.getBacktrackInstruction() == null, it is ok
                        r.setBacktrackInstruction(rPast.getBacktrackInstruction());

                    }
                    guidePath.removeFirst();
                    prev = e;
                }
                ++depth;
                notifyStateAdvanced();
                //if e.getType() != READ, fakeRead was false, so we don't have to care about this case
                // If it is not an end state and we restoring some unknown event, this event maybe not available. In that case, we just ommit it.
            } else if(p._1 && database.getDatabaseBacktrackMode() == GuideInfo.BacktrackTypes.RESTORE && e.getType() == TransactionalEvent.Type.UNKNOWN){
                guidePath.removeFirst();
            }

        }
        if(database.getDatabaseBacktrackMode() == GuideInfo.BacktrackTypes.SWAP) {
            WriteTransactionalEvent w = (WriteTransactionalEvent)
                    database.getEventFromInstruction(wSwap.getInstruction());
            ReadTransactionalEvent r = (ReadTransactionalEvent)
                    database.getEventFromInstruction(prev.getInstruction());

            database.changeWriteRead(w, r);
            //r.setBacktrackEvent(database.getEventFromInstruction(init.getInstruction()));
        }
        else{
            //the mode is RESTORE, we know that after an end either we will have:
            // - (backtrack)* swap -> after the swap the cgshared is correct
            // - (backtrack)* restore -> by induction, when it finish restoring, the cg would be correct
            // - (backtrack)* end -> after the end we should not share the cg
            trEventRegister.setChoiceGeneratorShared(false);
        }

        msgListener = database.getDatabaseBacktrackMode()+" mode ended.";
        notifyStateProcessed();
    }

    protected Pair<Boolean, Integer> computeStepsEvent(TransactionalEvent e){
        if(isEndState()) return new Pair<>(false, null);;
        if(trEventRegister.isChoiceGeneratorShared()) return new Pair<>(false, 0);
        //if(vm.getChoiceGenerator() != null && vm.getChoiceGenerator().isDone()) return new Pair<>(false, null);

        //we need to substitute this value during our mock tests, we will restore it before leaving.
        Integer n = 0, m = null;
        TrSingleProcessVM trvm = (TrSingleProcessVM) vm;

        database.setMockAccess(true);
        trEventRegister.setFakeRead(true);

        boolean b = true;
        while(b){
            b = forward();
            if(!b || !trEventRegister.isTransactionalTransition(getTransition())){
                break;
            }
            if(e!= null && database.isExecutingTransactionalEvent(getTransition(), e)){
                m = n;
            }
            trEventRegister.setChoiceGeneratorShared(false);

            backtrack();
            ++n;
        }

        //There is a local instruction, n has the correct value
        if(b) {
            backtrack();
        }
        else{
            //We are not backtracking, we reset to 0
            if(e == null)  n = 0;
            //We are backtracking and we have found the correct instruction
            else if (m != null) n = m;
            //We are backtracking and there is no local instruction nor the event we are searching for.
            else{
                n = null;
                //This case should never appear, if it does, this program is wrong (non-if version)
                if(database.getDatabaseBacktrackMode() != GuideInfo.BacktrackTypes.RESTORE)
                    System.out.println("DEBUG: error?");
            }
        }
        trEventRegister.setFakeRead(false);
        database.setMockAccess(false);
        return new Pair<>(true, n);

    }

    protected boolean continueBacktracking(){
        if(!backtrack()) return false;
        switch (database.getDatabaseBacktrackMode()){
            case READ:
                //In this case we have swapped some variable, so we are exploring a new branch
                return false;
            case JPF:
            case NONE:
                //Local instruction

                return true;
            case SWAP:
                return false;
            case RESTORE:
                return true;
            default:
                return true;
        }

    }



    @Override
    protected boolean forward() {
        //If it is guided, it has to follow every step, even if we can detect before reach the end that it is inconsistent
        if(!database.isGuided() && !database.isConsistent()){
            if(database.isAssertionViolated()) {
                AssertTransactionalEvent a = (AssertTransactionalEvent) database.getLastEvent();
                msgListener = "Invalid branch: assertion violated. " + a;
            }
            else {
                msgListener = "Invalid branch: inconsistent database.";
            }
            return false;
        }

        trEventRegister.addContextToPath();

        currentError = null;


        boolean ret = vm.forward();

        checkPropertyViolation();

        if(!ret) {
            trEventRegister.removeContextFromPath();
        }
        trEventRegister.printPath();


        return ret;
    }

    @Override
    protected boolean backtrack() {
        Transition lastTransition = vm.getLastTransition();
        TrEventRegister trEventRegister = TrEventRegister.getEventRegister();
        if(lastTransition != null && trEventRegister.isTransactionalTransition(lastTransition)) {

            database.backtrackDatabase();
            switch (database.getDatabaseBacktrackMode()){
                case READ:
                    trEventRegister.setChoiceGeneratorShared(true);
                    ReadTransactionalEvent r = (ReadTransactionalEvent) database.getLastEvent();
                    WriteTransactionalEvent nw = r.getWriteEvent();
                    WriteTransactionalEvent ow = database.getWriteEvent(nw.getVariable(), nw.getWriteIndex()+1);
                    msgListener = "Branch forked: change of write-read for event "+r +
                            "\n\t" + ow + " -> "+nw;
                    notifyStateProcessed();
                    notifyStateAdvanced();
                    return true;
                case SWAP:
                case RESTORE:
                    GuideInfo guide = database.getGuideInfo();
                    if(guide.hasPath()){
                        LinkedList<TransactionalEvent> path = guide.getGuidedPath();
                        TransactionalEvent endEvent = guide.getEndEvent();
                        WriteTransactionalEvent wSwap = guide.getWriteEventSwap();
                        database.resetGuidedInfo();
                        backtrackWithPath(path, endEvent, wSwap);
                        return true;
                    }
                    else break;
                case JPF:
                case NONE:
                    break;
            }
        }

        trEventRegister.printPath();
        trEventRegister.removeContextFromPath();

        return vm.backtrack();
    }

    public void applyResetJumps(Pair<Boolean, Integer> p){
        Boolean reset = p._1;
        Integer n = p._2;
        if(reset){
            vm.getChoiceGenerator().reset();
        }
        if(n != null){
            vm.getChoiceGenerator().advance(n);
        }
    }

    @Override
    public void search() {
        boolean depthLimitReached = false;

        depth = 0;

        notifySearchStarted();

        while (!done) {

            Pair<Boolean, Integer> p = computeStepsEvent(null);
            applyResetJumps(p);
            if (forward()) {
                depth++;
                notifyStateAdvanced();

                if (currentError != null){
                    notifyPropertyViolated();

                    if (hasPropertyTermination()) {
                        break;
                    }
                    // for search.multiple_errors we go on and treat this as a new state
                    // but hasPropertyTermination() will issue a backtrack request
                }

                if (depth >= depthLimit) {
                    depthLimitReached = true;
                    notifySearchConstraintHit("depth limit reached: " + depthLimit);
                    continue;
                }

                if (!checkStateSpaceLimit()) {
                    notifySearchConstraintHit("memory limit reached: " + minFreeMemory);
                    // can't go on, we exhausted our memory
                    break;
                }

            } else { // forward did not exbreakecute any instructions
                notifyStateProcessed();
                if (checkAndResetBacktrackRequest() || !isNewState() || isEndState() || isIgnoredState() || depthLimitReached || !database.isConsistent()) {
                    while(continueBacktracking()){
                        if(database.getDatabaseBacktrackMode() == GuideInfo.BacktrackTypes.JPF ||
                                database.getDatabaseBacktrackMode() == GuideInfo.BacktrackTypes.NONE ) {
                            depth--;
                            notifyStateBacktracked();
                            //vm.getChoiceGenerator().setDone();
                        }
                        database.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.NONE);

                    }
                    database.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.NONE);

                    //TODO: is this a hack?
                    if(depth == 0){
                        terminate();
                    }


                }
            }
        }

        notifySearchFinished();
    }

    public String getAndClearMessage() {
        String msg = msgListener;
        msgListener = null;
        return msg;
    }
}