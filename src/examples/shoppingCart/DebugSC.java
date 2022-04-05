package shoppingCart;

import database.TRDatabase;

import java.util.ArrayList;
import java.util.List;

public class DebugSC {


    private static final ArrayList<String> products = new ArrayList<>(List.of("1", "4"));
    /*public static void doOp(int threadId) {
        if(threadId == 0){
            sc.begin();
            sc.removeItem(shoes.getId());
            sc.end();

            sc.begin();
            var listA = sc.getList();
            sc.end();

            sc.begin();
            var listB = sc.getList();
            sc.end();

            int qa = 0, qb = 0;
            if(!listA.isEmpty()){
                qa = listA.get(0).getQuantity();
            }
            if(!listB.isEmpty()){
                qb = listB.get(0).getQuantity();
            }
        }
        else{
            sc.begin();
            sc.addItemQuantity(shoes, 3);
            sc.end();

            sc.begin();
            var listA = sc.getList();
            sc.end();

            sc.begin();
            var listB = sc.getList();
            sc.end();

            int qa = 0, qb = 0;
            if(!listA.isEmpty()){
                qa = listA.get(0).getQuantity();
            }
            if(!listB.isEmpty()){
                qb = listB.get(0).getQuantity();
            }
        }
    }

     */


    /*private static final shoppingcart.Item book =
            new shoppingcart.Item("book", "2", 200.0);
    private static final shoppingcart.Item umbrella =
            new shoppingcart.Item("umbrella", "3", 300.0);

    private static final shoppingcart.Item bat =
            new shoppingcart.Item("bat", "5", 500.0);
    private static final shoppingcart.Item table =
            new shoppingcart.Item("table", "6", 600.0);

     */

    private static final TRDatabase database =
            TRDatabase.getDatabase();

    public static void doOperations1(){
        var sc = ShoppingCart.getShoppingCart(0);

        var shoes = new Item("shoes", "1", 100.0);
        var ball = new Item("ball", "4", 400.0);
        sc.begin();
        sc.addItemQuantity(shoes, 2);
        sc.addItem(ball);
        sc.end();



        sc.begin();
        //sc.changeQuantity(shoes, 3);
        //sc.removeItem(shoes);
        //int qs = sc.getQuantity(shoes);
        //var list = sc.getList();

        sc.end();

        /*System.out.println("Cart list: " + list.size());
        for(var e: list){
            System.out.println(e);
        }




        database.begin();
        boolean b1 = qs == 0;
        database.assertDB(b1);
        boolean b = false;
        if(!list.isEmpty()){
            var e = list.get(0);
            var ballPath = ball.toString()+";"+e.getQuantity();
            b = list.size() == 1 && e.toString().equals(ballPath) &&
                    (e.getQuantity() == 1 || e.getQuantity() == 2);
        }
        database.assertDB(b);
        database.end();


         */


    }

    public static void doOperations2(){
        var sc = ShoppingCart.getShoppingCart(0);
        var shoes = new Item("shoes", "1", 100.0);
        var ball = new Item("ball", "4", 400.0);

        sc.begin();
        sc.addItem(ball);
        //var qshoes = sc.getQuantity(shoes);
        //var qball = sc.getQuantity(ball);
        sc.end();

        sc.begin();
        //sc.changeQuantity(shoes, 4);
        //var newQshoes = sc.getQuantity(shoes);


        sc.end();

        //System.out.println("Quantities. First:" + qshoes+ ", second: "+qball);

        database.begin();
        /*database.assertDB(qshoes == 0 || qshoes == 2);
        database.assertDB(qball == 1 || qball == 2);
        if(qshoes == 2){
            database.assertDB(qball == 2);
        }

         */
        //database.assertDB(newQshoes == 0 || newQshoes == 4);
        database.end();
    }


    public static void main(String[] args) throws InterruptedException {

        TRDatabase dbMain = TRDatabase.getDatabase();
        dbMain.begin();
        for(int i = 0; i < 1; ++i){
            dbMain.write("store["+i+"]", "{}");
        }
        dbMain.end();















        Thread ses1 = new Thread(() -> {
            doOperations1();
        });


        Thread ses2 = new Thread(() -> {
            doOperations2();
        });



        ses1.start();
        ses2.start();
        //ses3.start();
        ses1.join();
        ses2.join();
        //ses3.join();




    }
}
