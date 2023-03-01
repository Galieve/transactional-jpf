package country.lab.events;

import country.lab.database.Database;
import country.lab.bytecode.TRINVOKEVIRTUAL;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Transition;

import java.awt.geom.IllegalPathStateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TrEventRegister {

    protected ArrayList<String> argsEvent;

    protected boolean recordArguments;

    protected static TrEventRegister trEventRegisterInstance;

    protected boolean lastInstructionTransactional;

    protected int leadingZerosTrace;

    protected boolean fakeRead;

    protected String databaseClassName;

    private TrEventRegister(Config config){
        argsEvent = new ArrayList<>();
        recordArguments = false;
        databaseClassName = config.getString("db.database_api.class", "database.TRDatabase");
        fakeRead = false;
        leadingZerosTrace = config.getInt("event_path.leading_zeros",6);
    }

    public static TrEventRegister getEventRegister(Config config){
        if(trEventRegisterInstance == null){
            trEventRegisterInstance = new TrEventRegister(config);
        }
        return trEventRegisterInstance;
    }

    public static TrEventRegister getEventRegister(){
        if(trEventRegisterInstance == null){
            throw new IllegalPathStateException();

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
                || i.equals("beginInstruction") || i.equals("commitInstruction")
                || i.equals("abortInstruction") ||

                i.equals("assertInstruction");
    }

    public boolean isTransactionalTransition(Transition t){
        return t.getThreadInfo().getTopFrame() != null && isTransactionalBreakTransition(t.getThreadInfo().getTopFrame());
        //return isTransactionalStatement(t.getLastStep().getInstruction());
        //return t.getStepCount() >= 5 && isTransactionalStatement(t.getStep(t.getStepCount() - 5).getInstruction());
    }

    public boolean isTransactionalReturn(StackFrame frame){
        return frame.getClassName().equals(databaseClassName) && frame.getMethodName().equals("readInstruction");
    }

    public boolean isTransactionalBreakTransition(StackFrame frame){

        return frame.getClassName().equals(databaseClassName) && frame.getMethodName().equals("breakTransition");

    }

    public String getTransactionalStatement(Instruction i){
        if(!(i instanceof TRINVOKEVIRTUAL)) return "";
        TRINVOKEVIRTUAL j = (TRINVOKEVIRTUAL) i;
        if(j.getInvokedMethodClassName().equals(databaseClassName)){
            String s = j.getInvokedMethodName();
            return s.substring(0, s.length() - j.getInvokedMethodSignature().length());
        }
        return "";
    }

    public String getStackTrace(ThreadInfo ti){
        String s = ti.getStackTrace();
        String[] lines = s.split("\\r?\\n");
        Collections.reverse(Arrays.asList(lines));
        for(int i = 0; i < lines.length; ++i){
            String aux = lines[i].replaceAll( "[^\\d]", "" );
            if(!aux.equals("")) {
                String aux2 = String.format("%0" + Math.max(leadingZerosTrace + 1 - aux.length(), 1) + "d%s", 0, aux).substring(1);
                lines[i] = lines[i].replaceAll(aux, aux2);
            }
        }
        s = String.join("\n", lines);
        return ti.getName()+ s;
    }



    public void registerEvent(Instruction parsedInstruction, Instruction i, ThreadInfo ti){

        var database = Database.getDatabase();
        String statement = getTransactionalStatement(parsedInstruction);

        TransactionalEvent t;
        int trId = database.getTransactionalId();
        int soId = database.getTransactionalS0Id(ti.getId());
        int poId = database.getPOId(ti.getId());
        String s = getStackTrace(ti);

        int pos = database.getTimesPathExecuted(s) + 1;

        EventData lastBeg = database.getLastBeginData();
        EventData eventData = new EventData(s, pos, lastBeg);
        switch (statement){
            case "readInstruction":
                t = new ReadTransactionalEvent(eventData, new ArrayList<>(argsEvent), database.getNumberEvents(),
                        ti.getId(), trId, soId, poId);
                break;
            case "writeInstruction":
                String var =  argsEvent.get(0);
                t = new WriteTransactionalEvent(eventData, new ArrayList<>(argsEvent), database.getNumberOfWrites(var),
                        database.getNumberEvents(), ti.getId(), trId, soId, poId);
                break;
            case "beginInstruction":
                eventData = new EventData(s, pos);
                t = new BeginTransactionalEvent(eventData, new ArrayList<>(argsEvent), database.getNumberEvents(),
                        ti.getId(), trId+1, soId+1, poId);
                break;
            case "commitInstruction":

                t = new CommitTransactionalEvent(eventData, new ArrayList<>(argsEvent), database.getNumberEvents(),
                        ti.getId(), trId, soId, poId);
                break;

            case "assertInstruction":
                t = new AssertTransactionalEvent(eventData, new ArrayList<>(argsEvent), database.getNumberEvents(),
                        ti.getId(), trId, soId, poId);
                break;
            case "abortInstruction":
                t = new AbortTransactionalEvent(eventData, new ArrayList<>(argsEvent), database.getNumberEvents(),
                        ti.getId(), trId, soId, poId);
                break;
            default:
                throw new IllegalArgumentException(i.toString());
        }

        database.addEvent(t);
        argsEvent.clear();
        recordArguments = false;

    }

    public boolean isFakeRead() {
        return fakeRead;
    }

    public void setFakeRead(boolean fakeRead) {
        this.fakeRead = fakeRead;
    }





}
