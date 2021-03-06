package fr.irif.database;

import fr.irif.events.EventData;
import fr.irif.events.TransactionalEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class Oracle {

    //TODO: Oracle not robust! We have to control the model to accept transactions may miss!!!!

    protected ArrayList<EventData> oracleOrder;

    protected HashMap<EventData, Integer> instructionToOrder;

    protected HashMap<EventData, EventData> beginToEnd;

    protected static Oracle oracleInstance;

    protected Oracle() {
        oracleOrder = new ArrayList<>();
        instructionToOrder = new HashMap<>();
        beginToEnd = new HashMap<>();
    }

    public static Oracle getOracle(){
        if(oracleInstance == null){
            oracleInstance = new Oracle();
        }
        return oracleInstance;
    }


    public EventData getNextData(EventData data){
        int n = 0; //-1 + 1
        if(instructionToOrder.containsKey(data)) n = instructionToOrder.get(data) + 1;

        if(n == oracleOrder.size()) return null;
        else return oracleOrder.get(n);
    }

    public void addBegin(EventData e){
        if(instructionToOrder.containsKey(e)) return;
        int size = oracleOrder.size();
        oracleOrder.add(e);
        instructionToOrder.put(e, size);
    }

    public void addEnd(EventData end){
        if(oracleOrder.isEmpty()) throw new IllegalCallerException();
        EventData begin = oracleOrder.get(oracleOrder.size()-1);

        //As every transaction is available, in every execution that is not the end, begin has no relation with end
        //but there exists someone who links end and begin.
        if(beginToEnd.containsKey(begin)) return;
        beginToEnd.put(begin, end);
    }

    public EventData getEnd(EventData begin){
        return beginToEnd.get(begin);
    }

    public int size(){
        return oracleOrder.size();
    }

    private int compareEvents(EventData a, EventData b){
        EventData aBeginEvent = a.getBeginEvent();
        EventData bBeginEvent = b.getBeginEvent();
        int aPos = instructionToOrder.get(aBeginEvent);
        int bPos = instructionToOrder.get(bBeginEvent);
        return aPos - bPos;
    }

    public int compareEvents(TransactionalEvent a, TransactionalEvent b){
        int res = compareEvents(a.getEventData(), b.getEventData());
        if(res != 0) return res;
        else return a.getPoId() - b.getPoId();
    }

}
