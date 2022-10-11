package benchmarks.tpcc.procedures;

import benchmarks.tpcc.TPCC;
import benchmarks.tpcc.TPCCUtility;
import benchmarks.tpcc.objects.Customer;
import benchmarks.tpcc.objects.District;
import benchmarks.tpcc.objects.History;
import benchmarks.tpcc.objects.Warehouse;
import database.AbortDatabaseException;
import database.TRDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class Payment extends BasicTPCCProcedure{

    public Payment(TRDatabase db){
        super(db);
    }

    public void payment(int warehouseID, int districtID, int customerID,
                               String customerName, boolean customerIDSearch, float paymentAmount){

        try {
            db.begin();

            updateWarehouse(warehouseID, paymentAmount);


            var w = getWarehouse(warehouseID);
            updateDistrictPayment(warehouseID, districtID, paymentAmount);

            var d = getDistrict(warehouseID, districtID);

            var c = getCustomerAndPay(
                    warehouseID, districtID, customerID, customerName, customerIDSearch, paymentAmount);

            if(c.getCredit().equals("BC")) {

                var cData = getData(warehouseID, districtID, c, paymentAmount);
                if (cData != null) {
                    c.setData(cData);
                }

                //BC
                updateBalanceData(c);
            }
            else {
                //not BC (that is not equal to !bc)
                updateBalanceData(c);
            }
            insertHistory(w, d, c, paymentAmount);
            db.commit();
        } catch (AbortDatabaseException ignored) {
        }

    }

    private Customer getCustomerAndPay(int warehouseID, int districtID,
                                              int customerID, String customerName,
                                              boolean customerIDSearch, float payment) throws AbortDatabaseException {
        var c = getCustomer(warehouseID, districtID, customerID, customerName, customerIDSearch);

        if(c == null) db.abort();
        c.setBalance(c.getBalance() - payment);
        c.setYtdPayment(c.getYtdPayment() + payment);
        c.setPaymentCnt(c.getPaymentCnt()+1);
        return c;
    }

    private String getData( int warehouseID, int districtID, Customer c, float paymentAmount){

        var cus = getCustomerById(c);

        if(cus == null || c == null) return null;
        var cData = c.getID() + " " + c.getDistrictID() + " " + c.getWarehouseID() + " " +
                districtID + " " + warehouseID + " " + paymentAmount + " | " + cus.getData();
        if (cData.length() > 500) {
            cData = cData.substring(0, 500);
        }
        return cData;


    }

    private void updateBalanceData(Customer c){

        if(c != null) {
            var customerTable = TPCCUtility.readCustomer(db.read(TPCC.CUSTOMER));

            var customerList = customerTable.get(c.getWarehouseID() + ":" + c.getDistrictID());
            if(customerList == null) return;

            for (var cus : customerList) {
                if (cus.getID() == c.getID()) {
                    cus.setBalance(c.getBalance());
                    cus.setYtdPayment(c.getYtdPayment());
                    cus.setPaymentCnt(c.getPaymentCnt());
                    cus.setData(c.getData());
                }
            }

            db.write(TPCC.CUSTOMER, customerTable.toString());
        }

    }

    private void insertHistory(Warehouse w, District d,
                                      Customer c, float paymentAmount)  {


        var wName = w.getName();
        var dName = d.getName();
        if (wName.length() > 10) {
            wName = wName.substring(0, 10);
        }
        if (dName.length() > 10) {
            dName = dName.substring(0, 10);
        }
        String hData = wName + " " + dName;

        var h = new History(c.getID(), c.getDistrictID(), c.getWarehouseID(),d.getID(),
                w.getID(),System.currentTimeMillis(), paymentAmount, hData);

        var historyTable = TPCCUtility.readHistory(db.read(TPCC.HISTORY));
        historyTable.put(h.getWarehouseID()+":"+h.getDistrictID(), h);

        db.write(TPCC.HISTORY, historyTable.toString());



    }

    protected Customer getCustomerById(Customer c)  {

        if(c == null) return null;

        HashMap<String, ArrayList<Customer>> customerTable = null;
        customerTable = TPCCUtility.readCustomer(db.read(TPCC.CUSTOMER));

        //customerTable != null
        var cList = customerTable.get(c.getWarehouseID()+":"+c.getDistrictID());
        if(cList == null) return null;

        for(var cl: cList){
            if(cl.getID() == c.getID()){
                return c;
            }
        }
        return null;
    }

    private void updateWarehouse(int warehouseID, float paymentAmount){
        var warehouseTable = TPCCUtility.readWarehouse(db.read(TPCC.WAREHOUSE));
        var w = warehouseTable.get(warehouseID+"");

        if(w == null) return;
        w.setYtd(paymentAmount);
        db.write(TPCC.WAREHOUSE, warehouseTable.toString());
    }

    private void updateDistrictPayment(int warehouseID, int districtID, float paymentAmount){
        /*Function<District, District> f = (District d)->{
            d.setYtd(paymentAmount);
            return d;
        };

         */
        var districtTable = TPCCUtility.readDistrict(db.read(TPCC.DISTRICT));
        var d = districtTable.get(warehouseID+":"+districtID);
        if(d != null) {
            d.setYtd(paymentAmount);
            districtTable.put(warehouseID + ":" + districtID, d);
            db.write(TPCC.DISTRICT, districtTable.toString());
        }

    }



}
