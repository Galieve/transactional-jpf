package database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;

public class APIDatabase {

    protected static APIDatabase databaseInstance;

    protected int r;

    protected ReentrantLock l;


    private APIDatabase(){
        r = 0;
        l = new ReentrantLock();
    }

    public static APIDatabase getDatabase(){
        if(databaseInstance == null){
            databaseInstance = new APIDatabase();
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
        //return r.equals("null") ? null : r;
    }

    public void write(String variable, String value){
        if(value == null) value = "null";
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

    public String readRow(String table, String row){
        var t = read(table);
        if(t == null) return null;
        var tList = TRUtility.generateArrayList(t);
        for(var e: tList){
            if(e.equals(row)){
                var r = read(table+":"+row);
                if(r.equals("null")) return null;
                else return r;
            }
        }
        return null;
    }

    public ArrayList<String> readAll(String table){
        return readIfIDStartsWith(table, "");
    }

    public ArrayList<String> readIfIsIn(String table, HashSet<String> idSet){
        var t = read(table);
        if(t == null) return null;
        var tList = TRUtility.generateArrayList(t);
        var results = new ArrayList<String>();
        for(var e: tList) {
            if (idSet.contains(e)) {
                var r = read(table + ":" + e);
                if(r.equals("null")) r = null;
                results.add(r);
            }
        }
        return results;
    }

    public ArrayList<String> readIfIDStartsWith(String table,
                                                String prefix){
        var t = read(table);
        if(t == null) return null;
        var tList = TRUtility.generateArrayList(t);
        var results = new ArrayList<String>();

        for(var e: tList) {
            if (e.startsWith(prefix)) {
                var r = read(table + ":" + e);
                if(r.equals("null")) r = null;
                results.add(r);
            }
        }
        return results;
    }

    public ArrayList<String> readIfIDEndsWith(String table,
                                                String suffix){
        var t = read(table);
        if(t == null) return null;
        var tList = TRUtility.generateArrayList(t);
        var results = new ArrayList<String>();

        for(var e: tList) {
            if (e.endsWith(suffix)) {
                var r = read(table + ":" + e);
                if(r.equals("null")) r = null;
                results.add(r);
            }
        }
        return results;
    }

    public ArrayList<String> readAllIDs(String table){
        var t = read(table);
        if(t == null) return null;
        return TRUtility.generateArrayList(t);
    }

    public void insertRow(String table, String row, String value){
        insertRow(table, row);
        if(value == null) value = "null";
        write(table+":"+row, value);
    }

    public void insertRow(String table, String row){
        var t = read(table);
        var tList = TRUtility.generateArrayList(t);
        tList.add(row);
        write(table, tList.toString());
    }

    public void writeRow(String table, String row, String value){

        var t = read(table);
        var tList = TRUtility.generateArrayList(t);

        if(value == null) value = "null";
        for(var e: tList){
            if(e.equals(row)){
                write(table+":"+row, value);
                return;
            }
        }
    }

    public void deleteRow(String table, String row){
        var t = read(table);
        var tList = TRUtility.generateArrayList(t);
        tList.remove(row);
        write(table, tList.toString());
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


