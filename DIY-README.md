[`Back to init`](README.md)

# Do it yourself!

## Prerrequisites:

It is strongly recommended to read both the [`initial README`](README.md) and the original [`JPF README`](JPF-README.md)

## Constructing your test program

TrJPF takes as input a Java program and verify it using EXPLORE/EXPLORE-CE's algorithm. For this tutorial we will work with the [`example code`](src/examples/benchmarks/SimpleTest.java) located in `src/examples/benchmarks/SimpleTest.java`.

Your code should be deterministic and guarantee that all concurrent communication between sessions are done via the APIDatabase class.

For verifying the code one simply can execute the following command:
```` 
java -jar build/RunJPF.jar benchmarks.SimpleTest
````

Note that the output of the execution will be outputted on the terminal and not in a file. It is worth it to note that if the .java is changed, it is mandatory to recompile the whole project with the command below before verifying the modified program.
```` 
./gradlew buildJars 
````

If your program receive arguments, those have to be written after the name of the main java file (in this case, after benchmarks.SimpleTest).

## Modifying environment parameters

Some TrJPF parameters can be modified for analyzing the program in different settings. Here the reader can find a non-exhaustive list of the specific parameters TrJPF introduce; all presented in the use case of verifying SimpleTest.java. Unless specified, there is no restriction on the quantity, order or relation between each parameter.
 
#### Base Isolation Level (I_0 in explore-CE*(I_0, I))
Possible values:

  - country.lab.histories.CausalHistory (CC)
  - country.lab.histories.ReadAtomiclHistory (RA)
  - country.lab.histories.ReadCommittedHistory (RC)
  - country.lab.histories.TrivialHistory (true)

*Default: CC*
```` 
java -jar build/RunJPF.jar +db.database_true_isolation_level.class=country.lab.histories.CausalHistory benchmarks.SimpleTest
````


#### True Isolation Level (I in explore-CE*(I_0, I)).
Possible values:
  - country.lab.histories.CausalHistory (CC)
  - country.lab.histories.SnapshotIsolationHistory (SI)
  - country.lab.histories.SerializableHistory (SER)

*Default: CC*
```` 
java -jar build/RunJPF.jar +db.database_isolation_level.class=country.lab.histories.ReadAtomicHistory benchmarks.SimpleTest
````

#### Different algorithms

Search and Database classes have to know each other so both parameters have to be used together.

Algorithms available:

- explore-CE*
  - search.class=country.lab.search.TrDFSearch
  - db.database_model.class=country.lab.database.TrDatabase

- DFS 
    - search.class=country.lab.search.NaiveIsoTrDFSearch
    - db.database_model.class=country.lab.database.NaiveIsoTrDatabase

*Default: explore-CE*

```` 
java -jar build/RunJPF.jar +search.class=country.lab.search.NaiveIsoTrDFSearch +db.database_model.class=country.lab.database.NaiveIsoTrDatabase benchmarks.SimpleTest
````

#### Timeout

- It refers to the maximum time an example can be executed under TrJPF
  - search.timeout (in seconds)

*Default: 1800*

#### Reporting tools

- In case it is desired to redirect the output of the tool to a file.

  - report.console.file-folder=path/to/folder

*Default: none*

```` 
java -jar build/RunJPF.jar +report.console.file-folder=bin/benchmarks/ benchmarks.SimpleTest
````

#### Debugging tools

- In case it is desired to observe the full set of steps and database instructions that TrJPF is executing (*only for debugging purposes*)

  - listener=country.lab.report.TransactionalExecTracker
  
*Default: none*

```` 
java -jar build/RunJPF.jar +listener=country.lab.report.TransactionalExecTracker benchmarks.SimpleTest
````

