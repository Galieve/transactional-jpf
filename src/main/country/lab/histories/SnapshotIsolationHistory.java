package country.lab.histories;

import com.rits.cloning.Cloner;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.util.Pair;

import java.util.*;

public class SnapshotIsolationHistory extends PrefixHistory{

    public SnapshotIsolationHistory(Config config){
        super(config);
    }

    public SnapshotIsolationHistory(History h){
        super(h);
    }

    protected void addSIConflicts(HashMap<String,
            ArrayList<ArrayList<Pair<ArrayList<Integer>, HashSet<Integer>>>>> wr,
                                  ArrayList<HashMap<String, ArrayList<Integer>>> wpt){

        Cloner c = new Cloner();
        ArrayList<Pair<ArrayList<Integer>, HashSet<Integer>>> row = new ArrayList<>();
        for(int i = 0; i < 2*numberTransactions; ++i){
            row.add(new Pair<>(new ArrayList<>(), new HashSet<>()));
        }

        for(int i = 0; i < writesPerTransaction.size(); ++i){
            for(int j = i+1; j < writesPerTransaction.size(); ++j){
                var seti = writesPerTransaction.get(i);
                var setj = writesPerTransaction.get(j);
                var inter = new HashSet<>(seti.keySet());
                inter.removeAll(setj.keySet());
                if(!inter.isEmpty()){
                    String s1 = forbiddenVariable+"-"+i+","+j, s2 = forbiddenVariable+"-"+j+","+i;
                    wpt.get(2*i).put(s1, new ArrayList<>(List.of(1)));
                    wpt.get(2*j+1).put(s1,new ArrayList<>(List.of(1)));
                    wpt.get(2*j).put(s2,new ArrayList<>(List.of(1)));
                    wpt.get(2*i+1).put(s2,new ArrayList<>(List.of(1)));
                    wr.put(s1, new ArrayList<>());
                    wr.put(s2, new ArrayList<>());
                    for(int k = 0; k < 2*numberTransactions; ++k){
                        wr.get(s1).add(c.deepClone(row));
                        wr.get(s2).add(c.deepClone(row));
                    }
                    wr.get(s1).get(2*i).get(2*i+1)._1.add(0);
                    wr.get(s2).get(2*j).get(2*j+1)._1.add(1);
                    wr.get(s1).get(2*i).get(2*i+1)._2.add(0);
                    wr.get(s2).get(2*j).get(2*j+1)._2.add(1);
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
        var committed = computeCommitted();

        addSIConflicts(wrMatrix, wrPerTransaction);

        return new SerializableHistory(splitSO, wrMatrix,
                wrPerTransaction, committed, forbiddenVariable);




    }
}
