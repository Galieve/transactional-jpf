package database;

import java.util.concurrent.locks.ReentrantLock;

public class TRDatabase {

    protected static TRDatabase databaseInstance;

    protected int r;

    protected ReentrantLock l;


    private TRDatabase(){
        r = 0;
        l = new ReentrantLock();
    }

    public static TRDatabase getDatabase(){
        if(databaseInstance == null){
            databaseInstance = new TRDatabase();
        }
        return databaseInstance;
    }

    private void breakTransition(){
        for(int  i = 0; i < 1; ++i){}

    }

    // private void readInstruction(String variable, String objective){}


    private String readInstruction(String variable){
        breakTransition();
        return "NON-VALID-VALUE";
    }
    private void writeInstruction(String variable, String value){
        breakTransition();


    }
    private void beginInstruction(){
        breakTransition();
    }
    private void commitInstruction(){
        breakTransition();
    }

    private void abortInstruction(){
        breakTransition();
    }
    private void assertInstruction(String value1, String op, String value2){
        breakTransition();
    }

    public String read(String variable){

        return readInstruction(variable);
    }

    public void write(String variable, String value){
        writeInstruction(variable, value);
    }

    public void begin(){
        beginInstruction();
    }

    public void commit(){
        commitInstruction();
    }

    public void abort() throws AbortDatabaseException {
        abortInstruction();
        throw new AbortDatabaseException("Abort database.");
    }

    public void assertDB(String value1, String op, String value2){
        assertInstruction(value1,op,value2);
    }

    public void assertDB(boolean b){
        String s2 = "=";
        String s3 = "true";
        assertInstruction(String.valueOf(b), s2, s3);
    }
}


