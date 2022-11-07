#!/bin/bash

echo java -jar build/RunJPF.jar +db.database_true_isolation_level.class=fr.irif.histories.CausalHistory +report.console.file-prefix=bin/benchmarks/thread-scalability/twitter/5-thread-2/ benchmarks.twitter.BigTestTwitter -u src/benchmarks/twitter/database/users.in -t src/benchmarks/twitter/database/tweets.in -f src/benchmarks/twitter/database/follows.in -r src/benchmarks/twitter/database/followers.in src/benchmarks/twitter/thread-scalability/case-5-2/thread1.in src/benchmarks/twitter/thread-scalability/case-5-2/thread2.in src/benchmarks/twitter/thread-scalability/case-5-2/thread3.in src/benchmarks/twitter/thread-scalability/case-5-2/thread4.in src/benchmarks/twitter/thread-scalability/case-5-2/thread5.in
java -jar build/RunJPF.jar +db.database_true_isolation_level.class=fr.irif.histories.CausalHistory +report.console.file-prefix=bin/benchmarks/thread-scalability/twitter/5-thread-2/ benchmarks.twitter.BigTestTwitter -u src/benchmarks/twitter/database/users.in -t src/benchmarks/twitter/database/tweets.in -f src/benchmarks/twitter/database/follows.in -r src/benchmarks/twitter/database/followers.in src/benchmarks/twitter/thread-scalability/case-5-2/thread1.in src/benchmarks/twitter/thread-scalability/case-5-2/thread2.in src/benchmarks/twitter/thread-scalability/case-5-2/thread3.in src/benchmarks/twitter/thread-scalability/case-5-2/thread4.in src/benchmarks/twitter/thread-scalability/case-5-2/thread5.in

echo java -jar build/RunJPF.jar +db.database_true_isolation_level.class=fr.irif.histories.CausalHistory +report.console.file-prefix=bin/benchmarks/thread-scalability/tpcc/5-thread-2/ benchmarks.tpcc.BigTestTPCC -d src/benchmarks/tpcc/database/districts.in -n src/benchmarks/tpcc/database/neworder.in -i src/benchmarks/tpcc/database/item.in -w src/benchmarks/tpcc/database/warehouse.in -s src/benchmarks/tpcc/database/stock.in -l src/benchmarks/tpcc/database/orderline.in -c src/benchmarks/tpcc/database/customer.in -o src/benchmarks/tpcc/database/order.in src/benchmarks/tpcc/thread-scalability/case-5-2/thread1.in src/benchmarks/tpcc/thread-scalability/case-5-2/thread2.in src/benchmarks/tpcc/thread-scalability/case-5-2/thread3.in src/benchmarks/tpcc/thread-scalability/case-5-2/thread4.in src/benchmarks/tpcc/thread-scalability/case-5-2/thread5.in
java -jar build/RunJPF.jar +db.database_true_isolation_level.class=fr.irif.histories.CausalHistory +report.console.file-prefix=bin/benchmarks/thread-scalability/tpcc/5-thread-2/ benchmarks.tpcc.BigTestTPCC -d src/benchmarks/tpcc/database/districts.in -n src/benchmarks/tpcc/database/neworder.in -i src/benchmarks/tpcc/database/item.in -w src/benchmarks/tpcc/database/warehouse.in -s src/benchmarks/tpcc/database/stock.in -l src/benchmarks/tpcc/database/orderline.in -c src/benchmarks/tpcc/database/customer.in -o src/benchmarks/tpcc/database/order.in src/benchmarks/tpcc/thread-scalability/case-5-2/thread1.in src/benchmarks/tpcc/thread-scalability/case-5-2/thread2.in src/benchmarks/tpcc/thread-scalability/case-5-2/thread3.in src/benchmarks/tpcc/thread-scalability/case-5-2/thread4.in src/benchmarks/tpcc/thread-scalability/case-5-2/thread5.in

echo java -jar build/RunJPF.jar +db.database_true_isolation_level.class=fr.irif.histories.CausalHistory +report.console.file-prefix=bin/benchmarks/thread-scalability/shoppingCart/5-thread-2/ benchmarks.shoppingCart.BigTestShoppingCart -s src/benchmarks/shoppingCart/database/store.in src/benchmarks/shoppingCart/thread-scalability/case-5-2/thread1.in src/benchmarks/shoppingCart/thread-scalability/case-5-2/thread2.in src/benchmarks/shoppingCart/thread-scalability/case-5-2/thread3.in src/benchmarks/shoppingCart/thread-scalability/case-5-2/thread4.in src/benchmarks/shoppingCart/thread-scalability/case-5-2/thread5.in
java -jar build/RunJPF.jar +db.database_true_isolation_level.class=fr.irif.histories.CausalHistory +report.console.file-prefix=bin/benchmarks/thread-scalability/shoppingCart/5-thread-2/ benchmarks.shoppingCart.BigTestShoppingCart -s src/benchmarks/shoppingCart/database/store.in src/benchmarks/shoppingCart/thread-scalability/case-5-2/thread1.in src/benchmarks/shoppingCart/thread-scalability/case-5-2/thread2.in src/benchmarks/shoppingCart/thread-scalability/case-5-2/thread3.in src/benchmarks/shoppingCart/thread-scalability/case-5-2/thread4.in src/benchmarks/shoppingCart/thread-scalability/case-5-2/thread5.in

