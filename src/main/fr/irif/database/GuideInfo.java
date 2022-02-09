package fr.irif.database;

import fr.irif.events.TransactionalEvent;
import fr.irif.events.WriteTransactionalEvent;

import java.util.LinkedList;

public class GuideInfo {

    public enum BacktrackTypes{
        JPF, READ, SWAP, RESTORE, NONE
    }

    protected LinkedList<TransactionalEvent> guidedPath;

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

    public void addGuide(LinkedList<TransactionalEvent> path, TransactionalEvent end, WriteTransactionalEvent swapEvent){
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

    public LinkedList<TransactionalEvent> getGuidedPath() {
        return guidedPath;
    }

    public TransactionalEvent getEndEvent() {
        return endEvent;
    }

    public WriteTransactionalEvent getWriteEventSwap() {
        return writeEventSwap;
    }
}
