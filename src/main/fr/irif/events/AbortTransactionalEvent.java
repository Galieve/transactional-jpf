package fr.irif.events;

import java.util.ArrayList;

public class AbortTransactionalEvent extends TransactionalEvent{


    protected AbortTransactionalEvent(EventData eventData, ArrayList<String> args,
                                      int obsIdx, int threadId, int trId, int sesId, int poId) {

        super(eventData, args, Type.ABORT, obsIdx, threadId, trId, sesId, poId);
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
