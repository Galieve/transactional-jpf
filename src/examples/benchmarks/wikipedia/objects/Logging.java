package benchmarks.wikipedia.objects;

import database.TRUtility;

public class Logging {

    private long id;
    private long timestamp;
    private int userID;
    private String title;
    private int namespace;
    private String userText;
    private int pageID;
    private long nextRevID;
    private long revisionID;

    public Logging(long id, long timestamp, int userID, String title, int namespace, String userText, int pageID, long nextRevID, long revisionID) {
        this.id = id;
        this.timestamp = timestamp;
        this.userID = userID;
        this.title = title;
        this.namespace = namespace;
        this.userText = userText;
        this.pageID = pageID;
        this.nextRevID = nextRevID;
        this.revisionID = revisionID;
    }

    public Logging(String log) {
        this(TRUtility.getValue((s)->(Long.parseLong(s)),log, 0),
                TRUtility.getValue((s)->(Long.parseLong(s)),log, 1),
                Integer.parseInt(TRUtility.getValue(log, 2)),
                TRUtility.getValue(log, 3),
                Integer.parseInt(TRUtility.getValue(log, 4)),
                TRUtility.getValue(log, 5),
                Integer.parseInt(TRUtility.getValue(log, 6)),
                TRUtility.getValue((s)->(Long.parseLong(s)),log, 7),
                TRUtility.getValue((s)->(Long.parseLong(s)),log, 8)
        );
    }

    @Override
    public String toString() {
        return id + ";" + timestamp + ";" + userID + ";" + title +
                ";" + namespace + ";" + userText + ";" + pageID +
                ";" + nextRevID + ";" + revisionID;
    }
}
