package benchmarks.tpcc.objects;

import database.TRUtility;

public class Customer implements TPCCObject{

    private int ID;
    private int districtID;
    private int warehouseID;
    private int paymentCnt;
    private int deliveryCnt;
    private float balance;
    private float ytdPayment;
    private String credit;
    private String last;
    private String first;
    private String data;

    public Customer( int warehouseId, int districtID, int ID,  int paymentCnt,
                    int deliveryCnt, float balance, float ytdPayment,
                    String credit, String last, String first, String data) {
        this.ID = ID;
        this.districtID = districtID;
        this.warehouseID = warehouseId;
        this.paymentCnt = paymentCnt;
        this.deliveryCnt = deliveryCnt;
        this.balance = balance;
        this.ytdPayment = ytdPayment;
        this.credit = credit;
        this.last = last;
        this.first = first;
        this.data = data;
    }

    public Customer(String customer) {
        this(Integer.parseInt(TRUtility.getValue(customer, 0)),
                Integer.parseInt(TRUtility.getValue(customer, 1)),
                Integer.parseInt(TRUtility.getValue(customer, 2)),
                Integer.parseInt(TRUtility.getValue(customer, 3)),
                Integer.parseInt(TRUtility.getValue(customer, 4)),
                Float.parseFloat(TRUtility.getValue(customer, 5)),
                Float.parseFloat(TRUtility.getValue(customer, 6)),
                TRUtility.getValue(customer, 7),
                TRUtility.getValue(customer, 8),
                TRUtility.getValue(customer, 9),
                TRUtility.getValue(customer, 10));
    }

    @Override
    public String toString() {
        return    warehouseID + ";" + districtID+ ';' +ID+ ';' + paymentCnt
                + ';' + deliveryCnt + ';' + balance + ';' + ytdPayment
                + ';' + credit + ';' + last + ';' + first + ';' + data;
    }

    public String getKey(){
        return  warehouseID + ":" + districtID+ ':' +ID;
    }
    public String getLast() {
        return last;
    }

    public String getFirst() {
        return first;
    }

    public int getID() {
        return ID;
    }

    public String getCredit() {
        return credit;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getDistrictID() {
        return districtID;
    }

    public int getWarehouseID() {
        return warehouseID;
    }

    public void setPaymentCnt(int paymentCnt) {
        this.paymentCnt = paymentCnt;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public void setYtdPayment(float ytdPayment) {
        this.ytdPayment = ytdPayment;
    }

    public int getPaymentCnt() {
        return paymentCnt;
    }

    public float getBalance() {
        return balance;
    }

    public float getYtdPayment() {
        return ytdPayment;
    }

    public int getDeliveryCnt() {
        return deliveryCnt;
    }

    public void setDeliveryCnt(int deliveryCnt) {
        this.deliveryCnt = deliveryCnt;
    }
}
