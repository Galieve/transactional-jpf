import gov.nasa.jpf.vm.SystemTime;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Test {

    public static String testString(int a, char b){
        return b + " "+ a;
    }

    public static void main(String [] args) throws InterruptedException {
        TRDatabase dbMain = TRDatabase.getDatabase();
        dbMain.begin();
        dbMain.write("x","0");
        dbMain.write("y","0");
        //dbMain.write("z","0");
        dbMain.end();
        dbMain.begin();
        dbMain.write("x","1");
        String x = dbMain.read("x");
        String y = x + x;
        System.out.println(y);
        //dbMain.write("z","0");
        dbMain.end();



        /*
        Thread ser1 = new Thread(() -> {

            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            db.write("x","1");
            db.write("y","1");
            db.end();
        });
        Thread ser2 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            db.read("x","loc:a");
            db.read("y","b");
            db.write("x","loc:a");
            db.read("x", "c");
            db.end();
        });
        Thread ser3 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            db.read("x","c");
            db.read("y","d");
            db.write("y","2");
            db.end();
        });



        ser1.start();
        ser2.start();
        ser3.start();
        ser1.join();
        ser2.join();
        ser3.join();

         */



        /*
        Thread cas1 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            db.write("x","1");
            db.end();
            db.begin();
            //db.read("x","a");
            db.write("x","2");
            db.end();
        });




        Thread cas3 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            db.read("x","b");
            db.write("y","1");
            db.end();
        });
        Thread cas4 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            db.read("x","c");
            db.read("y","d");
            db.end();
        });


        Thread cas2 = new Thread(() -> {

            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            db.read("x","a");
            db.write("x","2");
            db.end();
        });


        cas1.start();
        cas2.start();
        cas3.start();
        cas4.start();



        cas1.join();
        cas2.join();
        cas3.join();
        cas4.join();

         */


        /*
        Thread t1 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            db.write("x","1");
            //db.write("y", "1");
            db.read("x","a");
            //db.read("y","b");
            db.end();
            db.begin();
            db.write("x","10");
            //db.write("y", "10");
            db.end();
        });

        Thread t2 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            //db.write("x","2");
            //db.write("y", "2");
            db.read("x","c");
            db.read("y", "d");
            db.end();
        });

        Thread t3 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            db.read("z", "e");
            db.write("x","3");
            db.write("y","3");
            db.end();
            db.begin();
            db.read("x", "e0");
            db.write("x","30");
            db.write("y","30");
            db.end();

            db.begin();
            db.read("x", "e1");
            db.write("x","31");
            db.write("y","31");
            db.end();


        });

        Thread t4 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            db.read("z", "f");
            db.write("x","4");
            db.write("y","4");
            db.end();
            db.begin();
            db.write("x","40");
            db.write("y", "40");
            db.end();


        });



        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t1.join();
        t2.join();
        t3.join();
        t4.join();

         */

    }
}
