public class ShoppingCart {

    public static void addItem(){
        TRDatabase db = TRDatabase.getDatabase();
        db.begin();
        int n = Integer.parseInt(db.read("cartNumber"));
        db.write("cartNumber", String.valueOf(n+1));
        db.end();
    }

    public static void deleteItem(){
        TRDatabase db = TRDatabase.getDatabase();
        db.begin();
        int n = Integer.parseInt(db.read("cartNumber"));
        db.write("cartNumber", String.valueOf(n-1));
        db.end();
    }


    public static void main(String [] args) throws InterruptedException {
        TRDatabase dbMain = TRDatabase.getDatabase();
        dbMain.begin();
        dbMain.write("cartNumber","1");
        dbMain.end();


        Thread ses1 = new Thread(() -> {
            addItem();
        });
        Thread ses2 = new Thread(() -> {
            deleteItem();
        });
        Thread ses3 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            db.read("cartNumber");
            db.end();

            db.begin();
            db.read("cartNumber");
            db.end();


        });

        ses1.start();
        ses2.start();
        ses3.start();
        ses1.join();
        ses2.join();
        ses3.join();



    }


}
