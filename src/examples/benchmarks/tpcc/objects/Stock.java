package benchmarks.tpcc.objects;

import database.TRUtility;

import java.util.HashMap;

public class Stock {

    private int itemID; // PRIMARY KEY 2
    private int warehouseID; // PRIMARY KEY 1
    private int orderCnt;
    private int remoteCnt;
    private int quantity;
    private float ytd;
    private HashMap<Integer, String> dist; //from 1 to 10

    public Stock( int warehouseID, int itemID, int orderCnt,
                 int remoteCnt, int quantity, float ytd, HashMap<Integer, String> dist) {
        this.itemID = itemID;
        this.warehouseID = warehouseID;
        this.orderCnt = orderCnt;
        this.remoteCnt = remoteCnt;
        this.quantity = quantity;
        this.ytd = ytd;
        this.dist = dist;
    }

    public Stock(String stock) {
        this(Integer.parseInt(TRUtility.getValue(stock, 0)),
                Integer.parseInt(TRUtility.getValue(stock, 1)),
                Integer.parseInt(TRUtility.getValue(stock, 2)),
                Integer.parseInt(TRUtility.getValue(stock, 3)),
                Integer.parseInt(TRUtility.getValue(stock, 4)),
                Float.parseFloat(TRUtility.getValue(stock, 5)),
                null);
        dist = computeMap(TRUtility.getValue(stock, 6));
    }

    private HashMap<Integer, String> computeMap(String mapData){
        var map = TRUtility.generateHashMap(mapData, (s)->(s));
        var nmap = new HashMap<Integer, String>();
        for(var e : map.entrySet()){
            nmap.put(Integer.valueOf(e.getKey()), e.getValue());
        }
        return nmap;
    }

    @Override
    public String toString() {
        return warehouseID+ ";"+itemID+ ";" + +orderCnt+ ";"
                +remoteCnt+ ";" +quantity+ ";" +ytd+ ";" +dist.toString();
    }

    public int getWarehouseID() {
        return warehouseID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setRemoteCnt(int remoteCnt) {
        this.remoteCnt = remoteCnt;
    }

    public void setOrderCnt(int orderCnt) {
        this.orderCnt = orderCnt;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setYtd(float ytd) {
        this.ytd = ytd;
    }

    public float getYtd() {
        return ytd;
    }

    public int getOrderCnt() {
        return orderCnt;
    }

    public int getRemoteCnt() {
        return remoteCnt;
    }

    public String getDistrict(int id){
        return dist.get(id);
    }

}
