package fr.irif.events;

import java.util.LinkedList;
import java.util.List;

public class Transaction {

    protected LinkedList<TransactionalEvent> body;

    protected boolean executing;

    public Transaction(LinkedList<TransactionalEvent> body) {
        this.body = body;
        executing = false;
    }


    public Transaction(TransactionalEvent begin, TransactionalEvent end) {
       this(new LinkedList<>(List.of(begin, end)));
    }


    public boolean isEmpty(){
        return body.isEmpty();
    }

    public void removeFirst(){
        body.removeFirst();
    }

    /*public boolean isNext(TransactionalEvent e){
        return body.getFirst().getEventData().equals(e.getEventData());
    }

     */

    public TransactionalEvent getNext(){
        return body.getFirst();
    }

    public int size(){
        return body.size();
    }

    public boolean isExecuting() {
        return executing;
    }

    public void setExecuting(boolean executing) {
        //this.executing = executing;
    }
}
