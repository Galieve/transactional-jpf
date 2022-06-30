package fr.irif.bytecode;

import gov.nasa.jpf.jvm.bytecode.JVMInstructionVisitor;

public class TRGOTO_W extends TRGOTO{


    public TRGOTO_W(int targetPos){
        super(targetPos);
    }

    @Override
    public int getLength() {
        return 5; // opcode, bb1, bb2, bb3, bb4
    }

    @Override
    public int getByteCode() {
        return 0xc8;
    }

    @Override
    public void accept(JVMInstructionVisitor insVisitor) {
        insVisitor.visit(this);
    }
}
