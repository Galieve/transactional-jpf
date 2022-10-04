package fr.irif.search;

import fr.irif.database.Database;
import fr.irif.database.GuideInfo;
import fr.irif.events.*;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.search.DFSearch;
import gov.nasa.jpf.util.Pair;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Transition;
import gov.nasa.jpf.vm.VM;

import java.util.LinkedList;

import static fr.irif.database.GuideInfo.BacktrackTypes.NONE;


public class NaiveTrDFSearch extends TrDFSearch {
    protected boolean naiveBacktracked;

    public NaiveTrDFSearch(Config config, VM vm) {
        super(config, vm);
        naiveBacktracked = false;
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
                //In this case we have swapped some variable, so we are exploring a new branch
                return false;
            case JPF:
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
                    // There is no SWAP nor RESTORE in Naive Searches.
                    break;
                case MOCK:
                    break;
                case JPF:
                    var id = lastTransition.getThreadInfo().getId();
                    naiveBacktracked =  naiveBacktrack(id);
                    return true;

                case NONE:
                    break;
            }
        }
        return vm.backtrack();
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

                    while (continueBacktracking()) {
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