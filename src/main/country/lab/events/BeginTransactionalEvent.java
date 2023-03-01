package country.lab.events;

import java.util.ArrayList;

public class BeginTransactionalEvent extends TransactionalEvent{


    protected BeginTransactionalEvent(EventData eventData, ArrayList<String> args,
                                      int obsIdx, int threadId, int trId, int sesId, int poId) {

        super(eventData, args, Type.BEGIN, obsIdx, threadId, trId, sesId, poId);
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
