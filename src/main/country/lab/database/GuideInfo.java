package country.lab.database;

import country.lab.events.BeginTransactionalEvent;
import country.lab.events.Transaction;
import country.lab.events.TransactionalEvent;
import country.lab.events.WriteTransactionalEvent;

import java.util.HashSet;
import java.util.LinkedList;

public class GuideInfo {

    public enum BacktrackTypes{
        JPF, READ, SWAP, RESTORE, NONE
    }
    protected LinkedList<Transaction> guidedPath;

    protected TransactionalEvent endEvent;

    protected WriteTransactionalEvent writeEventSwap;

    protected BacktrackTypes databaseBacktrackMode;

    protected HashSet<BeginTransactionalEvent> deleted;

    protected boolean hasPath;

    public GuideInfo() {
        guidedPath = null;
        endEvent = null;
        databaseBacktrackMode = BacktrackTypes.NONE;
        hasPath = false;
        deleted = null;
    }

    public BacktrackTypes getDatabaseBacktrackMode() {
        return databaseBacktrackMode;
    }

    public HashSet<BeginTransactionalEvent> getDeleted(){
        return deleted;
    }

    public void setDatabaseBacktrackMode(BacktrackTypes databaseBacktrackMode) {
        this.databaseBacktrackMode = databaseBacktrackMode;
    }

    public void addGuide(LinkedList<Transaction> path, TransactionalEvent end, WriteTransactionalEvent swapEvent,
                         HashSet<BeginTransactionalEvent> deletedTransactions){
        guidedPath = path;
        endEvent = end;
        writeEventSwap = swapEvent;
        hasPath = true;
        deleted = deletedTransactions;
    }


    public boolean hasPath(){
        return hasPath;
    }

    public void resetPath(){
        hasPath = false;
        //guidedPath = null;
        //endEvent = null;
    }

    public void fullResetPath(){
        hasPath = false;
        guidedPath = null;
        endEvent = null;
        deleted = null;
    }

    public LinkedList<Transaction> getGuidedPath() {
        return hasPath ? guidedPath : null;
    }

    public TransactionalEvent getEndEvent() {
        return hasPath ? endEvent : null;
    }

    public WriteTransactionalEvent getWriteEventSwap() {
        return hasPath ? writeEventSwap : null;
    }
}
