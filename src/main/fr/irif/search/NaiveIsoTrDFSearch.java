package fr.irif.search;

import fr.irif.database.GuideInfo;
import fr.irif.database.NaiveIsoTrDatabase;
import fr.irif.events.ReadTransactionalEvent;
import fr.irif.events.TransactionalEvent;
import fr.irif.events.WriteTransactionalEvent;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.util.Pair;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Transition;
import gov.nasa.jpf.vm.VM;

import static fr.irif.database.GuideInfo.BacktrackTypes.NONE;


public class NaiveIsoTrDFSearch extends TrDFSearch {
    protected boolean naiveBacktracked;

    protected Integer threadChosen;

    protected Long timeout;


    public NaiveIsoTrDFSearch(Config config, VM vm) {
        super(config, vm);
        naiveBacktracked = false;
        threadChosen = null;
        timeout = config.hasValue("search.timeout") ?
                System.currentTimeMillis() + config.getLong("search.timeout")*1000
                : null;
    }


    protected boolean naiveBacktrack(int id){
        vm.backtrack();
        var cg = vm.getChoiceGenerator();
        int n = 0;
        for(var c: cg.getAllChoices()){
            if(c instanceof ThreadInfo){
                var t = (ThreadInfo) c;
                if(t.getId() == id && n+1 < cg.getTotalNumberOfChoices()){
                    msgListener = "Branch forked: naive branch ";
                    --depth;
                    notifyStateProcessed();
                    vm.getChoiceGenerator().reset();
                    vm.getChoiceGenerator().advance(n+1);
                    forward();
                    threadChosen = database.getLastEvent().getThreadId();
                    ++depth;
                    notifyStateAdvanced();
                    return true;
                }
            }
            ++n;
        }
        return false;
    }

    protected boolean continueBacktracking(){
        if(!backtrack()) return false;
        switch (database.getDatabaseBacktrackMode()){
            case READ:
                threadChosen = database.getLastEvent().getThreadId();

                //In this case we have swapped some variable, so we are exploring a new branch
                return false;
            case JPF:
                if(!naiveBacktracked){
                    threadChosen = null;
                }
                var ret = !naiveBacktracked;
                naiveBacktracked = false;
                return ret;
            case NONE:
                //Local instruction

                return true;
            default:
                return true;
        }

    }


    @Override
    protected boolean backtrack() {
        Transition lastTransition = vm.getLastTransition();
        if(lastTransition != null && trEventRegister.isTransactionalTransition(lastTransition)) {

            TransactionalEvent e = null;
            if(!database.isMockAccess()) {
                e = database.getLastEvent();
            }
            backtrackDatabase();
            switch (database.getDatabaseBacktrackMode()){
                case READ:
                    ReadTransactionalEvent r = (ReadTransactionalEvent) database.getLastEvent();
                    WriteTransactionalEvent nw = r.getWriteEvent();
                    WriteTransactionalEvent ow = database.getWriteEvent(nw.getVariable(), nw.getWriteIndex()+1);
                    msgListener = "Branch forked: change of write-read for event "+r +
                            "\n\t" + ow + " -> "+nw;
                    notifyStateProcessed();
                    return true;
                case SWAP:
                case RESTORE:
                    // There is no SWAP nor RESTORE in Naive Searches.
                    break;
                case JPF:
                    if(e!= null && e.getType() == TransactionalEvent.Type.BEGIN){
                        var id = e.getThreadId();
                        naiveBacktracked =  naiveBacktrack(id);
                        return true;
                    }
                    break;
                case NONE:
                    break;
            }
        }
        return vm.backtrack();
    }


    @Override
    protected Pair<Boolean, Integer> computeStepsEvent(TransactionalEvent e){
        if(isEndState()) return new Pair<>(false, null);;
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
            if(threadChosen != null && vm.getCurrentThread().getId() == threadChosen){
                m = n;
            }
            backtrack();
            ++n;
        }
        var numChoices = vm.getChoiceGenerator() == null ? 0 : vm.getChoiceGenerator().getTotalNumberOfChoices();


        //There is a local instruction, n has the correct value
        if(b) {
            backtrack();
        }
        else{
            if(threadChosen == null || numChoices <= 1) n = 0; //To avoid problems with BreakTransitionCG.
            else n = m;
        }
        trEventRegister.setFakeRead(false);
        database.setMockAccess(false);
        return new Pair<>(true, n);

    }

    protected boolean isTimeout(){
        return timeout != null && System.currentTimeMillis() >= timeout;
    }

    @Override
    public void search() {
        boolean depthLimitReached = false;

        depth = 0;

        notifySearchStarted();

        while (!done && !isTimeout()) {

            Pair<Boolean, Integer> p = computeStepsEvent();
            applyResetJumps(p);
            if (forward()) {
                depth++;
                notifyStateAdvanced();

                if(threadChosen != null){
                    //There is at least one event available.
                    var e = database.getLastEvent();
                    if(e.getType() == TransactionalEvent.Type.COMMIT || e.getType() == TransactionalEvent.Type.ABORT){
                        threadChosen = null;
                    }
                }

                if (currentError != null) {
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

                    while (continueBacktracking() && !isTimeout()) {
                        if (database.getDatabaseBacktrackMode() == GuideInfo.BacktrackTypes.JPF ||
                                database.getDatabaseBacktrackMode() == NONE) {
                            depth--;
                            notifyStateBacktracked();
                            //vm.getChoiceGenerator().setDone();
                        }
                        database.setDatabaseBacktrackMode(NONE);

                    }
                    if (database.getDatabaseBacktrackMode() == GuideInfo.BacktrackTypes.READ) {
                        vm.getChoiceGenerator().reset();
                    }


                    database.setDatabaseBacktrackMode(NONE);

                    //TODO: is this a hack?
                    if (depth == 0) {
                        terminate();
                    }


                }
            }
        }

        notifySearchFinished();
    }

}