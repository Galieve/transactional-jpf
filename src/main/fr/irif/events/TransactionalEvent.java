package fr.irif.events;

import fr.irif.database.OracleData;
import gov.nasa.jpf.vm.Instruction;

import java.util.ArrayList;

public abstract class TransactionalEvent{

    public enum Type{
        BEGIN, READ, WRITE, END, ASSERT, UNKNOWN
    }

    protected Instruction instruction;

    protected ArrayList<String> args;

    protected Type type;

    protected OracleData oracleOrder;

    protected int transactionId;

    protected int threadId;

    protected int observationSequenceIndex;

    protected int relSOId;

    protected int poId;

    protected String callPath;

    protected TransactionalEvent(Instruction i, ArrayList<String> args, Type t, OracleData oOrder,
                                 int obsSeqIdx, int transactionId, int threadId, int sesId, int poId, String callPath){
        instruction = i;
        this.args = args;
        type = t;
        oracleOrder = oOrder;
        this.transactionId = transactionId;
        this.threadId = threadId;
        this.observationSequenceIndex = obsSeqIdx;
        relSOId = sesId;
        this.poId = poId;
        this.callPath = callPath;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public Type getType() {
        return type;
    }

    public abstract String getVariable();

    public abstract String getValue();

    public OracleData getOracleOrder() {
        return oracleOrder;
    }

    public int getTransactionId(){
        return transactionId;
    }

    public int getThreadId() {
        return threadId;
    }

    public int getObservationSequenceIndex() {
        return observationSequenceIndex;
    }

    public abstract String getComplementaryMessage();

    public String getBaseName(){
       return "Thread " + getThreadId() + " transaction " + relSOId+ " " + getType().toString()+ " event #"+poId;
    }

    public String toString(){
        String s = getComplementaryMessage();
        if(!s.equals("")){
            s = " "+ s;
        }
        return getBaseName() + s;
    }

    public String getCallPath() {
        return callPath;
    }

    /*public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }*/
}
