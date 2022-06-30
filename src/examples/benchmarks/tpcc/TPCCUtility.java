package benchmarks.tpcc;

import benchmarks.tpcc.objects.*;
import database.TRUtility;

import java.util.ArrayList;
import java.util.HashMap;

public class TPCCUtility {

    public static HashMap<String, ArrayList<Customer>> readCustomer(String customer){
        return TRUtility.generateHashMap(customer,
                (cList)->(TRUtility.generateArrayList(cList, (c)->(new Customer(c)))));
    }

    public static HashMap<String, ArrayList<Order>> readOpenOrder(String openOrder){
        return TRUtility.generateHashMap(openOrder,
                (oList)->(TRUtility.generateArrayList(oList, (o)->(new Order(o)))));
    }

    public static HashMap<String, District> readDistrict(String district){
        return TRUtility.generateHashMap(district,
                (d)->(new District(d)));
    }
    public static HashMap<String, ArrayList<OrderLine>> readOrderLine(String orderline){
        return TRUtility.generateHashMap(orderline,
                (olList)->(TRUtility.generateArrayList(olList, (ol)->(new OrderLine(ol)))));
    }
    public static HashMap<String, Stock> readStock(String stock){
        return TRUtility.generateHashMap(stock,
                (st)->(new Stock(st)));
    }

    public static HashMap<String, History> readHistory(String history){
        return TRUtility.generateHashMap(history,
                (h)->(new History(h)));
    }

    public static HashMap<String, Warehouse> readWarehouse(String warehouse){
        return TRUtility.generateHashMap(warehouse,
                (w)->(new Warehouse(w)));
    }


    public static HashMap<String, ArrayList<NewOrder>> readNewOrder(String no) {
        return TRUtility.generateHashMap(no,
                (nl)->(TRUtility.generateArrayList(nl, (n)->(new NewOrder(n)))));
    }

    public static HashMap<String, Item> readItem(String item) {
        return TRUtility.generateHashMap(item, (i)->(new Item(i)));
    }
}
