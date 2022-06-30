package benchmarks.wikipedia.objects;

import database.TRUtility;

public class Page {

    private int pageID;
    private int pageNamespace;
    private String pageTitle;
    private long latest;
    private int isNew;
    private int isRedirect;
    private int length;
    private long touched;

    public Page(int pageID,int pageNamespace, String pageTitle,  long latest, int isNew, int isRedirect, int length, long touched) {
        this.pageNamespace = pageNamespace;
        this.pageTitle = pageTitle;
        this.pageID = pageID;
        this.latest = latest;
        this.isNew = isNew;
        this.isRedirect = isRedirect;
        this.length = length;
        this.touched = touched;
    }

    public Page(String pag) {
        this(Integer.parseInt(TRUtility.getValue(pag, 0)),
                Integer.parseInt(TRUtility.getValue(pag, 1)),
                TRUtility.getValue(pag, 2),
                Long.parseLong(TRUtility.getValue(pag, 3)),
                Integer.parseInt(TRUtility.getValue(pag, 4)),
                Integer.parseInt(TRUtility.getValue(pag, 5)),
                Integer.parseInt(TRUtility.getValue(pag, 6)),
                TRUtility.getValue((s)->(Long.parseLong(s)),pag, 7)
        );
    }

    @Override
    public String toString() {
        return pageID + ";" + pageNamespace + ";" + pageTitle + ";" + latest +
                ";" + isNew + ";" + isRedirect + ";" + length +";" + touched;
    }

    public int getPageID() {
        return pageID;
    }

    public long getLatest() {
        return latest;
    }

    public int getPageNamespace() {
        return pageNamespace;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setLatest(long latest) {
        this.latest = latest;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public void setIsRedirect(int isRedirect) {
        this.isRedirect = isRedirect;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setTouched(long touched) {
        this.touched = touched;
    }
}
