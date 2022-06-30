import database.TRDatabase;

public class ForTest {

    public static void main(String [] args) throws InterruptedException {

        TRDatabase dbMain = TRDatabase.getDatabase();
        dbMain.begin();
        dbMain.write("x","0");
        //dbMain.write("y", "0");
        dbMain.end();


        Thread t1 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            var a = db.read("x");
            var b = db.read("x");
            var c = db.read("x");
            var d = db.read("x");
            var e = db.read("x");
            var f = db.read("x");
            var g = db.read("x");
            var h = db.read("x");


            System.out.println("a:"+a+ " b:"+ b+" c:"+c + "d :"+d);
            System.out.println("e:"+e+ " f:"+ f+" g:"+g + "h :"+h);


            db.end();
        });




        Thread t2 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            db.write("x", "2.1");
            db.end();
            db.begin();
            db.write("x", "2.2");
            db.end();
            db.begin();
            db.write("x", "2.3");
            db.end();

        });

/*
        Thread t3 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            db.write("x", "3.1");
            db.end();
            db.begin();
            db.write("x", "3.2");
            db.end();
            db.begin();
            db.write("x", "3.3");
            db.end();

        });


        Thread t4 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            db.write("x", "4.1");
            db.end();
            db.begin();
            db.write("x", "4.2");
            db.end();
            db.begin();
            db.write("x", "4.3");
            db.end();
        });




 */


        t1.start();
        t2.start();
        /*t3.start();
        t4.start();
        t4.join();

        t3.join();

         */

        t2.join();
        t1.join();








    }
}
