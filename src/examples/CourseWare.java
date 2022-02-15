public class CourseWare {


    public static void addOne(int i){
        TRDatabase db = TRDatabase.getDatabase();
        db.begin();
        int n = Integer.parseInt(db.read("numberStudents"));
        if(n < 1) {
            db.write("numberStudents", String.valueOf(n + 1));
            db.write("register[" + i + "]", "1");
        }
        db.end();
    }

    public static void main(String [] args) throws InterruptedException {
        TRDatabase dbMain = TRDatabase.getDatabase();
        dbMain.begin();
        dbMain.write("register[0]","0");
        dbMain.write("register[1]","0");
        dbMain.write("numberStudents", "0");
        dbMain.end();



        Thread ses1 = new Thread(() -> {
            addOne(0);
        });
        Thread ses2 = new Thread(() -> {
            addOne(1);
        });


        ses1.start();
        ses2.start();
        ses1.join();
        ses2.join();


        /*
        dbMain.begin();
        int n = Integer.parseInt(dbMain.read("register[0]"));
        int m = Integer.parseInt(dbMain.read("register[1]"));
        dbMain.assertDB(String.valueOf(n+m),
                "=", "numberStudents");
        dbMain.end();

         */



    }


}
