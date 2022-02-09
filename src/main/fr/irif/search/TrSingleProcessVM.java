package fr.irif.search;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFListenerException;
import gov.nasa.jpf.vm.*;

public class TrSingleProcessVM extends SingleProcessVM {

    public TrSingleProcessVM(JPF jpf, Config conf) {

        super(jpf, conf);
    }

    @Override
    public void initFields(Config config) {
        path = new Path("fix-this!");
        out = null;

        ss = new TrSystemState(config, this);

        stateSet = config.getInstance("vm.storage.class", StateSet.class);
        if (stateSet != null) stateSet.attach(this);
        backtracker = config.getEssentialInstance("vm.backtracker.class", Backtracker.class);
        backtracker.attach(this);

        scheduler = config.getEssentialInstance("vm.scheduler.class", Scheduler.class);

        newStateId = -1;
    }


    @Override
    protected void notifyInstructionExecuted(ThreadInfo ti, Instruction insn, Instruction nextInsn) {
        try {
            //listener.instructionExecuted(this);
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].instructionExecuted(this, ti, nextInsn, insn);
            }
        } catch (UncaughtException x) {
            throw x;
        } catch (JPF.ExitException x) {
            throw x;
        } catch (Throwable t) {
            throw new JPFListenerException("exception during instructionExecuted() notification", t);
        }
    }

    /*
    public void setDisableNotification(boolean disableNotification) {
        this.disableNotification = disableNotification;
    }

     */


}

