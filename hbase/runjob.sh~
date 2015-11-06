#!/bin/bash
#########################################
# Name of the Script : runjob.sh
# No. of Input Parameters : 2
# Input Parameter accepted : <hbase table> <output location>
# Comment : This shell script launch the habse mapreduce program.
# Created by : Rahul
###########################################
hpath=$HBASE_HOME
jar=/home/hadoop/Desktop/techsquids/repos/techsquids/map-reduce/target/map-reduce-1.0-SNAPSHOT.jar
classpath=$jar
for f in $hpath/lib/*.jar;do
classpath="${classpath},$f"
done
path=`echo $classpath | sed 's/,/:/g'`
export LIBJARS=`hadoop classpath`:$classpath
export HADOOP_CLASSPATH=$path
echo $classpath
hadoop jar $jar com.ts.mapreduce.hbase.ColumnsCounter -libjars ${classpath} "$@"
