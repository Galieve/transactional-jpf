package benchmarks.twitter;

import database.TRDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestTwitter {

    private static final User a = new User("a", "user0");
    private static final User b = new User("b", "user1");
    private static final User c = new User("c", "user2");

    private static final Tweet t0 = new Tweet("t0", "user0", "Hello", 0);
    private static final Tweet t1 =new Tweet("t1", "user0", "World", 1);

    private static ArrayList<Integer> results = new ArrayList<>(Arrays.asList(-1,-1));

    private static final List<User> allUsers = Arrays.asList(
            a, b, c
    );
    private static final List<Tweet> allTweets = Arrays.asList(
            t0, t1
    );

    private static void assertNW_TL(){
        var tw = Twitter.getTwitterSession();
        if(results.get(0) > -1 && results.get(1) > -1){
            tw.assertTransaction(results.get(1) >= results.get(0));
        }
    }

    public static void doOperations1(){
        var tw = Twitter.getTwitterSession();


        tw.publishTweet(t0);

        tw.publishTweet(t1);
    }

    public static void doOperations2(){
        var tw = Twitter.getTwitterSession();

        tw.follow(b, a);

        var newsfeed = tw.getNewsfeed(b);

        System.out.println("Newsfeed size: "+newsfeed.size());

        for(var t: newsfeed){
            System.out.println(t);
        }
        results.set(1, newsfeed.size());

        assertNW_TL();
    }

    public static void doOperations3(){
        var tw = Twitter.getTwitterSession();

        var tl = tw.getTimeline(a);

        System.out.println("Timeline size: "+tl.size());
        for(var t: tl){
            System.out.println(t);
        }
        results.set(0,tl.size());

        assertNW_TL();

    }

    public static void main(String [] args) throws InterruptedException {

       /* var tweets = "{user0=[t0;user0;Hello;0, t1;user0;World;1]}";

        var aux = TRUtility.generateHashMap(tweets, (s)->(s));

        System.out.println(aux);

        if(true){
            return;
        }

        */

        TRDatabase dbMain = TRDatabase.getDatabase();

        dbMain.begin();
        dbMain.write("users", new ArrayList<>().toString());

        for(var u: allUsers){
            dbMain.write(u.getFollowingId(), new ArrayList<>().toString());
            dbMain.write(u.getFollowersId(), new ArrayList<>().toString());
            dbMain.write(u.getTweetsId(), new ArrayList<>().toString());
        }

        for(var t: allTweets){
            dbMain.write(t.getTweetId(), "");
        }

        dbMain.commit();


        Thread t2 = new Thread(() -> doOperations2());

        Thread t3 = new Thread(() -> doOperations3());

        Thread t1 = new Thread(() -> doOperations1());




        t1.start();
        t2.start();
        t3.start();
        t3.join();
        t2.join();
        t1.join();


    }
}
