package benchmarks.wikipedia.procedures;

import benchmarks.wikipedia.Wikipedia;
import benchmarks.wikipedia.WikipediaUtility;
import benchmarks.wikipedia.objects.WatchList;
import database.TRDatabase;

public class AddWatchList extends WikipediaProcedure {

    public AddWatchList(TRDatabase db) {
        super(db);
    }

    public void addWatchList(int userID, int nameSpace, String pageTitle){
        db.begin();

        if(userID > 0){
            insertWatchList(userID, nameSpace, pageTitle);

            if(nameSpace == 0){
                insertWatchList(userID, 1, pageTitle);
            }

            updateUserTouched(userID);
        }

        db.commit();
    }

    private void insertWatchList(int userID, int nameSpace, String pageTitle){
        var w = new WatchList(userID, nameSpace, pageTitle, null);
        var watchListTable = WikipediaUtility.readWatchList(db.read(Wikipedia.WATCHLIST));
        watchListTable.put(userID +":"+nameSpace+":"+pageTitle, w);
        db.write(Wikipedia.WATCHLIST, watchListTable.toString());
    }


}
