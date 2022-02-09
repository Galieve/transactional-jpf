import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TRDatabase {

    protected static TRDatabase databaseInstance;

    protected int r;


    private TRDatabase(){
        r = 0;
    }

    public static TRDatabase getDatabase(){
        if(databaseInstance == null){
            databaseInstance = new TRDatabase();
        }
        return databaseInstance;
    }

    private void breakTransition(){
        if(r < 0){
            System.out.println("false");
        }
    }

    // private void readInstruction(String variable, String objective){}


    private String readInstruction(String variable){
        return "NON-VALID-VALUE";
    }
    private void writeInstruction(String variable, String value){}
    private void beginInstruction(){}
    private void endInstruction(){
        breakTransition();
    }
    private void assertInstruction(String value1, String op, String value2){}

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
}


