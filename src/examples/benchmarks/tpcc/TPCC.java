package benchmarks.tpcc;

import benchmarks.BenchmarkModule;
import benchmarks.tpcc.procedures.*;

import java.util.HashMap;

public class TPCC extends BenchmarkModule {

    private static TPCC TPCCInstance;

    public static final String DISTRICT = "DISTRICT";
    public static final String ORDERLINE = "ORDERLINE";
    public static final String STOCK = "STOCK";
    public static final String CUSTOMER = "CUSTOMER";

    public static final String OPENORDER = "OPENORDER";
    public static final String HISTORY = "HISTORY";
    public static final String WAREHOUSE = "WAREHOUSE";
    public static final String ITEM = "ITEM"; //???
    public static final String NEWORDER = "NEWORDER"; //????



    private TPCC() {

    }

    public static TPCC getInstance(){
        if(TPCCInstance == null){
            TPCCInstance = new TPCC();
        }
        return TPCCInstance;
    }

    @Override
    public HashMap<String, Class<?>[]> getAllMethods() {
        var map = new HashMap<String, Class<?>[]>();
        map.put("stockLevel", new Class<?>[]{int.class, int.class, int.class});
        map.put("orderStatus", new Class<?>[]{int.class, int.class, int.class, String.class, boolean.class});
        map.put("payment", new Class<?>[]{int.class, int.class, int.class, String.class, boolean.class, float.class});
        map.put("createNewOrder",new Class<?>[]{
                int.class, int.class, int.class, int.class, int.class,
                int[].class, int[].class, int[].class
        });
        map.put("delivery", new Class<?>[]{int.class,int.class,int.class});
        return map;
    }
    public Integer stockLevel(int warehouseID, int districtID, int threshold){
        return new StockLevel(db).stockLevel(warehouseID, districtID, threshold);
    }

    public String orderStatus(int warehouseID, int districtID, int customerID,
                                     String customerName, boolean customerIDSearch) {
        return new OrderStatus(db).orderStatus( warehouseID, districtID, customerID, customerName, customerIDSearch);
    }

    public void payment(int warehouseID, int districtID, int customerID,
                        String customerName, boolean customerIDSearch, float paymentAmount){
        new Payment(db).payment(warehouseID, districtID, customerID, customerName, customerIDSearch, paymentAmount);
    }

    public void createNewOrder(int warehouseId, int districtID, int customerID,
                         int orderLineCnt, int allLocal, int[] itemIDs,
                         int[] supplierWarehouseIDs, int[] orderQuantities){
        new CreateNewOrder(db).
                createNewOrder(warehouseId, districtID, customerID, orderLineCnt,
                        allLocal, itemIDs, supplierWarehouseIDs, orderQuantities);
    }

    public void delivery(int numDistricts, int warehouseID, int carrierID){
        new Delivery(db).delivery(numDistricts, warehouseID, carrierID);
    }


}
