package fr.irif.events;

import fr.irif.database.OracleData;
import gov.nasa.jpf.vm.Instruction;

import java.awt.geom.IllegalPathStateException;
import java.util.ArrayList;

public class WriteTransactionalEvent extends TransactionalEvent {

    protected int writeIndex;

    protected String computedValue;

    protected WriteTransactionalEvent(Instruction i, ArrayList<String> args, int writeIndex, OracleData time,
                                      int obsIdx, int transactionId, int threadId, int sesId, int poId, String callPath) {
        super(i, args, Type.WRITE, time, obsIdx,  transactionId, threadId, sesId, poId, callPath);
        this.writeIndex = writeIndex;
    }

    public int getWriteIndex() {
        return writeIndex;
    }

    @Override
    public String getVariable() {
        return args.get(0);
    }

    @Override
    public String getValue() {
        if(computedValue == null)
            return args.get(1);
        else return computedValue;
    }

    @Override
    public String getComplementaryMessage() {
        if(computedValue == null || computedValue.equals(args.get(1)))
            return "("+getVariable()+" = "+getValue()+")";
        else
            return "("+getVariable()+" = "+getValue()+ " ("+args.get(1)+") "+")";

    }

    public void setValue(String value) {
        computedValue = value;
    }
}
