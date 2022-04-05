package twitter;

import database.TRDatabase;
import database.TRUtility;

import java.util.ArrayList;
import java.util.HashMap;

public class Twitter {

    private TRDatabase db;

    private static HashMap<Integer, Twitter> twitterInstances = new HashMap<>();

    private Twitter() {
        db = TRDatabase.getDatabase();
    }

    public static Twitter getTwitterSession(int id){
        if(!twitterInstances.containsKey(id)){
            twitterInstances.put(id, new Twitter());
        }
        return twitterInstances.get(id);
    }

    public void addUser(User u){
        var users = TRUtility.generateArrayList(db.read("users"));
        users.add(u.getUserId());
        db.write("users", users.toString());
        db.write(u.getFollowingId(), new ArrayList<>().toString());
        db.write(u.getFollowersId(), new ArrayList<>().toString());
        db.write(u.getTweetsId(), new ArrayList<>().toString());
    }

    public void follow(User a, User b){


        var aFollowing = TRUtility.generateArrayList(db.read(a.getFollowingId()));
        if(aFollowing.contains(b.getUserId())) return;

        aFollowing.add(b.getUserId());
        db.write(a.getFollowingId(), aFollowing.toString());


        var bFollowers = TRUtility.generateArrayList(db.read(b.getFollowersId()));
        bFollowers.add(a.getUserId());
        db.write(b.getFollowersId(), bFollowers.toString());

    }

    public void publishTweet(User u, Tweet t){
        var tweetsId = TRUtility.generateArrayList(db.read(u.getTweetsId()));
        tweetsId.add(t.getTweetId());
        db.write(u.getTweetsId(), tweetsId.toString());
        db.write(t.getTweetId(), t.toString());
    }


    public ArrayList<Tweet> getNewsfeed(User u){
        var uFollowing = TRUtility.generateArrayList(db.read(u.getFollowingId()));

        var allTweets = new ArrayList<Tweet>();
        for(var fol : uFollowing){
            //We don't know the actual name of the follower, but we just need it's id.
            var folTL = getTimeline(new User("",fol));
            allTweets.addAll(folTL);
        }
        allTweets.sort((a, b)-> (int) (a.getTimestamp() - b.getTimestamp()));
        return allTweets;
    }

    public ArrayList<Tweet> getTimeline(User u){
        var tweetsId = TRUtility.generateArrayList(db.read(u.getTweetsId()));

        ArrayList<Tweet> allTweets = new ArrayList<>();
        for(var t: tweetsId){
            var tw = new Tweet(db.read(t));
            allTweets.add(tw);
        }

        allTweets.sort((a, b)-> (int) (a.getTimestamp() - b.getTimestamp()));
        return allTweets;
    }


    public void begin(){
        db.begin();
    }

    public void end(){
        db.end();
    }

    public void assertTransaction(boolean b){
        db.assertDB(b);
    }


}
