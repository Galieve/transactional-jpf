package fr.irif.events;

import java.util.ArrayList;

public abstract class TransactionalEvent{

    public enum Type{
        BEGIN, READ, WRITE, END, ASSERT, UNKNOWN
    }

    protected EventData eventData;

    protected ArrayList<String> args;

    protected Type type;

    protected int threadId;

    protected int observationSequenceIndex;

    protected int relSOId;

    protected int poId;

    protected int trId;

    protected TransactionalEvent(EventData eventData, ArrayList<String> args, Type t,
                                 int obsSeqIdx, int threadId, int trId, int sesId, int poId){
        this.eventData = eventData;
        this.args = args;
        type = t;
        this.threadId = threadId;
        this.observationSequenceIndex = obsSeqIdx;
        relSOId = sesId;
        this.poId = poId;
        this.trId = trId;
    }

    public EventData getEventData() {
        return eventData;
    }

    public int getTransactionId(){
        return trId;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public Type getType() {
        return type;
    }

    public abstract String getVariable();

    public abstract String getValue();

    protected String getWRMessage(){
        return "";
    }

    /*
    public int getTransactionId(){
        return transactionId;
    }
    
     */

    public int getThreadId() {
        return threadId;
    }

    public int getObservationSequenceIndex() {
        return observationSequenceIndex;
    }

    protected abstract String getComplementaryMessage();

    public String getBaseName(){
       return "Thread " + getThreadId() + " transaction " + relSOId+ " " + getType().toString()+ " event #"+poId;
    }

    public int getPoId() {
        return poId;
    }

    public String toString(){
        String s = getComplementaryMessage();
        if(!s.equals("")){
            s = " "+ s;
        }
        return getBaseName() + s;
    }

    public String toWRString(){
        String s = getWRMessage();
        if(!s.equals("")){
            s = " "+ s;
        }
        return getBaseName() + s;
    }

}
