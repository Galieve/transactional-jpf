package benchmarks.wikipedia.objects;

import database.TRUtility;

public class User {
    private int userID;
    private String userTouched;
    private String name;
    private int editCount;

    public User(int userID, String userTouched, String name, int editCount) {
        this.userID = userID;
        this.userTouched = userTouched;
        this.name = name;
        this.editCount = editCount;
    }

    public User(String u) {
        this(
            Integer.parseInt(TRUtility.getValue(u, 0)),
            TRUtility.getValue(u, 1),
            TRUtility.getValue(u, 2),
            Integer.parseInt(TRUtility.getValue(u, 3))
        );
    }

    @Override
    public String toString() {
        return userID+";"+userTouched+";"+name+";"+editCount;
    }

    public String getName() {
        return name;
    }

    public void setEditCount(int editCount) {
        this.editCount = editCount;
    }

    public int getEditCount() {
        return editCount;
    }

    public void setUserTouched(String userTouched) {
        this.userTouched = userTouched;
    }

}
