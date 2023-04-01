package benchmarks.twitter;

import database.APIDatabase;

public class TestTwitter2 {



    public static void doOperations11(){
        var db = APIDatabase.getDatabase();
        db.begin();
        db.read("x");
        db.read("a");
        db.read("y");
        db.read("b");
        db.read("z");
        db.write("z", "11");
        db.commit();
    }

    public static void doOperations1(){
        var db = APIDatabase.getDatabase();
        db.begin();
        db.read("y");
        db.read("a");
        db.commit();
    }

    public static void doOperations21(){
        var db = APIDatabase.getDatabase();
        db.begin();
        db.read("y");
        db.read("b");
        db.commit();
    }

    public static void doOperations22(){
        var db = APIDatabase.getDatabase();
        db.begin();
        db.read("y");
        db.read("a");
        db.read("y");
        db.write("a", "3");
        db.read("y");
        db.write("y", "3");
        db.read("x");
        db.commit();
    }

    public static void doOperations3(){
        var db = APIDatabase.getDatabase();
        db.begin();
        db.read("y");
        db.read("b");
        db.read("y");
        db.write("b", "3");
        db.read("y");
        db.write("y", "3");
        db.read("x");
        db.commit();
    }


    public static void main(String [] args) throws InterruptedException {
        var db = APIDatabase.getDatabase();
        db.begin();
        db.write("x", "0");
        db.write("y", "0");
        db.write("z", "0");
        db.write("w", "0");
        db.write("a", "0");
        db.write("b", "0");
        db.write("c", "0");
        db.write("d", "0");

        db.commit();

        Thread t1 = new Thread(() -> {
            doOperations1();
        });

        Thread t2 = new Thread(() -> {

            doOperations21();
            doOperations22();

        });

        Thread t3 = new Thread(() -> {

            doOperations3();

        });



        t1.start();
        t2.start();
        t2.join();
        t1.join();


    }
}