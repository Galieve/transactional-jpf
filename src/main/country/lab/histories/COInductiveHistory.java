package country.lab.histories;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class COInductiveHistory extends History {


    public COInductiveHistory(ArrayList<ArrayList<Boolean>> splitSO,
                              HashMap<String,ArrayList<ArrayList<Pair<ArrayList<Integer>, HashSet<Integer>>>>> wrMatrix,
                              ArrayList<HashMap<String, ArrayList<Integer>>> wrPerTransaction,
                              ArrayList<Boolean> committed, String forbidden) {
        super(splitSO, wrMatrix, wrPerTransaction, committed, forbidden);
    }

    public COInductiveHistory(Config config) {
        super(config);
    }

    public COInductiveHistory(History h) {
        super(h);
    }
}
