package benchmarks.wikipedia.procedures;


import benchmarks.wikipedia.Wikipedia;
import benchmarks.wikipedia.WikipediaUtility;
import benchmarks.wikipedia.objects.Article;
import benchmarks.wikipedia.objects.IPBlock;
import benchmarks.wikipedia.objects.User;
import benchmarks.wikipedia.objects.UserGroup;
import database.TRDatabase;

import java.util.ArrayList;

public class GetPageAuthenticated extends WikipediaProcedure{
    public GetPageAuthenticated(TRDatabase db) {
        super(db);
    }

    public Article getPageAuthenticated(boolean forSelect, String userIP, int userID,
                                    int pageNamespace, String pageTitle){

        Article a = null;
        db.begin();

        var userText = userIP;
        User user = null;
        if(userID > 0) {
            user = selectUser(userID);
            if(user != null) {
                userText = user.getName();
                var userGroups = selectGroup(userID);
            }
        }
        if(user != null) {


            var page = selectPage(pageNamespace, pageTitle);
            if (page != null) {
                var pageID = page.getPageID();
                var pageRestriction = selectPageRestriction(pageID);

                var ipBlocks = selectIPBlocks(userID);

                var rev = selectPageRevision(pageID);
                if (rev != null) {


                    var textID = rev.getTextID();
                    var revisionID = rev.getID();

                    var text = selectText(textID);

                    if (text != null && !forSelect) {
                        a = new Article(userIP, pageID, text.getText(), textID, revisionID);
                    }
                }
            }
        }
        db.end();

        return a;
    }


    protected ArrayList<IPBlock> selectIPBlocks(int userID){
        var ipBlocksTable
                = WikipediaUtility.readIPBlocks(
                db.read(Wikipedia.IPBLOCKS));

        var ipBlocks = new ArrayList<IPBlock>();
        for(var ipblockList : ipBlocksTable.values()){
            for(var i : ipblockList){
                if(i.getUserID() == userID){
                    ipBlocks.add(i);
                }
            }
        }
        return ipBlocks;
    }

    protected ArrayList<UserGroup> selectGroup(int userID){
        var userGroupsTable =
                WikipediaUtility.readUserGroup(db.read(Wikipedia.USERGROUPS));
        return userGroupsTable.get(userID+"");
    }
}
