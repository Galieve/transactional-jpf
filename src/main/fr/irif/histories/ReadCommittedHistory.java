package fr.irif.histories;

import gov.nasa.jpf.Config;

public class ReadCommittedHistory extends COPolynomialHistory{

    public ReadCommittedHistory(Config config){
        super(config);
    }
    @Override
    protected void computeInitializedCORelation() {
        //throw new IllegalCallerException("function not yet implemented");
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
                        var iTok = writeReadMatrix.get(var).get(i).get(k);
                        var jTok = writeReadMatrix.get(var).get(j).get(k);
                        if(iTok.size() > 0 && jTok.size() > 0 &&
                                jTok.get(0) < iTok.get(iTok.size() -1)){
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
