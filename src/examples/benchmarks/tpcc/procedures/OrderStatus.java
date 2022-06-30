package benchmarks.tpcc.procedures;

import benchmarks.tpcc.TPCC;
import benchmarks.tpcc.TPCCUtility;
import benchmarks.tpcc.objects.Customer;
import benchmarks.tpcc.objects.Order;
import database.TRDatabase;

import java.util.ArrayList;
import java.util.List;

public class OrderStatus  extends BasicTPCCProcedure{

    public OrderStatus(TRDatabase db){
        super(db);
    }

    //all but warehouseID are random!!!
    public String orderStatus(int warehouseID, int districtID, int customerID,
                                     String customerName, boolean customerIDSearch){

        db.begin();


        var c = getCustomer
                (warehouseID, districtID, customerID, customerName, customerIDSearch);


        var o = getOrderDetails(warehouseID, districtID, c);
        var oll = getOrderLines(warehouseID, districtID, o);

        db.end();


        StringBuilder sb = new StringBuilder();
        sb.append("ORDER STATUS:");
        sb.append("Warehouse: ").append(warehouseID);
        sb.append("District: ").append(districtID);

        if(c != null) {

            sb.append("Client: ").append(c);

            if(o != null) {
                sb.append("Order: ").append(o);
                if(oll != null){
                    sb.append("Number of order lines: ").append(oll.size());
                    for (int i = 0; i < oll.size(); ++i) {
                        sb.append("Order line ").append(i).append(": ").append(oll.get(i));
                    }
                }
            }
            else{
                sb.append("No open orders available");
            }
        }
        else{
            var clInfo = customerIDSearch ? customerID + " ID" : customerName + " name";
            sb.append("No clients with ").append(clInfo);
        }

        return sb.toString();
    }

    private Order getOrderDetails(int warehouseID, int districtID, Customer c){
        var orderTable = TPCCUtility.readOpenOrder(db.read(TPCC.OPENORDER));

        if(c == null) return null; //The transaction must always be executed.

        var orders = orderTable.get(warehouseID+":"+districtID);
        orders.sort((a, b)->(int) (b.getID() - a.getID()));
        for(var o: orders){
            if(o.getCustomerID() == c.getID()) return o;
        }
        return null;

    }

    private List<String> getOrderLines(int warehouseID, int districtID, Order o){
        var orderLineTable = TPCCUtility.readOrderLine(db.read(TPCC.ORDERLINE));

        if(o == null) return null; //The transaction must always be executed.


        orderLineTable.putIfAbsent(warehouseID+":"+districtID, new ArrayList<>());

        var ret = new ArrayList<String>();
        for(var ol : orderLineTable.get(warehouseID+":"+districtID)){
            if(ol.getOrderID() == o.getID()){
                ret.add(ol.toString());
            }
        }

        return ret;

    }


}
