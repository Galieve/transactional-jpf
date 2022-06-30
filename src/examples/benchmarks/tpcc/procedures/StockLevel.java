package benchmarks.tpcc.procedures;

import benchmarks.tpcc.TPCC;
import benchmarks.tpcc.TPCCUtility;
import database.TRDatabase;

import java.util.ArrayList;


public class StockLevel extends BasicTPCCProcedure{

    public StockLevel(TRDatabase db) {
        super(db);
    }

    //districtID and threshold are random!!!
    public Integer stockLevel( int warehouseID, int districtID, int threshold){

        db.begin();
        var orderID = getOrderID(warehouseID, districtID);
        var stockCount = 0;
        if(orderID != null) {
            stockCount = getStockCount(warehouseID, districtID, threshold, orderID);
        }
        db.end();
        return stockCount;
    }

    private Integer getStockCount(int warehouseID, int districtID,
                                  int threshold, int orderID){

        int count = 0;
        var orderLineTable = TPCCUtility.readOrderLine(db.read(TPCC.ORDERLINE));
        var stockTable = TPCCUtility.readStock(db.read(TPCC.STOCK));

        orderLineTable.putIfAbsent(warehouseID+":"+districtID, new ArrayList<>());

        for(var ol: orderLineTable.get(warehouseID+":"+districtID)){
            var st = stockTable.get(warehouseID+":"+ ol.getItemID());
            if(st != null && ol.getOrderID() < orderID && ol.getOrderID() >= orderID - 20
                    && st.getQuantity() < threshold){
                ++count;
            }

        }

        return count;




    }

    private Integer getOrderID(int warehouseID, int districtID){
        var districtTable = TPCCUtility.readDistrict(db.read(TPCC.DISTRICT));
        var d = districtTable.get(warehouseID+":"+districtID);
        return d == null ? null : d.getNextOrderID();
    }
}
