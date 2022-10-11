package benchmarks.wikipedia.procedures;


import benchmarks.wikipedia.Wikipedia;
import benchmarks.wikipedia.WikipediaUtility;
import benchmarks.wikipedia.objects.*;
import database.AbortDatabaseException;
import database.TRDatabase;

import java.util.ArrayList;

public class GetPageAnonymous extends WikipediaProcedure{
    public GetPageAnonymous(TRDatabase db) {
        super(db);
    }

    public Article getPageAnonymous(boolean forSelect, String userIP,
                                    int pageNamespace, String pageTitle){
        try {
            db.begin();
            var a = getPageAnonymousBody(forSelect, userIP, pageNamespace, pageTitle);
            db.commit();
            return a;

        }
        catch (AbortDatabaseException ignored){
            return null;
        }
    }

    public Article getPageAnonymousBody(boolean forSelect, String userIP,
                                    int pageNamespace, String pageTitle) throws AbortDatabaseException {

        Article a = null;

        var page = selectPage(pageNamespace, pageTitle);
        if(page == null) db.abort();

        var pageID = page.getPageID();
        var pageRestriction = selectPageRestriction(pageID);

        var ipBlocks = selectIPBlocks(userIP);

        var rev = selectPageRevision(pageID);
        if(rev == null) db.abort();


        var textID = rev.getTextID();
        var revisionID = rev.getID();

        var text = selectText(textID);
        if(text == null) db.abort();

        if (!forSelect) {
            a = new Article(userIP, pageID, text.getText(), textID, revisionID);
        }



        return a;
    }


    protected ArrayList<IPBlock> selectIPBlocks(String userIP){
        var ipBlocksTable
                = WikipediaUtility.readIPBlocks(
                db.read(Wikipedia.IPBLOCKS));
        return ipBlocksTable.get(userIP+"");
    }
}
