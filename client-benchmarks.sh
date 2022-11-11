#!/bin/bash

benchmarkNames=(
  #courseware
  #shoppingCart
  #tpcc
  #twitter
  wikipedia
)

num_rows=1
num_columns=5
num_cases=5

declare -a sources

#sources[courseware,1]="src/benchmarks/courseware/application-scalability/update-read-update/uru0.in src/benchmarks/courseware/application-scalability/update-read-update/uru1.in src/benchmarks/courseware/application-scalability/update-read-update/uru2.in"
#sources[courseware,2]="src/benchmarks/courseware/application-scalability/update-read-read/urr0.in src/benchmarks/courseware/application-scalability/update-read-read/urr1.in src/benchmarks/courseware/application-scalability/update-read-read/urr2.in"
#sources[courseware,3]="src/benchmarks/courseware/application-scalability/update-update-update/uuu0.in src/benchmarks/courseware/application-scalability/update-update-update/uuu1.in src/benchmarks/courseware/application-scalability/update-update-update/uuu2.in"
#sources[courseware,4]="src/benchmarks/courseware/application-scalability/update-update-update/uuu3.in src/benchmarks/courseware/application-scalability/update-update-update/uuu4.in src/benchmarks/courseware/application-scalability/update-update-update/uuu5.in"
#sources[courseware,5]="src/benchmarks/courseware/application-scalability/update-update-update/uuu4.in src/benchmarks/courseware/application-scalability/update-update-update/uuu3.in src/benchmarks/courseware/application-scalability/update-update-update/uuu0.in"


#sources[shoppingCart,1]="src/benchmarks/shoppingCart/application-scalability/update-read-read/urr0.in src/benchmarks/shoppingCart/application-scalability/update-read-read/urr1.in src/benchmarks/shoppingCart/application-scalability/update-read-read/urr2.in"
#sources[shoppingCart,2]="src/benchmarks/shoppingCart/application-scalability/update-read-update/uru0.in src/benchmarks/shoppingCart/application-scalability/update-read-update/uru1.in src/benchmarks/shoppingCart/application-scalability/update-read-update/uru2.in"
#sources[shoppingCart,3]="src/benchmarks/shoppingCart/application-scalability/update-read-update/uru3.in src/benchmarks/shoppingCart/application-scalability/update-read-update/uru4.in src/benchmarks/shoppingCart/application-scalability/update-read-update/uru5.in"
#sources[shoppingCart,4]="src/benchmarks/shoppingCart/application-scalability/update-update-update/uuu0.in src/benchmarks/shoppingCart/application-scalability/update-update-update/uuu1.in src/benchmarks/shoppingCart/application-scalability/update-update-update/uuu2.in"
#sources[shoppingCart,5]="src/benchmarks/shoppingCart/application-scalability/update-update-update/uuu3.in src/benchmarks/shoppingCart/application-scalability/update-update-update/uuu4.in src/benchmarks/shoppingCart/application-scalability/update-update-update/uuu5.in"


#sources[tpcc,1]="src/benchmarks/tpcc/application-scalability/update-update-update/uuu0.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu1.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu2.in"
#sources[tpcc,2]="src/benchmarks/tpcc/application-scalability/update-update-update/uuu3.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu4.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu5.in"
#sources[tpcc,3]="src/benchmarks/tpcc/application-scalability/update-update-update/uuu5.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu1.in src/benchmarks/tpcc/application-scalability/update-update-update/uuu3.in"
#sources[tpcc,4]="src/benchmarks/tpcc/application-scalability/update-update-update-light/uuu0.in src/benchmarks/tpcc/application-scalability/update-update-update-light/uuu1.in src/benchmarks/tpcc/application-scalability/update-update-update-light/uuu2.in"
#sources[tpcc,5]="src/benchmarks/tpcc/application-scalability/update-update-update-strong/uuu0.in src/benchmarks/tpcc/application-scalability/update-update-update-strong/uuu1.in src/benchmarks/tpcc/application-scalability/update-update-update-strong/uuu2.in"

#sources[twitter,1]="src/benchmarks/twitter/application-scalability/update-read-read/urr0.in src/benchmarks/twitter/application-scalability/update-read-read/urr1.in src/benchmarks/twitter/application-scalability/update-read-read/urr2.in"
#sources[twitter,2]="src/benchmarks/twitter/application-scalability/update-read-update/uru0.in src/benchmarks/twitter/application-scalability/update-read-update/uru1.in src/benchmarks/twitter/application-scalability/update-read-update/uru2.in"
#sources[twitter,3]="src/benchmarks/twitter/application-scalability/update-read-update/uru3.in src/benchmarks/twitter/application-scalability/update-read-update/uru4.in src/benchmarks/twitter/application-scalability/update-read-update/uru5.in"
#sources[twitter,4]="src/benchmarks/twitter/application-scalability/update-update-update/uuu0.in src/benchmarks/twitter/application-scalability/update-update-update/uuu1.in src/benchmarks/twitter/application-scalability/update-update-update/uuu2.in"
#sources[twitter,5]="src/benchmarks/twitter/application-scalability/update-update-update/uuu3.in src/benchmarks/twitter/application-scalability/update-update-update/uuu4.in src/benchmarks/twitter/application-scalability/update-update-update/uuu5.in"


sources[wikipedia,1]="src/benchmarks/wikipedia/application-scalability/update-read-update/uru0.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru1.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru2.in"
sources[wikipedia,2]="src/benchmarks/wikipedia/application-scalability/update-read-update/uru3.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru4.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru5.in"
sources[wikipedia,3]="src/benchmarks/wikipedia/application-scalability/update-read-update/uru6.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru2.in src/benchmarks/wikipedia/application-scalability/update-read-update/uru1.in"
sources[wikipedia,4]="src/benchmarks/wikipedia/application-scalability/update-read-read/urr0.in src/benchmarks/wikipedia/application-scalability/update-read-read/urr1.in src/benchmarks/wikipedia/application-scalability/update-read-read/urr2.in"
sources[wikipedia,5]="src/benchmarks/wikipedia/application-scalability/update-update-update/uuu4.in src/benchmarks/wikipedia/application-scalability/update-update-update/uuu5.in src/benchmarks/wikipedia/application-scalability/update-update-update/uuu6.in"


for ((i=0;i<num_rows;i++)) do
  benchmarkName="${benchmarkNames[i]}"
  for ((l=1;l<=num_cases;l++)) do

    #fold=src/benchmarks/$benchmarkName/transaction-scalability/case$l/$j-transactions-per-sessions/
    new_fold=src/benchmarks/$benchmarkName/application-scalability/case$l/

    mkdir -p "$new_fold"
    files="${sources["$benchmarkName",$l]}"
    #echo "$files"
    #echo "$new_fold"

    idx=1

    for f in $files
    do
      :


      cp $f "$new_fold"/thread$idx.in
      idx=$(($idx + 1))

      # do whatever on $i
    done
    #rmdir "$fold"

  done
done


exit




