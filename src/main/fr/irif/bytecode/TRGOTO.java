package fr.irif.events;

import gov.nasa.jpf.jvm.bytecode.GOTO;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

public class TRGOTO extends GOTO {


    public TRGOTO(int targetPosition) {
        super(targetPosition);
    }



    @Override
    public Instruction execute(ThreadInfo ti) {
        if(TrEventRegister.getEventRegister().isTransactionalBreakTransition(ti.getTopFrame())){
            if(isBackJump() && ti.breakTransition("Transactional break")){
                return this;
            }
        }
        return super.execute(ti);
    }
}
