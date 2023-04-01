package country.lab.database;

import com.rits.cloning.Cloner;
import country.lab.events.TransactionalEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class TransactionTranslator {

    protected HashMap<Integer, Integer> translator;

    protected HashMap<Integer, ArrayList<Integer>> so;

    public TransactionTranslator(HashMap<Integer, ArrayList<Integer>> sessionOrder){
        translator = new HashMap<>();
        so = new Cloner().deepClone(sessionOrder);
    }

    public Integer getID(TransactionalEvent e){
        return translator.get(e.getTransactionId());
    }

    public ArrayList<Integer> getSO(TransactionalEvent e){
        return so.get(e.getThreadId());
    }

    public void putID(TransactionalEvent e, int id){
        translator.put(e.getTransactionId(), id);
    }

    public void removeLastSO(int threadID){
        so.get(threadID).remove(so.get(threadID).size() - 1);
    }

    public void putSO(TransactionalEvent e){
        so.get(e.getThreadId()).add(getID(e));
    }

    public boolean containsID(Integer id){
        return translator.containsKey(id);
    }
}

