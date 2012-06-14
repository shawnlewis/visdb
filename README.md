# visdb

Clojure playground. Maybe this will eventually be a hypercard-like simple
visual database builder/viewer/manager. But for now we can use this repo for
just messing around with clojure.

## Setup

### Install leiningen.

Leiningen is clojure's awesome package manager. Don't install it via port as
port will install 1.6 and we want 1.7. Also the Leiningen script can update
itself, which would probably break another package manager.

- Download the leiningen script from the "Download the script" link at https://github.com/technomancy/leiningen

- Stick it somewhere on your path.


### Checkout this project

    git clone https://github.com/shawnlewis/visdb.git


### Setup git submodules

I wanted to use a newer version of jayq then is available via lein. I
decided the best way to do this is to checkout the dependency (in this case
jayq) as a git submodule, and then to symlink their from checkouts/, which
leiningen will insert into the classpath in front of everything else.

These commands will update all git submodules in the repository (currently
only jayq).

    git submodule init
    git submodule update


### Setup the project's dependencies

    cd visdb
    lein deps


### Run the web server

    lein run


### Compile the clojurescript

Run this in another terminal. It'll automatically recompile whenever a *.cljs
file changes.

    lein cljsbuild auto


Now you should be able to navigate to http://localhost:8080/welcome. Currently,
all you can do is drag the controls in the controls box into the drop area.


## Filesystem layout

The files are laid out as created by "lein noir new". noir is clojure's
most popular web framework. You won't have the "lein noir" command unless you
"lein plugin install noir" or something like that.

The code is in src/ with the clojurescript code in src/client


## Other cool stuff to do

### Play with clojure in a repl

Most lispers run a repl within their editor. That way you can do cool stuff
like send forms (meaning pieces of code) into the repl from an editor buffer.
This makes it easy to do interactive development. I'm running a repl in vi
using the slimv plugin. Let me know if you want help with this.

For now you can just run a standalone repl.

    lein repl


### Play with clojurescript in a repl

This is the simple repl that just lets you evaluate clojurescript:

    lein trampoline cljsbuild repl-rhino

But you should use rlwrap to get readline behavior (up arrow for previous
command etc):

    rlwrap -r -m '\"' -b "(){}[],^%3@\";:'" lein trampoline cljsbuild repl-rhino

On non-osx you may need a "-q".

The really cool stuff you can do though is connect a repl to the browser. Then
you can run clojurescript commands actually in the browser. Try this:

    rlwrap -r -m '\"' -b "(){}[],^%3@\";:'" lein trampoline cljsbuild repl-browser

Then navigate to http://localhost:8080/welcome. Then run

    (js/alert "Hello!")

in the repl. You should get a popup in your browser. This works because of line
12 in visdb/client/main.cljs which connects to the repl when the page is
loaded.
