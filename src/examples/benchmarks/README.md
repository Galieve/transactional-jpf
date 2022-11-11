# Benchmarks (Transactional JPF)

The benchmarks are split in two layers: one with the Java implementation of generic clients (with arbitrary number of sessions and instructions per session) and another one with the particular sessions that define a client. The first layer defines several procedures that are publicly available while the second layer is described by different textfiles that contain the code to be executed along with the initial state of the database.

When instantiated with some set of files, clients populate the database and execute instructions in the order specified by the files.

## Clients

Every session is represented by a Worker instance. Every Worker shares a common benchmark module. Currently, it supports CourseWare, ShoppingCart, TPC-C, Twitter and Wikipedia benchmarks. Clients allow several types of transactions, each one represented by a concrete benchmark method. The name of those methods along their parameters' types are public. When executing a particular transaction (i.e. when given a concrete transaction name and its arguments), it checks its availability and executes its homonymous method. Workers also have a special routine to initialize the state of the database.

Transactions execute database queries via APIDatabase calls. APIDatabase is a mock database with five basic routines: BEGIN, READ, WRITE, COMMIT and APPEND. 
- BEGIN: begin a query
- READ: read some value from the database
- WRITE: write some value to the database
- COMMIT: commit all written values and end the query
- ABORT: abort all written values and end the query

The routines have no functionality when a Java program executes them under the JVM them, but when run under Transactional JPF clients observe the described behavior. Under Transactional JPF there is no database crashes and isolation constraints are always guaranteed by axiomatic checks at every point. This way, Transactional JPF does not rely on a specific database implementation but only in the specifications.

For example, a method that retrieves an item from a table called STORE given its id would look as follows:

```
database.begin();
ShoppingItem si = 
    new ShoppingItem(database.readRow(STORE, id));
database.commit();
```

## Files

The textfiles our benchmark work with are separated in two types: database files and session files. The former contains csv-like data from a concrete table. For example, the table STORE from ShoppingCart benchmark can be initialized with the following file.

```
1;potato;1.5;8
2;tomato;7.5;2
3;aeroplane;0.5;1005
4;flower;1034.0;1
```

The later contain the actual instruction order of a client's session. There is exactly one file per session in a client. For example, one possible session of a client could look as follows:

```
addItemQuantity 5;almond;2.7 3
getQuantity 0
getList
```