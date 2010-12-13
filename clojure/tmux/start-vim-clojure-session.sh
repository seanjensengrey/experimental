#!/bin/sh

usage="$0 [file] [workspace]"

# check inputs
if [ -z "$1" ]; then
    echo $usage
    exit
fi

if [ -z "$2" ]; then
    echo $usage
    exit
fi

target=$1
workspace=$2

#kill and create this session
tmux kill-session -t $workspace
tmux new-session -d -s $workspace "vim $target"

#this window should be selected already
tmux select-window -t $workspace:0

#create vertical split
tmux split-window -t $workspace:0 -h

#place focus on the left pane (#0)
tmux select-pane -t $workspace:0.0

#launch clojure (remember the ^M is actually C-v RETURN) on the right hand pane
tmux send-keys -t $workspace:0.right 'lein repl'

#configure the tmux vim plugin to use this session/window/pane combination (right hand pane is pane #1)
tmux send-keys -t $workspace:0.0 ":call Tmux_Init('$workspace',0,1)"

#attach to the workspace
tmux attach-session -t $workspace
