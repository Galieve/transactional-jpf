package fr.irif.events;

import java.util.ArrayList;

public class ReadTransactionalEvent extends TransactionalEvent{

    protected WriteTransactionalEvent writeEvent;

    public EventData backtrackEvent;

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
        return args.get(1);
    }

    public WriteTransactionalEvent getWriteEvent() {
        return writeEvent;
    }

    public void changeWriteEvent(WriteTransactionalEvent writeEvent) {
        this.writeEvent = writeEvent;
    }

    public EventData getBacktrackEvent() {
        return backtrackEvent;
    }

    public void setBacktrackEvent(EventData backtrackEvent) {
        this.backtrackEvent = backtrackEvent;
    }

    @Override
    public String getComplementaryMessage() {
        return "(read <- " +getVariable()+")";
    }
}
