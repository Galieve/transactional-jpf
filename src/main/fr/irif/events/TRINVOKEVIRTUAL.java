package fr.irif.events;

import gov.nasa.jpf.jvm.bytecode.INVOKEVIRTUAL;
import gov.nasa.jpf.vm.*;

public class TRINVOKEVIRTUAL extends INVOKEVIRTUAL {

    TrEventRegister trEventRegister;

    protected TRINVOKEVIRTUAL(String clsDescriptor, String methodName, String signature) {
        super(clsDescriptor, methodName, signature);
        trEventRegister = TrEventRegister.getEventRegister();
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        if(trEventRegister.isTransactionalStatement(this)){

            StackFrame frame = ti.getTopFrame();
            LocalVarInfo [] localVarInfos = frame.getLocalVars();
            //IRIF: the first is this, no a proper argument
            for(int i = 1; i < localVarInfos.length; ++i){
                DynamicElementInfo dei = (DynamicElementInfo) frame.getLocalValueObject(localVarInfos[i]);
                trEventRegister.addArgument(new String(dei.getStringBytes()));
            }
            trEventRegister.registerEvent(this, frame.getCallerFrame().getPC(), ti);
            trEventRegister.setLastInstructionTransactional(true);
            //System.out.println("GTS:"+database.getTransactionalStatement(this));

        }

        return super.execute(ti);
    }
}
