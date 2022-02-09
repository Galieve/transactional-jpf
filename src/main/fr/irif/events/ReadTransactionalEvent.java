package fr.irif.events;

import fr.irif.database.OracleData;
import gov.nasa.jpf.vm.Instruction;

import java.util.ArrayList;

public class ReadTransactionalEvent extends TransactionalEvent{

    protected WriteTransactionalEvent writeEvent;

    public Instruction backtrackInstruction;

    protected ReadTransactionalEvent(Instruction i, ArrayList<String> args,
                                     OracleData time, int obsIdx, int transactionId, int threadId, int sesId, int poId, String callPath) {
        super(i, args, Type.READ, time, obsIdx, transactionId, threadId, sesId, poId, callPath);
        backtrackInstruction = null;
        writeEvent = null;
    }

    @Override
    public String getVariable() {
        return args.get(0);
    }

    @Override
    public String getValue() {
        return args.get(1);
    }

    public WriteTransactionalEvent getWriteEvent() {
        return writeEvent;
    }

    public void changeWriteEvent(WriteTransactionalEvent writeEvent) {
        this.writeEvent = writeEvent;
    }

    public Instruction getBacktrackInstruction() {
        return backtrackInstruction;
    }

    public void setBacktrackInstruction(Instruction backtrackInstruction) {
        this.backtrackInstruction = backtrackInstruction;
    }

    @Override
    public String getComplementaryMessage() {
        return "(read <- " +getVariable()+")";
    }
}
