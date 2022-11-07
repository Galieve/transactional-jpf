package benchmarks.twitter;

import benchmarks.BenchmarkModule;
import gov.nasa.jpf.jvm.bytecode.ARETURN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

public class Twitter extends BenchmarkModule {

    private static Twitter twitterInstance;

    protected static final String TWEETS = "TWEETS";

    protected static final String USERS = "USERS";

    protected static final String FOLLOWS = "FOLLOWS";

    protected static final String FOLLOWERS = "FOLLOWERS";

    private Twitter() {

    }

    public static Twitter getTwitterSession(){
        if(twitterInstance == null){
            twitterInstance = new Twitter();
        }
        return twitterInstance;
    }

    @Override
    public HashMap<String, Class<?>[]> getAllMethods() {
        var map = new HashMap<String, Class<?>[]>();
        map.put("getFollowers", new Class<?>[]{User.class});
        map.put("getTweet", new Class<?>[]{String.class});
        map.put("getNewsfeed", new Class<?>[]{User.class});
        map.put("getTimeline",new Class<?>[]{User.class});
        map.put("publishTweet", new Class<?>[]{Tweet.class});
        map.put("follow", new Class<?>[]{User.class, User.class});

        return map;
    }

    public ArrayList<User> getFollowers(User u){
        db.begin();

        var followersIDs = readAllIDsStartsWith(FOLLOWERS, u.getUserId());
        var followersUserIDs = new ArrayList<String>();
        for(var f: followersIDs){
            followersUserIDs.add(f.substring(u.getUserId().length()+1));
        }

        var followersSet = new HashSet<String>(followersUserIDs);
        var followersString = db.readIfIsIn(USERS, followersSet);

        db.commit();


        if(followersString == null) return null;
        var followers = new ArrayList<User>();
        for(var f: followersString){
            followers.add(new User(f));
        }

        return followers;



    }

    public Tweet getTweet(String tweetId){
        db.begin();
        //var t = db.readIfIDStartsWith(TWEETS, tweetId);
        db.commit();
        String t = null;
        return t == null ? null : new Tweet(t);
    }



    public void follow(User a, User b){
        db.begin();

        if(!a.getUserId().equals(b.getUserId())) {

            db.insertRow(FOLLOWS, a.getUserId()+":"+b.getUserId());
            db.insertRow(FOLLOWERS, b.getUserId()+":"+a.getUserId());

        }
        db.commit();


    }

    public void publishTweet(Tweet t){
        db.begin();
        db.insertRow(TWEETS,t.getTweetId()+":"+t.getUserId(), t.toString());
        db.commit();
    }


    public ArrayList<Tweet> getNewsfeed(User u){
        db.begin();
        var userID = u.getUserId();


        var followersIDAll = db.readAllIDs(FOLLOWS);
        var followersIDs = new ArrayList<String>();
        for(var f: followersIDAll){
            if(f.startsWith(userID)){
                followersIDs.add(f.substring(userID.length()+1));
            }
        }
        var tweets = new ArrayList<Tweet>();
        for(var f: followersIDs){
            var tweetsStr = db.readIfIDEndsWith(TWEETS,f);
            for(var t : tweetsStr){
                tweets.add(new Tweet(t));
            }
        }


        db.commit();

        tweets.sort((a, b)-> (int) (a.getTimestamp() - b.getTimestamp()));


        return tweets;

    }

    protected ArrayList<Tweet> getTimeline(String userID){
        db.begin();

        var tweetsStr = db.readIfIDEndsWith(TWEETS, userID);
        var tweets = new ArrayList<Tweet>();
        for(var t: tweetsStr){
            tweets.add(new Tweet(t));
        }
        db.commit();

        return tweets;
    }

    public ArrayList<Tweet> getTimeline(User u){
        return getTimeline(u.getUserId());
    }

    public void assertTransaction(boolean b){
        db.begin();
        db.assertDB(b);
        db.commit();
    }

    protected ArrayList<String> readAllIDsStartsWith(String tableName, String filter){
        var tList = db.readAllIDs(tableName);
        if(tList == null) return null;

        var filtered = new ArrayList<String>();
        for(var el: tList){
            if(el.startsWith(filter)){
                filtered.add(el);
            }
        }
        return filtered;

    }


}
