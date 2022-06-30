package benchmarks.tpcc.procedures;

import benchmarks.tpcc.TPCC;
import benchmarks.tpcc.TPCCUtility;
import benchmarks.tpcc.objects.*;
import database.TRDatabase;

import java.util.ArrayList;
import java.util.function.Function;

public class CreateNewOrder extends BasicTPCCProcedure{


    public CreateNewOrder(TRDatabase db) {
        super(db);
    }

    public void createNewOrder(int warehouseID, int districtID, int customerID,
                                     int orderLineCnt, int allLocal, int[] itemIDs,
                                     int[] supplierWarehouseIDs, int[] orderQuantities){

        db.begin();

        var c = getCustomerById(warehouseID, districtID, customerID);
        var w = getWarehouse(warehouseID);

        var d = getDistrict(warehouseID, districtID);

        if(d != null) {

            var orderID = d.getNextOrderID();
            updateDistrict(warehouseID, districtID);

            insertOpenOrder(warehouseID, districtID, customerID,
                    orderLineCnt, allLocal, orderID);

            insertNewOrder(warehouseID, districtID, d.getNextOrderID());

            for (int i = 0; i < orderLineCnt; ++i) {
                int supplyWarehouseID = supplierWarehouseIDs[i];
                int itemID = itemIDs[i];
                int quantity = orderQuantities[i];
                var price = getItemPrice(itemID);
                var amount = price == null ? null : quantity * price;
                var s = getStock(supplyWarehouseID, itemID, quantity);

                if (amount != null && s != null) {
                    var ol = new OrderLine(warehouseID, districtID, orderID, i, itemID,
                            supplyWarehouseID, quantity, amount, s.getDistrict(districtID));

                    insertOrderLine(warehouseID, districtID, ol);
                    updateStock(s, ol);

                }


            }
        }

        db.end();



    }

    private void insertNewOrder(int warehouseID, int districtID, int nextOrderID) {

        var no = new NewOrder(warehouseID, districtID, nextOrderID);

        var orderTable = TPCCUtility.readNewOrder(db.read(TPCC.NEWORDER));
        orderTable.putIfAbsent(warehouseID+":"+districtID, new ArrayList<>());
        orderTable.get(warehouseID+":"+districtID).add(no);
        db.write(TPCC.NEWORDER, orderTable.toString());
    }

    private void updateDistrict(int warehouseID, int districtID){
        Function<District, District> f = (District d)->{
            d.setNextOrderID(d.getNextOrderID()+1);
            return d;
        };
        updateDistrict(warehouseID, districtID, f);
    }

    private void insertOpenOrder(int warehouseID, int districtID, int customerID,
                                 int orderLineCnt, int allLocal, int orderId){

        var o = new Order(warehouseID, districtID,orderId,
                customerID, orderLineCnt,allLocal, System.currentTimeMillis());

        var orderTable = TPCCUtility.readOpenOrder(db.read(TPCC.OPENORDER));
        orderTable.putIfAbsent(warehouseID+":"+districtID, new ArrayList<>());
        orderTable.get(warehouseID+":"+districtID).add(o);
        db.write(TPCC.OPENORDER, orderTable.toString());
    }


    private Float getItemPrice(int itemID){

        var itemTable = TPCCUtility.readItem(db.read(TPCC.ITEM));
        var i = itemTable.get(itemID+"");
        return i == null ? null : i.getPrice();
    }

    private void insertOrderLine(int warehouseID, int districtID, OrderLine orderLine){
        var orderLineTable = TPCCUtility.readOrderLine(db.read(TPCC.ORDERLINE));
        orderLineTable.putIfAbsent(warehouseID + ":" + districtID, new ArrayList<>());
        orderLineTable.get(warehouseID + ":" + districtID).add(orderLine);
        db.write(TPCC.ORDERLINE, orderLineTable.toString());

    }

    private Stock getStock(int warehouseID, int itemID, int quantity){
        Stock s = null;

        var stockTable = TPCCUtility.readStock(db.read(TPCC.STOCK));
        s = stockTable.get(warehouseID + ":" + itemID);

        if(s == null) return null;

        var sq = s.getQuantity();
        if (sq - quantity >= 10) {
            s.setQuantity(sq - quantity);
        } else {
            s.setQuantity(sq + -quantity + 91);
        }


        return s;

    }

    private void updateStock(Stock s, OrderLine ol){

        var stockTable = TPCCUtility.readStock(db.read(TPCC.STOCK));
        int remoteCntIncrement = ol.getSupplyWarehouseID() == ol.getWarehouseID() ? 0 : 1;
        s.setQuantity(ol.getQuantity());
        s.setYtd(s.getYtd() + ol.getQuantity());
        s.setOrderCnt(s.getOrderCnt() + 1);
        s.setRemoteCnt(s.getRemoteCnt() + remoteCntIncrement);
        stockTable.put(ol.getWarehouseID() + ":" + ol.getItemID(), s);
        db.write(TPCC.STOCK, stockTable.toString());

    }

}
