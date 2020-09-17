#!/bin/bash

export CLASSPATH=.:lib/*
./build.sh

echo --- Running
#echo -n "Enter the .txt file name to be used as input (tiny/small/medium/large): "
#read F
F=large
SAMPLE_INPUT=sample_input/$F.txt
SAMPLE_OUTPUT=sample_output/$F.out
SERVER_OUTPUT=myoutput.txt

#echo -n "Enter the server's host name or IP address: "
#read SERVER_HOST
#echo -n "Enter the server's TCP port number: "
#read SERVER_PORT

#java Client $SERVER_HOST $SERVER_PORT $SAMPLE_INPUT $SERVER_OUTPUT
#read USELESS
java Client localhost 10123 $SAMPLE_INPUT $SERVER_OUTPUT

echo --- Comparing server\'s output against sample output
sort -o $SERVER_OUTPUT $SERVER_OUTPUT
sort -o $SAMPLE_OUTPUT $SAMPLE_OUTPUT

#diff $SERVER_OUTPUT $SAMPLE_OUTPUT
#if [ $? -eq 0 ]; then
#    echo Outputs match
#else
#    echo Outputs DO NOT match
#fi
