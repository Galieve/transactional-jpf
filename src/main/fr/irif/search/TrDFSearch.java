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

    protected Integer currentAdvance;

    public TrDFSearch(Config config, VM vm) {
        super(config, vm);
        database = Database.getDatabase(config);
        trEventRegister = TrEventRegister.getEventRegister(config);
        msgListener = null;
        currentAdvance = null;
    }

    protected boolean guideForward(TransactionalEvent e){
        if(e.getType() != TransactionalEvent.Type.UNKNOWN) {
            //TODO: careful, check if this is correct.
            forward();
            return true;
        }
        else{
            boolean ret = false;
            while(true){
                ret = forward();
                if(!ret && database.isLastEventReadBacktrackable()){
                    database.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.JPF);
                    backtrack();
                    database.setDatabaseBacktrackMode(GuideInfo.BacktrackTypes.RESTORE);

                }
                else{
                    return ret;
                }
            }
        }
    }

    protected void backtrackWithPath(LinkedList<Transaction> guidePath, TransactionalEvent end, WriteTransactionalEvent wSwap) {

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
        Transaction t = guidePath.getFirst();
        currentAdvance = -1;
        while(!guidePath.isEmpty()){

            TransactionalEvent e = t.getNext();

            Pair<Boolean, Integer> p = computeStepsEvent(e);
            applyResetJumps(p);

            if(p._2 != null){
                if (e.getType() == TransactionalEvent.Type.READ) {
                    trEventRegister.setFakeRead(true);
                }
                guideForward(e);
                trEventRegister.setFakeRead(false);


                if(database.isExecutingTransactionalEvent(getTransition(), e)) {
                    t.setExecuting(true);
                    if (e.getType() == TransactionalEvent.Type.READ) {
                        ReadTransactionalEvent rPast = (ReadTransactionalEvent) e;
                        ReadTransactionalEvent r = (ReadTransactionalEvent)
                                database.getEventFromEventData(rPast.getEventData());
                        WriteTransactionalEvent w = (WriteTransactionalEvent)
                                database.getEventFromEventData(rPast.getWriteEvent().getEventData());
                        if (w != null && (guidePath.size() != 1 || t.size() != 1)) {
                            database.changeWriteRead(w, r);
                        }
                        else if(guidePath.size() != 1 || t.size() != 1){
                            //r was the swapped event, r.getWriteEvent() is its IMA event.
                            //ONLY for notifying purposes
                            database.changeWriteRead(r.getWriteEvent(),r);
                        }
                        //If rPast.getBacktrackInstruction() == null, it is ok
                        r.setBacktrackEvent(rPast.getBacktrackEvent());

                    }
                    t.removeFirst();
                    currentAdvance = p._2;
                    if(t.isEmpty()){
                        currentAdvance = -1;
                        guidePath.removeFirst();
                        if(!guidePath.isEmpty())
                            t = guidePath.getFirst();

                    }
                    prev = e;
                }
                ++depth;
                if(!guidePath.isEmpty())
                    notifyStateAdvanced();
                //if e.getType() != READ, fakeRead was false, so we don't have to care about this case
                // If it is not an end state and we restoring some unknown event, this event maybe not available. In that case, we just ommit it.
            } /*else if(p._1 && database.getDatabaseBacktrackMode() == GuideInfo.BacktrackTypes.RESTORE && e.getType() == TransactionalEvent.Type.UNKNOWN){
                guidePath.removeFirst();
            }
            */

        }
        if(database.getDatabaseBacktrackMode() == GuideInfo.BacktrackTypes.SWAP) {
            WriteTransactionalEvent w = (WriteTransactionalEvent)
                    database.getEventFromEventData(wSwap.getEventData());
            ReadTransactionalEvent r = (ReadTransactionalEvent)
                    database.getEventFromEventData(prev.getEventData());

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
        notifyStateAdvanced();
        msgListener = database.getDatabaseBacktrackMode()+" mode ended.";
        notifyStateProcessed();
    }

    protected Pair<Boolean, Integer> computeStepsEvent(){
        return computeStepsEvent(null);
    }


    protected Pair<Boolean, Integer> computeStepsEvent(TransactionalEvent e){
        if(isEndState()) return new Pair<>(false, null);;
        //TODO: erase sharingCG; adding locks.
        //if(trEventRegister.isChoiceGeneratorShared()) return new Pair<>(true, 0);
        boolean reset = true;
        //if(vm.getChoiceGenerator() != null && vm.getChoiceGenerator().isDone()) return new Pair<>(false, null);

        //we need to substitute this value during our mock tests, we will restore it before leaving.
        Integer n = 0, m = null;

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
                //This case should never appear, if it does, this program is wrong (non-if version)
                //Now it can happen as breakTransition always
                /*if(database.getDatabaseBacktrackMode() != GuideInfo.BacktrackTypes.RESTORE)
                    System.out.println("DEBUG: error?");

                 */

                n = currentAdvance;
                /*else{
                    reset = false;
                    n = 0;
                }*/
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


    protected boolean checkDatabaseConsistency(){
        return database.isConsistent();
    }


    @Override
    protected boolean forward() {

        //If it is guided, it has to follow every step, even if we can detect before reach the end that it is inconsistent

        if(!checkDatabaseConsistency()){
            if(database.isAssertionViolated()) {
                AssertTransactionalEvent a = (AssertTransactionalEvent) database.getLastEvent();
                msgListener = "Invalid branch: assertion violated. " + a;
            }
            else{
                msgListener = "Invalid branch: inconsistent database.";

            }
            return false;

        }

        if((isEndState() && !database.isTrulyConsistent())){
            if(database.isAssertionViolated()) {
                AssertTransactionalEvent a = (AssertTransactionalEvent) database.getLastEvent();
                msgListener = "Invalid branch: assertion violated. " + a;
            }
            else{
                msgListener = "Invalid branch: no truly consistent database.";
            }
            return false;

        }


        database.confirmMaximumConsistency();

        //trEventRegister.addInvokeVirtual();

        currentError = null;


        boolean ret = vm.forward();

        checkPropertyViolation();

        /*if(!ret) {
            trEventRegister.addReturn();
        }

         */

        return ret;
    }

    protected void backtrackDatabase(){
        database.backtrackDatabase();
    }

    @Override
    protected boolean backtrack() {
        Transition lastTransition = vm.getLastTransition();
        if(lastTransition != null && trEventRegister.isTransactionalTransition(lastTransition)) {

            backtrackDatabase();
            switch (database.getDatabaseBacktrackMode()){
                case READ:
                    trEventRegister.setChoiceGeneratorShared(true);
                    ReadTransactionalEvent r = (ReadTransactionalEvent) database.getLastEvent();
                    WriteTransactionalEvent nw = r.getWriteEvent();
                    WriteTransactionalEvent ow = database.getWriteEvent(nw.getVariable(), nw.getWriteIndex()+1);
                    msgListener = "Branch forked: change of write-read for event "+r +
                            "\n\t" + ow + " -> "+nw;
                    notifyStateProcessed();
                    //notifyStateAdvanced();
                    return true;
                case SWAP:
                case RESTORE:
                    GuideInfo guide = database.getGuideInfo();
                    if(guide.hasPath()){
                        LinkedList<Transaction> path = guide.getGuidedPath();
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
        //trEventRegister.addReturn();

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

            Pair<Boolean, Integer> p = computeStepsEvent();
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

            } else { // forward did not execute any instructions
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
                    if(database.getDatabaseBacktrackMode() == GuideInfo.BacktrackTypes.READ){
                        vm.getChoiceGenerator().reset();
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

    @Override
    protected void notifyStateAdvanced() {
        super.notifyStateAdvanced();
        msgListener = null;
    }

    @Override
    protected void notifyStateProcessed() {
        super.notifyStateProcessed();
        msgListener = null;
    }

    @Override
    protected void notifyStateBacktracked() {
        super.notifyStateBacktracked();
        msgListener = null;
    }

    @Override
    protected void notifySearchStarted() {
        super.notifySearchStarted();
        msgListener = null;
    }

    @Override
    protected void notifySearchFinished() {
        super.notifySearchFinished();
        msgListener = null;
    }

    public String getMessage() {
        return msgListener;
    }

}