package benchmarks.wikipedia.objects;

import database.TRUtility;

public class IPBlock {

    private String address;
    private int userID;

    public IPBlock(String address, int userID) {
        this.address = address;
        this.userID = userID;
    }

    public IPBlock(String ipb) {
        this(TRUtility.getValue(ipb, 0),
                Integer.parseInt(TRUtility.getValue(ipb, 1))
        );
    }

    public int getUserID() {
        return userID;
    }

    @Override
    public String toString() {
        return this.address+";"+this.userID;
    }
}
