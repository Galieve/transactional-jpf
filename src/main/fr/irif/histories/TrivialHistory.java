package fr.irif.histories;

import gov.nasa.jpf.Config;

public class TrivialHistory extends COInductiveHistory {

    public TrivialHistory(Config config){
        super(config);
    }

    public TrivialHistory(History h){
        super(h);
    }

    @Override
    protected boolean computeConsistency() {
        return true;
    }
}
