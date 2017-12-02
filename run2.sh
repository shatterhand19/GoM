#!/usr/bin/env bash

javac -cp "lib/*" src/*.java
java -cp "lib/*:src" Labeler Memes_front/img2
