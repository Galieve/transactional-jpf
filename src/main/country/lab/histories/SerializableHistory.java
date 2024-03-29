package country.lab.histories;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SerializableHistory extends COInductiveHistory {

    protected HashSet<HashSet<Integer>> prefixes;

    public SerializableHistory(Config config) {
        super(config);
    }

    public SerializableHistory(History h){
        super(h);
    }

    public SerializableHistory(ArrayList<ArrayList<Boolean>> splitSO,
                               HashMap<String,ArrayList<ArrayList<Pair<ArrayList<Integer>, HashSet<Integer>>>>> wrMatrix,
                               ArrayList<HashMap<String, ArrayList<Integer>>> wrPerTransaction,
                               ArrayList<Boolean> committed,
                               String forbidden) {
        super(splitSO, wrMatrix, wrPerTransaction, committed, forbidden);
    }

    //idxSet contains the indexes' complementary extended
    protected boolean extend(HashSet<Integer> idxSet, Integer i){
        for(Integer j: idxSet){
            if(Objects.equals(i, j)) continue;
            if(areWR(j, i)){
                return false;
            }
        }
        for(String var: writesPerTransaction.get(i).keySet()){
            //j == t1
            for(int j = 0; j < numberTransactions; ++j){
                if(idxSet.contains(j)) continue;
                for(int k : idxSet){
                    if(k == i) continue;
                    if(writeReadMatrix.get(var).get(j).get(k)._1.size() > 0){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //idxSet = T \setminus T' (checkSER algorithm)
    protected boolean checkSER(HashSet<Integer> idxSet){
        if(idxSet.isEmpty()) return true;
        for(Integer i:idxSet){
            boolean admissible = true;
            for(Integer j: idxSet){
                if(areWRSORelated(j, i)){
                    admissible = false;
                    break;
                }
            }
            if(admissible){
                if(!extend(idxSet, i)) continue;
                HashSet<Integer> cloneIdx = new HashSet<>(idxSet);
                cloneIdx.remove(i);
                if(!prefixes.contains(cloneIdx) && checkSER(cloneIdx)){
                    return true;
                }
                prefixes.add(cloneIdx);
                //TODO: implement seen
            }
        }
        return false;
    }

    @Override
    protected boolean computeConsistency() {
        HashSet<Integer> idxSet = IntStream.range(0, numberTransactions).boxed()
                .collect(Collectors.toCollection(HashSet::new));
        prefixes = new HashSet<>();
        return checkSER(idxSet);
    }

    @Override
    protected void restoreSemanticFlags() {
        super.restoreSemanticFlags();
        prefixes = null;
    }
}

