package fr.irif.events;

import fr.irif.database.Database;
import fr.irif.database.OracleData;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Transition;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class TrEventRegister {

    protected ArrayList<String> argsEvent;

    protected boolean recordArguments;

    protected static TrEventRegister trEventRegisterInstance;

    protected boolean lastInstructionTransactional;

    protected Database database;

    protected boolean choiceGeneratorShared;

    //protected DatabaseRelations databaseRelations;

    protected boolean fakeRead;

    protected static String databaseClassName = "TRDatabase";

    protected ArrayDeque<String> callPath;

    private TrEventRegister(){
        argsEvent = new ArrayList<>();
        recordArguments = false;
        database = Database.getDatabase();
        choiceGeneratorShared = false;
        //databaseRelations = DatabaseRelations.getDatabaseRelations();
        fakeRead = false;
        callPath = new ArrayDeque<>();
    }

    public static TrEventRegister getEventRegister(){
        if(trEventRegisterInstance == null){
            trEventRegisterInstance = new TrEventRegister();
        }
        return trEventRegisterInstance;
    }

    public void addArgument(String name){
        argsEvent.add(name);
    }

    public boolean isLastInstructionTransactional() {
        return lastInstructionTransactional;
    }

    public void setLastInstructionTransactional(boolean lastInstructionTransactional) {
        this.lastInstructionTransactional = lastInstructionTransactional;
    }

    public boolean isTransactionalStatement(Instruction inst){
        String i = getTransactionalStatement(inst);
        return i.equals("readInstruction") || i.equals("writeInstruction")
                || i.equals("beginInstruction") || i.equals("endInstruction") ||
                i.equals("assertInstruction");
    }

    public boolean isTransactionalTransition(Transition t){
        return isTransactionalStatement(t.getLastStep().getInstruction());
    }

    public boolean isTransactionalReturn(StackFrame frame){
        return frame.getClassName().equals(databaseClassName) && frame.getMethodName().equals("readInstruction");
    }

   protected String getTransactionalStatementTRINVOKEVIRTUAL(TRINVOKEVIRTUAL j){
        if(j.getInvokedMethodClassName().equals(databaseClassName)){
            String s = j.getInvokedMethodName();
            return s.substring(0, s.length() - j.getInvokedMethodSignature().length());
        }
        return "";
    }

    public String getTransactionalStatement(Instruction i){
        if(!(i instanceof TRINVOKEVIRTUAL)) return "";

        return getTransactionalStatementTRINVOKEVIRTUAL((TRINVOKEVIRTUAL) i);

    }

    public void registerEvent(Instruction parsedInstruction, Instruction i, ThreadInfo ti){
        String statement = getTransactionalStatement(parsedInstruction);

        TransactionalEvent t;
        OracleData oraclePosition = database.getOrAddOraclePosition(i);
        int trId = database.getTransactionalId();
        int soId = database.getTransactionalS0Id(ti.getId());
        int poId = database.getPOId(ti.getId());
        String s = getCallPath();
        switch (statement){
            case "readInstruction":
                t = new ReadTransactionalEvent(i, new ArrayList<>(argsEvent), oraclePosition, database.getNumberEvents(),
                        trId, ti.getId(), soId, poId, s);
                break;
            case "writeInstruction":
                String var =  argsEvent.get(0);
                t = new WriteTransactionalEvent(i, new ArrayList<>(argsEvent), database.getNumberOfWrites(var),
                        oraclePosition, database.getNumberEvents(), trId, ti.getId(), soId, poId, s);
                break;
            case "beginInstruction":
                t = new BeginTransactionalEvent(i, new ArrayList<>(argsEvent), oraclePosition, database.getNumberEvents(),
                        trId +1, ti.getId(), soId+1, poId, s);
                break;
            case "endInstruction":
                t = new EndTransactionalEvent(i, new ArrayList<>(argsEvent), oraclePosition, database.getNumberEvents(),
                        trId, ti.getId(), soId, poId, s);
                break;

            case "assertInstruction":
                t = new AssertTransactionalEvent(i, new ArrayList<>(argsEvent), oraclePosition, database.getNumberEvents(),
                        trId, ti.getId(), soId, poId, s);
                break;
            default:
                throw new IllegalArgumentException(i.toString());
        }

        database.addEvent(t);
        argsEvent.clear();
        recordArguments = false;

    }

    public boolean isChoiceGeneratorShared() {
        return choiceGeneratorShared;
    }

    public void setChoiceGeneratorShared(boolean choiceGeneratorShared) {
        this.choiceGeneratorShared = choiceGeneratorShared;
    }

    public boolean isFakeRead() {
        return fakeRead;
    }

    public void setFakeRead(boolean fakeRead) {
        this.fakeRead = fakeRead;
    }

    public void addContextToPath(){
        if(!database.isMockAccess())
            callPath.addLast("");
    }

    public void addCall(String s){
        if(!database.isMockAccess()) {
            String last = callPath.getLast();
            callPath.removeLast();
            callPath.addLast(last + " " + s);
        }
    }

    public void removeContextFromPath(){
        if(!database.isMockAccess() && callPath.size() > 0){
            callPath.removeLast();
        }
    }

    public void printPath(){
        if(!database.isMockAccess() && callPath.size() > 0) {
            //System.out.println("DEBUG: "+callPath);
            //System.out.println("DEBUG: " + callPath.getLast()+ " size: "+callPath.size());
        }
    }

    protected String getCallPath(){
        return callPath.toString();
    }
}
