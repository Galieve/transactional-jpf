package benchmarks.tpcc.objects;

import database.TRUtility;

public class Item {

    private int itemID;

    private int imID;

    private float price;

    public Item(int itemID, int imID, float price) {
        this.itemID = itemID;
        this.imID = imID;
        this.price = price;
    }

    public Item(String item) {
        this(Integer.parseInt(TRUtility.getValue(item, 0)),
                Integer.parseInt(TRUtility.getValue(item, 1)),
                Float.parseFloat(TRUtility.getValue(item, 2)));
    }

    @Override
    public String toString() {
        return  itemID + ";" + imID+ ";" + price;
    }


    public float getPrice() {
        return price;
    }
}
