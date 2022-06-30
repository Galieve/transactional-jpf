package benchmarks.tpcc.objects;

import database.TRUtility;

public class Warehouse {

    private int ID; // PRIMARY KEY
    private float ytd;
    private String name;

    public Warehouse(int ID, float ytd, String name) {
        this.ID = ID;
        this.ytd = ytd;
        this.name = name;
    }

    public Warehouse(String warehouse) {
        this(Integer.parseInt(TRUtility.getValue(warehouse, 0)),
                Float.parseFloat(TRUtility.getValue(warehouse, 1)),
                TRUtility.getValue(warehouse, 2));
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setYtd(float ytd) {
        this.ytd = ytd;
    }

    @Override
    public String toString() {
        return ID + ";" + ytd + ";" + name;
    }
}


