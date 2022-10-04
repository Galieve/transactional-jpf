package fr.irif.histories;

import gov.nasa.jpf.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class SnapshotIsolationHistory extends PrefixHistory{

    public SnapshotIsolationHistory(Config config){
        super(config);
    }

    public SnapshotIsolationHistory(History h){
        super(h);
    }

    protected void addSIConflicts(HashMap<String, ArrayList<ArrayList<Integer>>> wr, ArrayList<HashMap<String, Integer>> wpt){
        for(int i = 0; i < writesPerTransaction.size(); ++i){
            for(int j = i+1; j < writesPerTransaction.size(); ++j){
                var seti = writesPerTransaction.get(i);
                var setj = writesPerTransaction.get(j);
                var inter = new HashSet<>(seti.keySet());
                inter.removeAll(setj.keySet());
                if(!inter.isEmpty()){
                    String s1 = forbiddenVariable+"-"+i+","+j, s2 = forbiddenVariable+"-"+j+","+i;
                    wpt.get(2*i).put(s1,1);
                    wpt.get(2*j+1).put(s1,1);
                    wpt.get(2*j).put(s2,1);
                    wpt.get(2*i+1).put(s2,1);
                    wr.put(s1, new ArrayList<>());
                    wr.put(s2, new ArrayList<>());
                    for(int k = 0; k < 2*numberTransactions; ++k){
                        wr.get(s1).add(new ArrayList<>(Collections.nCopies(2*numberTransactions,0)));
                        wr.get(s2).add(new ArrayList<>(Collections.nCopies(2*numberTransactions,0)));
                    }
                    wr.get(s1).get(2*i).set(2*i+1, 1);
                    wr.get(s2).get(2*j).set(2*j+1, 1);
                    //TODO FIX

                }
            }
        }
    }

    @Override
    protected SerializableHistory splitHistory() {

        var splitSO = computeSO_SER();
        var wrPerTransaction = computeWritesPerTransaction_SER();
        var wrMatrix = computeWR_SER();

        addSIConflicts(wrMatrix, wrPerTransaction);

        return new SerializableHistory(splitSO, wrMatrix, wrPerTransaction, forbiddenVariable);




    }
}
