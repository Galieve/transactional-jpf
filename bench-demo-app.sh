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
  bin/benchmarks/demo-application-scalability/courseware/
)

databaseArgs=(
  "-s src/benchmarks/courseware/database/students.in -c src/benchmarks/courseware/database/courses.in"
)

benchmarks=(
  benchmarks.courseware.BigTestCourseWare
)

benchmarkNames=(
  courseware
)

num_cases=2


for i in "${!folders[@]}"; do
  database="${databaseArgs[i]}"
  benchmark="${benchmarks[i]}"
  benchmarkName="${benchmarkNames[i]}"
  for ((j=1;j<=num_cases;j++)) do
    
    fold="${folders[i]}"/$j-threads/
    args=src/benchmarks/$benchmarkName/application-scalability/case$j/thread1.in\ src/benchmarks/$benchmarkName/application-scalability/case$j/thread2.in\ src/benchmarks/$benchmarkName/application-scalability/case$j/thread3.in

    execute_benchmark
  done
  

done


