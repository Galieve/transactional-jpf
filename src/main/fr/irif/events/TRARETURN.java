package fr.irif.events;

import fr.irif.database.Database;
import gov.nasa.jpf.jvm.bytecode.ARETURN;
import gov.nasa.jpf.jvm.bytecode.JVMInstructionVisitor;
import gov.nasa.jpf.vm.*;

public class TRARETURN extends ARETURN {

    boolean transactionalRead = false;

    @Override
    protected void pushReturnValue(StackFrame frame) {
        if(!transactionalRead) {
            super.pushReturnValue(frame);
        }
    }

    @Override
    public Instruction execute(ThreadInfo ti) {

        TrEventRegister.getEventRegister().addCall(this.toString()+ this.getLineNumber());


        if(TrEventRegister.getEventRegister().isTransactionalReturn(ti.getTopFrame())){

            transactionalRead = true;

            Instruction i = super.execute(ti);

            transactionalRead = false;


            //0 = this, 1 = variable
            TransactionalEvent t = Database.getDatabase().getLastEvent();

            if(t.getType() == TransactionalEvent.Type.READ){
                String val = ((ReadTransactionalEvent) t).getWriteEvent().getValue();
                ElementInfo rei = ti.getHeap().newString(val, ti);
                ret = rei.getObjectRef();
                pushReturnValue(ti.getTopFrame());

                return i;
                //return
            }
            else{
                throw new IllegalStateException("Impossible case");
            }


        }
        else return super.execute(ti);
    }
}

//heap.newString(concatenatedString, ti);
//ti.getTopFrame().getMethodName().equals("readReturn")