package fr.irif.search;

import fr.irif.database.Database;
import fr.irif.database.GuideInfo;
import fr.irif.events.TrEventRegister;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.VM;

public class TrSystemState extends SystemState {



    public TrSystemState(Config config, TrSingleProcessVM trSingleProcessVM) {
        super(config, trSingleProcessVM);
    }

    @Override
    protected boolean advanceCurCg(VM vm) {
        TrEventRegister trEventRegister = TrEventRegister.getEventRegister();
        var database = Database.getDatabase();
        //|| trEventRegister.isChoiceGeneratorShared()
        if(database.getDatabaseBacktrackMode() == GuideInfo.BacktrackTypes.READ){
            return super.advanceCurCg(vm);
        }
        else{
            return super.advanceCurCg(vm);
        }
    }

    @Override
    public void executeNextTransition(VM vm) {
        //Database.getDatabase().setChoiceGeneratorShared(false);
        super.executeNextTransition(vm);
    }

    @Override
    protected void notifyChoiceGeneratorSet(VM vm, ChoiceGenerator<?> cg) {
        super.notifyChoiceGeneratorSet(vm,cg);
        /*if(!TrEventRegister.getEventRegister().isChoiceGeneratorShared())
            super.notifyChoiceGeneratorSet(vm, cg);

         */
    }

    @Override
    public void setId(int newId) {
        super.setId(newId);
        /*if(!TrEventRegister.getEventRegister().isChoiceGeneratorShared())
            super.setId(newId);

         */
    }
}
