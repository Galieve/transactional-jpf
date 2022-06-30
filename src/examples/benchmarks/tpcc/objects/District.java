package benchmarks.tpcc.objects;

import database.TRUtility;

public class District {

    private int warehouseID;//Primary key 2?
    private int ID; //Primary key 1?
    private int nextOrderID;
    private float ytd;
    private String name;

    public District(int warehouseID, int ID, int nextOrderID, float ytd, String name) {
        this.ID = ID;
        this.warehouseID = warehouseID;
        this.nextOrderID = nextOrderID;
        this.ytd = ytd;
        this.name = name;
    }

    public District(String district) {
        this(Integer.parseInt(TRUtility.getValue(district, 0)),
                Integer.parseInt(TRUtility.getValue(district, 1)),
                Integer.parseInt(TRUtility.getValue(district, 2)),
                Float.parseFloat(TRUtility.getValue(district, 3)),
                TRUtility.getValue(district, 4));
    }
    @Override
    public String toString() {
        return  warehouseID+ ";" + ID+ ";" + nextOrderID
                + ";" + ytd + ";" + name;
    }


    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public int getNextOrderID() {
        return nextOrderID;
    }

    public void setYtd(float ytd) {
        this.ytd = ytd;
    }

    public void setNextOrderID(int nextOrderID) {
        this.nextOrderID = nextOrderID;
    }
}
