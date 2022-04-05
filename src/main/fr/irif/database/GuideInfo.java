package fr.irif.database;

import fr.irif.events.Transaction;
import fr.irif.events.TransactionalEvent;
import fr.irif.events.WriteTransactionalEvent;

import java.util.LinkedList;

public class GuideInfo {

    public enum BacktrackTypes{
        JPF, READ, SWAP, RESTORE, NONE
    }

    protected LinkedList<Transaction> guidedPath;

    protected TransactionalEvent endEvent;

    protected WriteTransactionalEvent writeEventSwap;

    protected BacktrackTypes databaseBacktrackMode;

    public GuideInfo() {
        guidedPath = null;
        endEvent = null;
        databaseBacktrackMode = BacktrackTypes.NONE;
    }

    public BacktrackTypes getDatabaseBacktrackMode() {
        return databaseBacktrackMode;
    }

    public void setDatabaseBacktrackMode(BacktrackTypes databaseBacktrackMode) {
        this.databaseBacktrackMode = databaseBacktrackMode;
    }

    public void addGuide(LinkedList<Transaction> path, TransactionalEvent end, WriteTransactionalEvent swapEvent){
        guidedPath = path;
        endEvent = end;
        writeEventSwap = swapEvent;
    }

    public boolean hasPath(){
        return guidedPath != null;
    }

    public void resetPath(){
        guidedPath = null;
        endEvent = null;
    }

    public LinkedList<Transaction> getGuidedPath() {
        return guidedPath;
    }

    public TransactionalEvent getEndEvent() {
        return endEvent;
    }

    public WriteTransactionalEvent getWriteEventSwap() {
        return writeEventSwap;
    }
}
