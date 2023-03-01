package country.lab.events;

import java.util.ArrayList;

public class CommitTransactionalEvent extends TransactionalEvent{


    protected CommitTransactionalEvent(EventData eventData, ArrayList<String> args,
                                       int obsIdx, int threadId, int trId, int sesId, int poId) {

        super(eventData, args, Type.COMMIT, obsIdx, threadId, trId, sesId, poId);
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
