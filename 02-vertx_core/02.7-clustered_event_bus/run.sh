#!/bin/bash

mvn package

for i in 1 2
do
    java -jar target/vertx-*.jar 2 &
done

java -jar target/vertx-*.jar producer
