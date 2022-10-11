package benchmarks.tpcc.procedures;

import benchmarks.Procedure;
import benchmarks.tpcc.TPCC;
import benchmarks.tpcc.TPCCUtility;
import benchmarks.tpcc.objects.Customer;
import benchmarks.tpcc.objects.District;
import benchmarks.tpcc.objects.Warehouse;
import database.AbortDatabaseException;
import database.TRDatabase;

import java.util.ArrayList;
import java.util.function.Function;

public abstract class BasicTPCCProcedure extends Procedure {


    public BasicTPCCProcedure(TRDatabase db) {
        super(db);
    }

    protected Customer getCustomer(int warehouseID, int districtID,
                                          int customerID, String customerName, boolean customerIDSearch) throws AbortDatabaseException {
        if(customerIDSearch){
            return getCustomerById(warehouseID, districtID, customerID);
        }
        else{
            return getCustomerByName(warehouseID, districtID, customerName);
        }
    }

    protected Customer getCustomerByName(int warehouseID, int districtID, String customerLast) throws AbortDatabaseException {

        var customerTable = TPCCUtility.readCustomer(db.read(TPCC.CUSTOMER));

        var cList = customerTable.get(warehouseID+":"+districtID);
        if(cList == null) db.abort();

        cList.sort((a, b)-> (int) (a.getFirst().compareTo(b.getFirst())));

        var customers = new ArrayList<Customer>();
        for(var c: cList){
            if(c.getLast().equals(customerLast)){
                customers.add(c);
            }
        }

        if(customers.isEmpty()) db.abort();

        int index = customers.size() / 2;
        if (customers.size() % 2 == 0) {
            index -= 1;
        }
        return customers.get(index);
    }

    // prepared statements
    protected Customer getCustomerById(int warehouseID, int districtID, int customerID) throws AbortDatabaseException {


        var customerTable = TPCCUtility.readCustomer(db.read(TPCC.CUSTOMER));

        var cList = customerTable.get(warehouseID+":"+districtID);

        if(cList == null) db.abort();

        for(var c: cList){
            if(c.getID() == customerID){
                return c;
            }
        }
        db.abort();
        return null;
    }


    protected District getDistrict(int warehouseID, int districtID) throws AbortDatabaseException {
        var districtTable = TPCCUtility.readDistrict(db.read(TPCC.DISTRICT));
        var d = districtTable.get(warehouseID +":"+ districtID);
        if(d == null) db.abort();
        return d;
    }

    protected void updateDistrict(int warehouseID, int districtID, Function<District, District> f) throws AbortDatabaseException {
        var districtTable = TPCCUtility.readDistrict(db.read(TPCC.DISTRICT));
        var d = districtTable.get(warehouseID+":"+districtID);

        if(d == null) db.abort();
        d = f.apply(d);
        districtTable.put(warehouseID + ":" + districtID, d);
        db.write(TPCC.DISTRICT, districtTable.toString());


    }

    protected Warehouse getWarehouse(int warehouseID) throws AbortDatabaseException {
        var s =db.read(TPCC.WAREHOUSE);
        var warehouseTable = TPCCUtility.readWarehouse(s);
        var w = warehouseTable.get(warehouseID+"");
        if(w == null) db.abort();
        return w;
    }
}
