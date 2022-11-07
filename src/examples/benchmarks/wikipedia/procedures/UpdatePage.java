package benchmarks.wikipedia.procedures;

import benchmarks.wikipedia.Wikipedia;
import benchmarks.wikipedia.objects.*;
import database.APIDatabase;

import java.util.ArrayList;

public class UpdatePage extends WikipediaProcedure{
    public UpdatePage(APIDatabase db) {
        super(db);
    }

    public void updatePageBody(long textID, int pageID, String pageTitle, String pageText,
                           int pageNamespace, int userID, String userIP, String userText,
                           long revisionID, String revComment, int revMinorEdit){

        var timestamp = System.currentTimeMillis();

        var nextTextID = insertText(pageID, pageText);

        var nextRevisionID = insertRevision(pageID,textID,revComment, revMinorEdit, userID,
                userText, timestamp, pageText, revisionID);

        updatePage(nextRevisionID, timestamp, pageText, pageID);

        insertRecentChanges(timestamp, pageNamespace, pageTitle, pageID, userID, userText,
                revComment, nextTextID, textID, userIP, pageText.length());

        var usersWL = selectWatchList(pageTitle, pageNamespace, userID);

        for(var otherUserID: usersWL){
            updateWatchList(otherUserID,pageNamespace, pageTitle, timestamp);
            selectUser(otherUserID);
        }

        insertLogging(timestamp,userID, pageTitle, pageNamespace, userText, pageID, nextRevisionID, revisionID);
        updateUserEditCount(userID);
        updateUserTouched(userID, timestamp);


    }

    protected long insertText(int pageID, String pageText){

        var maxID = getFreeID(Wikipedia.TEXT);

        var txt = new Text(maxID, pageID, pageText, "utf-8");

        db.insertRow(Wikipedia.TEXT, maxID+"", txt.toString());
        return maxID;
    }

    protected long insertRevision(int pageID, long textID, String revComment, int revMinorEdit,
                                  int userID, String userText, long timestamp,
                                  String pageText, long parentID){


       var maxID = getFreeID(Wikipedia.REVISION);

        var revision = new Revision(maxID, pageID, textID, revComment, revMinorEdit, userID,
                userText, timestamp, pageText.length(), parentID);

        db.insertRow(Wikipedia.REVISION, maxID+"", revision.toString());
        return maxID;
    }

    protected void updatePage(long nextRevID, long timestamp, String pageText, int pageID){

        var pageSt = db.readRow(Wikipedia.PAGE, pageID+"");
        if(pageSt == null) return;

        var page = new Page(pageSt);

        page.setIsNew(0);
        page.setIsRedirect(0);
        page.setLatest(nextRevID);
        page.setTouched(timestamp);
        page.setLength(pageText.length());

        db.writeRow(Wikipedia.PAGE, pageID+"", page.toString());


    }

    protected ArrayList<Integer> selectWatchList(String pageTitle, int pageNamespace, int userID){

        var wlStr =  db.readIfIDEndsWith(Wikipedia.WATCHLIST, pageNamespace+":"+pageTitle);
        var watchListUser = new ArrayList<Integer>();
        for(var wls: wlStr){
            var wl = wls == null ? null : new WatchList(wls);
            if(wl != null && wl.getUserID() != userID && wl.getNotificationStamp() == null){
                watchListUser.add(wl.getUserID());
            }
        }
        return watchListUser;
    }

    private void updateWatchList(int userID, int nameSpace, String pageTitle, long timestamp){

        var wl = db.readRow(Wikipedia.WATCHLIST, userID +":"+nameSpace+":"+pageTitle);
        if(wl == null ) return;
        var w = new WatchList(wl);
        w.setNotificationStamp(timestamp);
        db.writeRow(Wikipedia.WATCHLIST,userID +":"+nameSpace+":"+pageTitle, w.toString());
    }

    private void updateUserEditCount(int userID){

        var uSt = db.readRow(Wikipedia.USER, userID+"");
        if(uSt == null) return;
        var u = new User(uSt);
        u.setEditCount(u.getEditCount()+1);
        db.writeRow(Wikipedia.USER, userID+"", u.toString());

    }

    private void insertRecentChanges(long timestamp, int namespace, String title, int curID,
                                     int userID, String userText, String comment, long textID,
                                     long nextTextID, String userIP, int length){

        var ID = getFreeID(Wikipedia.RECENTCHANGES);

        var r = new RecentChange(ID, timestamp, namespace, title, curID, userID,
                userText, comment, textID, nextTextID, userIP, length);
        db.insertRow(Wikipedia.RECENTCHANGES, ID+"", r.toString());
    }

    private void insertLogging(long timestamp, int userID, String title, int namespace,
                                      String userText, int pageID, long nextRevID, long revisionID){


        var ID = getFreeID(Wikipedia.LOGGING);

        var l = new Logging(ID, timestamp, userID, title, namespace,
                userText, pageID, nextRevID, revisionID);

        db.insertRow(Wikipedia.LOGGING, ID+"", l.toString());
    }

}
