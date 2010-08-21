
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

function! Send_to_Tmux(text)
  if !exists("g:tmux_session") || !exists("g:tmux_window") || !exists("g:tmux_pane")
    call Tmux_Vars()
  end
  echo system("tmux send-keys -t " . g:tmux_session . ":" . g:tmux_window . "." . g:tmux_pane . " '" . a:text . "'")
endfunction

function! Tmux_Sessions()
  echo system("tmux list-sessions | sed 's/:.*//'")
  return system("tmux list-sessions | sed 's/:.*//'")
endfunction

function! Tmux_Init(session, window, pane)
  let g:tmux_session = a:session
  let g:tmux_window = a:window
  let g:tmux_pane = a:pane
endfunction

function! Tmux_Vars()
  if !exists("g:tmux_session") 
    let g:tmux_session = ""
    let g:tmux_window = "0"
    let g:tmux_pane="right"
  end

  let g:tmux_session = input("tmux session: ", g:tmux_session)
  let g:tmux_window = input("tmux window: ", g:tmux_window)
  let g:tmux_pane = input("tmux pane: ", g:tmux_window)
endfunction

""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

vmap <C-c><C-c> "ry :call Send_to_Tmux(@r)<CR>
nmap <C-c><C-c> vip<C-c><C-c>

nmap <C-c>v :call Tmux_Vars()<CR>

