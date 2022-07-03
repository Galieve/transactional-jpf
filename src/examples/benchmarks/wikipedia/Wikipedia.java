package benchmarks.wikipedia;

import benchmarks.BenchmarkModule;
import benchmarks.wikipedia.objects.Article;
import benchmarks.wikipedia.procedures.*;

import java.util.HashMap;

public class Wikipedia extends BenchmarkModule {

    public static final String WATCHLIST = "WATCHLIST";
    public static final String USER = "USER";
    public static final String PAGE = "PAGE";
    public static final String PAGERESTRICTIONS = "PAGERESTRICTIONS";
    public static final String IPBLOCKS = "IPBLOCKS";
    public static final String REVISION = "REVISION";
    public static final String TEXT = "TEXT";
    public static final String USERGROUPS = "USERGROUPS";
    public static final String RECENTCHANGES = "RECENTCHANGES";
    public static final String LOGGING = "LOGGING";

    private static Wikipedia wikipediaInstance;


    public static Wikipedia getInstance(){
        if(wikipediaInstance == null){
            wikipediaInstance = new Wikipedia();
        }
        return wikipediaInstance;
    }

    @Override
    public HashMap<String, Class<?>[]> getAllMethods() {

        var map = new HashMap<String, Class<?>[]>();
        map.put("addWatchList", new Class<?>[]{int.class, int.class, String.class});
        map.put("removeWatchList", new Class<?>[]{int.class, int.class, String.class});
        map.put("getPageAnonymous", new Class<?>[]{boolean.class, String.class, int.class, String.class});
        map.put("getPageAuthenticated",new Class<?>[]{boolean.class, String.class, int.class, int.class, String.class});
        map.put("updatePage", new Class<?>[]{String.class, String.class, int.class, int.class, String.class, String.class, int.class});

        return map;
    }

    public void addWatchList(int userID, int nameSpace, String pageTitle){
        new AddWatchList(db).addWatchList(userID,nameSpace,pageTitle);
    }

    public void removeWatchList(int userID, int nameSpace, String pageTitle){
        new RemoveWatchList(db).removeWatchList(userID,nameSpace,pageTitle);
    }

    public Article getPageAnonymous(boolean forSelect, String userIP,
                                    int pageNamespace, String pageTitle){
        return new GetPageAnonymous(db).getPageAnonymous(forSelect, userIP, pageNamespace, pageTitle);
    }

    public Article getPageAuthenticated(boolean forSelect, String userIP, int userID,
                                    int pageNamespace, String pageTitle){
        return new GetPageAuthenticated(db).getPageAuthenticated(forSelect, userIP, userID, pageNamespace, pageTitle);
    }

    public void updatePage(String pageTitle, String pageText,
                           int pageNamespace, int userID, String userIP, String revComment, int revMinorEdit){
        db.begin();
        var a = new GetPageAnonymous(db).getPageAnonymousBody(false, userIP, pageNamespace, pageTitle);
        if(a != null) {
            new UpdatePage(db).updatePageBody(a.getTextID(), a.getPageID(), pageTitle, pageText, pageNamespace,
                    userID, userIP, a.getText(), a.getRevisionID(), revComment, revMinorEdit);
        }

        db.end();
    }



}
