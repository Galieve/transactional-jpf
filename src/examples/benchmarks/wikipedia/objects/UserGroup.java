package benchmarks.wikipedia.objects;

import database.TRUtility;

public class UserGroup {

    private int userID;
    private String groupName;

    public UserGroup(int userID, String groupName) {
        this.userID = userID;
        this.groupName = groupName;
    }

    public UserGroup(String gro) {
        this(
            Integer.parseInt(TRUtility.getValue(gro, 0)),
            TRUtility.getValue(gro, 1)
        );
    }

    @Override
    public String toString() {
        return userID+";"+groupName;
    }
}
