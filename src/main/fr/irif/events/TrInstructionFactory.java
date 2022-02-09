package fr.irif.events;

import gov.nasa.jpf.jvm.bytecode.InstructionFactory;
import gov.nasa.jpf.vm.Instruction;

public class TrInstructionFactory extends InstructionFactory {

    public TrInstructionFactory() {
        super();
    }

    @Override
    public Instruction invokevirtual(String clsName, String methodName, String methodSignature) {
        return new TRINVOKEVIRTUAL(clsName, methodName, methodSignature);
    }


    @Override
    public Instruction areturn() {
        return new TRARETURN();
    }

    /*
    @Override
    public Instruction invokestatic(String clsName, String methodName, String methodSignature) {
        return new TRINVOKESTATIC(clsName, methodName, methodSignature);
    }
     */

}
