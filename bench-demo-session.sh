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
  for ((l=1;l<=num_cases;l++)) do
    for ((j=1;j<=num_columns;j++)) do
      fold=bin/benchmarks/demo-session-scalability/"$benchmarkName"/case$l/$j-sessions/
      args=""
      for ((k=1;k<=j;k++)) do
        args="${args} src/benchmarks/$benchmarkName/session-scalability/case$l/$j-sessions/thread$k.in"
      done
      execute_benchmark

    done
  done
done

cd "graphics/files"

python3 generate_csv.py "demo-session-scalability" 
python3 graphics-docker.py "demo-session-scalability" 




