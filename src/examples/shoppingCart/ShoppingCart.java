package shoppingCart;

import database.TRDatabase;
import database.TRUtility;

import java.util.ArrayList;
import java.util.HashMap;

public class ShoppingCart {

    private int userId;

    private TRDatabase db;

    private static HashMap<Integer,ShoppingCart> shoppingCartInstances = new HashMap<>();

    private ShoppingCart(int id){
        userId = id;
        db = TRDatabase.getDatabase();
    }

    public static ShoppingCart getShoppingCart(int id){
        if(!shoppingCartInstances.containsKey(id)){
            shoppingCartInstances.put(id, new ShoppingCart(id));
        }
        return shoppingCartInstances.get(id);
    }

    protected HashMap<String, ShoppingItem> generateHashMap(String s){
        return TRUtility.generateHashMap(s, (t)->{
            return new ShoppingItem(t);}
        );
    }

    public void addItem(Item product){
        addItemQuantity(product, 1);
    }

    public void addItemQuantity(Item product, int q){
        var map = generateHashMap(db.read("store["+userId+"]"));
        map.put(product.getId(), new ShoppingItem(product.toString()+";"+q));
        db.write("store["+userId+"]", map.toString());
    }

    public void removeItem(Item product){
        var map = generateHashMap(db.read("store["+userId+"]"));
        map.remove(product.getId());
        db.write("store["+userId+"]", map.toString());

    }

    protected ShoppingItem getShoppingItem(String id){
        var map = generateHashMap(db.read("store["+userId+"]"));
        return map.get(id);
    }

    public Item getItem(String id){
        return getShoppingItem(id);
    }

    public int getQuantity(Item item){
        var si = getShoppingItem(item.getId());
        return si == null? 0: si.getQuantity();
    }

    public ArrayList<ShoppingItem> getList(){
        var map = generateHashMap(db.read("store["+userId+"]"));
        return new ArrayList<>(map.values());
    }

    public void changeQuantity(Item i, int q){
        var map = generateHashMap(db.read("store["+userId+"]"));
        var si = map.get(i.getId());
        if(si != null) {
            si.setQuantity(q);
        }
        db.write("store[" + userId + "]", map.toString());



    }


    public void begin(){
        db.begin();
    }

    public void end(){
        db.end();
    }

}
