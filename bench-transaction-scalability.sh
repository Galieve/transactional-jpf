#!/bin/bash

execute_benchmark () {

  mkdir -p $fold

  # shellcheck disable=SC2068
  for model in ${models[@]}; do
    echo "Executing:" java -jar build/RunJPF.jar +db.database_isolation_level.class=$model +report.console.file-prefix=$fold $benchmark $database $args
    java -jar build/RunJPF.jar +db.database_isolation_level.class=$model +report.console.file-prefix=$fold $benchmark $database $args
    echo "Done!"
  done
}

models=(
  country.lab.histories.CausalHistory
)

benchmarks=(
  benchmarks.tpcc.BigTestTPCC
  benchmarks.wikipedia.BigTestWikipedia
)

databaseArgs=(
  "-d src/benchmarks/tpcc/database/districts.in -n src/benchmarks/tpcc/database/neworder.in -i src/benchmarks/tpcc/database/item.in -w src/benchmarks/tpcc/database/warehouse.in -s src/benchmarks/tpcc/database/stock.in -l src/benchmarks/tpcc/database/orderline.in -c src/benchmarks/tpcc/database/customer.in -o src/benchmarks/tpcc/database/order.in"
  "-p src/benchmarks/wikipedia/database/page.in -r src/benchmarks/wikipedia/database/revision.in -t src/benchmarks/wikipedia/database/text.in -u src/benchmarks/wikipedia/database/user.in -w src/benchmarks/wikipedia/database/watchlist.in"
)

benchmarkNames=(
  tpcc
  wikipedia
)

num_rows=2
num_columns=5
num_cases=5

for ((i=0;i<num_rows;i++)) do
  benchmarkName="${benchmarkNames[i]}"
  benchmark="${benchmarks[i]}"
  database="${databaseArgs[i]}"
  for((k=1;k<=num_cases;k++)) do
    for ((j=1;j<=num_columns;j++)) do
      fold=bin/benchmarks/transaction-scalability/"$benchmarkName"/case$k/$j-transactions-per-session/

      args=src/benchmarks/$benchmarkName/transaction-scalability/case$k/$j-transactions-per-session/thread1.in\ src/benchmarks/$benchmarkName/transaction-scalability/case$k/$j-transactions-per-session/thread2.in\ src/benchmarks/$benchmarkName/transaction-scalability/case$k/$j-transactions-per-session/thread3.in
      execute_benchmark

    done
  done


done


exit




