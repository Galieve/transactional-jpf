package fr.irif.events;

import java.util.*;

public class Transaction implements  Iterable<TransactionalEvent>{

    protected LinkedList<TransactionalEvent> body;

    protected HashMap<String, LinkedList<WriteTransactionalEvent>> listOfWriteEvents;

    protected boolean executing;

    public Transaction(LinkedList<TransactionalEvent> body) {
        this.body = body;
        executing = false;
        listOfWriteEvents = constructListWE(body);

    }

    protected HashMap<String, LinkedList<WriteTransactionalEvent>> constructListWE(LinkedList<TransactionalEvent> body){
        var list = new HashMap<String, LinkedList<WriteTransactionalEvent>>();
        for(var e: body){
            if(e.getType() == TransactionalEvent.Type.WRITE){
                list.putIfAbsent(e.getVariable(), new LinkedList<>());
                list.get(e.getVariable()).add((WriteTransactionalEvent) e);
            }
        }
        return list;
    }

    public Transaction(TransactionalEvent begin, TransactionalEvent end) {
       this(new LinkedList<>(List.of(begin, end)));
    }

    public boolean isEmpty(){
        return body.isEmpty();
    }

    public void removeFirst(){
        var e = body.getFirst();
        body.removeFirst();
        if(e.getType() == TransactionalEvent.Type.WRITE){
            listOfWriteEvents.get(e.getVariable()).removeFirst();
        }
    }

    public void addEvent(TransactionalEvent e){
        body.add(e);
        if(e.getType() == TransactionalEvent.Type.WRITE){
            listOfWriteEvents.putIfAbsent(e.getVariable(), new LinkedList<>());
            listOfWriteEvents.get(e.getVariable()).add((WriteTransactionalEvent) e);
        }
    }

    public TransactionalEvent getNext(){
        return body.getFirst();
    }

    public TransactionalEvent getFirst(){
        return body.getFirst();
    }

    public TransactionalEvent getLast(){
        return body.getLast();
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

    public ListIterator<TransactionalEvent> getListIterator(int id){
        return body.listIterator(id);
    }

    public void removeLast(){
        var e = body.getLast();
        body.removeLast();
        if(e.getType() == TransactionalEvent.Type.WRITE){
            listOfWriteEvents.get(e.getVariable()).removeLast();
        }
    }

    public WriteTransactionalEvent getLastWriteEvent(String variable){
        var writeEvents = listOfWriteEvents.get(variable);
        return writeEvents.isEmpty() ? null : writeEvents.getLast();
    }

    @Override
    public Iterator<TransactionalEvent> iterator() {
        return body.iterator();
    }
}
