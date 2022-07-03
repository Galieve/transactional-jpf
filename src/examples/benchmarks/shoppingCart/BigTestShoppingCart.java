package benchmarks.shoppingCart;

import benchmarks.MainUtility;
import benchmarks.Worker;
import database.TRDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

public class BigTestShoppingCart {



    private static Function<String, String> idGenerator(String type){

        switch (type) {
            case ShoppingCart.STORE:
                return (s)->{
                    var sl = s.split(";");
                    return sl[0];
                };
            default:
                return null;

        }

    }

    private static int populateDB(String ... args){

        TRDatabase dbMain = TRDatabase.getDatabase();

        HashMap<String, String> tableInfo = new HashMap<>();
        HashSet<String> arraySet = new HashSet<>();


        tableInfo.put(ShoppingCart.STORE, "{}");

        int ret = args.length - 1;


        for(int i = 0; i +1 < args.length && ret == args.length - 1; i+=2){
            switch (args[i]){
                case "-s":
                    tableInfo.put(ShoppingCart.STORE,  args[i+1]);
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


        var ses = ShoppingCart.getInstance();

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
