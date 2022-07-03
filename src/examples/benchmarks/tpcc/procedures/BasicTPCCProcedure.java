package benchmarks.tpcc.procedures;

import benchmarks.Procedure;
import benchmarks.tpcc.TPCC;
import benchmarks.tpcc.TPCCUtility;
import benchmarks.tpcc.objects.Customer;
import benchmarks.tpcc.objects.District;
import benchmarks.tpcc.objects.Warehouse;
import database.TRDatabase;

import java.util.ArrayList;
import java.util.function.Function;

public abstract class BasicTPCCProcedure extends Procedure {


    public BasicTPCCProcedure(TRDatabase db) {
        super(db);
    }

    protected Customer getCustomer(int warehouseID, int districtID,
                                          int customerID, String customerName, boolean customerIDSearch){
        if(customerIDSearch){
            return getCustomerById(warehouseID, districtID, customerID);
        }
        else{
            return getCustomerByName(warehouseID, districtID, customerName);
        }
    }

    protected Customer getCustomerByName(int warehouseID, int districtID, String customerLast){

        var customerTable = TPCCUtility.readCustomer(db.read(TPCC.CUSTOMER));

        var cList = customerTable.get(warehouseID+":"+districtID);

        if(cList == null) return null;

        cList.sort((a, b)-> (int) (a.getFirst().compareTo(b.getFirst())));

        var customers = new ArrayList<Customer>();
        for(var c: cList){
            if(c.getLast().equals(customerLast)){
                customers.add(c);
            }
        }

        if(customers.isEmpty()) return null;

        int index = customers.size() / 2;
        if (customers.size() % 2 == 0) {
            index -= 1;
        }
        return customers.get(index);
    }

    // prepared statements
    protected Customer getCustomerById(int warehouseID, int districtID, int customerID)  {

        var customerTable = TPCCUtility.readCustomer(db.read(TPCC.CUSTOMER));

        var cList = customerTable.get(warehouseID+":"+districtID);

        if(cList == null) return null;

        for(var c: cList){
            if(c.getID() == customerID){
                return c;
            }
        }
        return null;
    }


    protected District getDistrict(int warehouseID, int districtID) {
        var districtTable = TPCCUtility.readDistrict(db.read(TPCC.DISTRICT));
        return districtTable.get(warehouseID +":"+ districtID);
    }

    protected void updateDistrict(int warehouseID, int districtID, Function<District, District> f){
        //var s = db.read(TPCC.DISTRICT);
        var districtTable = TPCCUtility.readDistrict(db.read(TPCC.DISTRICT));
        var d = districtTable.get(warehouseID+":"+districtID);
        if(d != null) {
            d = f.apply(d);
            districtTable.put(warehouseID + ":" + districtID, d);
            //db.write(TPCC.DISTRICT, districtTable.toString());
        }

    }

    protected Warehouse getWarehouse(int warehouseID) {
        var s =db.read(TPCC.WAREHOUSE);
        var warehouseTable = TPCCUtility.readWarehouse(s);

        return warehouseTable.get(warehouseID+"");
    }
}
