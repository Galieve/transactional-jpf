public class Test {

    public static String testString(int a, char b){
        return b + " "+ a;
    }

    public static void main(String [] args) throws InterruptedException {
        TRDatabase dbMain = TRDatabase.getDatabase();
        dbMain.begin();
        dbMain.write("x","0");
        dbMain.write("y","0");
        dbMain.write("z","0");
        dbMain.end();
        /*
        dbMain.write("x","1");
        String x = dbMain.read("x");
        String y = x + x;
        System.out.println(y);
        //dbMain.write("z","0");
        dbMain.end();


         */


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
            var a = db.read("x");
            var b = db.read("y");
            db.write("x",a);
            var c = db.read("x");
            System.out.println("a = "+a+", b = "+b+", c ="+c);
            db.end();
        });
        Thread ser3 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            var d = db.read("x");
            var e = db.read("y");
            db.write("y","2");
            System.out.println("d = "+d+", e = "+e);

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



        Thread t1 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            for(int i = 0; i < 3; ++i) {
                db.write("x", "1.1:"+i);
            }
            //db.write("y", "1");
            var a = db.read("x");
            //db.read("y","b");
            System.out.println("a = "+a);
            db.end();
            db.begin();
            if(a.startsWith("4")) {
                db.write("x", "1.2:0");
            }
            //db.write("y", "10");
            db.end();
        });

        Thread t2 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            //db.write("x","2");
            //db.write("y", "2");
            var c = db.read("x");
            var d = db.read("y");
            System.out.println("c = "+c+", d ="+d);


            db.end();
        });

        Thread t3 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            var e1 = db.read("z");
            db.write("x","3.0:0");
            db.write("y","3.0:1");
            db.end();
            db.begin();
            var e2 = db.read("x");
            for(int i = 0; i < 3; ++i) {
                if(i %2 == 0){
                    db.write("x", "3.0:"+(2*(i+1)));
                }
                db.write("y", "3.0:"+(2*(i+1)+ 1));
            }
            System.out.println("e1 = "+e1+", e2 ="+e2);

            db.end();

            db.begin();
            var e3 = db.read("x");
            db.write("x","3.1:0");
            db.write("y","3.1:1");
            System.out.println("e3 = "+e3);

            db.end();


        });

        Thread t4 = new Thread(() -> {
            TRDatabase db = TRDatabase.getDatabase();
            db.begin();
            var f = db.read("z");
            db.write("x","4.0:0");
            db.write("y","4.0:1");
            System.out.println("f = "+f);

            db.end();
            db.begin();
            db.write("x","4.1:0");
            db.write("y", "4.1:1");
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



    }
}