echo java -jar build/RunJPF.jar +db.database_true_isolation_level.class=fr.irif.histories.CausalHistory +report.console.file-prefix=bin/benchmarks/thread-scalability/shoppingCart/5-thread-1/ benchmarks.shoppingCart.BigTestShoppingCart -s src/benchmarks/shoppingCart/database/store.in src/benchmarks/shoppingCart/thread-scalability/case-5-1/thread1.in src/benchmarks/shoppingCart/thread-scalability/case-5-1/thread2.in src/benchmarks/shoppingCart/thread-scalability/case-5-1/thread3.in src/benchmarks/shoppingCart/thread-scalability/case-5-1/thread4.in src/benchmarks/shoppingCart/thread-scalability/case-5-1/thread5.in
java -jar build/RunJPF.jar +db.database_true_isolation_level.class=fr.irif.histories.CausalHistory +report.console.file-prefix=bin/benchmarks/thread-scalability/shoppingCart/5-thread-1/ benchmarks.shoppingCart.BigTestShoppingCart -s src/benchmarks/shoppingCart/database/store.in src/benchmarks/shoppingCart/thread-scalability/case-5-1/thread1.in src/benchmarks/shoppingCart/thread-scalability/case-5-1/thread2.in src/benchmarks/shoppingCart/thread-scalability/case-5-1/thread3.in src/benchmarks/shoppingCart/thread-scalability/case-5-1/thread4.in src/benchmarks/shoppingCart/thread-scalability/case-5-1/thread5.in



exit

execute_benchmark () {

  mkdir -p $fold
  # shellcheck disable=SC2068
  for true_model in ${true_models[@]}; do
    echo  java -jar build/RunJPF.jar  +db.database_true_isolation_level.class=$true_model +report.console.file-prefix=$fold $benchmark $database $args
    java -jar build/RunJPF.jar  +db.database_true_isolation_level.class=$true_model +report.console.file-prefix=$fold $benchmark $database $args
  done

  #for model in ${models[@]}; do
    #echo  java -jar build/RunJPF.jar +db.database_isolation_level.class=$model +report.console.file-prefix=$fold $benchmark $database $args
    #java -jar build/RunJPF.jar +db.database_isolation_level.class=$model +report.console.file-prefix=$fold $benchmark $database $args
  #done

  #echo  java -jar build/RunJPF.jar +search.class=fr.irif.search.NaiveIsoTrDFSearch +db.database_model.class=fr.irif.database.NaiveIsoTrDatabase +report.console.file-prefix=$fold $benchmark $database $args
  #java -jar build/RunJPF.jar +search.class=fr.irif.search.NaiveIsoTrDFSearch +db.database_model.class=fr.irif.database.NaiveIsoTrDatabase +report.console.file-prefix=$fold $benchmark $database $args
}

true_models=(
  fr.irif.histories.CausalHistory
  #fr.irif.histories.SnapshotIsolationHistory
  #fr.irif.histories.SerializableHistory
)

models=(
   fr.irif.histories.ReadAtomicHistory
   fr.irif.histories.ReadCommittedHistory
   fr.irif.histories.TrivialHistory
)

benchmarks=(
  #benchmarks.courseware.BigTestCourseWare
  benchmarks.shoppingCart.BigTestShoppingCart
  benchmarks.tpcc.BigTestTPCC
  benchmarks.twitter.BigTestTwitter
  benchmarks.wikipedia.BigTestWikipedia
)

databaseArgs=(
  #"-s src/benchmarks/courseware/database/students.in -c src/benchmarks/courseware/database/courses.in"
  "-s src/benchmarks/shoppingCart/database/store.in"
  "-d src/benchmarks/tpcc/database/districts.in -n src/benchmarks/tpcc/database/neworder.in -i src/benchmarks/tpcc/database/item.in -w src/benchmarks/tpcc/database/warehouse.in -s src/benchmarks/tpcc/database/stock.in -l src/benchmarks/tpcc/database/orderline.in -c src/benchmarks/tpcc/database/customer.in -o src/benchmarks/tpcc/database/order.in"
  "-u src/benchmarks/twitter/database/users.in -t src/benchmarks/twitter/database/tweets.in -f src/benchmarks/twitter/database/follows.in -r src/benchmarks/twitter/database/followers.in"
  "-p src/benchmarks/wikipedia/database/page.in -r src/benchmarks/wikipedia/database/revision.in -t src/benchmarks/wikipedia/database/text.in -u src/benchmarks/wikipedia/database/user.in -w src/benchmarks/wikipedia/database/watchlist.in"
)

benchmarkNames=(
  #courseware
  shoppingCart
  tpcc
  twitter
  wikipedia
)

num_rows=4
num_columns=5


for ((i=0;i<num_rows;i++)) do
  benchmarkName="${benchmarkNames[i]}"
  benchmark="${benchmarks[i]}"
  database="${databaseArgs[i]}"
  for ((j=0;j<num_columns;j++)) do
    fold=bin/benchmarks/thread-scalability/$benchmarkName/$(($j+1))-thread-1/
    args=""
    for ((k=0;k<=j;k++)) do
      args="${args} src/benchmarks/$benchmarkName/thread-scalability/case-$(($j+1))-1/thread$(($k+1)).in"
    done
    execute_benchmark

  done
  for ((j=0;j<num_columns;j++)) do
    fold=bin/benchmarks/thread-scalability/$benchmarkName/$(($j+1))-thread-2/
    args=""
    for ((k=0;k<=j;k++)) do
      args="${args} src/benchmarks/$benchmarkName/thread-scalability/case-$(($j+1))-2/thread$(($k+1)).in"
    done
    execute_benchmark
  done
done


exit




