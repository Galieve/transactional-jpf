package benchmarks.wikipedia;

import benchmarks.wikipedia.objects.*;
import database.TRUtility;

import java.util.ArrayList;
import java.util.HashMap;

public class WikipediaUtility {

    public static HashMap<String, WatchList> readWatchList(String wl) {
        return TRUtility.generateHashMap(wl, (w)->(new WatchList(w)));
    }

    public static HashMap<String, User> readUser(String ul) {
        return TRUtility.generateHashMap(ul, (u)->(new User(u)));
    }

    public static HashMap<String, Page> readPage(String pageList) {
        return TRUtility.generateHashMap(pageList, (p)->(new Page(p)));
    }

    public static HashMap<String, ArrayList<PageRestriction>> readPageRestriction(String pageRestrictionList) {
        return TRUtility.generateHashMap(pageRestrictionList, (pl)->(TRUtility.generateArrayList(pl, (p)->(new PageRestriction(p)))));
    }

    public static HashMap<String, ArrayList<IPBlock>> readIPBlocks(String ipBlocks) {
        return TRUtility.generateHashMap(ipBlocks, (il)->(TRUtility.generateArrayList(il, (i)->(new IPBlock(i)))));
    }

    public static HashMap<String, Revision> readRevision(String revision) {
        return TRUtility.generateHashMap(revision,(r)->(new Revision(r)));
    }

    public static HashMap<String, ArrayList<Text>> readText(String txt) {
        return TRUtility.generateHashMap(txt, (tl)->(TRUtility.generateArrayList(tl, (t)->(new Text(t)))));
    }

    public static HashMap<String, ArrayList<UserGroup>> readUserGroup(String userGroups) {
        return TRUtility.generateHashMap(userGroups, (ul)->(TRUtility.generateArrayList(ul, (g->(new UserGroup(g))))));
    }

    public static HashMap<String, RecentChange> readRecentChanges(String read) {
        return TRUtility.generateHashMap(read, (r)->(new RecentChange(r)));
    }

    public static HashMap<String, Logging> readLogging(String log) {
        return TRUtility.generateHashMap(log, (l)->(new Logging(l)));
    }
}
