package benchmarks.wikipedia.procedures;

import benchmarks.Procedure;
import benchmarks.wikipedia.Wikipedia;
import benchmarks.wikipedia.WikipediaUtility;
import benchmarks.wikipedia.objects.*;
import database.TRDatabase;

import java.util.ArrayList;

public abstract class WikipediaProcedure extends Procedure {
    public WikipediaProcedure(TRDatabase db) {
        super(db);
    }

    protected void updateUserTouched(int userID, long timestamp){
        var userTable = WikipediaUtility.readUser(db.read(Wikipedia.USER));
        var u = userTable.get(userID+"");
        if(u != null){
            u.setUserTouched(timestamp+"");
            db.write(Wikipedia.USER, userTable.toString());
        }
    }

    protected void updateUserTouched(int userID){
        updateUserTouched(userID, System.currentTimeMillis());
    }

    protected Page selectPage(int pageNamespace, String pageTitle){
        var pageTable = WikipediaUtility.readPage(db.read(Wikipedia.PAGE));
        for(var pes: pageTable.entrySet()){
            var p = pes.getValue();
            if(p.getPageNamespace() == pageNamespace && p.getPageTitle().equals(pageTitle)){
                return p;
            }

        }
        return null;
    }

    protected ArrayList<PageRestriction> selectPageRestriction(int pageId){
        var pageRestrictionTable
                = WikipediaUtility.readPageRestriction(
                db.read(Wikipedia.PAGERESTRICTIONS));
        return pageRestrictionTable.get(pageId+"");
    }

    protected Revision selectPageRevision(int pageID){
        var revisionTable =
                WikipediaUtility.readRevision(
                        db.read(Wikipedia.REVISION));

        var pageTable =
                WikipediaUtility.readPage(db.read(Wikipedia.PAGE));

        var page = pageTable.get(pageID+"");
        if(page == null) return null;

        for(var r: revisionTable.values()){
            if(r.getPageID() == pageID && r.getID() == page.getLatest()){
                return r;
            }
        }
        return null;
    }

    protected Text selectText(long textID){
        var textTable
                = WikipediaUtility.readText(
                db.read(Wikipedia.TEXT));
        var texts = textTable.get(textID+"");
        if(texts == null || texts.isEmpty()) return null;
        else return texts.get(0);
    }

    protected User selectUser(int userID){
        var userTable = WikipediaUtility.
                readUser(db.read(Wikipedia.USER));

        return userTable.get(userID + "");
    }


}
