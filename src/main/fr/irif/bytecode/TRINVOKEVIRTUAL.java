package fr.irif.bytecode;

import fr.irif.events.TrEventRegister;
import gov.nasa.jpf.jvm.bytecode.INVOKEVIRTUAL;
import gov.nasa.jpf.vm.*;

public class TRINVOKEVIRTUAL extends INVOKEVIRTUAL {

    TrEventRegister trEventRegister;

    protected TRINVOKEVIRTUAL(String clsDescriptor, String methodName, String signature) {
        super(clsDescriptor, methodName, signature);
        trEventRegister = TrEventRegister.getEventRegister();
    }

    protected String translateVarInfo(StackFrame frame, LocalVarInfo lvi){
        Object o = frame.getLocalValueObject(lvi);
        switch(lvi.getSignature()){
            case "Z":
            case "B":
            case "C":
            case "S":
            case "I":
            case "J":
            case "F":
            case "D":
                return String.valueOf(o);
            default:
                return new String(((DynamicElementInfo)o).getStringBytes());
        }
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        if(trEventRegister.isTransactionalStatement(this)){

            StackFrame frame = ti.getTopFrame();
            LocalVarInfo [] localVarInfos = frame.getLocalVars();
            //The first is this, no a proper argument
            for(int i = 1; i < localVarInfos.length; ++i){
                trEventRegister.addArgument(translateVarInfo(frame, localVarInfos[i]));
            }
            trEventRegister.registerEvent(this, frame.getCallerFrame().getPC(), ti);
            trEventRegister.setLastInstructionTransactional(true);


        }
        return super.execute(ti);
    }
}
