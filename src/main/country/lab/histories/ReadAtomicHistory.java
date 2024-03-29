package country.lab.histories;

import gov.nasa.jpf.Config;

public class ReadAtomicHistory extends COPolynomialHistory{

    public ReadAtomicHistory(Config config){
        super(config);
    }

    public ReadAtomicHistory(History h){
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
                        //If we have found some t3 (or it is [WR u SO] adj) that satisfies the formula,
                        // we can move on to the next possible edge.
                        if(writeReadMatrix.get(var).get(i).get(k)._1.size()> 0 &&
                                areWRSORelated(j,k)){
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
