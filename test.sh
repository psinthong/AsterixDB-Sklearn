#!/bin/bash

cd lib

javac -cp jep-3.7.0.jar hello.java

java -cp .:jep-3.7.0.jar hello

rm hello.class

cd ..