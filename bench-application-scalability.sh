#!/bin/bash

execute_benchmark () {

  mkdir -p $fold
  # shellcheck disable=SC2068
  for true_model in ${true_models[@]}; do
    echo "Executing:" java -jar build/RunJPF.jar  +db.database_true_isolation_level.class=$true_model +report.console.file-folder=$fold $benchmark $database $args
    java -jar build/RunJPF.jar  +db.database_true_isolation_level.class=$true_model +report.console.file-folder=$fold $benchmark $database $args
    echo "Done!"

  done
  
  # shellcheck disable=SC2068
  for model in ${models[@]}; do
      echo "Executing:" java -jar build/RunJPF.jar +db.database_isolation_level.class=$model +report.console.file-folder=$fold $benchmark $database $args
      java -jar build/RunJPF.jar +db.database_isolation_level.class=$model +report.console.file-folder=$fold $benchmark $database $args
      echo "Done!"

  done
    
  echo "Executing:" java -jar build/RunJPF.jar +search.class=country.lab.search.NaiveIsoTrDFSearch +db.database_model.class=country.lab.database.NaiveIsoTrDatabase +report.console.file-folder=$fold $benchmark $database $args
  java -jar build/RunJPF.jar +search.class=country.lab.search.NaiveIsoTrDFSearch +db.database_model.class=country.lab.database.NaiveIsoTrDatabase +report.console.file-folder=$fold $benchmark $database $args
  echo "Done!"
}


true_models=(
  country.lab.histories.CausalHistory
  country.lab.histories.SnapshotIsolationHistory
  country.lab.histories.SerializableHistory
)

models=(
  country.lab.histories.ReadAtomicHistory
  country.lab.histories.ReadCommittedHistory
  country.lab.histories.TrivialHistory
)

folders=(
  #bin/benchmarks/application-scalability/courseware
  #bin/benchmarks/application-scalability/shoppingCart
  #bin/benchmarks/application-scalability/tpcc
  bin/benchmarks/application-scalability/twitter
  #bin/benchmarks/application-scalability/wikipedia
)

databaseArgs=(
  #"-s src/benchmarks/courseware/database/students.in -c src/benchmarks/courseware/database/courses.in"
  #"-s src/benchmarks/shoppingCart/database/store.in" 
  #"-d src/benchmarks/tpcc/database/districts.in -n src/benchmarks/tpcc/database/neworder.in -i src/benchmarks/tpcc/database/item.in -w src/benchmarks/tpcc/database/warehouse.in -s src/benchmarks/tpcc/database/stock.in -l src/benchmarks/tpcc/database/orderline.in -c src/benchmarks/tpcc/database/customer.in -o src/benchmarks/tpcc/database/order.in"
  "-u src/benchmarks/twitter/database/users.in -t src/benchmarks/twitter/database/tweets.in -f src/benchmarks/twitter/database/follows.in -r src/benchmarks/twitter/database/followers.in"
  #"-p src/benchmarks/wikipedia/database/page.in -r src/benchmarks/wikipedia/database/revision.in -t src/benchmarks/wikipedia/database/text.in -u src/benchmarks/wikipedia/database/user.in -w src/benchmarks/wikipedia/database/watchlist.in"
)

benchmarks=(
  #benchmarks.courseware.BigTestCourseWare
  #benchmarks.shoppingCart.BigTestShoppingCart
  #benchmarks.tpcc.BigTestTPCC
  benchmarks.twitter.BigTestTwitter
  #benchmarks.wikipedia.BigTestWikipedia
)

benchmarkNames=(
  #courseware
  #shoppingCart
  #tpcc
  twitter
  #wikipedia
)

num_cases=4


for i in "${!folders[@]}"; do
  database="${databaseArgs[i]}"
  benchmark="${benchmarks[i]}"
  benchmarkName="${benchmarkNames[i]}"
  for ((j=4;j<=num_cases;j++)) do
    
    fold="${folders[i]}"/$j-threads/
    args=src/benchmarks/$benchmarkName/application-scalability/case$j/thread1.in\ src/benchmarks/$benchmarkName/application-scalability/case$j/thread2.in\ src/benchmarks/$benchmarkName/application-scalability/case$j/thread3.in

    execute_benchmark
  done
  

done

cd "graphics/files"

python3 generate_csv.py "application-scalability" 
python3 graphics.py "application-scalability" 

exit

