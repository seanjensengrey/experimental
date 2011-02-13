=================================================
Hacking Through The Erlang Wilderness : Episode 3 
=================================================

.. footer:: Copyright (c) 2011 Todd D. Greenwood-Geer 

:Author: Todd D. Greenwood-Geer
:Date: Tue Feb 13  2011
:Version: 0.1
:Index: Index_ : Listing of all the episodes


----------------------------
How to Create a Time Server
----------------------------

Review
------

So in the previous installment, we created an application named 'app01'. To this empty application, we added a gen_server behaviour, 'src/app01.erl'. We had to modify the 'src/app01_sup.erl' to reference the behaviour. Lastly, we had to update the 'ebin/app01.app' to include this behaviour in the top level application definition:

 * src/app01_sup.erl : code that determines the lifecycle of our application, restarts it if it dies and so on
 * src/app01_app.erl : code that describes our application, has a start() and stop() methods
 * src/app01.erl : a gen_server behavior that defines what our app *is* and *does*
 * ebin/app01.app : the top level definition of our application, modules are specified here

To go more in-depth on project structure and whatnot, check these out:

 * SinanProjects_ : shows single and multi app projects
 * [LOGAN]_ : page 119 starts the chapter on 'OTP applications and supervision' and lays out application structure


Time Server Basics
------------------

We're going to create an app that spits back the time. Super simple. Along the way, we'll 

 * instrument the app with print statements so we can see what's going on inside
 * add some eunit tests
 * play more with sinan and rebar

1. Create new application, 'time_srv'

::

    todd@ubuntu:~/temp$ sinan gen time_srv

::

    starting: gen
    Please specify your name 
    your name> Todd Greenwood-Geer
    Please specify your email address 
    your email> todd@niovb.com
    Please specify the copyright holder 
    copyright holder ("Todd Greenwood-Geer")> 
    Please specify version of your project
    project version> 0.0.1
    Please specify the ERTS version ("5.8.2")> 
    Is this a single application project ("n")> y
    /home/todd/temp/time_srv/doc created ok.
    /home/todd/temp/time_srv/bin created ok.
    /home/todd/temp/time_srv/config created ok.
    /home/todd/temp/time_srv/ebin created ok.
    /home/todd/temp/time_srv/src created ok.
    /home/todd/temp/time_srv/include created ok.
    /home/todd/temp/time_srv/doc exists ok.
    Would you like a build config? ("y")> 
    Project was created, you should be good to go!
    
    
2. Add the gen_server behaviour to this application

::
    
    todd@ubuntu:~/temp$ cd time_srv/
    todd@ubuntu:~/temp/time_srv$ wget http://bitbucket.org/basho/rebar/downloads/rebar; chmod u+x rebar

::

    --2011-02-13 10:49:30--  http://bitbucket.org/basho/rebar/downloads/rebar
    Resolving bitbucket.org... 207.223.240.181, 207.223.240.182
    Connecting to bitbucket.org|207.223.240.181|:80... connected.
    HTTP request sent, awaiting response... 301 Moved Permanently
    Location: https://bitbucket.org/basho/rebar/downloads/rebar [following]
    --2011-02-13 10:49:30--  https://bitbucket.org/basho/rebar/downloads/rebar
    Connecting to bitbucket.org|207.223.240.181|:443... connected.
    HTTP request sent, awaiting response... 302 FOUND
    Location: http://cdn.bitbucket.org/basho/rebar/downloads/rebar [following]
    --2011-02-13 10:49:30--  http://cdn.bitbucket.org/basho/rebar/downloads/rebar
    Resolving cdn.bitbucket.org... 216.137.35.70, 216.137.35.73, 216.137.35.249, ...
    Connecting to cdn.bitbucket.org|216.137.35.70|:80... connected.
    HTTP request sent, awaiting response... 200 OK
    Length: 85084 (83K) [binary/octet-stream]
    Saving to: `rebar'

    100%[===============================================================================>] 85,084      --.-K/s   in 0.1s    
    2011-02-13 10:49:30 (839 KB/s) - `rebar' saved [85084/85084]

If you download the source to rebar, you can check out the template variables. We'll use one now...

What are the rebar commands?

::

    todd@ubuntu:~/temp/time_srv$ rebar -c

    analyze                              Analyze with Dialyzer
    build_plt                            Build Dialyzer PLT
    check_plt                            Check Dialyzer PLT

    clean                                Clean
    compile                              Compile sources

    create      template= [var=foo,...]  Create skel based on template and vars
    create-app  [appid=myapp]            Create simple app skel
    create-node [nodeid=mynode]          Create simple node skel
    list-templates                       List available templates

    doc                                  Generate Erlang program documentation

    check-deps                           Display to be fetched dependencies
    get-deps                             Fetch dependencies
    delete-deps                          Delete fetched dependencies

    generate    [dump_spec=0/1]          Build release with reltool

    eunit       [suite=foo]              Run eunit [test/foo_tests.erl] tests
    ct          [suite=] [case=]         Run common_test suites in ./test

    xref                                 Run cross reference analysis

    help                                 Show the program options
    version                              Show version information

What templates are available again?

