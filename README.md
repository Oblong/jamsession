# Jamsession

Jamsession is an application that hosts & serves pools for [Oblong](http://www.oblong.com)'s Plasma message-passing system.  Pools are a container and a forwarding mechanism for messages called proteins. 

Jamsession is implemented as a thin Clojure wrapper around a feature of the Jelly library, a pure-Java implementation of Plasma.

In the Plasma message-passing framework, the usual application for hosting and serving pools is *pool_tcp_server*, written in C.  It is so far the only completely implementation of a pool server. It's available from Oblong as part of the [g-speak platform](http://www.oblong.com/g-speak/). 

By contrast, the Jamsession pool server is restricted in two ways:

1. Normal pools are backed by a file on disk; these are not.  They live in the memory of the server process only.  Thus they are not persistent from session to session; hence the name Jamsession.  After the Jamsession ends, the data is gone.

2. It has the same Plasma communication restrictions as Oblong's free [Greenhouse SDK](http://greenhouse.oblong.com).  See *Implementation*, below.

Jamsession can run as a standalone program from the command line, or it can be invoked as a library by other JVM software (see the MakeJam example which is included).


## Implementation

Jamsession is written in a small amount of Clojure (see [src/jamsession/jam.clj](https://github.com/sandover/jamsession/blob/master/src/jamsession/jam.clj)).  The application is really only a thin wrapper around the TCPMemProxy feature of **Jelly**.

A copy of Jelly, in a jarfile, is included in this repo.  This is a specially restricted version of Jelly suitable for broad public release on the same terms as the [Greenhouse SDK](http://greenhouse.oblong.com).  The restriction is that it can't be used to communicate with regular g-speak pool-tcp-servers; only Greenhouse-affiliated ones. 

You can lift this restriction if you already have an unrestricted Jelly jar (or Jelly source) from Oblong.  See the instructions below for "Building Jamsession with a different Jelly jar".

Jamsession **can** accept incoming communications from any Plasma client: Greenhouse or full g-speak.  Only Jelly's outgoing (client) capabilities are restricted.


## Documentation

Documentation for the code was generated with [Marginalia](https://github.com/fogus/marginalia).  Open [docs/uberdoc.html](http://htmlpreview.github.io/?https://github.com/sandover/jamsession/blob/master/docs/uberdoc.html) in a browser.


## Building

This project is built with [leiningen](https://github.com/technomancy/leiningen) 2.0 or greater.  Once you have leiningen installed, just do:

    lein uberjar

This will fetch all dependencies, and compile Jamsession and its dependencies together into one standalone artifact: the *uberjar*.  It's created in target/


## To run Jamsession from the command line

### Using leiningen

    # default port: 65456
    lein run

You can provide command line arguments following '--':

    lein run -- --port 60000

### Using the uberjar

  java -jar jamsession-1.0.x-standalone.jar --port 60000

To see options:

  java -jar jamsession-1.0.x-standalone.jar --help


## To run Jamsession from a Java program

See the MakeJam example, included.  Do **build.sh**, then **run.sh**.  It may be necessary to edit MakeJam/run.sh to match the uberjar's name.


## Building Jamsession with a different Jelly jar

To build around the Jelly jar as a "local" jar, rather than one that is accessed from a maven server, leiningen requires us to *pretend* that the jar is in a maven repository.  I followed [this blog post](http://www.pgrs.net/2011/10/30/using-local-jars-with-leiningen/) and [this gist](https://gist.github.com/stuartsierra/3062743).  Given a file called 'jelly-0.1.jar', I did this command that set up the local maven pseudo-repository which now lives under maven_repository/:

     mvn deploy:deploy-file -DartifactId=jelly -Dversion=0.1 -DgroupId=com.oblong -Dfile=jelly-0.1.jar -Dpackaging=jar -Durl=file:maven_repository -DcreateChecksum=true

You can copy your own Jelly jar into the top level directory and do the same.





