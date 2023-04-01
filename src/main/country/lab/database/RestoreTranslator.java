package country.lab.database;

import country.lab.events.EventData;
import country.lab.events.TransactionalEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class RestoreTranslator extends TransactionTranslator{

    protected HashMap<EventData, Integer> beginToIndex;

    protected HashMap<Integer, EventData> indexToBegin;



    public RestoreTranslator(HashMap<Integer, ArrayList<Integer>> sessionOrder) {
        super(sessionOrder);

        beginToIndex = new HashMap<>();

        indexToBegin = new HashMap<>();
    }

    @Override
    public void putID(TransactionalEvent e, int id) {
        super.putID(e, id);
        beginToIndex.put(e.getEventData(), id);
        indexToBegin.put(id, e.getEventData());

    }

    public boolean containsID(TransactionalEvent e){
        return beginToIndex.containsKey(e.getEventData());
    }

    public Integer translate(TransactionalEvent e){
        return beginToIndex.get(e.getEventData());
    }

    public EventData translate(Integer id){
        return indexToBegin.get(id);
    }
}
