package twitter;

import database.TRUtility;

public class User {

    private String username;

    private String userId;

    public User(String username, String userId) {
        this.username = username;
        this.userId = userId;
    }

    public User(String s) {
         this(TRUtility.getValue(s, 0), TRUtility.getValue(s, 1));
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }


    public String toString() {
        return userId+";"+username;
    }

    public String getFollowingId(){
        return userId+":following";
    }

    public String getFollowersId(){
        return userId+":followers";
    }

    public String getTweetsId(){
        return userId+":tweets";
    }
}
