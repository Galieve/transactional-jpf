package benchmarks.wikipedia.objects;

import database.TRUtility;

public class Article {

    private String userIP;
    private int pageID;
    private String text;
    private long textID;
    private long revisionID;

    public Article(String userIP, int pageID, String text, long textID, long revisionID) {
        this.userIP = userIP;
        this.pageID = pageID;
        this.text = text;
        this.textID = textID;
        this.revisionID = revisionID;
    }

    public Article(String art){
        this(TRUtility.getValue(art, 0),
                Integer.parseInt(TRUtility.getValue(art, 1)),
                TRUtility.getValue(art, 2),
                Long.parseLong(TRUtility.getValue(art, 3)),
                TRUtility.getValue((s)->(Long.parseLong(s)),art, 4)
        );
    }

    public int getPageID() {
        return pageID;
    }

    public String getText() {
        return text;
    }

    public long getTextID() {
        return textID;
    }

    public long getRevisionID() {
        return revisionID;
    }
}
