package country.lab.histories;

import com.rits.cloning.Cloner;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public abstract class History {
    protected ArrayList<ArrayList<Boolean>> sessionOrderMatrix;

    protected HashMap<String,ArrayList<ArrayList<Pair<ArrayList<Integer>, HashSet<Integer>>>>> writeReadMatrix;

    protected ArrayList<HashMap<String, ArrayList<Integer>>> writesPerTransaction;
    protected ArrayList<ArrayList<Boolean>> transitiveClosure;
    protected int numberTransactions;

    protected Boolean consistent;
    protected String forbiddenVariable;

    protected History(Config config){
        this(new ArrayList<>(), new HashMap<>(), new ArrayList<>(),
                config.getString("db.database_isolation_level.forbidden_variable", "FORBIDDEN"));
    }

    protected History(ArrayList<ArrayList<Boolean>> soMatrix,
                      HashMap<String,ArrayList<ArrayList<Pair<ArrayList<Integer>, HashSet<Integer>>>>> wrMatrix,
                      ArrayList<HashMap<String, ArrayList<Integer>>> wrPerTransaction, String forbidden){
        sessionOrderMatrix = soMatrix;
        writeReadMatrix = wrMatrix;
        writesPerTransaction = wrPerTransaction;
        numberTransactions = soMatrix.size();
        consistent = null;
        transitiveClosure = null;
        forbiddenVariable = forbidden;
    }

    protected History(History h){
        this(new Cloner().deepClone(h.sessionOrderMatrix),
                new Cloner().deepClone(h.writeReadMatrix),
                new Cloner().deepClone(h.writesPerTransaction),
                h.forbiddenVariable);
    }

    public void addTransaction(int transId, int threadId, ArrayList<Integer> sessionOrder){

        sessionOrderMatrix.add(new ArrayList<>(Collections.nCopies(numberTransactions,false)));
        writesPerTransaction.add(new HashMap<>());
        restoreSemanticFlags();

        Cloner c = new Cloner();
        ArrayList<Pair<ArrayList<Integer>, HashSet<Integer>>> row = new ArrayList<>();
        for(int i = 0; i < numberTransactions; ++i){
            row.add(new Pair<>(new ArrayList<>(), new HashSet<>()));
        }
        ++numberTransactions;
        for(var wrx : writeReadMatrix.values()){

            wrx.add(c.deepClone(row));
            for (var integers : wrx) {
                integers.add(new Pair<>(new ArrayList<>(), new HashSet<>()));
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

    public void setWR(String var, int write, int read, int id){
        if(var.startsWith(forbiddenVariable)){
            throw new IllegalCallerException("variable "+ var + " forbidden: reserved prefix");
        }
        if(!writeReadMatrix.containsKey(var)){

            addVarToWriteReadMatrix(var);
        }
        writeReadMatrix.get(var).get(write).get(read)._1.add(id);
        writeReadMatrix.get(var).get(write).get(read)._2.add(id);
        //writeReadMatrix.get(var).get(write).set(read, n+1);
        restoreSemanticFlags();


    }

    public void removeWR(String var, int write, int read){
        var l = writeReadMatrix.get(var).get(write).get(read);
        var e = l._1.get(l._1.size() - 1);
        l._2.remove(e);
        l._1.remove(l._1.size() - 1);

        //writeReadMatrix.get(var).get(write).set(read, n-1);
        restoreSemanticFlags();
    }

    protected void addVarToWriteReadMatrix(String var){
        Cloner c = new Cloner();
        var row = new ArrayList<Pair<ArrayList<Integer>, HashSet<Integer>>>();
        var mat = new ArrayList<ArrayList<Pair<ArrayList<Integer>, HashSet<Integer>>>>();
        for(int i = 0; i < numberTransactions; ++i){
            row.add(new Pair<>(new ArrayList<>(), new HashSet<>()));
        }
        for(int i = 0; i < numberTransactions; ++i){
            mat.add(c.deepClone(row));
        }
        writeReadMatrix.put(var, mat);
    }
    public void addWrite(String var, int id, int poID){
        if(var.startsWith(forbiddenVariable)){
            throw new IllegalCallerException("variable "+ var + " forbidden: reserved prefix");
        }
        if(!writeReadMatrix.containsKey(var)){

            addVarToWriteReadMatrix(var);
        }

        //For consistency checks we have to know the number of writes; for reading it, we only care about the last one.
        writesPerTransaction.get(id).putIfAbsent(var, new ArrayList<>());
        writesPerTransaction.get(id).get(var).add(poID);
        //restoreSemanticFlags();
    }

    public boolean isWrittingVariable(String var, int id){
        var tr = writesPerTransaction.get(id);
        return tr.containsKey(var) && tr.get(var).size() > 0;
    }
    public boolean isWrittingVariable(String var, int id, int poID){
        var tr = writesPerTransaction.get(id);
        if(tr.containsKey(var) && tr.get(var).size() > 0)
            return tr.get(var).get(0) < poID;
        else return false;
    }

    public void removeWrite(String var, int id){

        var writes =writesPerTransaction.get(id).get(var);
        writes.remove(writes.size() - 1);
        if(writes.size() == 0) writesPerTransaction.get(id).remove(var);

    }

    protected ArrayList<ArrayList<Boolean>> computeWRSORelation(){
        //SO u WR
       Cloner cloner = new Cloner();
        var sowr = cloner.deepClone(sessionOrderMatrix);

        //var sowr = Utility.deepCopyMatrix(sessionOrderMatrix);
        for(int i = 0; i < numberTransactions; ++i){
            for(int j = 0; j < numberTransactions; ++j){
                boolean reads = false;
                for(var wrx: writeReadMatrix.values()){
                    reads = wrx.get(i).get(j)._1.size() > 0;
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
        for(var wrx : writeReadMatrix.values()){
            if(wrx.get(a).get(b)._1.size() > 0){
                return true;
            }
        }
        return false;
    }

    public boolean areWR(int a, int b, int poB){
        for(var wrx : writeReadMatrix.values()){
            if(wrx.get(a).get(b)._2.contains(poB)){
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


        for(var wrx : writeReadMatrix.values()){
            wrx.remove(numberTransactions);
            for(var wrxi : wrx){
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

    public int getNumberTransactions() {
        return numberTransactions;
    }
}
