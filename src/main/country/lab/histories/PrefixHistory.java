package country.lab.histories;

import com.rits.cloning.Cloner;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class PrefixHistory extends SerializableHistory {

    public PrefixHistory(Config config){
        super(config);
    }

    public PrefixHistory(History h) {
        super(h);
    }

    protected ArrayList<ArrayList<Boolean>> computeSO_SER(){
        ArrayList<ArrayList<Boolean>> splitSO = new ArrayList<>();
        for(int i = 0; i < sessionOrderMatrix.size(); ++i){
            splitSO.add( new ArrayList<>(Collections.nCopies(2*numberTransactions,false))); //reads
            splitSO.add( new ArrayList<>(Collections.nCopies(2*numberTransactions,false))); //writes
            splitSO.get(2*i).set(2*i+1, true);
            splitSO.get(2*i+1).set(2*i+1, true);
            splitSO.get(2*i).set(2*i, true);

            for(int j = 0; j < sessionOrderMatrix.get(i).size(); ++j){
                if(sessionOrderMatrix.get(i).get(j)){
                    splitSO.get(2*i).set(2*j, true);
                    splitSO.get(2*i).set(2*j+1, true);
                    splitSO.get(2*i+1).set(2*j, true);
                    splitSO.get(2*i+1).set(2*j+1, true);
                }
            }
        }

        computeTransitiveClosure(splitSO);

        return splitSO;
    }

    protected ArrayList<HashMap<String, ArrayList<Integer>>> computeWritesPerTransaction_SER(){
        ArrayList<HashMap<String, ArrayList<Integer>>> wrPerTransaction = new ArrayList<>();
        var cloner = new Cloner();
        for(int i = 0; i < writesPerTransaction.size(); ++i){
            wrPerTransaction.add(new HashMap<>()); //reads (empty)
            wrPerTransaction.add(new HashMap<>()); //writes
            for(var p : writesPerTransaction.get(i).entrySet()){
                wrPerTransaction.get(wrPerTransaction.size()-1).put(p.getKey(), cloner.deepClone(p.getValue()));
            }
            //wrPerTransaction.set(2*i+1, writesPerTransaction.get(i));
        }
        return wrPerTransaction;
    }

    protected HashMap<String,ArrayList<ArrayList<Pair<ArrayList<Integer>, HashSet<Integer>>>>> computeWR_SER(){
        HashMap<String,ArrayList<ArrayList<Pair<ArrayList<Integer>, HashSet<Integer>>>>> wrMatrix = new HashMap<>();
        var c = new Cloner();
        for(var p : writeReadMatrix.entrySet()){
            var k = p.getKey();
            var v = p.getValue();
            wrMatrix.put(k,  new ArrayList<>());

            var row = new ArrayList<Pair<ArrayList<Integer>, HashSet<Integer>>>();
            for(int i = 0; i < 2*numberTransactions; ++i){
                row.add(new Pair<>(new ArrayList<>(), new HashSet<>()));
            }
            for(int i = 0; i < v.size(); ++i){
                wrMatrix.get(k).add(c.deepClone(row));
                wrMatrix.get(k).add(c.deepClone(row));
                for(int j = 0; j < v.get(i).size(); ++j){

                    if(i != j) {
                        wrMatrix.get(k).get(2 * i + 1).set(2 * j, c.deepClone(v.get(i).get(j)));
                    }
                    //What happens if a transaction reads from itself?
                }
            }
        }
        return wrMatrix;
    }

    protected SerializableHistory splitHistory(){
        var splitSO = computeSO_SER();
        var wrPerTransaction = computeWritesPerTransaction_SER();
        var wrMatrix = computeWR_SER();

        return new SerializableHistory(splitSO, wrMatrix, wrPerTransaction, forbiddenVariable);
    }

    @Override
    protected boolean computeConsistency() {
        SerializableHistory h = splitHistory();
        return h.computeConsistency();
    }

}
