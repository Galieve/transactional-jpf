
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
  bin/benchmarks/application-scalability/courseWare/1-threads/
  bin/benchmarks/application-scalability/courseWare/2-threads/
  bin/benchmarks/application-scalability/courseWare/3-threads/
  bin/benchmarks/application-scalability/courseWare/4-threads/
  bin/benchmarks/application-scalability/courseWare/5-threads/
  bin/benchmarks/application-scalability/shoppingCart/1-threads/
  bin/benchmarks/application-scalability/shoppingCart/2-threads/
  bin/benchmarks/application-scalability/shoppingCart/3-threads/
  bin/benchmarks/application-scalability/shoppingCart/4-threads/
  bin/benchmarks/application-scalability/shoppingCart/5-threads/
  bin/benchmarks/application-scalability/tpcc/1-threads/
  bin/benchmarks/application-scalability/tpcc/2-threads/
  bin/benchmarks/application-scalability/tpcc/3-threads/
  bin/benchmarks/application-scalability/tpcc/4-threads/
  bin/benchmarks/application-scalability/tpcc/5-threads/
  bin/benchmarks/application-scalability/twitter/1-threads/
  bin/benchmarks/application-scalability/twitter/2-threads/
  bin/benchmarks/application-scalability/twitter/3-threads/
  bin/benchmarks/application-scalability/twitter/4-threads/
  bin/benchmarks/application-scalability/twitter/5-threads/
  bin/benchmarks/application-scalability/wikipedia/1-threads/
  bin/benchmarks/application-scalability/wikipedia/2-threads/
  bin/benchmarks/application-scalability/wikipedia/3-threads/
  bin/benchmarks/application-scalability/wikipedia/4-threads/
  bin/benchmarks/application-scalability/wikipedia/5-threads/
)



