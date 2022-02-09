package fr.irif.events;

import fr.irif.database.OracleData;
import gov.nasa.jpf.vm.Instruction;

public class UnknownEvent extends TransactionalEvent{
    public UnknownEvent(Instruction i, OracleData oOrder) {

        super(i, null, Type.UNKNOWN, oOrder, -1, -1, -1, -1, -1, null);
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
