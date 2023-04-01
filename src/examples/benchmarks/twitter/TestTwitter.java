package benchmarks.twitter;

import country.lab.database.TrDatabase;
import database.APIDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestTwitter {



    public static void doOperations11(){
        var db = APIDatabase.getDatabase();
        db.begin();
        db.read("b");
        db.commit();
    }

    public static void doOperations12(){
        var db = APIDatabase.getDatabase();
        db.begin();
        db.read("a");
        db.commit();
    }

    public static void doOperations21(){
        var db = APIDatabase.getDatabase();
        db.begin();
        db.read("c");
        db.commit();
    }

    public static void doOperations22(){
        var db = APIDatabase.getDatabase();
        db.begin();
        db.read("d");
        db.write("b", "12");
        db.commit();
    }


    public static void doOperations23(){
        var db = APIDatabase.getDatabase();
        db.begin();
        db.read("x");
        db.read("y");
        db.commit();
    }



    public static void doOperations3(){
        var db = APIDatabase.getDatabase();
        db.begin();
        db.write("w", "3");
        db.write("y", "3");
        db.write("z", "3");
        db.write("c", "3");
        db.commit();

    }

    private static void doOperations4() {
        var db = APIDatabase.getDatabase();
        db.begin();

        db.write("x", "4");
        db.write("y", "4");
        db.write("w", "4");
        db.write("d", "4");

        db.commit();
    }

    private static void doOperations5() {
        var db = APIDatabase.getDatabase();
        db.begin();
        db.read("w");
        db.read("z");
        db.write("a", "5");
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
            doOperations11();
            doOperations12();
        });

        Thread t2 = new Thread(() -> {
            doOperations21();
            doOperations22();
            doOperations23();
        });

        Thread t3 = new Thread(() -> doOperations3());
        Thread t4 = new Thread(() -> {
            doOperations4();
        }
        );
        Thread t5 = new Thread(() -> {
            doOperations5();
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t5.join();
        t4.join();
        t3.join();
        t2.join();
        t1.join();


    }
}