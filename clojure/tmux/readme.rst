============================
Vim+Clojure+Tmux
============================
Based on:
    http://technotales.wordpress.com/2008/10/17/screencast-like-slime-for-vim/
    http://github.com/kikijump/tslime.vim/blob/master/tslime.vim

Installation
------------

1. modify the clj.sh to point at your clojure jars
2. copy the .vim script into ~/.vim/plugin/.
3. launch the start script with a workspace name and a clojure file to edit...

Usage
------------

Example::

     ./start-vim-clojure-session.sh foo.clj ws01

1. You should now have a tmux (like screen) session, divided into two panes, with a vim editor on the left and a clojure repl on the right.

2. In vim, place your cursor on any block of text that you want to send to the repl, and type C-c C-c (control-c control-c, in quick succession). This should send your text to the repl, and execute it there.

3. To send the entire file to the repl, type C-c C-a.

Have fun.
-Todd
