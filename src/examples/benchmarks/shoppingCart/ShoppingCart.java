package benchmarks.shoppingCart;

import benchmarks.BenchmarkModule;
import database.AbortDatabaseException;
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

    public void addItem(String product){
        addItemQuantity(product, 1);
    }

    public void addItemQuantity(String productStr, int q){


        try {

            db.begin();
            if(productStr == null) db.abort();

            var product = new ShoppingItem(productStr+";"+q);

            db.insertRow(STORE, product.getId(), product.toString());

            db.commit();

        } catch (AbortDatabaseException ignored) {
        }
    }

    public void removeItem(int productID){
        db.begin();

        db.deleteRow(STORE, productID+"");

        db.commit();

    }

    protected ShoppingItem getShoppingItem(String id){

        var item = db.readRow(STORE, id);
        return item == null ? null : new ShoppingItem(item);
    }

    public Item getItem(String id){
        db.begin();
        var si = getShoppingItem(id);
        db.commit();
        return si;
    }

    public Integer getQuantity(String itemID){
        db.begin();
        var si = getShoppingItem(itemID);
        db.commit();
        return si == null ? null : si.getQuantity();
    }

    public ArrayList<ShoppingItem> getList(){
        db.begin();
        var map = db.readAll(STORE);
        db.commit();
        var list = new ArrayList<ShoppingItem>();
        for(var s: map){
            var item = s == null ? null : new ShoppingItem(s);
            list.add(item);
        }
        return list;
    }

    public void changeQuantity(String itemID, int q){

        try {
            db.begin();

            var row = db.readRow(STORE, itemID);
            if(row == null) {
                db.abort();
            }
            var si = new ShoppingItem(row);
            si.setQuantity(q);
            db.writeRow(STORE, itemID, si.toString());

            db.commit();
        } catch (AbortDatabaseException ignored) {
        }
    }

}