argsList=(
  "-s src/benchmarks/courseware/database/students.in -c src/benchmarks/courseware/database/courses.in src/benchmarks/courseware/application-scalability/update-read-update/uru0.in src/benchmarks/courseware/application-scalability/update-read-update/uru1.in src/benchmarks/courseware/application-scalability/update-read-update/uru2.in"
  "-s src/benchmarks/courseware/database/students.in -c src/benchmarks/courseware/database/courses.in src/benchmarks/courseware/application-scalability/update-read-read/urr0.in src/benchmarks/courseware/application-scalability/update-read-read/urr1.in src/benchmarks/courseware/application-scalability/update-read-read/urr2.in"
  "-s src/benchmarks/courseware/database/students.in -c src/benchmarks/courseware/database/courses.in src/benchmarks/courseware/application-scalability/update-update-update/uuu0.in src/benchmarks/courseware/application-scalability/update-update-update/uuu1.in src/benchmarks/courseware/application-scalability/update-update-update/uuu2.in"
  "-s src/benchmarks/courseware/database/students.in -c src/benchmarks/courseware/database/courses.in src/benchmarks/courseware/application-scalability/update-update-update/uuu3.in src/benchmarks/courseware/application-scalability/update-update-update/uuu4.in src/benchmarks/courseware/application-scalability/update-update-update/uuu5.in"
  "-s src/benchmarks/courseware/database/students.in -c src/benchmarks/courseware/database/courses.in src/benchmarks/courseware/application-scalability/update-update-update/uuu4.in src/benchmarks/courseware/application-scalability/update-update-update/uuu3.in src/benchmarks/courseware/application-scalability/update-update-update/uuu0.in"
  "-s src/benchmarks/shoppingCart/database/store.in src/benchmarks/shoppingCart/application-scalability/update-read-read/urr0.in src/benchmarks/shoppingCart/application-scalability/update-read-read/urr1.in src/benchmarks/shoppingCart/application-scalability/update-read-read/urr2.in"
  "-s src/benchmarks/shoppingCart/database/store.in src/benchmarks/shoppingCart/application-scalability/update-read-update/uru0.in src/benchmarks/shoppingCart/application-scalability/update-read-update/uru1.in src/benchmarks/shoppingCart/application-scalability/update-read-update/uru2.in"
  "-s src/benchmarks/shoppingCart/database/store.in src/benchmarks/shoppingCart/application-scalability/update-read-update/uru3.in src/benchmarks/shoppingCart/application-scalability/update-read-update/uru4.in src/benchmarks/shoppingCart/application-scalability/update-read-update/uru5.in"
  "-s src/benchmarks/shoppingCart/database/store.in src/benchmarks/shoppingCart/application-scalability/update-update-update/uuu0.in src/benchmarks/shoppingCart/application-scalability/update-update-update/uuu1.in src/benchmarks/shoppingCart/application-scalability/update-update-update/uuu2.in"
  "-s src/benchmarks/shoppingCart/database/store.in src/benchmarks/shoppingCart/application-scalability/update-update-update/uuu3.in src/benchmarks/shoppingCart/application-scalability/update-update-update/uuu4.in src/benchmarks/shoppingCart/application-scalability/update-update-update/uuu5.in"
  "-d src/benchmarks/tpcc/database/districts.in -n src/benchmarks/tpcc/database/neworder.in -i src/benchmarks/tpcc/database/item.in -w src/benchmarks/tpcc/database/warehouse.in -s src/benchmarks/tpcc/database/stock.in -l src/benchmarks/tpcc/database/orderline.in -c src/benchmarks/tpcc/database/customer.in -o src/benchmarks/tpcc/database/order.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu0.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu1.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu2.in"
  "-d src/benchmarks/tpcc/database/districts.in -n src/benchmarks/tpcc/database/neworder.in -i src/benchmarks/tpcc/database/item.in -w src/benchmarks/tpcc/database/warehouse.in -s src/benchmarks/tpcc/database/stock.in -l src/benchmarks/tpcc/database/orderline.in -c src/benchmarks/tpcc/database/customer.in -o src/benchmarks/tpcc/database/order.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu3.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu4.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu5.in"
  "-d src/benchmarks/tpcc/database/districts.in -n src/benchmarks/tpcc/database/neworder.in -i src/benchmarks/tpcc/database/item.in -w src/benchmarks/tpcc/database/warehouse.in -s src/benchmarks/tpcc/database/stock.in -l src/benchmarks/tpcc/database/orderline.in -c src/benchmarks/tpcc/database/customer.in -o src/benchmarks/tpcc/database/order.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu5.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu1.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu3.in"
  "-d src/benchmarks/tpcc/database/districts.in -n src/benchmarks/tpcc/database/neworder.in -i src/benchmarks/tpcc/database/item.in -w src/benchmarks/tpcc/database/warehouse.in -s src/benchmarks/tpcc/database/stock.in -l src/benchmarks/tpcc/database/orderline.in -c src/benchmarks/tpcc/database/customer.in -o src/benchmarks/tpcc/database/order.in src/benchmarks/tpcc/application-scalability/update-update-update-light/uuu0.in src/benchmarks/tpcc/application-scalability/update-update-update-light/uuu1.in src/benchmarks/tpcc/application-scalability/update-update-update-light/uuu2.in"
  "-d src/benchmarks/tpcc/database/districts.in -n src/benchmarks/tpcc/database/neworder.in -i src/benchmarks/tpcc/database/item.in -w src/benchmarks/tpcc/database/warehouse.in -s src/benchmarks/tpcc/database/stock.in -l src/benchmarks/tpcc/database/orderline.in -c src/benchmarks/tpcc/database/customer.in -o src/benchmarks/tpcc/database/order.in src/benchmarks/tpcc/application-scalability/update-update-update-strong/uuu0.in src/benchmarks/tpcc/application-scalability/update-update-update-strong/uuu1.in src/benchmarks/tpcc/application-scalability/update-update-update-strong/uuu2.in"
  "-u src/benchmarks/twitter/database/users.in -t src/benchmarks/twitter/database/tweets.in -f src/benchmarks/twitter/database/follows.in -r src/benchmarks/twitter/database/followers.in
  src/benchmarks/twitter/application-scalability/update-read-read/urr0.in src/benchmarks/twitter/application-scalability/update-read-read/urr1.in src/benchmarks/twitter/application-scalability/update-read-read/urr2.in"
  "-u src/benchmarks/twitter/database/users.in -t src/benchmarks/twitter/database/tweets.in -f src/benchmarks/twitter/database/follows.in -r src/benchmarks/twitter/database/followers.in
  src/benchmarks/twitter/application-scalability/update-read-update/uru0.in src/benchmarks/twitter/application-scalability/update-read-update/uru1.in src/benchmarks/twitter/application-scalability/update-read-update/uru2.in"
  "-u src/benchmarks/twitter/database/users.in -t src/benchmarks/twitter/database/tweets.in -f src/benchmarks/twitter/database/follows.in -r src/benchmarks/twitter/database/followers.in
  src/benchmarks/twitter/application-scalability/update-read-update/uru3.in src/benchmarks/twitter/application-scalability/update-read-update/uru4.in src/benchmarks/twitter/application-scalability/update-read-update/uru5.in"
  "-u src/benchmarks/twitter/database/users.in -t src/benchmarks/twitter/database/tweets.in -f src/benchmarks/twitter/database/follows.in -r src/benchmarks/twitter/database/followers.in
  src/benchmarks/twitter/application-scalability/update-update-update/uuu0.in src/benchmarks/twitter/application-scalability/update-update-update/uuu1.in src/benchmarks/twitter/application-scalability/update-update-update/uuu2.in"
  "-u src/benchmarks/twitter/database/users.in -t src/benchmarks/twitter/database/tweets.in -f src/benchmarks/twitter/database/follows.in -r src/benchmarks/twitter/database/followers.in
  src/benchmarks/twitter/application-scalability/update-update-update/uuu3.in src/benchmarks/twitter/application-scalability/update-update-update/uuu4.in src/benchmarks/twitter/application-scalability/update-update-update/uuu5.in"
  "-p src/benchmarks/wikipedia/database/page.in -r src/benchmarks/wikipedia/database/revision.in -t src/benchmarks/wikipedia/database/text.in -u src/benchmarks/wikipedia/database/user.in -w src/benchmarks/wikipedia/database/watchlist.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru0.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru1.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru2.in"
  "-p src/benchmarks/wikipedia/database/page.in -r src/benchmarks/wikipedia/database/revision.in -t src/benchmarks/wikipedia/database/text.in -u src/benchmarks/wikipedia/database/user.in -w src/benchmarks/wikipedia/database/watchlist.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru3.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru4.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru5.in"
  "-p src/benchmarks/wikipedia/database/page.in -r src/benchmarks/wikipedia/database/revision.in -t src/benchmarks/wikipedia/database/text.in -u src/benchmarks/wikipedia/database/user.in -w src/benchmarks/wikipedia/database/watchlist.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru6.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru2.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru1.in"
  "-p src/benchmarks/wikipedia/database/page.in -r src/benchmarks/wikipedia/database/revision.in -t src/benchmarks/wikipedia/database/text.in -u src/benchmarks/wikipedia/database/user.in -w src/benchmarks/wikipedia/database/watchlist.in src/benchmarks/wikipedia/application-scalability/update-read-read/urr0.in src/benchmarks/wikipedia/application-scalability/update-read-read/urr1.in src/benchmarks/wikipedia/application-scalability/update-read-read/urr2.in"
  "-p src/benchmarks/wikipedia/database/page.in -r src/benchmarks/wikipedia/database/revision.in -t src/benchmarks/wikipedia/database/text.in -u src/benchmarks/wikipedia/database/user.in -w src/benchmarks/wikipedia/database/watchlist.in src/benchmarks/wikipedia/application-scalability/update-update-update/uuu4.in src/benchmarks/wikipedia/application-scalability/update-update-update/uuu5.in src/benchmarks/wikipedia/application-scalability/update-update-update/uuu6.in"
)

