#!/bin/bash
## 数组控制

source Profile.sh

# 返回数组长度 $1
arrayLength (){
  echo $1
  temp_array="$1"
  RETURN_VAR=${#temp_array[@]}
  return $RETURN_VAR
}

# 打印数组 $1
arrayPrint (){
  temp_array=$1
  for var in ${temp_array[@]}
    do
      prompt -i "$var"
    done
}

a=(1 2 3 4 5)
# echo ${#a[@]}
arrayLength "${a[@]}"
echo $RETURN_VAR
arrayPrint $a