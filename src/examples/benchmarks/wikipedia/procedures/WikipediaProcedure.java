package benchmarks.wikipedia.procedures;

import benchmarks.Procedure;
import benchmarks.wikipedia.Wikipedia;
import benchmarks.wikipedia.objects.*;
import database.APIDatabase;

import java.util.ArrayList;

public abstract class WikipediaProcedure extends Procedure {
    public WikipediaProcedure(APIDatabase db) {
        super(db);
    }

    protected void updateUserTouched(int userID, long timestamp){

        var uSt = db.readRow(Wikipedia.USER, userID+"");
        if(uSt == null) return;

        var u = new User(uSt);
        u.setUserTouched(timestamp+"");
        db.writeRow(Wikipedia.USER, userID+"", u.toString());
    }

    protected void updateUserTouched(int userID){
        updateUserTouched(userID, System.currentTimeMillis());
    }

    protected Page selectPage(int pageNamespace, String pageTitle){
        var pageRestSt = db.readAll(Wikipedia.PAGE);
        for(var pr : pageRestSt){
            var p = pr == null ? null : new Page(pr);
            if(p.getPageNamespace() == pageNamespace && p.getPageTitle().equals(pageTitle)){
                return p;
            }
        }
        return null;
    }

    protected ArrayList<PageRestriction> selectPageRestriction(int pageId){

        var pageRestSt = db.readIfIDStartsWith(Wikipedia.PAGERESTRICTIONS, pageId+"");
        var pageRes = new ArrayList<PageRestriction>();
        for(var pr : pageRestSt){
            var p = pr == null ? null : new PageRestriction(pr);
            pageRes.add(p);
        }
        return pageRes;
    }

    protected Revision selectPageRevision(int pageID){

        var revisions = db.readAll(Wikipedia.REVISION);

        var pageSt = db.readRow(Wikipedia.PAGE, pageID+"");

        if(pageSt == null) return null;
        var page = new Page(pageSt);

        for(var rev: revisions){
            var r = rev == null ? null : new Revision(rev);
            if(r != null && r.getPageID() == pageID && r.getID() == page.getLatest()){
                return r;
            }
        }
        return null;
    }

    protected Text selectText(long textID){


        var tSt = db.readRow(Wikipedia.TEXT, textID+"");
        return tSt == null ? null : new Text(tSt);
    }

    protected User selectUser(int userID){

        var uSt = db.readRow(Wikipedia.USER, userID+"");
        return uSt == null ? null : new User(uSt);
    }

    protected Long getFreeID(String tableName){
        var ids = db.readAllIDs(tableName);
        // We have two reads of the same variable, so under CC, RC it should work
        Long maxID = Long.MIN_VALUE;
        for(var i : ids){
            if(i.matches("-?\\d+")){
                maxID = Math.max(Long.parseLong(i) + 1, maxID);
            }
        }
        return maxID;
    }


}
