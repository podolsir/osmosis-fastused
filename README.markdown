osmosis-fastused
====================
Faster But More Complicated Used-Node and Used-Way Tasks
========================================================

Summary
-------

          osmosis --read-xml input.osm outPipe.0=W
                  --read-xml input.osm outPipe.0=N
                  --tag-filter accept-ways boundary=* inPipe.0=W outPipe.0=WF 
                  --fast-used-used-node inPipe.0=N inPipe.1=WF outPipe.1=WFN
                  --write-xml output.osm inPipe.0=WFN

I warned you it is complicated ;)

About
-----

This plugin provides a way to speed up the used-node and used-way tasks in some use cases, especially restrictive tag-based filtering.

Background
----------

Let's start with a basic pipeline commonly used in the tag-based filtering. For example, if we want to get all administrative boundaries in a particular OSM data set, we would write:

            osmosis --rb a.osm.pbf --tf accept-ways boundary=administrative --used-node --wb b.osm.pbf

or, more concise:

            RB-->TF(ways)-->UN-->WB

If a.osm.pbf is big enough (say, about 100 MB), you will soon find out this setup consumes lots of CPU, I/O and disk space and creates humongous temporary files. It gets even worse if you have a proper filter for multiple types of objects:

                  /-->TF(rels)-->UW-->UN-->SORT--\
            RB-->T                                MERGE-->WB
                  \-->TF(ways)-->UN------->SORT--/

In this case, you will discover that those huge temporary files will be created _thrice_ (just look into your /tmp directory).

The task creating those temporary files is mostly --used-node. It works like this:

1. Store all ways, nodes and relations coming in into a "simple object store".
2. During this, records all node references.
3. Replay the simple object store to the output, filtering out unneeded nodes.

The temporary files are that "simple object store". Basically, --used-node caches the incoming stream on disk and holds off until it has seen everything. This is the _safe_ way to accomplish the task --used-node was intended for, but it is also slow.

However, we can do better than that if we pose some restrictions on the pipeline structure and make it a bit more complicated.

The trick is to read the a.osm.pbf stream _twice_: in the first pass, ignore all the nodes and just filter the ways. Once you have the ways, read the input the second time, now ignoring everything but the nodes used by those ways. The --fast-used-node task tracks the nodes used by ways and relations, and then takes the nodes from a second input stream. Basically, it is a merge with an ID tracker. The pipeline looks like this:

            RB1-------------\
            RB2-->TF(ways)--->FUN-->WB
            
or, for ways and relations:

                           RB2--\              RB3--\
                  /-->TF(rels)-->FUW-->SORT--\       \
           RB1-->T                            MERGE--->FUN-->WB
                  \-->TF(ways)-------->SORT--/


Restrictions
------------

There are quite a few things to be aware of when using the tasks from this plugin:

First, the **output order** is: **ways/relations, then nodes**, so you may need a --sort afterwards.

You actually will need two **different** streams with the same input, not just two different threads like with --merge. This will **not** work:

                  /--BUF-----------------------\
            RB-->T                              FUN-->WB
                  \--BUF-->TF(ways)--->SORT----/

Again, to be very clear: **this pipeline will deadlock**, except for some very, very special cases.

Building and Installation
-------------------------

Building is quite straightforward. You will need a JDK for Java 6 and Ant 1.7+ installed. Also, for the first build you will need an internet connections so the build system can download the dependencies.

Once you have the dependencies, execute:

          git clone git://github.com/podolsir/osmosis-fastused.git
          cd osmosis-fastused
          ant dist

After the build completes there should be a jar file in the `dist` subdirectory: `osmosis-fastused-...jar`. 
Copy that jar files into your Osmosis classpath (for example, `$OSMOSIS_HOME/lib/default`).

Usage
-----

### --fast-used-node (--fun) ###

Takes a set of ways and relations from one input stream and a set of nodes from another input stream. All ways and relations are passed through to the output stream unmodified. A node is only passed through if it is used by a way and/or relation.

#### Pipes ####
_inPipe.0:_ Consumes an entity stream. Nodes are taken from this stream, everything else is ignored.
_inPipe.1:_ Consumes an entity stream. Everything but nodes is taken from this stream.
_outPipe.0:_ Produces an entity stream.

#### Parameters ####
_bufferCapacity_: __Optional, default value: 20.__ The size (in entities) to use for the input buffers.    
_idTrackerType_: __Optional, default value: IdList.__ The id tracker to use for id recording. See documentation for standard --bounding-box tasks for different types of trackers.


### --fast-used-way (--fuw) ###

Takes a set of relations from one input stream and a set of ways from another input stream. All relations are passed through to the output stream unmodified. A way is only passed through if it is referenced by a relation.

#### Pipes ####
_inPipe.0:_ Consumes an entity stream. Ways are taken from this stream, everything else is ignored.
_inPipe.1:_ Consumes an entity stream. Everything but ways is taken from this stream.
_outPipe.0:_ Produces an entity stream.

#### Parameters ####
_bufferCapacity_: __Optional, default value: 20.__ The size (in entities) to use for the input buffers.   
_idTrackerType_: __Optional, default value: IdList.__ The id tracker to use for id recording. See documentation for standard --bounding-box tasks for different types of trackers.

Licenses
--------

Everybody is granted an irrevocable and perpetual license to use osmosis-fastused for any purpose whatsoever.

DISCLAIMER:
By making osmosis-fastused publicly available, it is hoped that users will find the
software useful.  However:

* osmosis-fastused comes without any warranty, to the extent permitted by applicable
law.

* Unless required by applicable law, no liability will be accepted by
the authors and distributors of this software for any damages caused
as a result of its use. 
