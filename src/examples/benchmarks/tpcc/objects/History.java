package benchmarks.tpcc.objects;

import database.TRUtility;

public class History implements TPCCObject {
    private int customerID;
    private int customerDistrictID;
    private int customerWarehouseID;
    private int districtID;
    private int warehouseID;
    private long date;
    private float amount;
    private String data;

    public History(int customerID, int customerDistrictID,
                   int customerWarehouseID, int districtID,
                   int warehouseID, long date, float amount,
                   String data) {
        this.customerID = customerID;
        this.customerDistrictID = customerDistrictID;
        this.customerWarehouseID = customerWarehouseID;
        this.districtID = districtID;
        this.warehouseID = warehouseID;
        this.date = date;
        this.amount = amount;
        this.data = data;
    }

    public History(String history) {
        this(Integer.parseInt(TRUtility.getValue(history, 0)),
                Integer.parseInt(TRUtility.getValue(history, 1)),
                Integer.parseInt(TRUtility.getValue(history, 2)),
                Integer.parseInt(TRUtility.getValue(history, 3)),
                Integer.parseInt(TRUtility.getValue(history, 4)),
                Long.parseLong(TRUtility.getValue(history, 5)),
                Float.parseFloat(TRUtility.getValue(history, 6)),
                TRUtility.getValue(history, 7));
    }

    @Override
    public String toString() {
        return  customerID + ";" + customerDistrictID + ";" + customerWarehouseID
                + ";" + districtID + ";" + warehouseID + ";" + date + ";"
                + amount + ";" + data;
    }

    public int getCustomerID() {
        return customerID;
    }

    public int getCustomerDistrictID() {
        return customerDistrictID;
    }

    public int getCustomerWarehouseID() {
        return customerWarehouseID;
    }

    public int getDistrictID() {
        return districtID;
    }

    public int getWarehouseID() {
        return warehouseID;
    }

    @Override
    public String getKey() {
        return warehouseID+":"+ districtID;
    }
}
