package fr.irif.search;

import fr.irif.database.Database;
import fr.irif.events.ReadTransactionalEvent;
import fr.irif.events.TrEventRegister;
import fr.irif.events.TransactionalEvent;
import fr.irif.events.WriteTransactionalEvent;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.listener.ExecTracker;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class TransactionalExecTracker extends ExecTracker {

    protected Database database;

    protected TrEventRegister trEventRegister;

    protected PrintWriter out;

    public TransactionalExecTracker(Config config) {
        super(config);

        database = Database.getDatabase(config);
        trEventRegister = TrEventRegister.getEventRegister();
        //config.getEssentialInstance("out.database_model.class", DatabaseRelations.class);
        String path = config.getString("listener.out", null);
        if (path == null) {
            out = new PrintWriter(System.out, true);
        } else {
            File file = new File(path);
            //File file = config.getPath(path);
            try {
                out = new PrintWriter(file);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("File not found");
            }
        }


        //out = new PrintWriter(System.out, true);


    }

    @Override
    public void gcEnd(VM vm) {
        //super.gcEnd(vm);
    }

    @Override
    public void choiceGeneratorAdvanced(VM vm, ChoiceGenerator<?> currentCG) {
        //super.choiceGeneratorAdvanced(vm, currentCG);
    }

    @Override
    public void threadStarted(VM vm, ThreadInfo ti) {
        //super.threadStarted(vm, ti);
    }

    @Override
    public void threadTerminated(VM vm, ThreadInfo ti) {
        //super.threadTerminated(vm, ti);
    }

    @Override
    public void objectExposed(VM vm, ThreadInfo currentThread, ElementInfo fieldOwnerObject, ElementInfo exposedObject) {
        //super.objectExposed(vm, currentThread, fieldOwnerObject, exposedObject);
    }

    @Override
    public void objectShared(VM vm, ThreadInfo currentThread, ElementInfo sharedObject) {
        //super.objectShared(vm, currentThread, sharedObject);
    }

    @Override
    public void stateProcessed(Search search) {
        if(search instanceof TrDFSearch){
            TrDFSearch trDFSearch = (TrDFSearch) search;
            String msg = trDFSearch.getAndClearMessage();
            if(msg != null)
                out.println(msg);
        }
        //super.stateProcessed(search);
        int id = search.getStateId();
        out.println("----------------------------------- [" +
                search.getDepth() + "] done: " + id);
        if(search.isEndState()){
            out.println("Branch ended.");
        }
    }

    @Override
    public void stateAdvanced(Search search) {
        VM vm = search.getVM();
        if(trEventRegister.isTransactionalTransition(vm.getCurrentTransition()) && !database.isMockAccess()){
            TransactionalEvent t = database.getLastEvent();
            out.println(t+ " executed.");
            if(t.getType() == TransactionalEvent.Type.READ){
                if(!TrEventRegister.getEventRegister().isFakeRead()) {
                    WriteTransactionalEvent w = ((ReadTransactionalEvent) t).getWriteEvent();
                    out.println("\t reading from " + t.getVariable() + " = " + w.getValue()+" ("+ w.getBaseName()+").");
                }
            }
        }
        //super.stateAdvanced(search);
        int id = search.getStateId();

        out.print("----------------------------------- [" +
                search.getDepth() + "] forward: " + id);
        if (search.isNewState()) {
            out.print(" new");
        } else {
            out.print(" visited");
        }

        if (search.isEndState()) {
            out.print(" end");
        }

        out.println();
    }

    @Override
    public void searchFinished(Search search) {
        //super.searchFinished(search);
        out.println("----------------------------------- search finished");
    }

    @Override
    public void searchStarted(Search search) {
        //super.searchStarted(search);
        out.println("----------------------------------- search started");
        /*if (skipInit) {
            ThreadInfo tiCurrent = ThreadInfo.getCurrentThread();
            miMain = tiCurrent.getEntryMethod();

            out.println("      [skipping static init instructions]");
        }
         */
    }

    @Override
    public void stateBacktracked(Search search) {
        //super.stateBacktracked(search);

        int id = search.getStateId();
        out.println("----------------------------------- [" +
                search.getDepth() + "] backtrack: " + id);
    }
}
