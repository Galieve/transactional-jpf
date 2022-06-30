package benchmarks.twitter;

import database.TRUtility;

public class Tweet {

    private String tweetId;

    private String userId;

    private String content;

    private long timestamp;



    public Tweet(String tweetId, String userId, String content, long timestamp) {
        this.tweetId = tweetId;
        this.content = content;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public Tweet(String s) {
        this(TRUtility.getValue(s, 0), TRUtility.getValue(s, 1),
                TRUtility.getValue(s, 2),
                Long.parseLong(TRUtility.getValue(s, 3)));

    }

    public String getTweetId() {
        return tweetId;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return tweetId + ';' +userId + ';' +content + ';' + timestamp;
    }
}