benchmarks=(
  benchmarks.courseware.BigTestCourseWare
  benchmarks.shoppingCart.BigTestShoppingCart
  benchmarks.tpcc.BigTestTPCC
  benchmarks.twitter.BigTestTwitter
  benchmarks.wikipedia.BigTestWikipedia
)


for i in "${!folders[@]}"; do
  fold="${folders[i]}"
  args="${argsList[i]}"
  benchmark="${benchmarks[$((i / 5))]}"
  mkdir -p $fold
  # shellcheck disable=SC2068
  for true_model in ${true_models[@]}; do
    echo  java -jar build/RunJPF.jar  +db.database_true_isolation_level.class=$true_model +report.console.file-prefix=$fold $benchmark $args
    java -jar build/RunJPF.jar  +db.database_true_isolation_level.class=$true_model +report.console.file-prefix=$fold $benchmark $args
  done
  
  # shellcheck disable=SC2068
  for model in ${models[@]}; do
      echo  java -jar build/RunJPF.jar +db.database_isolation_level.class=$model +report.console.file-prefix=$fold $benchmark $args
      java -jar build/RunJPF.jar +db.database_isolation_level.class=$model +report.console.file-prefix=$fold $benchmark $args
  done
    
  echo  java -jar build/RunJPF.jar +search.class=country.lab.search.NaiveIsoTrDFSearch +db.database_model.class=country.lab.database.NaiveIsoTrDatabase +report.console.file-prefix=$fold $benchmark $args
  java -jar build/RunJPF.jar +search.class=country.lab.search.NaiveIsoTrDFSearch +db.database_model.class=country.lab.database.NaiveIsoTrDatabase +report.console.file-prefix=$fold $benchmark $args
done


