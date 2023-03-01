#!/bin/bash

execute_benchmark () {

  mkdir -p $fold


  # shellcheck disable=SC2068
  for model in ${models[@]}; do
    echo  java -jar build/RunJPF.jar +db.database_isolation_level.class=$model +report.console.file-prefix=$fold $benchmark $database $args
    java -jar build/RunJPF.jar +db.database_isolation_level.class=$model +report.console.file-prefix=$fold $benchmark $database $args
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
  for ((l=1;l<=num_cases;l++)) do
    for ((j=1;j<=num_columns;j++)) do
      fold=bin/benchmarks/thread-scalability/"$benchmarkName"/case$l/$j-sessions/
      args=""
      for ((k=1;k<=j;k++)) do
        args="${args} src/benchmarks/$benchmarkName/thread-scalability/case$l/$j-sessions/thread$k.in"
      done
      execute_benchmark

    done
  done
done


exit




