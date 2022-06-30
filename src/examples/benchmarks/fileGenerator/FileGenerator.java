package benchmarks.fileGenerator;

import benchmarks.twitter.Tweet;
import benchmarks.twitter.User;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FileGenerator {

    private static final Random gen = new Random();

    private static final HashMap<String, Object> allObjects = new HashMap<>();

    private static ArrayList<ArrayList<String>> getListOfMethods(String name){
        switch (name) {
            case "twitter":
                return new ArrayList<>(
                        List.of(
                                new ArrayList<>(List.of("follow", "User", "User")),
                                new ArrayList<>(List.of("getTweet", "User")),
                                new ArrayList<>(List.of("getNewsfeed", "User")),
                                new ArrayList<>(List.of("getFollowers", "User")),
                                new ArrayList<>(List.of("getTimeline", "User")),
                                new ArrayList<>(List.of("publishTweet","Tweet"))
                        )
                );

            default:
                return new ArrayList<>();
        }
    }

    /*
       <transactiontype>
                <name>GetTweet</name>
        </transactiontype>
        <transactiontype>
                <name>GetTweetsFromFollowing</name>
        </transactiontype>
        <transactiontype>
                <name>GetFollowers</name>
        </transactiontype>
        <transactiontype>
                <name>GetUserTweets</name>
        </transactiontype>
        <transactiontype>
                <name>InsertTweet</name>

     */

    //2.5*3 = 7.5 +15.5

    private static ArrayList<Double> getListOfWeights(String name){
        switch (name) {
            case "twitter":
                //return new ArrayList<>(List.of(0.07, 0.07,0.07,7.6725,91.1956,0.9219));
                return new ArrayList<>(List.of(2.5,2.5,2.5, 15.5,67.0,10.0));

            default:
                return new ArrayList<>();
        }
    }


    private static Object buildObject(String type, ArrayList<String> args){
        var id = "";
        switch (type){
            case "User":
                id = args.get(0) + ";" + 0;
                if(!allObjects.containsKey(id)){
                    allObjects.put(id, new User(id));
                }
                return allObjects.get(id);
            case "Tweet":
                id = args.get(1)+";"+ args.get(0)+ ";Empty-tweet;"  + 0;
                if(!allObjects.containsKey(id)){
                    allObjects.put(id, new Tweet(id));
                }
                return allObjects.get(id);
            default:
                return null;
        }
    }

    private static String getNewTransactionStd(ArrayList<ArrayList<String>> benchmark,
                                            int method, ArrayList<String> args){

        var parameters = benchmark.get(method);
        if(args.size() < 2) return null;

        StringBuilder sb = new StringBuilder();

        sb.append(parameters.get(0));

        for(int i = 1; i < parameters.size(); ++i){
            var obj = buildObject(parameters.get(i), args);
            if (obj == null) return null;
            sb.append(" ").append(obj);
        }
        return sb.toString();

    }

    private static String getFollowTransaction(ArrayList<ArrayList<String>> benchmark,
                                            int method, ArrayList<String> args){

        var parameters = benchmark.get(method);

        //special
        if(args.size() < 2) return null;

        StringBuilder sb = new StringBuilder();

        sb.append(parameters.get(0));



        for(int i = 1; i < parameters.size(); ++i){
            var obj = buildObject(parameters.get(i), new ArrayList<>(List.of(args.get(2*(i-1)), args.get(1))));
            if (obj == null) return null;
            sb.append(" ").append(obj);
        }
        return sb.toString();

    }

    private static String getNewTransaction(String benchmarkName, ArrayList<ArrayList<String>> benchmark,
                                                   int method, ArrayList<String> args) {
        switch (benchmarkName) {
            case "twitter":
                if (method == 0) {
                    return getFollowTransaction(benchmark, method, args);
                }
            default:
        }
        return getNewTransactionStd(benchmark, method, args);
    }

    private static int selectTransaction(ArrayList<Double> weights){
        double randomPercentage = 100 * gen.nextDouble();
        double weight = 0.0;
        for (int i = 0; i < weights.size(); i++) {
            weight += weights.get(i);
            if (randomPercentage <= weight) {
                return i;
            }
        }
        return weights.size() - 1;
    }

    public static void writeData(String filePath, Class<?> classType){
        try {
            FileWriter dataFile = new FileWriter(filePath);
            for(var o: allObjects.values()){
                if(o.getClass().equals(classType)){
                    dataFile.write(o.toString()+"\n");
                }
            }
            dataFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        String benchmarkName = "";
        int times = -1;
        String filePath = "";
        int threads = -1;


        for(int i = 0; i + 1< args.length; i+=2){
            switch (args[i]){
                case "-b":
                    benchmarkName = args[i+1];
                    break;
                case "-n":
                    times = Integer.parseInt(args[i+1]);
                    break;
                case "-f":
                    filePath = args[i+1];
                    break;
                case "-t":
                    threads = Integer.parseInt(args[i+1]);
                    break;
                default:
                    break;
            }
        }


        if(benchmarkName.equals("") || times == 1 || filePath.equals("") || threads == -1){
            System.err.println("Wrong arguments...");
            return;
        }
        File uidFile = new File("bin/benchmarks/twitter/twitter_user_ids.txt");
        File tidFile = new File("bin/benchmarks/twitter/twitter_tweet_ids.txt");



        try {
            var fileWriters = new ArrayList<FileWriter>();
            for(int t = 0; t < threads; ++t) {
                fileWriters.add(new FileWriter(filePath + benchmarkName + "-" + t + ".in"));
            }
            BufferedReader brUser = new BufferedReader(new FileReader(uidFile));
            BufferedReader brTweet = new BufferedReader(new FileReader(tidFile));

            var benchmark = getListOfMethods(benchmarkName);
            var weights = getListOfWeights(benchmarkName);

            var userStr = "";
            var tweetStr = "";
            int i = 0;

            int perc = 0;

            var userStrPrev = "";
            while((userStr = brUser.readLine()) != null &&
                    (tweetStr = brTweet.readLine()) != null && i < times){

                int nperc = (i*100)/times;
                if(nperc > perc){
                    perc = (i*100)/times;
                    System.out.println(perc+"% computed");
                }

                for(var fileWriter : fileWriters){
                    var tr = selectTransaction(weights);
                    tr = tr == 0 && i == 0 ? 1 : tr;

                    ArrayList<String> parameters = new ArrayList<>(List.of(userStr, tweetStr, userStrPrev));

                    var newline = getNewTransaction(benchmarkName, benchmark, tr, parameters);

                    if(newline != null){
                        fileWriter.write(newline+"\n");
                    } else{
                        System.err.println("Wrong arguments...");
                        return;
                    }
                }

                ++i;
                userStrPrev = userStr;
            }

            for(var f: fileWriters){
                f.close();
            }

            writeData(filePath+benchmarkName+"-users.in", User.class);

        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();

        }

    }
}
