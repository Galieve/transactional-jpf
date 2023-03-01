package country.lab.search;
import country.lab.events.TrEventRegister;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFListenerException;
import gov.nasa.jpf.vm.*;

public class TrSingleProcessVM extends SingleProcessVM {

    public TrSingleProcessVM(JPF jpf, Config conf) {

        super(jpf, conf);
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

    @Override
    public void print(String s) {
        if(!TrEventRegister.getEventRegister().isFakeRead())
            super.print(s);
    }

    @Override
    public void println(String s) {
        if(!TrEventRegister.getEventRegister().isFakeRead())
            super.println(s);
    }

    @Override
    public void print(boolean b) {
        if(!TrEventRegister.getEventRegister().isFakeRead())
            super.print(b);
    }

    @Override
    public void print(char c) {
        if(!TrEventRegister.getEventRegister().isFakeRead())
            super.print(c);
    }

    @Override
    public void print(int i) {
        if(!TrEventRegister.getEventRegister().isFakeRead())
            super.print(i);
    }

    @Override
    public void print(long l) {
        if(!TrEventRegister.getEventRegister().isFakeRead())
            super.print(l);
    }

    @Override
    public void print(double d) {
        if(!TrEventRegister.getEventRegister().isFakeRead())
            super.print(d);
    }

    @Override
    public void print(float f) {
        if(!TrEventRegister.getEventRegister().isFakeRead())
            super.print(f);
    }

    @Override
    public void println() {
        if(!TrEventRegister.getEventRegister().isFakeRead())
            super.println();
    }
}

