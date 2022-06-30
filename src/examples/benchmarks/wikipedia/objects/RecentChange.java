package benchmarks.wikipedia.objects;

import database.TRUtility;

public class RecentChange {

    private long rcID;
    private long timestamp;
    private int namespace;
    private String title;
    private int curID;
    private int userID;
    private String userText;
    private String comment;
    private long textID;
    private long nextTextID;
    private String userIP;
    private int length;

    public RecentChange(long rcID, long timestamp, int namespace, String title,
                        int curID, int userID, String userText, String comment,
                        long textID, long nextTextID, String userIP, int length) {
        this.rcID = rcID;
        this.timestamp = timestamp;
        this.namespace = namespace;
        this.title = title;
        this.curID = curID;
        this.userID = userID;
        this.userText = userText;
        this.comment = comment;
        this.textID = textID;
        this.nextTextID = nextTextID;
        this.userIP = userIP;
        this.length = length;
    }

    public RecentChange(String rec) {
        this(TRUtility.getValue((s)->(Long.parseLong(s)),rec, 0),
                TRUtility.getValue((s)->(Long.parseLong(s)),rec, 1),
                Integer.parseInt(TRUtility.getValue(rec, 2)),
                TRUtility.getValue(rec, 3),
                Integer.parseInt(TRUtility.getValue(rec, 4)),
                Integer.parseInt(TRUtility.getValue(rec, 5)),
                TRUtility.getValue(rec, 6),
                TRUtility.getValue(rec, 7),
                TRUtility.getValue((s)->(Long.parseLong(s)),rec, 8),
                TRUtility.getValue((s)->(Long.parseLong(s)),rec, 9),
                TRUtility.getValue(rec, 10),
                Integer.parseInt(TRUtility.getValue(rec, 11))
        );
    }

    @Override
    public String toString() {
        return rcID + ";" + timestamp + ";" + namespace + ";" +
                title + ";" + curID + ";" + userID + ";" + userText +
                ";" + comment + ";" + textID + ";" + nextTextID
                + ";" + userIP + ";" + length;
    }
}
