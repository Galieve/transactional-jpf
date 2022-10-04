package fr.irif.histories;

import gov.nasa.jpf.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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

    protected ArrayList<HashMap<String, Integer>> computeWritesPerTransaction_SER(){
        ArrayList<HashMap<String, Integer>> wrPerTransaction = new ArrayList<>();
        for(int i = 0; i < writesPerTransaction.size(); ++i){
            wrPerTransaction.add(new HashMap<>()); //reads (empty)
            wrPerTransaction.add(new HashMap<>()); //writes
            for(var p : writesPerTransaction.get(i).entrySet()){
                wrPerTransaction.get(wrPerTransaction.size()-1).put(p.getKey(), p.getValue());
            }
            //wrPerTransaction.set(2*i+1, writesPerTransaction.get(i));
        }
        return wrPerTransaction;
    }

    protected HashMap<String,ArrayList<ArrayList<Integer>>> computeWR_SER(){
        HashMap<String,ArrayList<ArrayList<Integer>>> wrMatrix = new HashMap<>();

        for(var p : writeReadMatrix.entrySet()){
            var k = p.getKey();
            var v = p.getValue();
            wrMatrix.put(k,  new ArrayList<>());
            //wrMatrix.put(k,  new ArrayList<>(Collections.nCopies(2*numberTransactions,
            //                        new ArrayList<>(Collections.nCopies(2*numberTransactions,0)))));
            for(int i = 0; i < v.size(); ++i){
                wrMatrix.get(k).add(new ArrayList<>(Collections.nCopies(2*numberTransactions,0)));
                wrMatrix.get(k).add(new ArrayList<>(Collections.nCopies(2*numberTransactions,0)));
                for(int j = 0; j < v.get(i).size(); ++j){

                    if(i != j) {
                        wrMatrix.get(k).get(2 * i + 1).set(2 * j, v.get(i).get(j));
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
