# Transactional JPF Artifact 

# Getting started

This repository contains the software artifact that supports our PLDI'23 submission on "_Dynamic Partial Order Reduction for Checking Correctness Against Transaction Isolation Levels_". Transactional JPF (TrJPF) is an extension of [`Java PathFinder`](JPF-README.md) capable to analyze programs whose sessions _only_ communicate via database calls. Applications require adapting their transactional statements to the API syntax to be tested under TrJPF.

Our artifact is split into different parts following the architecture of Java PathFinder. The main ones include:

- The directory [`src/main`](src/main/README.md) contains the main source code, including JPF and TrJPF.
- The directory [`src/examples`](src/examples/README.md) contains both the API database along with the source code that describe the benchmarks in section 7.2 (Courseware, ShoppingCart, TPC-C, Twitter and Wikipedia).
- The directory [`src/benchmarks`](src/benchmarks/README.md) contains the parameters that define clients tested in section 7.3 and the initial state of the database in each execution. Clients are instantiation of code from examples directory according to adequate parameters.
- The directory [`bin/benchmarks`](bin/benchmarks/README.md) containing the default output files of our tool.
- Configuration file [`jpf.properties`](jpf.properties), including the isolation levels, search mode, output configuration, client APIs to take into account, etc... Configuration parameters can be modified 


# Build

For building, simply run the following:

```
docker build -t tr-jpf:latest.
```

# Run

The three experiments in section 7.3 have an associated script. It suffices to run it for obtaining the results. For a detailed description about the design, input or output of the experiments check the links above.

## First experiment: application scalability

The following command shall be run. Its outcome can be found in "bin/benchmarks/application-scalability" folder.

```
docker run tr-jpf:latest "bash bench-application-scalability.sh"
```

It will produce 5 folders ("courseware/", "shoppingCart/", "tpcc/", "twitter/" and "wikipedia/"), each with 5 subfolders (one per number of sessions in the benchmark). Each subfolder will contain 7 .out files, one per isolation level treated (Appendix F, Table F1).

## Second and third experiment: session and transaction scalability

The following command shall be run.

- Second experiment. Its outcome can be found in "bin/benchmarks/thread-scalability" folder:

```
docker run tr-jpf:latest "bash bench-thread-scalability.sh"
```

- Third experiment. Its outcome can be found in "bin/benchmarks/transaction-scalability" folder:

```
docker run tr-jpf:latest "bash bench-transaction-scalability.sh"
```

Both of them will produce 5 folders ("courseware/", "shoppingCart/", "tpcc/", "twitter/" and "wikipedia/"), each with 10 subfolders (two per number of sessions in the benchmark). Each subfolder will contain 1 .out file (Appendix F, Table F2 and Table F3).

**Notes**

The time limit set is to 30' per case. It is recommended to be careful when running each script as it may take up to 1 day per script. 

# Requirements

This artifact was tested on a Mac OS. We recommend using a Mac/Linux OS version with updated software. 

Docker is required. Please install it for your OS. The necessary documentation is available [here](https://docs.docker.com/get-docker).

<!---
This artifact was tested on a Linux OS. We recommend using a new Unix/Linux OS version with updated software. 

Docker is required. Please install it for your OS. The necessary documentation is available [here](https://docs.docker.com/get-docker) and then follow the [post installation steps](https://docs.docker.com/engine/install/linux-postinstall) so that you can run `docker` commands without admin privileges or sudo.

-->

