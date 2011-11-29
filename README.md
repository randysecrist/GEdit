GEdit
=====

Overview
========

A simple directed and undirected graph editor written in Java Swing.  This provides the ability to create, edit, load and save a graph data structure.  In addition this tool provides a number of graph algorithms which can be executed visually within the UI.

Getting Started
===============

To run this app, the following steps need to happen:

1.  Get the GEdit source using git.
2.  Get either [ANT](http://ant.apache.org) or [MAVEN](http://maven.apache.org).
3.  Build the distribution.
4.  Run!

Getting the Source
==================
Clone the source tree using git:

    git clone https://github.com/randysecrist/GEdit.git

This is a Java app that provides both a ANT and a MAVEN build script.  There is not a significant difference between the two, but the ANT script is faster since it doesn't have to download maven related dependencies.

Build using ANT:

    ant build

Build MAVEN:

    mvn package

Run
===
The easiest way to run the app is to open a terminal and run the following from the source directory:

    java -jar gedit-1.0.0.jar

Documentation
=============

Refer to the [full documentation](https://github.com/randysecrist/GEdit/blob/master/src/docs/FinalUserDocumentation.pdf) located in the source tree.  It will tell you how to create / load a graph, and how to run algorithms.

Status
======

* Code is complete and has been updated to work with Java 1.6.

Features
========

* Provides a graphical display for directed and undirected graphs.
* Provides a graphical display for a number of traversal algorithms.
* Displays disconnected graph islands within a multi document display.
* Keeps the entire graph together as a single data file which can be loaded or saved at any time.
* Provides support for both weighted and unweighted graphs.
* Autobalances graph nodes using edge tension.
* Enables / Disables potiential algorithms as per graph type.
* Graph API provides support to nest pluggable data types within Nodes.

Feature and Issues
==================

[Feature requests and issues](https://github.com/randysecrist/GEdit/issues?sort=created&direction=desc&state=open) can be created by anyone.  Community feedback is welcome.
