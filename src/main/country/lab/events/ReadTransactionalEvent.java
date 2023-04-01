package country.lab.events;

import gov.nasa.jpf.util.Pair;

import java.util.ArrayList;

public class ReadTransactionalEvent extends TransactionalEvent{

    protected WriteTransactionalEvent writeEvent;
    public EventData backtrackEvent;

    public EventData backtrackWriteEvent;
    protected ReadTransactionalEvent(EventData eventData, ArrayList<String> args,
                                     int obsIdx, int threadId, int trId, int sesId, int poId) {
        super(eventData, args, Type.READ, obsIdx, threadId, trId, sesId, poId);
        backtrackEvent = null;
        writeEvent = null;
    }

    @Override
    public String getVariable() {
        return args.get(0);
    }

    @Override
    public String getValue() {
        throw new IllegalCallerException(type.toString() + " instruction has no value");
    }

    public WriteTransactionalEvent getWriteEvent() {
        return writeEvent;
    }

    public void setWriteEvent(WriteTransactionalEvent writeEvent) {
        this.writeEvent = writeEvent;
    }

    public EventData getBacktrackEvent() {
        return backtrackEvent;
    }

    public EventData getBacktrackWriteEvent() {
        return backtrackWriteEvent;
    }

    public void setBacktrackEvent(Pair<EventData, EventData> backtrackPair) {
        this.backtrackEvent = backtrackPair._1;
        this.backtrackWriteEvent = backtrackPair._2;
    }

    @Override
    public String getComplementaryMessage() {
        return "(read <- " +getVariable()+")";
    }

    public String getWRMessage() {
        return "("+ getVariable()+" <- " + getWriteEvent().getValue()+ ") ["+ getWriteEvent().getBaseName()+"]";
    }
}
