package fr.irif.events;

import fr.irif.database.Database;

import java.util.ArrayList;

public class AssertTransactionalEvent extends TransactionalEvent{

    protected Database database;

    protected AssertTransactionalEvent(EventData eventData, ArrayList<String> args,
                                       int obsSeqIdx, int threadId, int trId, int sesId, int poId) {
        super(eventData, args, Type.ASSERT, obsSeqIdx, threadId, trId, sesId, poId);
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
        String va = evaluate(a), vb = evaluate(b);
        switch (args.get(1)){
            case "<=":
                return Integer.parseInt(va) <= Integer.parseInt(vb);
            case "=":
                return va.equals(vb);
            default:
                throw new IllegalCallerException("Operator not defined");
        }
    }

    protected String evaluate(String s){
        TransactionalEvent t = database.getLastWriteEvent(s);
        if(t == null){
            return s;
        }
        else return t.getValue();
    }
}
