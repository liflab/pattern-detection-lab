A study of pattern detection strategies using runtime verification
==================================================================

- Author: Sylvain Hallé
- Veresion: 1.1
- 2023-01-18

Integrating security in the development and operation of information systems is
the cornerstone of SecDevOps. From an operational perspective, one of the key
activities for achieving such an integration is the detection of incidents (such
as intrusions), especially in an automated manner. However, one of the stumbling
blocks of an automated approach to intrusion detection is the management of the
large volume of information typically produced by this type of solution.
Existing works on the topic have concentrated on the reduction of volume by
increasing the precision of the detection approach, thus lowering the rate of
false alarms. However, another, less explored possibility is to reduce the
volume of evidence gathered for each alarm raised.

This lab explores the concept of intrusion detection from the angle of complex
event processing. It provides a formalization of the notion of pattern matching
in a sequence of events produced by an arbitrary system, by framing the task as
a runtime monitoring problem. It then focuses on the topic of incident
reporting, and proposes a technique to automatically extract relevant elements
of a stream that explain the occurrence of an intrusion.

The current repository contains a benchmark that compares different detection
strategies by running them on synthetically-generated event streams for various
incident patterns. Among the elements that are measured, the number of events
reported for each pattern match and the total computation time are considered.
This data is compiled and reported in various tables and plots.

The lab produces data that is reported in the following book chapter:

- S. Hallé. (2023). A Stream-Based Approach to Intrusion Detection.
  *Notes on Software Engineering Methods and Security in a DevOps Environment*,
  Springer Lecture Notes on Computer Science, to appear in June 2023.

Instructions on using this repository
-------------------------------------

This repository contains an instance of LabPal, an environment for running
experiments on a computer and collecting their results in a user-friendly way.
The author of this archive has set up a set of experiments, which typically
involve running scripts on input data, processing their results and displaying
them in tables and plots. LabPal is a library that wraps around these
experiments and displays them in an easy-to-use web interface. The principle
behind LabPal is that all the necessary code, libraries and input data should be
bundled within a single self-contained JAR file, such that anyone can download
and *easily* reproduce someone else's experiments. Detailed instructions can be
found on the LabPal website, [https://liflab.github.io/labpal]

Building the benchmark
----------------------

First make sure you have the following installed:

- The Java Development Kit (JDK) to compile. The lab is developed to comply
  with Java version 11; it is probably safe to use any later version.
- [Ant](http://ant.apache.org) to automate the compilation and build process

### Dependencies

In order to run, the lab requires the following Java libraries, all of which are
open source and publicly available:

- Version 0.10.8 of the [BeepBeep](https://github.com/liflab/beepbeep-3)
  event stream processing engine
- The *LTL* and *Provenance*
  [palettes](https://github.com/liflab/beepbeep-3-palettes) of the January 2023
  pre-compiled bundle (i.e. `ltl.jar` and `provenance.jar`)
- Version 2.99-beta 1 of the [LabPal](https://github.com/liflab/labpal)
  experimental environment

You can use the Ant script to automatically download any libraries missing from
your system by typing:

    ant download-deps

This will put the missing JAR files in the `Source/dep` folder in the project's
root.

### Compiling

Once these steps have been taken care of, compile the sources by simply typing:

    ant

This will produce a file called `pattern-detection-lab.jar` in the folder.

Running LabPal
--------------

If you want to see any plots associated to the experiments, you need to have
[GnuPlot](http://gnuplot.info) installed and available from the command line
by typing `gnuplot`.

To start the lab and use its web interface, type at the command line:

    java -jar pattern-detection-lab.jar

You should see something like this:

    LabPal 2.99 - A versatile environment for running experiments
    (C) 2014-2022 Laboratoire d'informatique formelle
    Université du Québec à Chicoutimi, Canada
    Please visit http://localhost:21212/index to run this lab
    Hit Ctrl+C in this window to stop

Open your web browser, and type `http://localhost:21212/index` in the address
bar. This should lead you to the main page of LabPal's web control panel.
(Note that the machine running LabPal does not need to have a web browser.
You can open a browser in another machine, and replace `localhost` by the IP
address of the former.)

Using the web interface
-----------------------

The main page should give you more details about the actual experiments that
this lab contains. Here is how you typically use the LabPal web interface.

1. Go to the Experiments page.
2. Select some experiments in the list by clicking on the corresponding
   checkbox.
3. Click on the "Add to assistant" button to queue these experiments
4. Go to the Assistant page
5. Click on the "Start" button. This will launch the execution of each
   experiment one after the other.
6. At any point, you can look at the results of the experiments that have run so
   far. You can do so by:
   - Going to the Plots or the Tables page and see the plots and tables created
     for this lab being updated in real time
   - Going back to the list of experiments, clicking on one of them and get the
     detailed description and data points that this experiment has generated
7. Once the assistant is done, you can export any of the plots and tables to a
   file, or the raw data points by using the Export button in the Status page.

Please refer to the [LabPal website](https://liflab.github.io/labpal)
or to the Help page within the web interface for more information about
LabPal's functionalities.

Disclaimer
----------

The LabPal *library* was written by Sylvain Hallé, Professor at Université du
Québec à Chicoutimi, Canada. However, the *experiments* contained in this
specific lab instance and the results they produce are the sole responsibility
of their author.

<!-- :maxLineLen=80: -->