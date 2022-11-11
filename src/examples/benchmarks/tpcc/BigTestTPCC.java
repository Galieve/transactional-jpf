package benchmarks.tpcc;

import benchmarks.MainUtility;
import benchmarks.Worker;
import database.APIDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

public class BigTestTPCC {

    /**
     * ID Generator. Given a database table name, returns a function that given some data
     * (String representation) returns its ID.
     * @param type
     * @return
     */

    private static Function<String, String> idGenerator(String type){
        switch (type) {
            case TPCC.ORDERLINE:
                return (s)->{
                    var sl = s.split(";");
                    return sl[0]+":"+sl[1]+":"+sl[2]+":"+sl[3]+":"+sl[4]+":"+sl[5];
                };
            case TPCC.CUSTOMER:
            case TPCC.NEWORDER:
            case TPCC.OPENORDER:
                return (s)->{
                    var sl = s.split(";");
                    return sl[0]+":"+sl[1]+":"+sl[2];
                };
            case TPCC.DISTRICT:
            case TPCC.HISTORY:
            case TPCC.STOCK: //warehouse + item (but it is the same formula)
                return (s)->{
                    var sl = s.split(";");

                    return sl[0]+":"+sl[1];
                };
            case TPCC.WAREHOUSE:
            case TPCC.ITEM:
                return (s)->{
                    var sl = s.split(";");
                    return sl[0];
                };
            default:
                return null;

        }

    }

    private static int populateDB(String ... args){

        APIDatabase dbMain = APIDatabase.getDatabase();

        HashMap<String, String> tableInfo = new HashMap<>();
        HashSet<String> arraySet = new HashSet<>();


        tableInfo.put(TPCC.CUSTOMER, "{}");
        tableInfo.put(TPCC.DISTRICT, "{}");
        tableInfo.put(TPCC.HISTORY, "{}");
        tableInfo.put(TPCC.ITEM, "{}");
        tableInfo.put(TPCC.NEWORDER,"{}");
        tableInfo.put(TPCC.OPENORDER, "{}");
        tableInfo.put(TPCC.ORDERLINE, "{}");
        tableInfo.put(TPCC.STOCK, "{}");
        tableInfo.put(TPCC.WAREHOUSE, "{}");

        //warehouse, district, history, item, stock are not array



        int ret = args.length - 1;


        for(int i = 0; i +1 < args.length && ret == args.length - 1; i+=2){
            switch (args[i]){
                case "-d":
                    tableInfo.put(TPCC.DISTRICT,  args[i+1]);
                    break;
                case "-c":
                    tableInfo.put(TPCC.CUSTOMER,  args[i+1]);
                    break;
                case "-h":
                    tableInfo.put(TPCC.HISTORY,  args[i+1]);
                    break;
                case "-i":
                    tableInfo.put(TPCC.ITEM,  args[i+1]);
                    break;
                case "-n":
                    tableInfo.put(TPCC.NEWORDER, args[i+1]);
                    break;
                case "-o":
                    tableInfo.put(TPCC.OPENORDER,  args[i+1]);
                    break;
                case "-l":
                    tableInfo.put(TPCC.ORDERLINE,  args[i+1]);
                    break;
                case "-s":
                    tableInfo.put(TPCC.STOCK,  args[i+1]);
                    break;
                case "-w":
                    tableInfo.put(TPCC.WAREHOUSE,  args[i+1]);
                    break;
                default:
                    ret = i;
                    break;

            }
        }
        new MainUtility(dbMain).initTransaction(tableInfo, arraySet, (s)->(idGenerator(s)));

        return ret;

    }

    public static void main(String [] args) {


        var idx = populateDB(args);


        var ses = TPCC.getInstance();

        try {
            var threads = new ArrayList<Thread>();
            for(int i = idx; i < args.length; ++i){
                threads.add(new Thread(new Worker<>(ses, args[i])));
            }
            for(var t : threads){
                t.start();
            }
            for(var t : threads){
                t.join();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }





    }
}
