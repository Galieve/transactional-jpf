package benchmarks.wikipedia.procedures;

import benchmarks.wikipedia.Wikipedia;
import benchmarks.wikipedia.WikipediaUtility;
import benchmarks.wikipedia.objects.Logging;
import benchmarks.wikipedia.objects.RecentChange;
import benchmarks.wikipedia.objects.Revision;
import benchmarks.wikipedia.objects.Text;
import database.TRDatabase;

import java.util.ArrayList;

public class UpdatePage extends WikipediaProcedure{
    public UpdatePage(TRDatabase db) {
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
        var textTable = WikipediaUtility.readText(
                db.read(Wikipedia.TEXT));
        //new ID would be maximumID +1
        long maxID = Long.MIN_VALUE;
        for(var t: textTable.keySet()){
            maxID = Math.max(maxID, Long.parseLong(t));
        }
        maxID+= 1;
        var txt = new Text(maxID, pageID, pageText, "utf-8");

        textTable.putIfAbsent(maxID+"", new ArrayList<>());
        textTable.get(maxID+"").add(txt);
        db.write(Wikipedia.TEXT, textTable.toString());
        return maxID;
    }

    protected long insertRevision(int pageID, long textID, String revComment, int revMinorEdit,
                                  int userID, String userText, long timestamp,
                                  String pageText, long parentID){

        var revisionTable = WikipediaUtility.readRevision(
                db.read(Wikipedia.REVISION));



        //new ID would be maximumID +1
        long maxID = Long.MIN_VALUE;
        for(var t: revisionTable.keySet()){
            maxID = Math.max(maxID, Long.parseLong(t));
        }
        maxID+= 1;
        var revision = new Revision(maxID, pageID, textID, revComment, revMinorEdit, userID,
                userText, timestamp, pageText.length(), parentID);

        revisionTable.put(maxID+"",revision);
        db.write(Wikipedia.REVISION, revisionTable.toString());
        return maxID;
    }

    protected void updatePage(long nextRevID, long timestamp, String pageText, int pageID){
        var pageTable = WikipediaUtility.readPage(db.read(Wikipedia.PAGE));
        var page = pageTable.get(pageID+"");
        if(page == null) return;

        page.setIsNew(0);
        page.setIsRedirect(0);
        page.setLatest(nextRevID);
        page.setTouched(timestamp);
        page.setLength(pageText.length());

        db.write(Wikipedia.PAGE, pageTable.toString());


    }

    protected ArrayList<Integer> selectWatchList(String pageTitle, int pageNamespace, int userID){
        var watchListTable = WikipediaUtility.readWatchList(db.read(Wikipedia.WATCHLIST));
        var watchListUser = new ArrayList<Integer>();
        for(var wl: watchListTable.values()){
            if(wl.getTitle().equals(pageTitle) && wl.getNamespace() == pageNamespace
                    && wl.getUserID() != userID && wl.getNotificationStamp() == null){
                watchListUser.add(wl.getUserID());
            }
        }
        return watchListUser;
    }

    private void updateWatchList(int userID, int nameSpace, String pageTitle, long timestamp){

        var watchListTable = WikipediaUtility.readWatchList(db.read(Wikipedia.WATCHLIST));
        var w = watchListTable.get(userID +":"+nameSpace+":"+pageTitle);
        w.setNotificationStamp(timestamp);
        db.write(Wikipedia.WATCHLIST, watchListTable.toString());
    }

    private void updateUserEditCount(int userID){
        var userTable = WikipediaUtility.readUser(db.read(Wikipedia.USER));
        var u = userTable.get(userID+"");
        if(u != null){
            u.setEditCount(u.getEditCount()+1);
            db.write(Wikipedia.USER, userTable.toString());
        }
    }

    private void insertRecentChanges(long timestamp, int namespace, String title, int curID,
                                     int userID, String userText, String comment, long textID,
                                     long nextTextID, String userIP, int length){
        var recentChangesTable =
                WikipediaUtility.readRecentChanges(db.read(Wikipedia.RECENTCHANGES));
        long ID = Long.MIN_VALUE;
        for(var rc : recentChangesTable.keySet()){
            ID = Math.max(ID, Long.parseLong(rc));
        }
        var r = new RecentChange(ID, timestamp, namespace, title, curID, userID,
                userText, comment, textID, nextTextID, userIP, length);
        recentChangesTable.put(ID+"", r);
        db.write(Wikipedia.RECENTCHANGES, recentChangesTable.toString());
    }

    private void insertLogging(long timestamp, int userID, String title, int namespace,
                                      String userText, int pageID, long nextRevID, long revisionID){
        var loggingTable =
                WikipediaUtility.readLogging(db.read(Wikipedia.LOGGING));
        long ID = Long.MIN_VALUE;
        for(var rc : loggingTable.keySet()){
            ID = Math.max(ID, Long.parseLong(rc));
        }
        var l = new Logging(ID, timestamp, userID, title, namespace,
                userText, pageID, nextRevID, revisionID);
        loggingTable.put(ID+"", l);
        db.write(Wikipedia.LOGGING, loggingTable.toString());
    }

}
