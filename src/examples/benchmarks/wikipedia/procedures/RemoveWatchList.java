package benchmarks.wikipedia.procedures;

import benchmarks.wikipedia.Wikipedia;
import benchmarks.wikipedia.WikipediaUtility;
import database.TRDatabase;

public class RemoveWatchList extends WikipediaProcedure {
    public RemoveWatchList(TRDatabase db) {
        super(db);
    }

    public void removeWatchList(int userID, int nameSpace, String pageTitle){
        db.begin();

        if(userID > 0){
            deleteWatchList(userID, nameSpace, pageTitle);

            if(nameSpace == 0){
                deleteWatchList(userID, 1, pageTitle);
            }

            updateUserTouched(userID);
        }

        db.end();
    }

    private void deleteWatchList(int userID, int nameSpace, String pageTitle){
        var watchListTable = WikipediaUtility.readWatchList(db.read(Wikipedia.WATCHLIST));
        if(watchListTable.containsKey(userID +":"+nameSpace+":"+pageTitle)) {
            watchListTable.remove(userID + ":" + nameSpace + ":" + pageTitle);
            db.write(Wikipedia.WATCHLIST, watchListTable.toString());
        }
    }
}
