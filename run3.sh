#!/usr/bin/env bash

javac -cp "lib/*" src/*.java
java -cp "lib/*:src" DBCombine Memes_front/TYLER_DATABASES/DATABASE1/image_db2 Memes_front/TYLER_DATABASES/DATABASE2/image_db2 Memes_front/TYLER_DATABASES/DATABASE1/keyw_db2 Memes_front/TYLER_DATABASES/DATABASE2/keyw_db2
