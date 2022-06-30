package fr.irif.database;

import gov.nasa.jpf.Config;

import java.util.ArrayList;
import java.util.Collections;

public abstract class COPolynomialHistory extends History {

    protected ArrayList<ArrayList<Boolean>> commitOrderMatrix;

    protected COPolynomialHistory(Config config){
        super(config);
    }

    protected boolean dfsCOAcyclic(ArrayList<Integer> color, Integer u){
        color.set(u,1);
        for(int v = 0; v < commitOrderMatrix.get(u).size(); ++v){
            if(u == v) continue;
            if(commitOrderMatrix.get(u).get(v)
                    && color.get(v) == 0){
                boolean b = dfsCOAcyclic(color, v);
                if(!b) return false;

            }
            else if(commitOrderMatrix.get(u).get(v)
                    && color.get(v) == 1){
                return false;
            }
        }
        color.set(u,2);
        return true;
    }

    protected boolean isCommitOrderAcyclic(){
        //0 = not visited, 1 = in process, 2 = finished
        ArrayList<Integer> color = new ArrayList<>(Collections.nCopies(numberTransactions,0));
        for(int i = 0; i < numberTransactions; ++i){
            if(color.get(i) == 0){
                if(!dfsCOAcyclic(color, i)){
                    return false;
                }
            }
        }
        return true;
    }

    protected abstract void computeInitializedCORelation();

    protected abstract ArrayList<ArrayList<Boolean>> initializeCORelation();

    protected void generateCommitOrderMatrix(){
        var co = initializeCORelation();
        /*Cloner cloner = new Cloner();
        commitOrderMatrix = cloner.deepClone(co);

         */
        commitOrderMatrix = Utility.deepCopyMatrix(co);

        computeInitializedCORelation();

    }

    @Override
    protected boolean computeConsistency() {
        if(commitOrderMatrix == null){
            generateCommitOrderMatrix();
        }
        return isCommitOrderAcyclic();
    }

    @Override
    protected void restoreSemanticFlags() {
        super.restoreSemanticFlags();
        commitOrderMatrix = null;
    }

    /*
    if (commitOrderMatrix == null) {
        generateCommitOrderMatrix();
    }
    consistent = isCommitOrderAcyclic();

     */

}
