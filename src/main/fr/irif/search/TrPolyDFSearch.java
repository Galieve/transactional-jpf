package fr.irif.search;

import fr.irif.database.Database;
import fr.irif.database.GuideInfo;
import fr.irif.events.ReadTransactionalEvent;
import fr.irif.events.Transaction;
import fr.irif.events.TransactionalEvent;
import fr.irif.events.WriteTransactionalEvent;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.util.Pair;
import gov.nasa.jpf.vm.RestorableVMState;
import gov.nasa.jpf.vm.Transition;
import gov.nasa.jpf.vm.VM;

import java.util.LinkedList;
import java.util.Stack;

public class TrPolyDFSearch extends TrDFSearch {

    protected Stack<Pair<Pair<RestorableVMState, Integer>, Database>> previousStates;

    public TrPolyDFSearch(Config config, VM vm) {
        super(config, vm);
        previousStates = new Stack<>();
    }

    @Override
    protected boolean backtrack() {
        Transition lastTransition = vm.getLastTransition();
        if (lastTransition != null && trEventRegister.isTransactionalTransition(lastTransition)) {

            backtrackDatabase();
            switch (database.getDatabaseBacktrackMode()) {
                case READ:
                    trEventRegister.setChoiceGeneratorShared(true);
                    ReadTransactionalEvent r = (ReadTransactionalEvent) database.getLastEvent();
                    WriteTransactionalEvent nw = r.getWriteEvent();
                    WriteTransactionalEvent ow = database.getWriteEvent(nw.getVariable(), nw.getWriteIndex() + 1);
                    msgListener = "Branch forked: change of write-read for event " + r +
                            "\n\t" + ow + " -> " + nw;
                    notifyStateProcessed();
                    //notifyStateAdvanced();
                    return true;
                case SWAP:

                    GuideInfo guide = database.getGuideInfo();
                    if (guide.hasPath()) {

                        previousStates.push(new Pair<>(new Pair<>(vm.getRestorableState(), depth),database.cloneDatabase()));
                        LinkedList<Transaction> path = guide.getGuidedPath();
                        TransactionalEvent endEvent = guide.getEndEvent();
                        WriteTransactionalEvent wSwap = guide.getWriteEventSwap();
                        database.resetGuidedInfo();
                        backtrackWithPath(path, endEvent, wSwap);
                        return true;
                    }
                    else break;
                case RESTORE:

                    var par = previousStates.pop();
                    database = par._2;
                    database.setDatabaseInstance(database);
                    vm.restoreState(par._1._1);

                    depth = par._1._2;
                    msgListener = "Restoring state...\n"+
                    "----------------------------------- ["+   depth + "] restore: " + getStateId();

                    notifyStateProcessed();
                    //add notify
                    return true;

                case JPF:
                case NONE:
                    break;
            }
        }

        return vm.backtrack();


    }
}