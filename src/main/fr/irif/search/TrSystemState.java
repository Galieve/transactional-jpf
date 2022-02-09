package fr.irif.search;

import fr.irif.database.Database;
import fr.irif.database.GuideInfo;
import fr.irif.events.TrEventRegister;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.VM;

public class TrSystemState extends SystemState {

    public TrSystemState(Config config, TrSingleProcessVM trSingleProcessVM) {
        super(config, trSingleProcessVM);
    }

    @Override
    protected boolean advanceCurCg(VM vm) {
        TrEventRegister trEventRegister = TrEventRegister.getEventRegister();
        Database database = Database.getDatabase();
        if(database.getDatabaseBacktrackMode() == GuideInfo.BacktrackTypes.READ||
                trEventRegister.isChoiceGeneratorShared()){

            trEventRegister.setChoiceGeneratorShared(false);
            this.setForced(true);
            return true;
        }
        else{
            /*for(int i = 0; i < database.getJumpsCG() - 1; ++i){
                super.advanceCurCg(vm);
            }
            database.setJumpsCG(null);
            */return super.advanceCurCg(vm);
        }
    }

    @Override
    public void executeNextTransition(VM vm) {
        //Database.getDatabase().setChoiceGeneratorShared(false);
        super.executeNextTransition(vm);
    }

    /*
    @Override
    protected void advance(VM vm, ChoiceGenerator<?> cg) {
        Database.getDatabase();
        if(Database.getDatabase().isDatabaseBacktrackMode()){

        }
        else{
            super.advance(vm, cg);
        }
    }
    */
}
