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
        /*if(r < 0){
            System.out.println("false");
        }*/
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

        //l.lock();
        //breakTransition();
    }
    private void endInstruction(){
        //l.unlock();
        breakTransition();
    }
    private void assertInstruction(String value1, String op, String value2){
        breakTransition();
    }

    /*

     private String readInstruction(String variable){
        breakTransition();
        return "NON-VALID-VALUE";
    }
    private void writeInstruction(String variable, String value){ breakTransition();}
    private void beginInstruction(){ breakTransition();}
    private void endInstruction(){
        breakTransition();
    }
    private void assertInstruction(String value1, String op, String value2){
        breakTransition();
    }
     */

    /*

    public void read(String variable, String objective){
        readInstruction(variable, objective);
    }

     */
    public String read(String variable){

        return readInstruction(variable);
        /*System.out.println(x);
        return x;

         */
    }

    public void write(String variable, String value){
        writeInstruction(variable, value);
    }

    public void begin(){
        //l.lock();
        beginInstruction();
    }

    public void end(){
        endInstruction();
        //l.unlock();
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


