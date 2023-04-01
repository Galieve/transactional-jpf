package country.lab.histories;

import gov.nasa.jpf.Config;

import java.util.ArrayList;
import java.util.HashMap;

public class ReadCommittedHistory extends COPolynomialHistory{

    public ReadCommittedHistory(History h){ super(h); }

    public ReadCommittedHistory(Config config){
        super(config);
    }
    @Override
    protected void computeInitializedCORelation() {
        //throw new IllegalCallerException("function not yet implemented");

        ArrayList<HashMap<Integer, Integer>> minTo = new ArrayList<>();
        for(int j = 0; j < numberTransactions; ++j){
            HashMap<Integer, Integer> minJToK = new HashMap<>();
            for(int k = 0; k < numberTransactions; ++k){
                minJToK.putIfAbsent(k, Integer.MAX_VALUE);
                for(String var: writesPerTransaction.get(j).keySet()){
                    var jTok = writeReadMatrix.get(var).get(j).get(k)._1;
                    if(jTok.size() > 0){
                        minJToK.put(k, Math.min(minJToK.get(k), jTok.get(0)));
                    }
                }
            }
            minTo.add(minJToK);
        }

        for(int i = 0; i < numberTransactions; ++i){
            for(int j = 0; j < numberTransactions; ++j){
                if(i == j) continue;
                if(commitOrderMatrix.get(j).get(i)) continue;

                //t2 writes var
                for(String var : writesPerTransaction.get(j).keySet()){

                    if(commitOrderMatrix.get(j).get(i)) break;

                    for(int k = 0; k < numberTransactions; ++k){
                        //If we have found some t3 (or it is [WR u SO]+ adj) that satisfies the formula,
                        // we can move on to the next possible edge.
                        var iTok = writeReadMatrix.get(var).get(i).get(k)._1;
                        var minJToK = minTo.get(j);
                        if(iTok.size() > 0 &&
                                minJToK.get(k) < iTok.get(iTok.size() -1)){
                            commitOrderMatrix.get(j).set(i, true);
                            //If we add CO-edge for one variable, other variables
                            // won't add anything else meaningful
                            break;
                        }
                    }

                }
            }
        }


    }

}
