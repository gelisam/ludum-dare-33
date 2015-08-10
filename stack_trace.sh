#!/bin/bash
# This is the only way I found to get a stack trace.
# I can't run the jar because it can't find lwjgl classes, and mvn test shows the maven stack trace.
echo run | sbt -Djava.library.path="$(pwd)/target/natives"
