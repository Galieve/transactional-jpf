#!/bin/bash

execute_benchmark () {

  mkdir -p $fold

  # shellcheck disable=SC2068
  for model in ${models[@]}; do
    echo "Executing:" java -jar build/RunJPF.jar +db.database_isolation_level.class=$model +report.console.file-folder=$fold $benchmark $database $args
    java -jar build/RunJPF.jar +db.database_isolation_level.class=$model +report.console.file-folder=$fold $benchmark $database $args
    echo "Done!"
  done
}

models=(
  country.lab.histories.CausalHistory
)

benchmarks=(
  benchmarks.tpcc.BigTestTPCC
)

databaseArgs=(
  "-d src/benchmarks/tpcc/database/districts.in -n src/benchmarks/tpcc/database/neworder.in -i src/benchmarks/tpcc/database/item.in -w src/benchmarks/tpcc/database/warehouse.in -s src/benchmarks/tpcc/database/stock.in -l src/benchmarks/tpcc/database/orderline.in -c src/benchmarks/tpcc/database/customer.in -o src/benchmarks/tpcc/database/order.in"
)

benchmarkNames=(
  tpcc
)

num_rows=1
num_columns=3
num_cases=2

for ((i=0;i<num_rows;i++)) do
  benchmarkName="${benchmarkNames[i]}"
  benchmark="${benchmarks[i]}"
  database="${databaseArgs[i]}"
  for((k=1;k<=num_cases;k++)) do
    for ((j=1;j<=num_columns;j++)) do
      fold=bin/benchmarks/demo-transaction-scalability/"$benchmarkName"/case$k/$j-transactions-per-session/

      args=src/benchmarks/$benchmarkName/transaction-scalability/case$k/$j-transactions-per-session/thread1.in\ src/benchmarks/$benchmarkName/transaction-scalability/case$k/$j-transactions-per-session/thread2.in\ src/benchmarks/$benchmarkName/transaction-scalability/case$k/$j-transactions-per-session/thread3.in
      execute_benchmark

    done
  done


done


exit




