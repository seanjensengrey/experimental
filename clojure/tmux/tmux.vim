""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" tmux.vim
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

function! Send_to_Tmux(text)
  if !exists("g:tmux_session") || !exists("g:tmux_window") || !exists("g:tmux_pane")
    call Tmux_Vars()
  end
  "echo 'sending... ' . a:text
  let target = g:tmux_session . ":" . g:tmux_window . "." . g:tmux_pane
  call system("tmux set-buffer -t " . g:tmux_session . " '" . substitute(a:text, "'", "'\\\\''", 'g') . "'" )
  call system("tmux paste-buffer -t " . target)
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

" copy and send a block of text
vmap <C-c><C-c> "ry :call Send_to_Tmux(@r)<CR>
nmap <C-c><C-c> vip<C-c><C-c>

" copy and send entire doc
"vmap <C-c><C-a> "by :call Send_to_Tmux(@b)<CR>
"nmap <C-c><C-a> govG<end><C-c><C-a>
nmap <C-c><C-a> govG<end><C-c><C-c>

"nmap <C-c>v :call Tmux_Vars()<CR>
