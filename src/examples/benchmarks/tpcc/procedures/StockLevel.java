package benchmarks.tpcc.procedures;

import benchmarks.tpcc.TPCC;
import benchmarks.tpcc.TPCCUtility;
import database.AbortDatabaseException;
import database.TRDatabase;

import java.util.ArrayList;


public class StockLevel extends BasicTPCCProcedure{

    public StockLevel(TRDatabase db) {
        super(db);
    }

    //districtID and threshold are random!!!
    public Integer stockLevel( int warehouseID, int districtID, int threshold){

        try {
            db.begin();
            Integer orderID = getOrderID(warehouseID, districtID);
            var stockCount = getStockCount(warehouseID, districtID, threshold, orderID);
            db.commit();
            return stockCount;
        } catch (AbortDatabaseException ignored) {

        }
        return -1;

    }

    private Integer getStockCount(int warehouseID, int districtID,
                                  int threshold, int orderID) throws AbortDatabaseException {

        int count = 0;
        var orderLineTable = TPCCUtility.readOrderLine(db.read(TPCC.ORDERLINE));
        var stockTable = TPCCUtility.readStock(db.read(TPCC.STOCK));


        orderLineTable.putIfAbsent(warehouseID+":"+districtID, new ArrayList<>());

        System.out.println("IRIF");
        System.out.println(stockTable);
        System.out.println(orderLineTable);
        System.out.println(warehouseID+":"+districtID);


        for(var ol: orderLineTable.get(warehouseID+":"+districtID)){
            var st = stockTable.get(warehouseID+":"+ ol.getItemID());
            System.out.println(st);
            System.out.println(ol);
            System.out.println(warehouseID+":"+ol.getItemID());
            System.out.println(orderID);
            System.out.println(threshold);
            System.out.println("----");

            if(st != null && ol.getOrderID() < orderID && ol.getOrderID() >= orderID - 20
                    && st.getQuantity() < threshold){
                ++count;
            }

        }
        if(count == 0) db.abort();

        return count;




    }

    private Integer getOrderID(int warehouseID, int districtID) throws AbortDatabaseException {
        var districtTable = TPCCUtility.readDistrict(db.read(TPCC.DISTRICT));
        var d = districtTable.get(warehouseID+":"+districtID);
        if(d == null) db.abort();
        return d.getNextOrderID();
    }
}