::

    todd@ubuntu:~/temp/time_srv$ rebar list-templates
    ==> time_srv (list-templates)
    Available templates:
            * simplesrv: priv/templates/simplesrv.template (escript)
            * simplenode: priv/templates/simplenode.template (escript)
            * simplemod: priv/templates/simplemod.template (escript)
            * simplefsm: priv/templates/simplefsm.template (escript)
            * simpleapp: priv/templates/simpleapp.template (escript)
            * basicnif: priv/templates/basicnif.template (escript)

Cool, let's make create a file from a template

::

    todd@ubuntu:~/temp/time_srv$ rebar create template=simplesrv srvid=time_srv
    ==> time_srv (create)
    Writing src/time_srv.erl

And the file we just created, 'src/time_srv.erl'

::

    todd@ubuntu:~/temp/time_srv$ cat src/time_srv.erl 
    -module(time_srv).
    -behaviour(gen_server).
    -define(SERVER, ?MODULE).

    %% ------------------------------------------------------------------
    %% API Function Exports
    %% ------------------------------------------------------------------

    -export([start_link/0]).

    %% ------------------------------------------------------------------
    %% gen_server Function Exports
    %% ------------------------------------------------------------------

    -export([init/1, handle_call/3, handle_cast/2, handle_info/2, terminate/2, code_change/3]).

    %% ------------------------------------------------------------------
    %% API Function Definitions
    %% ------------------------------------------------------------------

    start_link() ->
      gen_server:start_link({local, ?SERVER}, ?MODULE, [], []).

    %% ------------------------------------------------------------------
    %% gen_server Function Definitions
    %% ------------------------------------------------------------------

    init(Args) ->
      {ok, Args}.

    handle_call(_Request, _From, State) ->
      {noreply, ok, State}.

    handle_cast(_Msg, State) ->
      {noreply, State}.

    handle_info(_Info, State) ->
      {noreply, State}.

    terminate(_Reason, _State) ->
      ok.

    code_change(_OldVsn, State, _Extra) ->
      {ok, State}.

    %% ------------------------------------------------------------------
    %% Internal Function Definitions
    %% ------------------------------------------------------------------

3. Update the superviser, 'src/time_srv_sup.erl', by inserting references to time_srv in the child definition

::

     46     %AChild = {'AName', {'AModule', start_link, []},
     47     %          Restart, Shutdown, Type, ['AModule']},
     48     
     49     AChild = {time_srv, {time_srv, start_link, []},
     50               Restart, Shutdown, Type, [time_srv]},

4. Update the modules list in the application definition, 'ebin/time_srv.app'

::

      1 %% This is the application resource file (.app file) for the time_srv,
      2 %% application.
      3 {application, time_srv,
      4   [{description, "Your Desc HERE"},
      5    {vsn, "0.0.1"},
      6    {modules, [  time_srv,
      7                 time_srv_app,
      8                 time_srv_sup]},
      9    {registered,[time_srv_sup]},
     10    {applications, [kernel, stdlib]},
     11    {mod, {time_srv_app,[]}},
     12    {start_phases, []}]}.

I inserted the reference to the time_srv behaviour in line 6 above.

5. Run the app

 * Build::

    todd@ubuntu:~/temp/time_srv$ sinan build
    starting: depends
    starting: build
    Building /home/todd/temp/time_srv/src/time_srv_sup.erl
    Building /home/todd/temp/time_srv/src/time_srv_app.erl
    Building /home/todd/temp/time_srv/src/time_srv.erl

 * Run::

    todd@ubuntu:~/temp/time_srv$ sinan shell
    Erlang R14B01 (erts-5.8.2) [source] [rq:1] [async-threads:0] [hipe] [kernel-poll:false]

    Eshell V5.8.2  (abort with ^G)
    1> starting: depends
    starting: build
    starting: shell
    Eshell V5.8.2  (abort with ^G)
    1> application:start(time_srv).
    ok

 * Check process registry::

    2> regs().

    ** Registered procs on node nonode@nohost **
    Name                  Pid          Initial Call                      Reds Msgs
    time_srv              <0.100.0>    time_srv:init/1                     26    0
    time_srv_sup          <0.99.0>     supervisor:time_srv_sup/1          110    0
    timer_server          <0.67.0>     timer:init/1                       106    0

6. Ok, at this point, we're where we left off in Episode-02_. A copy of the code at this point is here under time-srv-01.

Time Server Functionality
-------------------------

TODO
 

References
==========

.. [ARMSTRONG]
    Armstrong, Joe.
    Programming Erlang
    The Pragmatic Bookshelf, 2007. ISBN 978-1-934356-00-5

.. [CESARINI] 
    Cesarini, Francesco, Thompson, Simon.
    Erlang Programming
    O'Reily, 2009. ISBN 978-0-596-51818-9

.. [LOGAN]
    Logan, Martin, Merritt, Eric, Carlsson, Richard.
    Erlang and OTP in Action
    Manning, 2011. ISBN 9781933988788

.. _SinanProjects: http://erlware.github.com/sinan/SinanProjects.html

.. _Sinan_Faxien_Demo: http://www.youtube.com/watch?v=XI7S2NwFPOE

.. _Basho_Rebar_Demo: http://blog.basho.com/category/rebar/

.. _Erlware: http://erlware.com/

.. _Rebar: https://bitbucket.org/basho/rebar/wiki/GettingStarted

.. _Index: https://github.com/ToddG/experimental/tree/master/erlang/wilderness

.. _Episode-02: https://github.com/ToddG/experimental/tree/master/erlang/wilderness/02/episode-02.html
