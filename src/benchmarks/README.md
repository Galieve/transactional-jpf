[`Back to init`](../../README.md)

## Client structure
For each application, the folder contains:

* One file per table in the database. Each row in the file corresponds to a row in an actual table. Those rows are parsed by the MainUtility when populating the database.

* For each experiment (application-scalability, thread-scalability, transaction-scalability), several folder each containing a client. A client is composed of several files, one per session. Each file contains several procedure names together with their parameters; one per line. Every line is parsed by the BenchmarkModule and transformed in a transaction (Procedure).
