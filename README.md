# Jamsession

Jamsession is an application that hosts & serves pools for Oblong's Plasma message-passing system.  It's implemented as a thin Clojure wrapper around a feature of the Jelly library, which is written in pure Java.  Pools are a container and a forwarding mechanism for messages called proteins. 

In the Plasma system, the usual application for hosting and serving pools is *pool_tcp_server*, written in C.  It is so far the only completely implementation of a pool server. It's available from Oblong as part of the g-speak SDK. 

This Jamsession pool server is restricted in two ways:

1. Normal pools are backed by a file on disk; these are not.  They live in the memory of the server process only.  Thus they are not persistent from session to session; hence the name Jamsession.  After the Jamsession ends, the data is gone.

2. It has the same Plasma communication restrictions as Greenhouse.  See *Implementation*, below.

Jamsession can run as a standalone program, or it can be invoked as a library by other Java or JVM-based programs (see the MakeJam example which is included).


## Implementation

Jamsession is written in a small amount of Clojure (see src/jamsession/jam.clj).  The application is really only a thin wrapper around Oblong's pure-Java Plasma implementation **Jelly**.

A copy of Jelly, in a jarfile, is included in this repo.  However, this is a specially restricted version of Jelly suitable for broad public release on the same terms as the Greenhouse SDK.  The restriction is that it can't be used to communicate with regular g-speak pool-tcp-servers; only Greenhouse ones. 

Jamsession _can_ accept incoming communications from any Plasma client: Greenhouse or full g-speak. 

You can lift this restriction if you already have an unrestricted Jelly jar (or Jelly source) from Oblong.  See the instructions below for "Building Jamsession with a different Jelly jar".


## Documentation

Documentation for the code was generated with [Marginalia](https://github.com/fogus/marginalia).  Open docs/uberdoc.html in a browser.


## Building

This project is built with [leiningen](https://github.com/technomancy/leiningen) 2.0 or greater.  Once you have leiningen, just do:

    lein uberjar

This will fetch all dependencies, compile the libraries, and both the program and all its dependencies into one standalone artifact: the uberjar.  It's created in target/


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

Copy your Jelly jar into the top level directory and do the same.





