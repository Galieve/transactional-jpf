package benchmarks.shoppingCart;

import database.TRUtility;

public class Item {

    private String name;

    private String id;

    private double price;

    public Item(String id,String name,  double price) {
        this.name = name;
        this.id = id;
        this.price = price;
    }

    public Item(String s){
        this(TRUtility.getValue(s, 0),TRUtility.getValue(s, 1), Double.parseDouble(TRUtility.getValue(s, 2)));
    }

    public Item(Item i) {
        this(i.name, i.id, i.price);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return id+";"+name +";"+price;
    }

}
