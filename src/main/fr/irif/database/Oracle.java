package fr.irif.database;

import fr.irif.events.TrEventRegister;
import gov.nasa.jpf.util.Pair;
import gov.nasa.jpf.vm.Instruction;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class Oracle {



    protected TreeMap<OracleData, Instruction> instructionsOrdered;

    protected HashMap<Instruction, OracleData> oracleOrder;

    /**
     * Map for getting the begin of a transaction given a instruction I
     */
    protected TreeSet<Instruction> beginSet;

    protected static Oracle oracleInstance;

    protected Oracle() {
        oracleOrder = new HashMap<>();
        instructionsOrdered = new TreeMap<>();
        beginSet = new TreeSet<>(Comparator.comparingInt(Instruction::getLineNumber));
    }

    public static Oracle getOracle(){
        if(oracleInstance == null){
            oracleInstance = new Oracle();
        }
        return oracleInstance;
    }

    public Instruction getInstruction(OracleData data){
        return instructionsOrdered.get(data);
    }

    public OracleData getNextData(OracleData data){
        return instructionsOrdered.higherKey(data);
    }

    protected OracleData getBeginInstruction(Instruction i){
        return oracleOrder.get(beginSet.lower(i));
    }

    protected void addInstruction(Instruction i, Integer trid){
        OracleData p = new OracleData(trid, i.getLineNumber());
        instructionsOrdered.put(p, i);
        oracleOrder.put(i, p);
    }

    public void addInstructionIfAbsent(Instruction i){
        if(oracleOrder.containsKey(i)) return;

        String s = TrEventRegister.getEventRegister().getTransactionalStatement(i);
        switch (s){
            case "begin":
                addInstruction(i, beginSet.size());
                beginSet.add(i);
                break;
            case "end":
            case "read":
            case "write":
                OracleData o = getBeginInstruction(i);
                addInstruction(i, o.getInfo()._1);
                break;
            default:
                break;
        }

    }

    public OracleData getInstrucionIndex(Instruction i){
        return oracleOrder.get(i);
    }

    public int size(){
        return instructionsOrdered.size();
    }

}
