package fr.irif.histories;

import gov.nasa.jpf.Config;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class COInductiveHistory extends History {


    public COInductiveHistory(ArrayList<ArrayList<Boolean>> splitSO, HashMap<String, ArrayList<ArrayList<ArrayList<Integer>>>> wrMatrix,
                              ArrayList<HashMap<String, Integer>> wrPerTransaction, String forbidden) {
        super(splitSO, wrMatrix, wrPerTransaction, forbidden);
    }

    public COInductiveHistory(Config config) {
        super(config);
    }

    public COInductiveHistory(History h) {
        super(h);
    }
}
