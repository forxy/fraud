#!/bin/sh
export JAVA_OPTS="$JAVA_OPTS
 -Dspring.profiles.active=dev
 -Dconfig.dir=$TOMCAT_HOME/conf
 -Xdebug
 -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8084
 -Dcom.sun.management.jmxremote
 -Dcom.sun.management.jmxremote.ssl=false
 -Dcom.sun.management.jmxremote.port=8087
 -Dcom.sun.management.jmxremote.authenticate=false
 -Djava.rmi.server.hostname=localhost
 -XX:MaxPermSize=1024m
 -XX:PermSize=1024m
 -XX:+DisableExplicitGC
 -XX:+UseConcMarkSweepGC
 -XX:+CMSClassUnloadingEnabled
 -XX:CMSInitiatingOccupancyFraction=40
 -XX:+UseCMSInitiatingOccupancyOnly
 -XX:+CMSIncrementalMode
 -XX:+CMSIncrementalPacing
 -XX:CMSIncrementalDutyCycleMin=10
 -XX:CMSIncrementalDutyCycle=50
 -XX:ParallelGCThreads=8
 -XX:+UseParNewGC
 -XX:MaxGCPauseMillis=2000
 -XX:GCTimeRatio=10"
# -javaagent:/usr/local/appdynamics/javaagent/javaagent.jar"
