package fr.irif.database;

import com.rits.cloning.Cloner;
import gov.nasa.jpf.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public abstract class History {
    protected ArrayList<ArrayList<Boolean>> sessionOrderMatrix;

    protected HashMap<String,ArrayList<ArrayList<Integer>>> writeReadMatrix;

    protected ArrayList<HashMap<String, Integer>> writesPerTransaction;

    protected ArrayList<ArrayList<Boolean>> transitiveClosure;

    protected int numberTransactions;

    protected Boolean consistent;

    protected String forbiddenVariable;

    protected History(Config config){
        this(new ArrayList<>(), new HashMap<>(), new ArrayList<>(), config.getString("db.database_model.forbidden_variable", "FORBIDDEN"));
    }

    protected History(ArrayList<ArrayList<Boolean>> soMatrix, HashMap<String,ArrayList<ArrayList<Integer>>> wrMatrix,
                      ArrayList<HashMap<String, Integer>> wrPerTransaction, String forbidden){
        sessionOrderMatrix = soMatrix;
        writeReadMatrix = wrMatrix;
        writesPerTransaction = wrPerTransaction;
        numberTransactions = soMatrix.size();
        consistent = null;
        transitiveClosure = null;
        forbiddenVariable = forbidden;
    }

    protected History(History h){
        this(h.sessionOrderMatrix, h.writeReadMatrix, h.writesPerTransaction, h.forbiddenVariable);
    }

    public void addTransaction(int transId, int threadId, ArrayList<Integer> sessionOrder){

        sessionOrderMatrix.add(new ArrayList<>(Collections.nCopies(numberTransactions,false)));
        writesPerTransaction.add(new HashMap<>());
        restoreSemanticFlags();


        ++numberTransactions;
        for(ArrayList<ArrayList<Integer>> wrx : writeReadMatrix.values()){
            wrx.add(new ArrayList<>(Collections.nCopies(numberTransactions - 1,0)));
            for (ArrayList<Integer> integers : wrx) {
                integers.add(0);
            }
        }

        for (ArrayList<Boolean> orderMatrix : sessionOrderMatrix) {
            orderMatrix.add(false);
        }
        if(transId != 0) {
            sessionOrderMatrix.get(0).set(transId, true);

            //TODO: hack for assert after all code
            if(threadId == 0){
                for(int i = 1; i < sessionOrderMatrix.size(); ++i){
                    sessionOrderMatrix.get(i).set(transId, true);
                }
            }
        }

        for(int trId : sessionOrder){
            sessionOrderMatrix.get(trId).set(transId, true);

        }
    }

    public void setWR(String var,int write, int read){
        if(var.startsWith(forbiddenVariable)){
            throw new IllegalCallerException("variable "+ var + " forbidden: reserved prefix");
        }
        if(!writeReadMatrix.containsKey(var)){
            writeReadMatrix.put(var, new ArrayList<>(Collections.nCopies(numberTransactions,new ArrayList<>(Collections.nCopies(numberTransactions,0)))));
        }
        int n = writeReadMatrix.get(var).get(write).get(read);
        writeReadMatrix.get(var).get(write).set(read, n+1);
        restoreSemanticFlags();


    }

    public void removeWR(String var, int write, int read){
        int n = writeReadMatrix.get(var).get(write).get(read);
        writeReadMatrix.get(var).get(write).set(read, n-1);
        restoreSemanticFlags();
    }

    public void addWrite(String var, int id){
        if(var.startsWith(forbiddenVariable)){
            throw new IllegalCallerException("variable "+ var + " forbidden: reserved prefix");
        }
        if(!writeReadMatrix.containsKey(var)){
            writeReadMatrix.put(var, new ArrayList<>(Collections.nCopies(numberTransactions,new ArrayList<>(Collections.nCopies(numberTransactions,0)))));
        }

        //For consistency checks we have to know the number of writes; for reading it, we only care about the last one.
        writesPerTransaction.get(id).putIfAbsent(var, 0);
        int n = writesPerTransaction.get(id).get(var);
        writesPerTransaction.get(id).put(var, n+1);
        //restoreSemanticFlags();
    }

    public void removeWrite(String var, int id){
        int n = writesPerTransaction.get(id).get(var);
        if(n == 1) writesPerTransaction.get(id).remove(var);
        else writesPerTransaction.get(id).put(var, n-1);
        restoreSemanticFlags();

    }

    protected ArrayList<ArrayList<Boolean>> computeWRSORelation(){
        //SO u WR
       Cloner cloner = new Cloner();
        var sowr = cloner.deepClone(sessionOrderMatrix);

        //var sowr = Utility.deepCopyMatrix(sessionOrderMatrix);
        for(int i = 0; i < numberTransactions; ++i){
            for(int j = 0; j < numberTransactions; ++j){
                boolean reads = false;
                for(ArrayList<ArrayList<Integer>> wrx: writeReadMatrix.values()){
                    reads = wrx.get(i).get(j) > 0;
                    if(reads) break;
                }
                sowr.get(i).set(j, sessionOrderMatrix.get(i).get(j) ||
                        reads || (j == i) );
            }
        }
        return sowr;
    }

    protected static void computeTransitiveClosure(ArrayList<ArrayList<Boolean>> matAdj){

        int numberTransactions = matAdj.size();
        //Warhsall-floyd
        for(int k = 0; k <numberTransactions; ++k) {
            for (int i = 0; i < numberTransactions; ++i) {
                for (int j = 0; j < numberTransactions; ++j) {
                    matAdj.get(i).set(j, matAdj.get(i).get(j) || (matAdj.get(i).get(k) && matAdj.get(k).get(j)));
                }
            }
        }
    }


    //TODO
    protected void computeTransitiveClosure(){

        transitiveClosure = computeWRSORelation();

        computeTransitiveClosure(transitiveClosure);
    }

    public boolean areWRSO_plusRelated(int a, int b){

        return a != b && areWRSO_starRelated(a, b);
    }

    public boolean areWRSO_starRelated(int a, int b){
        if(transitiveClosure == null){
            computeTransitiveClosure();
        }
        return transitiveClosure.get(a).get(b);
    }

    public boolean areWR(int a, int b){
        for(ArrayList<ArrayList<Integer>> wrx : writeReadMatrix.values()){
            if(wrx.get(a).get(b) > 0){
                return true;
            }
        }
        return false;
    }

    public boolean areWRSORelated(int a, int b){
        return a!= b && (sessionOrderMatrix.get(a).get(b) || areWR(a, b));
    }

    public void removeLastTransaction(){
        --numberTransactions;
        sessionOrderMatrix.remove(numberTransactions);
        writesPerTransaction.remove(numberTransactions);


        for(ArrayList<ArrayList<Integer>> wrx : writeReadMatrix.values()){
            wrx.remove(numberTransactions);
            for(ArrayList<Integer> wrxi : wrx){
                wrxi.remove(numberTransactions);
            }
        }

        for(int i = 0; i < numberTransactions; ++i){
            sessionOrderMatrix.get(i).remove(numberTransactions);
        }
        restoreSemanticFlags();

    }

    protected abstract boolean computeConsistency();

    public boolean isConsistent(){
        if(consistent == null) {
            consistent = computeConsistency();
        }
        return consistent;
        //return true;
    }

    protected void restoreSemanticFlags(){
        transitiveClosure = null;
        consistent = null;
    }


}
