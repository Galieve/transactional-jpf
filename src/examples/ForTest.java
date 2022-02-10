import java.util.*;

public class ForTest {

    public static void main(String [] args) throws InterruptedException {

        TRDatabase dbMain = TRDatabase.getDatabase();
        dbMain.begin();
        dbMain.write("x","0");
        dbMain.write("y", "0");
        dbMain.end();



        Thread t1 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            String s = db.read("y");
            System.out.println(s);
            db.end();
        });
        Thread t2 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            String x = db.read("x");
            if(x.equals("0")) {
                db.write("x", "found!");
            }
            else{
                db.write("x", "notFound!");
            }
            db.end();
        });
        Thread t3 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            db.write("y","3");
            db.end();
        });

        Thread t4 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            db.write("x", "4");
            db.end();
        });



        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t4.join();
        t3.join();
        t2.join();
        t1.join();








    }
}
