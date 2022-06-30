package benchmarks.wikipedia.objects;

import database.TRUtility;

public class PageRestriction {

    private int pageID;

    public PageRestriction(int pageID) {
        this.pageID = pageID;
    }

    public PageRestriction(String p) {
        this(Integer.parseInt(TRUtility.getValue(p,0)));
    }

    @Override
    public String toString() {
        return pageID +"";
    }
}
