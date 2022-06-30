package benchmarks.tpcc.objects;

import database.TRUtility;

public class OrderLine {

    private int warehouseID;
    private int districtID;
    private int orderID;
    private int number;

    private int itemID;
    private int supplyWarehouseID;
    private int quantity;
    private long deliveryDate;
    private float amount;
    private String districtInfo;

    public OrderLine(int warehouseID, int districtID, int orderID, int number, int itemID,
                     int supplyWarehouseID, int quantity, long deliveryDate,
                     float amount, String districtInfo) {
        this.warehouseID = warehouseID;
        this.districtID = districtID;
        this.orderID = orderID;
        this.number = number;
        this.itemID = itemID;
        this.supplyWarehouseID = supplyWarehouseID;
        this.quantity = quantity;
        this.amount = amount;
        this.districtInfo = districtInfo;
    }

    public OrderLine(String ol) {
        this(Integer.parseInt(TRUtility.getValue(ol, 0)),
                Integer.parseInt(TRUtility.getValue(ol, 1)),
                Integer.parseInt(TRUtility.getValue(ol, 2)),
                Integer.parseInt(TRUtility.getValue(ol, 3)),
                Integer.parseInt(TRUtility.getValue(ol, 4)),
                Integer.parseInt(TRUtility.getValue(ol, 5)),
                Integer.parseInt(TRUtility.getValue(ol, 6)),
                Long.parseLong(TRUtility.getValue(ol,7)),
                Float.parseFloat(TRUtility.getValue(ol, 8)),
                TRUtility.getValue(ol, 9));
    }

    public OrderLine(int warehouseID, int districtID, int orderID, int number, int itemID,
                     int supplyWarehouseID, int quantity, Float amount, String district) {
        this(warehouseID,districtID,orderID,number, itemID, supplyWarehouseID,
                quantity, System.currentTimeMillis(),amount, district);
    }

    @Override
    public String toString() {
        return warehouseID + ";" + districtID + ";" + orderID + ";" + number
                + ";" + itemID + ";" + supplyWarehouseID + ";" + quantity
                + ";" + deliveryDate + ";" + amount + ";" + districtInfo;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getItemID() {
        return itemID;
    }

    public int getSupplyWarehouseID() {
        return supplyWarehouseID;
    }

    public int getWarehouseID() {
        return warehouseID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setDeliveryDate(long deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public float getAmount() {
        return amount;
    }
}
