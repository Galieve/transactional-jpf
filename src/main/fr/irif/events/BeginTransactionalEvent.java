package fr.irif.events;

import fr.irif.database.OracleData;
import gov.nasa.jpf.vm.Instruction;

import java.util.ArrayList;

public class BeginTransactionalEvent extends TransactionalEvent{


    protected BeginTransactionalEvent(Instruction i, ArrayList<String> args, OracleData time,
                                      int obsIdx, int transactionId, int threadId, int sesId, int poId, String callPath) {

        super(i, args, Type.BEGIN, time, obsIdx, transactionId, threadId, sesId, poId, callPath);
    }

    @Override
    public String getVariable() {
        throw new IllegalCallerException(type.toString() + " instruction has no variable");

    }

    @Override
    public String getValue() {
        throw new IllegalCallerException(type.toString() + " instruction has no value");
    }

    @Override
    public String getComplementaryMessage() {
        return "";
    }
}
