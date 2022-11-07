package benchmarks.tpcc.objects;

import database.TRUtility;

public class NewOrder implements TPCCObject{

    private int warehouseID;
    private int districtID;
    private int orderID;

    public NewOrder(int warehouseID, int districtID, int orderID) {
        this.warehouseID = warehouseID;
        this.districtID = districtID;
        this.orderID = orderID;
    }

    public NewOrder(String newOrder) {
        this(Integer.parseInt(TRUtility.getValue(newOrder, 0)),
                Integer.parseInt(TRUtility.getValue(newOrder, 1)),
                Integer.parseInt(TRUtility.getValue(newOrder, 2)));
    }

    public int getOrderID() {
        return orderID;
    }

    @Override
    public String toString() {
        return  warehouseID + ";" + districtID+ ";" + orderID;
    }

    public String getKey() {
        return  warehouseID + ":" + districtID+ ":" + orderID;
    }

}
