package benchmarks.wikipedia;

import benchmarks.MainUtility;
import benchmarks.Worker;
import database.APIDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

public class BigTestWikipedia {

    /**
     * ID Generator. Given a database table name, returns a function that given some data
     * (String representation) returns its ID.
     * @param type
     * @return
     */

    private static Function<String, String> idGenerator(String type){

        switch (type) {
            case Wikipedia.USER:
            case Wikipedia.PAGE:
            case Wikipedia.PAGERESTRICTIONS:
            case Wikipedia.IPBLOCKS:
            case Wikipedia.REVISION:
            case Wikipedia.RECENTCHANGES:
            case Wikipedia.TEXT:
            case Wikipedia.USERGROUPS:
            case Wikipedia.LOGGING:
                return (s)->{
                    var sl = s.split(";");
                    return sl[0];
                };
            case Wikipedia.WATCHLIST:
                return (s)->{
                    var sl = s.split(";");
                    return sl[0]+":"+sl[1]+":"+sl[2];
                };
            default:
                return null;

        }

    }

    private static int populateDB(String ... args){

        APIDatabase dbMain = APIDatabase.getDatabase();

        HashMap<String, String> tableInfo = new HashMap<>();
        HashSet<String> arraySet = new HashSet<>();


        tableInfo.put(Wikipedia.IPBLOCKS, "{}");
        tableInfo.put(Wikipedia.LOGGING, "{}");
        tableInfo.put(Wikipedia.PAGE, "{}");
        tableInfo.put(Wikipedia.PAGERESTRICTIONS, "{}");
        tableInfo.put(Wikipedia.RECENTCHANGES, "{}"); //TODO: not used yet
        tableInfo.put(Wikipedia.REVISION,"{}");
        tableInfo.put(Wikipedia.TEXT, "{}");
        tableInfo.put(Wikipedia.USER, "{}");
        tableInfo.put(Wikipedia.USERGROUPS, "{}");
        tableInfo.put(Wikipedia.WATCHLIST, "{}");


        int ret = args.length - 1;


        for(int i = 0; i +1 < args.length && ret == args.length - 1; i+=2){
            switch (args[i]){
                case "-i":
                    tableInfo.put(Wikipedia.IPBLOCKS,  args[i+1]);
                    break;
                case "-l":
                    tableInfo.put(Wikipedia.LOGGING,  args[i+1]);
                    break;
                case "-p":
                    tableInfo.put(Wikipedia.PAGE,  args[i+1]);
                    break;
                case "-s":
                    tableInfo.put(Wikipedia.PAGERESTRICTIONS,  args[i+1]);
                    break;
                case "-c":
                    tableInfo.put(Wikipedia.RECENTCHANGES,  args[i+1]);
                    break;
                case "-r":
                    tableInfo.put(Wikipedia.REVISION, args[i+1]);
                    break;
                case "-t":
                    tableInfo.put(Wikipedia.TEXT,  args[i+1]);
                    break;
                case "-u":
                    tableInfo.put(Wikipedia.USER,  args[i+1]);
                    break;
                case "-g":
                    tableInfo.put(Wikipedia.USERGROUPS,  args[i+1]);
                    break;
                case "-w":
                    tableInfo.put(Wikipedia.WATCHLIST,  args[i+1]);
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

        var ses = Wikipedia.getInstance();

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
