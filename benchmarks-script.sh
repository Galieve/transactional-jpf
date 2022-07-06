#!/bin/bash

RA="fr.irif.database.ReadAtomicHistory"
CC="fr.irif.database.CausalHistory"
trueModels=("fr.irif.database.CausalHistory"
          "fr.irif.database.PrefixHistory" "fr.irif.database.SnapshotIsolationHistory"
          "fr.irif.database.SerializableHistory")

commands=(
  "+report.console.file-prefix=bin/benchmarks/application-scalability/twitter/urr benchmarks.twitter.BigTestTwitter -u src/benchmarks/twitter/database/users.in src/benchmarks/twitter/application-scalability/update-read-read/urr0.in src/benchmarks/twitter/application-scalability/update-read-read/urr1.in src/benchmarks/twitter/application-scalability/update-read-read/urr2.in"
  "+report.console.file-prefix=bin/benchmarks/application-scalability/twitter/uru benchmarks.twitter.BigTestTwitter -u src/benchmarks/twitter/database/users.in src/benchmarks/twitter/application-scalability/update-read-update/uru0.in src/benchmarks/twitter/application-scalability/update-read-update/uru1.in src/benchmarks/twitter/application-scalability/update-read-update/uru2.in"
  "+report.console.file-prefix=bin/benchmarks/application-scalability/wikipedia/urr benchmarks.wikipedia.BigTestWikipedia -p src/benchmarks/wikipedia/database/page.in -r src/benchmarks/wikipedia/database/revision.in -t src/benchmarks/wikipedia/database/text.in -u src/benchmarks/wikipedia/database/user.in src/benchmarks/wikipedia/application-scalability/update-read-read/urr0.in src/benchmarks/wikipedia/application-scalability/update-read-read/urr1.in src/benchmarks/wikipedia/application-scalability/update-read-read/urr2.in"
  "+report.console.file-prefix=bin/benchmarks/application-scalability/wikipedia/uru benchmarks.wikipedia.BigTestWikipedia -p src/benchmarks/wikipedia/database/page.in -r src/benchmarks/wikipedia/database/revision.in -t src/benchmarks/wikipedia/database/text.in -u src/benchmarks/wikipedia/database/user.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru0.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru1.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru2.in"
  "+report.console.file-prefix=bin/benchmarks/application-scalability/courseware/uru benchmarks.courseware.BigTestCourseWare -s src/benchmarks/courseware/database/students.in -c src/benchmarks/courseware/database/courses.in src/benchmarks/courseware/application-scalability/update-read-update/uru0.in src/benchmarks/courseware/application-scalability/update-read-update/uru1.in src/benchmarks/courseware/application-scalability/update-read-update/uru2.in"
  "+report.console.file-prefix=bin/benchmarks/application-scalability/courseware/urr benchmarks.courseware.BigTestCourseWare -s src/benchmarks/courseware/database/students.in -c src/benchmarks/courseware/database/courses.in src/benchmarks/courseware/application-scalability/update-read-read/urr0.in src/benchmarks/courseware/application-scalability/update-read-read/urr1.in src/benchmarks/courseware/application-scalability/update-read-read/urr2.in"
  "+report.console.file-prefix=bin/benchmarks/application-scalability/shoppingCart/uru benchmarks.shoppingCart.BigTestShoppingCart -s src/benchmarks/shoppingCart/database/store.in src/benchmarks/shoppingCart/application-scalability/update-read-update/uru0.in src/benchmarks/shoppingCart/application-scalability/update-read-update/uru1.in src/benchmarks/shoppingCart/application-scalability/update-read-update/uru2.in"
  "+report.console.file-prefix=bin/benchmarks/application-scalability/shoppingCart/urr benchmarks.shoppingCart.BigTestShoppingCart -s src/benchmarks/shoppingCart/database/store.in src/benchmarks/shoppingCart/application-scalability/update-read-read/urr0.in src/benchmarks/shoppingCart/application-scalability/update-read-read/urr1.in src/benchmarks/shoppingCart/application-scalability/update-read-read/urr2.in"
  "+report.console.file-prefix=bin/benchmarks/application-scalability/tpcc/uuu-light benchmarks.tpcc.BigTestTPCC -d src/benchmarks/tpcc/database/districts.in -n src/benchmarks/tpcc/database/neworder.in -i src/benchmarks/tpcc/database/item.in -w src/benchmarks/tpcc/database/warehouse.in -s src/benchmarks/tpcc/database/stock.in -h src/benchmarks/tpcc/database/history.in -l src/benchmarks/tpcc/database/orderline.in -c src/benchmarks/tpcc/database/customer.in -o src/benchmarks/tpcc/database/order.in src/benchmarks/tpcc/application-scalability/update-update-update-light/uuu0.in src/benchmarks/tpcc/application-scalability/update-update-update-light/uuu1.in src/benchmarks/tpcc/application-scalability/update-update-update-light/uuu2.in"
  "+report.console.file-prefix=bin/benchmarks/application-scalability/tpcc/uuu benchmarks.tpcc.BigTestTPCC -d src/benchmarks/tpcc/database/districts.in -n src/benchmarks/tpcc/database/neworder.in -i src/benchmarks/tpcc/database/item.in -w src/benchmarks/tpcc/database/warehouse.in -s src/benchmarks/tpcc/database/stock.in -h src/benchmarks/tpcc/database/history.in -l src/benchmarks/tpcc/database/orderline.in -c src/benchmarks/tpcc/database/customer.in -o src/benchmarks/tpcc/database/order.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu0.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu1.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu2.in"
)

for command in "${commands[@]}"; do
  echo java -jar build/RunJPF.jar +db.database_model.class=$RA $command
  java -jar build/RunJPF.jar +db.database_model.class=$RA $command
  for trueModel in "${trueModels[@]}"; do
    echo java -jar build/RunJPF.jar +db.database_true_model.class="$trueModel" +db.database_model.class=$CC $command
    java -jar build/RunJPF.jar +db.database_true_model.class="$trueModel" +db.database_model.class=$CC $command
  done
done
