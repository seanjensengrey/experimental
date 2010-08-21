#!/bin/bash
# Runs Clojure using the classpath specified in the `.clojure` file of the
# current directory.
#
#Usage: java -cp clojure.jar clojure.main [init-opt*] [main-opt] [arg*]
#
#  With no options or args, runs an interactive Read-Eval-Print Loop
#
#  init options:
#    -i, --init path   Load a file or resource
#    -e, --eval string Evaluate expressions in string; print non-nil values
#
#  main options:
#    -r, --repl        Run a repl
#    path              Run a script from from a file or resource
#    -                 Run a script from standard input
#    -h, -?, --help    Print this help message and exit
#
#  operation:
#
#    - Establishes thread-local bindings for commonly set!-able vars
#    - Enters the user namespace
#    - Binds *command-line-args* to a seq of strings containing command line
#      args that appear after any main option
#    - Runs all init options in order
#    - Runs a repl or script if requested
#
#  The init options may be repeated and mixed freely, but must appear before
#  any main option. The appearance of any eval option before running a repl
#  suppresses the usual repl greeting message: "Clojure ~(clojure-version)".
#
#  Paths may be absolute or relative in the filesystem or relative to
#  classpath. Classpath-relative paths have prefix of @ or @/
#

JAVA=java
JLINE_JAR=/Users/todd/bin/jline/jline-0_9_5.jar
CLOJURE_JAR=/Users/todd/bin/clojure/clojure.jar
CLOJURE_CONTRIB_JAR=/Users/todd/bin/clojure/clojure-contrib.jar
CP=$JLINE_JAR:$CLOJURE_JAR:$CLOJURE_CONTRIB_JAR

# Add extra jars as specified by `.clojure` file
if [ -f .clojure ]; then
    CP=$CP:`cat .clojure`
fi

if [ -z "$1" ]; then
    if [ -f init.clj ]; then
        COMMAND="$JAVA -server -cp $CP jline.ConsoleRunner clojure.main -i init.clj -r"
    else
        COMMAND="$JAVA -server -cp $CP jline.ConsoleRunner clojure.main -r"
    fi
else
    if [ -f init.clj ]; then
        COMMAND="$JAVA -server -cp $CP jline.ConsoleRunner clojure.main -i init.clj $*"
    else
        COMMAND="$JAVA -server -cp $CP jline.ConsoleRunner clojure.main $*"
    fi
fi
echo $COMMAND
$COMMAND
