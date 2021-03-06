package benchmarks.shoppingCart;

import benchmarks.BenchmarkModule;
import database.TRUtility;

import java.util.ArrayList;
import java.util.HashMap;

public class ShoppingCart extends BenchmarkModule {

    public static final String STORE = "STORE:USERID=0";


    private static ShoppingCart shoppingCartInstance;

    @Override
    public HashMap<String, Class<?>[]> getAllMethods() {
        var map = new HashMap<String, Class<?>[]>();
        map.put("addItem", new Class<?>[]{String.class});
        map.put("addItemQuantity", new Class<?>[]{String.class, int.class});
        map.put("removeItem", new Class<?>[]{int.class});
        map.put("getItem",new Class<?>[]{String.class});
        map.put("getQuantity", new Class<?>[]{String.class});
        map.put("getList",new Class<?>[]{});
        map.put("changeQuantity", new Class<?>[]{String.class, int.class});
        return map;
    }

    private ShoppingCart(){
    }

    public static ShoppingCart getInstance(){
        if(shoppingCartInstance == null){
            shoppingCartInstance = new ShoppingCart();
        }
        return shoppingCartInstance;
    }

    protected HashMap<String, ShoppingItem> generateHashMap(String s){
        return TRUtility.generateHashMap(s, (t)->{
            return new ShoppingItem(t);}
        );
    }

    public void addItem(String product){
        addItemQuantity(product, 1);
    }

    public void addItemQuantity(String productStr, int q){

        if(productStr == null) return;
        var product = new ShoppingItem(productStr+";"+q);

        db.begin();
        var map = generateHashMap(db.read(STORE));
        map.put(product.getId(), product);
        db.write(STORE, map.toString());
        db.end();
    }

    public void removeItem(int productID){
        db.begin();
        var map = generateHashMap(db.read(STORE));
        if(map.containsKey(productID+"")) {
            map.remove(productID + "");
            db.write(STORE, map.toString());
        }
        db.end();

    }

    protected ShoppingItem getShoppingItem(String id){
        var map = generateHashMap(db.read(STORE));
        return map.get(id);
    }

    public Item getItem(String id){
        db.begin();
        var si = getShoppingItem(id);
        db.end();
        return si;
    }

    public Integer getQuantity(String itemID){
        db.begin();
        var si = getShoppingItem(itemID);
        db.end();
        return si == null? null : si.getQuantity();
    }

    public ArrayList<ShoppingItem> getList(){
        db.begin();
        var map = generateHashMap(db.read(STORE));
        db.end();
        return new ArrayList<>(map.values());
    }

    public void changeQuantity(String itemID, int q){
        db.begin();
        var map = generateHashMap(db.read(STORE));
        var si = map.get(itemID);
        if(si != null) {
            si.setQuantity(q);
            db.write(STORE, map.toString());
        }
        db.end();



    }

}
