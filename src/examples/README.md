[`Back to init`](../../README.md)


## Examples source code
For each application, the source code contains:

* Files describing internal APIs used by the application

* Application API which is used by the client and its implementation using the key-value store interface

* Client code (test driver) which can run in two modes:
  * randomly select operations
  * specific operations with assertions
  
* Client's mode can be selected using command line parameter: random/fixed

Code of the mock database:

* Database API:
    * APIs: APIDatabase.java
    * Utility: TRUtility.java, AbortDatabaseException.java

Code structure of each application skeleton is as follows:

* Shared skeleton: BenchmarkModule.java, Procedure.java, Worker.java, MainUtility.java

* Courseware:

    * Items: Course.java, Student.java
    * Procedures: CourseWare.java

* Shopping Cart:
    * Items: Item.java, ShoppingItem.java
    * Procedures: ShoppingCart.java

* TPC-C:
    * Items: Customer.java, District.java, History.java, Item.java, NewOrder.java, Order.java, OrderLine.java, Stock.java, Warehouse.java
    * Procedures: CreateNewOrder.java, Delivery.java, OrderStatus.java, Payment.java, StockLevel.java
    * APIs: TPCCObject.java, BasicTPCCProcedure.java, TPCC.java, TPCCUtility.java

* Twitter:
    * Items: Tweet.java, User.java
    * Procedures: Twitter.java

* Wikipedia:
    * Items: Article.java, IPBlock.java, Logging.java, Page.java, PageRestriction.java, Revision.java, Text.java, User.java, UserGroup.java, WatchList.java
    * Procedures: AddWatchList.java, GetPageAnonymous.java, GetPageAuthenticated.java, RemoveWatchList.java, UpdatePage.java
    * APIs: WikipediaProcedure.java, Wikipedia.java

The reader can easily play around with the source code of these benchmarks. For instance, it is possible to add new procedures and/or items, update the clients and run TrJPF to observe if your procedure is source or not of big amount of interleavings.
