import fr.irif.database.Database;

public class Twitter {

    public static void follow(int a, int b){
        if(a == b) return;
        TRDatabase db = TRDatabase.getDatabase();
        db.begin();
        db.write("follow["+a+"]["+b+"]","1");
        db.end();
    }

    public static void timeline(String name, int a){
        TRDatabase db = TRDatabase.getDatabase();
        db.begin();
        db.write(name + "["+a+"]", "1");
        db.write(name + "["+((a+1)%3)+"]", "0");
        db.write(name + "["+((a+2)%3)+"]", "0");
        db.end();
    }
    public static void newsfeed(String name, int a){

        TRDatabase db = TRDatabase.getDatabase();
        String [] nw = new String[3];
        db.begin();
        nw[0] = db.read("follow["+a+"]["+0+"]");
        db.write(name + "["+0+"]",nw[0]);
        nw[1] = db.read("follow["+a+"]["+1+"]");
        db.write(name + "["+1+"]",nw[1]);
        nw[2] = db.read("follow["+a+"]["+2+"]");
        db.write(name + "["+2+"]",nw[2]);
        db.end();

    }

    public static void main(String [] args) throws InterruptedException {
        TRDatabase dbMain = TRDatabase.getDatabase();
        dbMain.begin();
        dbMain.write("follow["+0+"]["+0+"]","0");
        dbMain.write("follow["+0+"]["+1+"]","1");
        dbMain.write("follow["+0+"]["+2+"]","0");
        dbMain.write("tlB[0]","0");
        dbMain.write("tlB[1]","0");
        dbMain.write("tlB[2]","0");
        dbMain.write("tlA[0]","0");
        dbMain.write("tlA[1]","0");
        dbMain.write("tlA[2]","0");

        dbMain.end();


        Thread ses1 = new Thread(() -> {
            timeline("tlB",2);

        });
        Thread ses2 = new Thread(() -> {
            follow(0, 2);
            newsfeed("tlA",0);
        });



        ses1.start();
        ses2.start();
        ses1.join();
        ses2.join();


        dbMain.begin();
        dbMain.assertDB("tlB[0]", "<=", "tlA[0]");
        dbMain.assertDB("tlB[1]", "<=", "tlA[1]");
        dbMain.assertDB("tlB[2]", "<=", "tlA[2]");
        dbMain.end();

    }

}
