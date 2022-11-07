package fr.irif.events;

import java.util.ArrayList;

public class WriteTransactionalEvent extends TransactionalEvent {

    protected int writeIndex;

    protected String computedValue;

    public WriteTransactionalEvent(EventData eventData, ArrayList<String> args, int writeIndex,
                                   int obsIdx, int threadId, int trId, int sesId, int poId) {
        super(eventData, args, Type.WRITE, obsIdx, threadId, trId, sesId, poId);
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

    @Override
    protected String getWRMessage() {
        return getComplementaryMessage();
    }

    public void setValue(String value) {
        computedValue = value;
    }
}
