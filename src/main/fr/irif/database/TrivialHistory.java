package fr.irif.database;

import gov.nasa.jpf.Config;

public class TrivialHistory extends COInductiveHistory {

    public TrivialHistory(Config config){
        super(config);
    }

    @Override
    protected boolean computeConsistency() {
        return true;
    }
}
