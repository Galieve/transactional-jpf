package country.lab.events;

public class UnknownEvent extends TransactionalEvent{
    public UnknownEvent(EventData eventData) {

        super(eventData, null, Type.UNKNOWN, -1, -1, -1, -1, -1);
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
