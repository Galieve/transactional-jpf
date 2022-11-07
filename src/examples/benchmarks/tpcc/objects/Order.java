package benchmarks.tpcc.objects;

import database.TRUtility;

public class Order implements TPCCObject{

    //warehouseID+":"+districtID+":"+customerID
    private int ID;
    private int warehouseID;
    private int districtID;
    private int customerID;
    private Integer carrierID;
    private int orderLineCnt;
    private int allLocal;
    private long entryDistrict;

    public Order(int warehouseID, int districtID,int ID,  int customerID,
                 Integer carrierID, int orderLineCnt, int allLocal, long entryDistrict) {
        this.ID = ID;
        this.warehouseID = warehouseID;
        this.districtID = districtID;
        this.customerID = customerID;
        this.carrierID = carrierID;
        this.orderLineCnt = orderLineCnt;
        this.allLocal = allLocal;
        this.entryDistrict = entryDistrict;
    }

    public Order(int warehouseID, int districtID, int ID,
                 int customerID, int orderLineCnt, int allLocal, long entryDistrict) {
        this(warehouseID,districtID, ID,customerID, 0, orderLineCnt, allLocal, entryDistrict);

    }

    public Order(String order) {
        this(Integer.parseInt(TRUtility.getValue(order, 0)),
                Integer.parseInt(TRUtility.getValue(order, 1)),
                Integer.parseInt(TRUtility.getValue(order, 2)),
                Integer.parseInt(TRUtility.getValue(order, 3)),
                Integer.parseInt(TRUtility.getValue(order, 4)),
                Integer.parseInt(TRUtility.getValue(order, 5)),
                Integer.parseInt(TRUtility.getValue(order, 6)),
                Long.parseLong(TRUtility.getValue(order, 7)));
    }

    @Override
    public String toString() {
        return  warehouseID + ";" + districtID + ";" +ID + ";" +  customerID
                + ";" + carrierID + ";" + orderLineCnt + ";" + allLocal + ";" + entryDistrict;
    }

    public String getKey() {
        return  warehouseID + ":" + districtID + ":" +ID;
    }

    public int getID() {
        return ID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCarrierID(Integer carrierID) {
        this.carrierID = carrierID;
    }
}
