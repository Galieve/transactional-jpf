package benchmarks.tpcc.procedures;

import benchmarks.tpcc.TPCC;
import benchmarks.tpcc.TPCCUtility;
import database.AbortDatabaseException;
import database.TRDatabase;

public class Delivery extends BasicTPCCProcedure{

    public Delivery(TRDatabase db) {
        super(db);
    }

    public void delivery(int numDistricts, int warehouseID, int carrierID){

        try {
            db.begin();

            for(int districtID = 0; districtID < numDistricts; ++districtID){

                var orderID = getOrderID(warehouseID, districtID);
                System.out.println("IRIF: "+ orderID);
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


            db.commit();
        } catch (AbortDatabaseException ignored) {
        }


    }

    private Integer getOrderID(int warehouseID, int districtID){

        var newOrderTable = TPCCUtility.readNewOrder(db.read(TPCC.NEWORDER));
        var nol = newOrderTable.get(warehouseID+":"+districtID);
        if(nol == null) return null;
        nol.sort((a, b)->(int) (b.getOrderID() - a.getOrderID()));
        return nol.isEmpty() ? null : nol.get(0).getOrderID();
    }

    private void deleteOrder(int warehouseID, int districtID, int orderID) throws AbortDatabaseException {
        var newOrderTable = TPCCUtility.readNewOrder(db.read(TPCC.NEWORDER));
        var nol = newOrderTable.get(warehouseID + ":" + districtID);
        if(nol == null) db.abort();

        nol.removeIf((no) -> (no.getOrderID() == orderID));
        if(nol.isEmpty()) newOrderTable.remove(warehouseID + ":" + districtID);

        db.write(TPCC.NEWORDER, newOrderTable.toString());
    }

    private Integer getCustomerID(int warehouseID, int districtID, int orderID) throws AbortDatabaseException {
        var orderTable = TPCCUtility.readOpenOrder(db.read(TPCC.OPENORDER));
        var ol = orderTable.get(warehouseID+":"+districtID);
        System.out.println("IRIF");
        System.out.println(warehouseID+":"+districtID);
        System.out.println(orderID);
        System.out.println(orderTable);
        System.out.println(ol);
        if(ol == null) db.abort(); //TODO: To be fixed when mutex per row.
        for(var o: ol){
            if(o.getID() == orderID) {
                return o.getCustomerID();
            }
        }
        db.abort();
        return null;
    }

    private void updateCarrierID(int warehouseID, int carrierID, int districtID, int orderID) throws AbortDatabaseException {
        var orderTable = TPCCUtility.readOpenOrder(db.read(TPCC.OPENORDER));
        var ol = orderTable.get(warehouseID + ":" + districtID);

        if(ol == null) db.abort();

        for (var o : ol) {
            if (o.getID() == orderID) {
                o.setCarrierID(carrierID);
            }
        }

        db.write(TPCC.OPENORDER, orderTable.toString());


    }

    private void updateDeliveryDate(int warehouseID, int districtID, int orderID) throws AbortDatabaseException {

        var time = System.currentTimeMillis();

        var orderLineTable = TPCCUtility.readOrderLine(db.read(TPCC.ORDERLINE));

        var oll = orderLineTable.get(warehouseID + ":" + districtID);

        if(oll == null) db.abort();

        for (var o : oll) {
            if (o.getOrderID() == orderID) {
                o.setDeliveryDate(time); //TODO: fix when mutex per row.
            }
        }
        db.write(TPCC.ORDERLINE, orderLineTable.toString());
    }

    private float getOrderLineTotal(int warehouseID, int districtID, int orderID) throws AbortDatabaseException {

        var amount = 0;

        var orderLineTable = TPCCUtility.readOrderLine(db.read(TPCC.ORDERLINE));

        var oll = orderLineTable.get(warehouseID + ":" + districtID);

        if(oll == null) db.abort();

        for (var o : oll) {
            if (o.getOrderID() == orderID) {
                amount += o.getAmount();
            }
        }

        return amount;
    }

    private void updateBalanceAndDelivery(int warehouseID, int districtID, int customerID, float orderLineTotal)
            throws AbortDatabaseException {
        var customerTable = TPCCUtility.readCustomer(db.read(TPCC.CUSTOMER));

        var listCustomer = customerTable.get(warehouseID + ":" + districtID);

        if(listCustomer == null) db.abort();
        for (var cu : listCustomer) {
            if (cu.getID() == customerID) {
                cu.setBalance(cu.getBalance() + orderLineTotal);
                cu.setDeliveryCnt(cu.getDeliveryCnt() + 1);
            }
        }
        db.write(TPCC.CUSTOMER, customerTable.toString());

    }

}
