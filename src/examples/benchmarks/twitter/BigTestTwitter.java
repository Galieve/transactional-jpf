package benchmarks.twitter;

import benchmarks.MainUtility;
import benchmarks.Worker;
import database.TRDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

public class BigTestTwitter {

    private static Function<String, String> idGenerator(String type){
        switch (type) {
            case Twitter.FOLLOWERS:
            case Twitter.FOLLOWS:
            case Twitter.TWEETS:
            case Twitter.USERS:
                return (s)->{
                    var sl = s.split(";");
                    return sl[0];
                };
            default:
                return null;

        }

    }

    private static int populateDB(String ... args){

        HashMap<String, String> tableInfo = new HashMap<>();
        HashSet<String> arraySet = new HashSet<>();



        TRDatabase dbMain = TRDatabase.getDatabase();

        tableInfo.put(Twitter.FOLLOWS, "{}");
        tableInfo.put(Twitter.FOLLOWERS, "{}");
        tableInfo.put(Twitter.TWEETS, "{}");
        tableInfo.put(Twitter.USERS, "{}");

        int ret = args.length - 1;

        for(int i = 0; i +1 < args.length && ret == args.length - 1; i+=2){
            switch (args[i]){
                case "-u":
                    tableInfo.put(Twitter.USERS,  args[i+1]);
                    break;
                case "-t":
                    tableInfo.put(Twitter.TWEETS,  args[i+1]);
                    break;
                case "-f":
                    tableInfo.put(Twitter.FOLLOWS,  args[i+1]);
                    break;
                case "-r":
                    tableInfo.put(Twitter.FOLLOWERS,  args[i+1]);
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


        var iStart = populateDB(args);


        var ses = Twitter.getTwitterSession();

        try {
            var threads = new ArrayList<Thread>();
            for(int i = iStart; i < args.length; ++i){
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
