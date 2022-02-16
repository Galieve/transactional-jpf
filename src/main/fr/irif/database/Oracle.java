package fr.irif.database;

import fr.irif.events.EventData;
import gov.nasa.jpf.vm.Instruction;

import java.util.TreeMap;

public class Oracle {

    /**
     * Map for getting the begin of a transaction given a instruction I
     */

    protected TreeMap<String, Instruction> oracleOrder;

    protected int size;

    protected static Oracle oracleInstance;

    protected Oracle() {
        oracleOrder = new TreeMap<>();
        size = 0;
    }

    public static Oracle getOracle(){
        if(oracleInstance == null){
            oracleInstance = new Oracle();
        }
        return oracleInstance;
    }


    public EventData getNextData(EventData data){
        int pos = 0;
        String nextPath = oracleOrder.higherKey(data.getPath());
        if(data.getPath().equals(nextPath)){ //For loop case
            pos = data.getPos()+1;
        }
        if(nextPath != null){
            return new EventData(nextPath, pos, oracleOrder.get(nextPath));
        }
        else return null;
    }


    public void addEventDataIfAbsent(EventData e){

        if(oracleOrder.containsKey(e.getPath())) return;

        oracleOrder.put(e.getPath(), e.getInstruction());
        ++size;
    }

    public int size(){
        return size;
    }

}
