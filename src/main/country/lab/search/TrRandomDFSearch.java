package country.lab.search;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.vm.VM;

import java.util.Random;

public class TrRandomDFSearch extends TrDFSearch{

    protected Random r = new Random();

    public TrRandomDFSearch(Config config, VM vm) {
        super(config, vm);

    }

    @Override
    protected boolean continueBacktracking() {
        boolean backRandom;
        if(!backtrack()) return false;
        switch (database.getDatabaseBacktrackMode()){
            case READ:
                //In this case we have swapped some variable, so we are exploring a new branch
                backRandom= r.nextBoolean();
                if(backRandom){
                    msgListener = "Branch skipped: discarded read";
                    notifyStateProcessed();
                }
                return backRandom;
            case JPF:
            case NONE:
                //Local instruction

                return true;
            case SWAP:
                backRandom = r.nextBoolean();
                if(backRandom){
                    msgListener = "Branch skipped: discarded swap";
                    notifyStateProcessed();
                }
                return backRandom;
            case RESTORE:
                return true;
            default:
                return true;
        }
    }
}
