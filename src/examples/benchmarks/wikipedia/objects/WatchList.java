package benchmarks.wikipedia.objects;

import database.TRUtility;

public class WatchList {
    private int userID;
    private int namespace;
    private String title;
    private Long notificationStamp;

    public WatchList(int userID, int namespace,
                     String title, Long notificationStamp) {
        this.userID = userID;
        this.namespace = namespace;
        this.title = title;
        this.notificationStamp = notificationStamp;
    }

    public WatchList(String watchlist){
        this(
            Integer.parseInt(TRUtility.getValue(watchlist, 0)),
            Integer.parseInt(TRUtility.getValue(watchlist, 1)),
            TRUtility.getValue(watchlist, 2),
            TRUtility.getValue((s)->(Long.parseLong(s)),watchlist, 3)
        );
    }

    @Override
    public String toString() {
        return  userID +
                ";" + namespace +
                ";" + title + ";" + notificationStamp;
    }

    public int getUserID() {
        return userID;
    }

    public int getNamespace() {
        return namespace;
    }

    public String getTitle() {
        return title;
    }

    public Long getNotificationStamp() {
        return notificationStamp;
    }

    public void setNotificationStamp(Long notificationStamp) {
        this.notificationStamp = notificationStamp;
    }
}
