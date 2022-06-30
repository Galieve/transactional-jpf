package benchmarks.wikipedia.objects;

import database.TRUtility;

public class Revision {
    private int userID;
    private String userText;
    private long timestamp;
    private int length;
    private int revMinorEdit;
    private String revComment;
    private int pageID;
    private long ID;
    private long parentID;
    private long textID;

    public Revision(long ID, int pageID, long textID, String revComment,
                    int revMinorEdit, int userID, String userText, long timestamp,
                    int length, long parentID) {

        this.pageID = pageID;
        this.textID = textID;
        this.revComment = revComment;
        this.revMinorEdit = revMinorEdit;
        this.userID = userID;
        this.userText = userText;
        this.timestamp = timestamp;
        this.length = length;
        this.ID = ID;
        this.parentID = parentID;
    }

    public Revision(String rev){
        this(
                TRUtility.getValue((s)->(Long.parseLong(s)),rev, 0),
                Integer.parseInt(TRUtility.getValue(rev, 1)),
                TRUtility.getValue((s)->(Long.parseLong(s)),rev, 2),
                TRUtility.getValue(rev, 3),
                Integer.parseInt(TRUtility.getValue(rev, 4)),
                Integer.parseInt(TRUtility.getValue(rev, 5)),
                TRUtility.getValue(rev, 6),
                TRUtility.getValue((s)->(Long.parseLong(s)),rev, 7),
                Integer.parseInt(TRUtility.getValue(rev, 8)),
                TRUtility.getValue((s)->(Long.parseLong(s)),rev, 9)
        );
    }

    @Override
    public String toString() {
        return this.ID + ";" + this.pageID + ";" + this.textID + ";" + this.revComment + ";" +
            this.revMinorEdit + ";" + this.userID + ";" +  this.userText + ";" +
            this.timestamp + ";" + this.length + ";" + this.parentID;
    }

    public long getID() {
        return ID;
    }

    public long getTextID() {
        return textID;
    }

    public int getPageID() {
        return pageID;
    }
}
