package shoppingCart;

import database.TRUtility;

public class ShoppingItem extends Item{

    private int quantity;

    public ShoppingItem(String s) {
        super(s);
        quantity = Integer.parseInt(TRUtility.getValue(s, 3));
    }

    public ShoppingItem(Item i){
        super(i);
        quantity = 1;
    }

    @Override
    public String toString() {
        return super.toString() + ";"+quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
