package benchmarks.tpcc.procedures;

import benchmarks.tpcc.TPCC;
import benchmarks.tpcc.TPCCUtility;
import database.TRDatabase;

public class Delivery extends BasicTPCCProcedure{

    public Delivery(TRDatabase db) {
        super(db);
    }

    public void delivery(int numDistricts, int warehouseID, int carrierID){

        db.begin();

        for(int districtID = 0; districtID < numDistricts; ++districtID){

            var orderID = getOrderID(warehouseID, districtID);
            if(orderID != null) {
                deleteOrder(warehouseID, districtID, orderID);
                var customerID = getCustomerID(warehouseID, districtID, orderID);
                updateCarrierID(warehouseID, carrierID, districtID, orderID);
                updateDeliveryDate(warehouseID, districtID, orderID);
                var orderLineTotal = getOrderLineTotal(warehouseID, districtID, orderID);

                if(customerID != null)
                    updateBalanceAndDelivery(warehouseID, districtID, customerID, orderLineTotal);
            }
        }


        db.end();


    }

    private Integer getOrderID(int warehouseID, int districtID){

        var newOrderTable = TPCCUtility.readNewOrder(db.read(TPCC.NEWORDER));
        var nol = newOrderTable.get(warehouseID+":"+districtID);
        if(nol == null) return null;
        nol.sort((a, b)->(int) (b.getOrderID() - a.getOrderID()));
        return nol.isEmpty() ? null : nol.get(0).getOrderID();
    }

    private void deleteOrder(int warehouseID, int districtID, int orderID){
        var newOrderTable = TPCCUtility.readNewOrder(db.read(TPCC.NEWORDER));
        var nol = newOrderTable.get(warehouseID + ":" + districtID);
        if(nol == null) return;

        nol.removeIf((no) -> (no.getOrderID() == orderID));
        if(nol.isEmpty()) newOrderTable.remove(warehouseID + ":" + districtID);

        db.write(TPCC.NEWORDER, newOrderTable.toString());
    }

    private Integer getCustomerID(int warehouseID, int districtID, int orderID){
        Integer ret = null;
        var orderTable = TPCCUtility.readOpenOrder(db.read(TPCC.OPENORDER));
        var ol = orderTable.get(warehouseID+":"+districtID);

        if(ol == null) return null;
        for(var o: ol){
            if(o.getID() == orderID) {
                ret = o.getCustomerID();
                break;
            }
        }
        return ret;
    }

    private void updateCarrierID(int warehouseID, int carrierID, int districtID, int orderID){
        var orderTable = TPCCUtility.readOpenOrder(db.read(TPCC.OPENORDER));
        var ol = orderTable.get(warehouseID + ":" + districtID);

        if(ol != null) {
            for (var o : ol) {
                if (o.getID() == orderID) {
                    o.setCarrierID(carrierID);
                }
            }

            db.write(TPCC.OPENORDER, orderTable.toString());
        }

    }

    private void updateDeliveryDate(int warehouseID, int districtID, int orderID){

        var time = System.currentTimeMillis();

        var orderLineTable = TPCCUtility.readOrderLine(db.read(TPCC.ORDERLINE));

        var oll = orderLineTable.get(warehouseID + ":" + districtID);
        if(oll != null) {
            for (var o : oll) {
                if (o.getOrderID() == orderID) {
                    o.setDeliveryDate(time);
                }
            }
            db.write(TPCC.ORDERLINE, orderLineTable.toString());
        }
    }

    private float getOrderLineTotal(int warehouseID, int districtID, int orderID){

        var amount = 0;

        var orderLineTable = TPCCUtility.readOrderLine(db.read(TPCC.ORDERLINE));

        var oll = orderLineTable.get(warehouseID + ":" + districtID);

        if(oll != null) {
            for (var o : oll) {
                if (o.getOrderID() == orderID) {
                    amount += o.getAmount();
                }
            }
        }
        return amount;
    }

    private void updateBalanceAndDelivery(int warehouseID, int districtID, int customerID, float orderLineTotal){
        var customerTable = TPCCUtility.readCustomer(db.read(TPCC.CUSTOMER));

        var listCustomer = customerTable.get(warehouseID + ":" + districtID);
        if(listCustomer != null) {
            for (var cu : listCustomer) {
                if (cu.getID() == customerID) {
                    cu.setBalance(cu.getBalance() + orderLineTotal);
                    cu.setDeliveryCnt(cu.getDeliveryCnt() + 1);
                }
            }
            db.write(TPCC.CUSTOMER, customerTable.toString());
        }
    }

}
