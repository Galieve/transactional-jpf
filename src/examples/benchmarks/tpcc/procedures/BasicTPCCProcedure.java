package benchmarks.tpcc.procedures;

import benchmarks.Procedure;
import benchmarks.tpcc.TPCC;
import benchmarks.tpcc.TPCCUtility;
import benchmarks.tpcc.objects.Customer;
import benchmarks.tpcc.objects.District;
import benchmarks.tpcc.objects.Warehouse;
import database.AbortDatabaseException;
import database.APIDatabase;

import java.util.ArrayList;
import java.util.function.Function;

public abstract class BasicTPCCProcedure extends Procedure {


    public BasicTPCCProcedure(APIDatabase db) {
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

        var cuLSt = db.readIfIDStartsWith(TPCC.CUSTOMER, warehouseID+":"+districtID);
        var cList = new ArrayList<Customer>();

        for(var cSt: cuLSt){
            if(cSt == null) db.abort();

            var c = new Customer(cSt);
            if(c.getLast().equals(customerLast)){
                cList.add(c);
            }
        }

        cList.sort((a, b)-> (int) (a.getFirst().compareTo(b.getFirst())));


        if(cList.isEmpty()) db.abort();

        int index = cList.size() / 2;
        if (cList.size() % 2 == 0) {
            index -= 1;
        }
        return cList.get(index);
    }

    // prepared statements
    protected Customer getCustomerById(int warehouseID, int districtID, int customerID) throws AbortDatabaseException {


        var cSt = db.readRow(TPCC.CUSTOMER, warehouseID+":"+districtID+":"+customerID);
        var c = cSt == null ? null : new Customer(cSt);
        if(c == null) db.abort();
        return c;

    }


    protected District getDistrict(int warehouseID, int districtID) throws AbortDatabaseException {
        var dSt = db.readRow(TPCC.DISTRICT, warehouseID +":"+ districtID);
        var d = dSt == null ? null : new District(dSt);
        if(d == null) db.abort();
        return d;
    }

    protected void updateDistrict(int warehouseID, int districtID, Function<District, District> f) throws AbortDatabaseException {
        var dSt = db.readRow(TPCC.DISTRICT, warehouseID +":"+ districtID);
        var d = dSt == null ? null : new District(dSt);
        if(d == null) db.abort();
        d = f.apply(d);

        db.writeRow(TPCC.DISTRICT, warehouseID +":"+ districtID, d.toString());
    }

    protected Warehouse getWarehouse(int warehouseID) throws AbortDatabaseException {

        var wSt = db.readRow(TPCC.WAREHOUSE, warehouseID +"");
        var w = wSt == null ? null : new Warehouse(wSt);
        if(w == null) db.abort();
        return w;
    }
}
