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
