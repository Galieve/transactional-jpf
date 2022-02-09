package fr.irif.events;

import fr.irif.database.Database;
import fr.irif.database.OracleData;
import gov.nasa.jpf.vm.Instruction;

import java.awt.geom.IllegalPathStateException;
import java.util.ArrayList;

public class AssertTransactionalEvent extends TransactionalEvent{

    protected Database database;

    protected AssertTransactionalEvent(Instruction i, ArrayList<String> args, OracleData oOrder,
                                       int obsSeqIdx, int transactionId, int threadId, int sesId, int poId, String callPath) {
        super(i, args, Type.ASSERT, oOrder, obsSeqIdx, transactionId, threadId, sesId, poId, callPath);
        database = Database.getDatabase();
    }

    @Override
    public String getVariable() {
        return null;
    }

    @Override
    public String getValue() {
        return String.valueOf(checkAssertion());
    }

    @Override
    public String getComplementaryMessage() {
        return evaluate(args.get(0)) + " "+args.get(1)+ " "+evaluate(args.get(2));
    }

    public boolean checkAssertion(){
        String a = args.get(0);
        String b = args.get(2);
        Integer va = evaluate(a), vb = evaluate(b);
        switch (args.get(1)){
            case "<=":
                return va <= vb;
            case "=":
                return va == vb;
            default:
                throw new IllegalCallerException("Operator not defined");
        }
    }

    protected Integer evaluate(String s){
        TransactionalEvent t = database.getLastWriteEvent(s);
        if(t == null){
            return Integer.valueOf(s);
        }
        else return Integer.valueOf(t.getValue());
    }
}
