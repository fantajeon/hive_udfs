#!/bin/bash

hive_jar=$(ls /usr/local/Cellar/hive/2.1.0/libexec/lib/hive-exec-*.jar)
hadoop_jar='/usr/local/Cellar/hadoop/2.7.2/libexec/share/hadoop/common/hadoop-common-2.7.2.jar'

echo "hive_jar=${hive_jar}"
echo "hadoop_jar=${hadoop_jar}"

javac -cp ${hive_jar}:${hadoop_jar} org/sch/udf/HangulEditDistance.java
jar -cf hangul_editdistance.jar -C . .

#hive -f hangul_editdistance_test.sql
./test.sh
