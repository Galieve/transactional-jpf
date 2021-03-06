package fr.irif.events;

import gov.nasa.jpf.vm.Instruction;

import java.util.ArrayList;

public class EndTransactionalEvent extends TransactionalEvent{


    protected EndTransactionalEvent(EventData eventData, ArrayList<String> args,
                                    int obsIdx, int threadId, int trId, int sesId, int poId) {

        super(eventData, args, Type.END, obsIdx, threadId, trId, sesId, poId);
    }

    @Override
    public String getVariable() {
        throw new IllegalCallerException(type.toString() + " instruction has no variable");
    }

    @Override
    public String getValue() {
        throw new IllegalCallerException(type.toString() + " instruction has no variable");
    }

    @Override
    public String getComplementaryMessage() {
        return "";
    }
}
