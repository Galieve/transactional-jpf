package country.lab.histories;

import gov.nasa.jpf.Config;

public class CausalHistory extends COPolynomialHistory {

    public CausalHistory(Config config) {
        super(config);
    }

    public CausalHistory(History h){
        super(h);
    }

    @Override
    protected void computeInitializedCORelation() {

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
                        if(writeReadMatrix.get(var).get(i).get(k).size() > 0 &&
                                areWRSO_plusRelated(j,k)){
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