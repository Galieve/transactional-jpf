package twitter;

import database.TRUtility;

public class Tweet {

    private String tweetId;

    private String content;

    private long timestamp;

    private long likeCount;

    private long retweetCount;

    public Tweet(String tweetId, String content, long timestamp) {
        this(tweetId, content, timestamp, 0, 0);

    }

    public Tweet(String tweetId, String content, long timestamp, long likeCount, long retweetCount) {
        this.tweetId = tweetId;
        this.content = content;
        this.timestamp = timestamp;
        this.likeCount = likeCount;
        this.retweetCount = retweetCount;
    }

    public Tweet(String s) {
        this(TRUtility.getValue(s, 0), TRUtility.getValue(s, 1),
                Long.parseLong(TRUtility.getValue(s, 2)), Long.parseLong(TRUtility.getValue(s, 3)),
                Long.parseLong(TRUtility.getValue(s, 4)));

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

    public long getLikeCount() {
        return likeCount;
    }

    public long getRetweetCount() {
        return retweetCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public void setRetweetCount(long retweetCount) {
        this.retweetCount = retweetCount;
    }

    public void incrementLikeCount(){
        ++likeCount;
    }

    public void incrementRetweetCount(){
        ++retweetCount;
    }

    @Override
    public String toString() {
        return tweetId + ';' + content + ';' +
                timestamp + ";" + likeCount +
                ";" + retweetCount;
    }
}
