package benchmarks;

import database.APIDatabase;

public class SimpleTest {
    
    public static void doOperations11(){
        var db = APIDatabase.getDatabase();
        db.begin();
        db.write("x", "1");
        db.write("y", "1");
        db.commit();
    }

    public static void doOperations12(){
        var db = APIDatabase.getDatabase();
        db.begin();
        var a = db.read("y");
        if(a.equals("0")) {
            db.write("y", "-1");
            System.out.println(a);
        }
        db.commit();
    }

    public static void doOperations2(){
        var db = APIDatabase.getDatabase();
        db.begin();
        var a = db.read("x");
        var b = db.read("y");
        db.write("z", a + b);
        db.commit();
    }

    public static void doOperations3(){
        var db = APIDatabase.getDatabase();
        db.begin();
        db.read("x");
        db.read("y");
        db.read("z");
        db.commit();
    }

    public static void main(String [] args) throws InterruptedException {
        var db = APIDatabase.getDatabase();
        db.begin();
        db.write("x", "0");
        db.write("y", "0");
        db.write("z", "0");

        db.commit();

        Thread t1 = new Thread(() -> {
            doOperations11();
            doOperations12();
        });

        Thread t2 = new Thread(() -> {
            doOperations2();
        });

        Thread t3 = new Thread(() -> doOperations3());


        t1.start();
        t2.start();
        t3.start();
        t3.join();
        t2.join();
        t1.join();


    }
}