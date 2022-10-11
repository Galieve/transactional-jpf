package benchmarks.twitter;

import benchmarks.BenchmarkModule;
import database.TRUtility;

import java.util.ArrayList;
import java.util.HashMap;

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

        var followersIds = TRUtility.generateHashMap(db.read(FOLLOWERS),
                (lu)->(TRUtility.generateArrayList(lu)));

        var users = TRUtility.generateHashMap(db.read(USERS), (s)->(new User(s)));


        db.commit();

        followersIds.putIfAbsent(u.getUserId(), new ArrayList<>());

        var followers = new ArrayList<User>();
        for(var fol: followersIds.get(u.getUserId())){
            followers.add(users.get(fol));
        }
        return followers;
    }


    public void follow(User a, User b){

        if(a.getUserId().equals(b.getUserId())) return;

        db.begin();

        var followIds = TRUtility.generateHashMap(db.read(FOLLOWS),
                (lu)->(TRUtility.generateArrayList(lu)));

        var followersIds = TRUtility.generateHashMap(db.read(FOLLOWERS),
                (lu)->(TRUtility.generateArrayList(lu)));

        followIds.putIfAbsent(a.getUserId(), new ArrayList<>());
        followersIds.putIfAbsent(b.getUserId(), new ArrayList<>());


        if(!followIds.get(a.getUserId()).contains(b.getUserId())){
            followIds.get(a.getUserId()).add(b.getUserId());
            followersIds.get(b.getUserId()).add(a.getUserId());
        }

        db.write(FOLLOWS, followIds.toString());

        db.write(FOLLOWERS, followersIds.toString());

        db.commit();


    }

    public void publishTweet(Tweet t){
        db.begin();
        var map = TRUtility.generateHashMap(db.read(TWEETS), TRUtility::generateArrayList);

        map.putIfAbsent(t.getUserId(), new ArrayList<>());
        map.get(t.getUserId()).add(t.toString());

        db.write(TWEETS, map.toString());

        db.commit();
    }

    public Tweet getTweet(String tweetId){
        db.begin();
        var map =
                TRUtility.generateHashMap(db.read(TWEETS),
                        (lt) -> (TRUtility.generateArrayList(lt)));


        db.commit();
        for(var list: map.values()){
            for(var t : list){
                var tw = new Tweet(t);
                if(tw.getTweetId().equals(tweetId)){
                    return tw;
                }
            }
        }

        return null;
    }

    public ArrayList<Tweet> getNewsfeed(User u){
        db.begin();
        var uFollowing = TRUtility.generateHashMap(db.read(FOLLOWS),
                (s)->(TRUtility.generateArrayList(s)));


        var allTweets = new ArrayList<Tweet>();
        uFollowing.putIfAbsent(u.getUserId(), new ArrayList<>());
        for(var folId : uFollowing.get(u.getUserId())){

            var folTL = getTimeline(folId);
            allTweets.addAll(folTL);
        }
        db.commit();
        //allTweets.sort((a, b)-> (int) (a.getTimestamp() - b.getTimestamp()));


        return allTweets;
    }

    protected ArrayList<Tweet> getTimeline(String userId){
        db.begin();

        var map = TRUtility.generateHashMap(db.read(TWEETS),
                (t) -> (TRUtility.generateArrayList(t,(s) -> (new Tweet(s)))));

        db.commit();

        map.putIfAbsent(userId, new ArrayList<>());
        return map.get(userId);

    }

    public ArrayList<Tweet> getTimeline(User u){
        return getTimeline(u.getUserId());
    }

    public void assertTransaction(boolean b){
        db.begin();
        db.assertDB(b);
        db.commit();
    }


}
