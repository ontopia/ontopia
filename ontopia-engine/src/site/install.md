Ontopia
=======

Installation and getting started
--------------------------------

<p class="introduction">
This is a guide to installing and getting started with using Ontopia. It is assumed that the reader
is familiar with the basics of the host platform and of Java development. See the *What's new in
this release?* document for information about this release.
</p>

<span class="version">5.4.0 2022-10-20</p>

### Requirements ###

The most important requirement is that you must have the Java Software Development Kit (SDK)
version 1.8 or newer. There should be no platform dependencies in the code, so the software should
run on all operating systems for which there is a supported JDK available.

We have verified that the Navigator Framework runs in Tomcat versions 7, 8 and 9. It is quite likely 
that the framework also works in other application servers than these, but this has not been verified.

The RDBMS backend has been verified to work with Oracle 8.1.7, 9i, and 10g, PostgreSQL 8.1, 8.2 and
8.3, MySQL 5.0, H2 Database 1.0.74 and Microsoft SQL Server 2005.

### What the release contains ###

Ontopia has the following components:

*  The Ontopia Topic Maps Engine
    *  Full-text Integration
    *  Query Engine
*  The Ontopia Navigator Framework
    *  TMRAP
*  The Ontopia Web Editor Framework
*  The Ontopia Vizigator
*  DB2TM
*  The Ontopia RDBMS Backend Connector, with SQL implementation of Query Engine
*  The Ontopia Omnigator
*  The Ontopoly Topic Maps Editor

### Installation ###

The first step in the installation is to uncompress the distribution file, which you have presumably
already done, since you are reading this document. The topmost directory created by uncompressing
the distribution, the file called `ontopia-X.X.X.zip` , will from now on be referred to as
`basedir`.

#### Setting the CLASSPATH ####

If you want to use the Ontopia APIs to write Java software, or just run the test suite, you will
have to set up the `CLASSPATH` environment variable. If you only want to run the Navigator Framework
this is not necessary, and you can skip this section.

To set up the `CLASSPATH`, put the `${basedir}/lib/ontopia-engine-x.y.x.jar` file in your
`CLASSPATH` (Change x.y.z to your Ontopia version). If this does not work, or you want more detailed
control over the jar files being used, please see [Details about CLASSPATH
setup](#details-about-classpath-setup).

> **Note**
> The jar-file automatically includes the other required jar-files for you via its manifest. In
> practice this means that you normally need not put the other jar-files on your `CLASSPATH`
> explicitly, but they all have to be located in the same directory for this to
> work.

> **Warning**
> *There may be some Java Virtual Machines out there that do not support the new CLASSPATH jar-file
> manifest header. If that is the case you'll have to add all the individual jar-files to your
> CLASSPATH.*

#### Verifying ####

Now that you've set up your `CLASSPATH` environment variable you can verify it by issuing the
following command:

````shell
java net.ontopia.Ontopia
````

It will run and produce the following output if it can find all the classes required:

````shell
Ontopia Topic Maps Engine [version]
Success: All required classes found.
````

If it fails you will get output similar to the following:

````shell
Ontopia Topic Maps Engine [version]
Class 'org.apache.log4j.BasicConfigurator' not found. Please add log4j.jar to your CLASSPATH.
````

The message is hopefully self-explanatory.



#### Testing ####

Since Ontopia 5.2.0, testing is automatically done during creation of the distribution. It is no
longer needed to run the test suite yourself. If you are experiencing problems, please report an
issue at [Ontopia @ Github](https://github.com/ontopia/ontopia/issues)
including:

*  a brief description of what you have done,
*  the output from the test run,
*  your entire `CLASSPATH` when you ran the tests,
*  what platform you ran the test on,
*  the output from the command `java -version`, and
*  the output from the command `java net.ontopia.Ontopia`.

#### Installing the RDBMS Backend Connector ####

See the *The RDBMS Backend Connector - Installation Guide* document for information about how to
install and setup the relational database backend. Note that it's possible to run Ontopia and the
web applications without using the RDBMS backend.

### Getting started ###

Following is a short introduction to the end-user applications that come with Ontopia.

For more information about developing with Ontopia you can find a complete list of developer
documentation [here](../index.html).

#### The engine ####

The engine consists of an SDK and some command-line utilities, and so there is little that can be
done with it apart from developing applications on top of it.

To get started with application development we recommend that you start by reading the [developer's
guide](engine/devguide.html). Once you have read that we recommend that you use the javadocs in
`${basedir}/doc/api` to learn the details of the APIs.

You can find example topic maps in the
`${basedir}/apache-tomcat/webapps/omnigator/WEB-INF/topicmaps` directory, which contains topic maps
in the XTM 1.0, 2.0, CTM, and LTM formats.

#### The command-line utilities ####

The engine comes with a collection of command-line utilities, which can be used to perform various
tasks on topic maps. The command-line options of the utilities are not given here, but explanations
can be had by running the utilities with no arguments.

net.ontopia.topicmaps.cmdlineutils.Canonicalizer
:   This application reads in a topic map and creates a canonical representation of it using CXTM. CXTM
    has the property that logically equivalent topic maps will always have byte-by-byte identical
    canonical representations.
:   For more information on CXTM, see [the home page](http://www.isotopicmaps.org/cxtm/).

net.ontopia.topicmaps.cmdlineutils.TopicMapConverter
:   Used to convert to and from different topic map syntaxes. It can read XTM 1.0, ISO HyTM, and LTM 1.3
    syntax, and can export to XTM 1.0, HyTM, and LTM 1.3. It also supports importing from and exporting
    to RDF.

net.ontopia.topicmaps.cmdlineutils.Merger
:   This application reads in two topic maps, merges them according to XTM 1.0 rules, and outputs the
    result to a third file.

net.ontopia.topicmaps.cmdlineutils.StatisticsPrinter
:   Prints various kinds of statistics for topic maps to standard output.

#### The Web Applications ####

Ontopia comes with a number of useful web applications built with Ontopia already set up and
configured in a Tomcat installation. The main applications are:

*  **Ontopoly: The Ontopia Topic Map Editor**, the user's guide to which can be found
   in`${basedir}/apache-tomcat/webapps/ontopoly/doc/user-guide.html`
*  **Ontopia Omnigator**, the user's guide to which can be found
   in`${basedir}/apache-tomcat/webapps/omnigator/docs/navigator/userguide.html`

Try out these web applications by following these steps:

##### Start the application server #####

Windows
:   Locate the `${basedir}/apache-tomcat/bin` directory in Windows Explorer.
:   Double-click on the `startup.bat` file.

Unix
:   Change directory to `${basedir}/apache-tomcat`.
:   Execute the `bin/startup.sh` script.

After starting the application server it will take a few seconds before the server is initialized
and ready to answer requests.

If the application server fails to start correctly you may have to set the `JAVA_HOME` environment
variable to point to the directory where the Java Runtime Environment is installed. This can be done
either at the command-line before running the tomcat startup script, or by editing the script to
insert a line setting it. See [Setting JAVA_HOME](#setting-java_home) for
details.

##### Try it out #####

To try out Ontopia you can use any of the applications listed below. The first time you're accessing
a page it may take some time since the web application server needs to compile the JavaServer Pages.
If you are able to see the front page of the application then the installation is successful.
Congratulations!

> **Warning**
> Internet Explorer Users: Please note that IE 5.0 may not be able to connect to the application
> server if you are working 'Offline'.

[Overview Page](http://localhost:8080/)
:    This link takes you to an Ontopia Overview Page, which can be very useful for getting started.

[The Omnigator](http://localhost:8080/omnigator/)
:    The Omnigator is a Navigator application which can display any topic map in a user-friendly fashion.
     Use this application to learn more about topic maps, to explore the example topic maps, and also to
     check your own topic maps. See *[The Ontopia Omnigator - User's Guide](navigator/userguide.html)*
     for information on how to use this application.

[Ontopoly](http://localhost:8080/ontopoly/)
:    Ontopoly is a user-friendly Topic Maps editor, which can be used to create and populate an
     ontology.

[Scripts and languages](http://localhost:8080/i18n/)
:    This is a web application written specifically for the i18n.ltm topic map, to show how the Navigator
     Framework can be used to create ontology-specific web applications. This application is very useful
     for learning to use the tag libraries.

[Free XML Tools](http://localhost:8080/xmltools/)
:    This is the beginnings of a web application for the 'Free XML Tools' topic map. It is used as an
     example in the navigator developer's guide. The application is not complete, but you may find it
     instructive to complete it on your own.


##### Stop the application server #####

Windows
:   Close the server window(s) or run `shutdown.bat`.

Unix
:   Change directory to `${basedir}/apache-tomcat`.
:   Execute the `bin/shutdown.sh` script.

### Frequently asked questions ###

This section contains answers to frequently asked questions about Ontopia.

Why do I get Not Found (404) when accessing the Omnigator?
:   If you get an error message saying `Not Found (404)` followed by `Original request:
    /navigator/models/index.jsp` and `Not found request: /navigator/views/template_no_frames.jsp` when
    accessing the Omnigator you have used a decompression tool that truncates file names when unpacking
    files.
:   We have confirmed that WinZip version 6.3 and earlier have this problem, as does WinRar 2.90. WinAce
    may also have this problem. We have confirmed that WinZip 8.0 and later do not have this
    problem.
:   The solution to the problem is simply to install a newer version of the decompression tool and
    unpacking the distribution again.

(Windows) When I start the server the window appears for a moment, then disappears, so I can't even
read the error messages.
:   There are two things you can do. If you've created an icon to start the server you can go into the
    properties of the icon, under the "Program" tab, and unset the "Close on exit" option. This will
    make the window stay around after the server exits, and give you time to read any error messages
    that may appear. (If you're just starting it by double-clicking `startup.bat`, then either make an
    icon and set it up as described above, or try the option below.)
:   The other approach is to change the `startup.bat` so that the line near that end that now reads:
:   is changed to:
:   This will keep Tomcat from opening a new window, so if you run `startup.bat` from the command-line
    you should get the error messages there.
:   The server log is in `${basedir}/apache-tomcat/logs/localhost_log.<date>.txt`, and it is likely to
    contain much detail information on errors. You can try to decipher this file to find the cause of
    the problem, or if that is difficult, you can just send the file to us at
    [support@ontopia.net](mailto:support@ontopia.net), and we will try to help. Note that if the file
    doesn't exist it means the server has been unable to start and you should try one of the two options
    above to find out why.

Installing the Omnigator in another Tomcat instance
:   To install the Omnigator in another Tomcat instance than the one which comes with Ontopia, follow
    these steps:
    1.  Copy all the .jar files, except `ontopia-tests.jar` and `junit.jar`, from `${basedir}/lib` to
       `${tomcat.home}/common/lib`.
    2.  Copy `${basedir}/apache-tomcat/webapps/omnigator` to the other Tomcat installation.

### Appendix 1: Details about CLASSPATH setup ###

#### Required jar-files ####

Ontopia has the following jar-file dependencies. The files can be found in the `${basedir}/lib`
directory.

*  Topic Map Engine:
    1.  `crimson.jar`, version 1.1.3, ([Apache XML - Crimson](http://xml.apache.org/crimson/))
    2.  `getopt.jar`, version 1.0.9, ([GNU Java
       getopt](http://www.urbanophile.com/arenn/hacking/download.html))
    3.  `slf4j-api.jar`, `slf4j-log4j12.jar`, `jcl-over-slf4j.jar` version 1.5.8, ([Simple Logging Facade
       for Java (SLF4J)](http://www.slf4j.org/))
    4.  `log4j.jar`, version 1.2.14, ([Apache log4J](http://logging.apache.org/log4j/1.2/index.html))
    5.  `antlr.jar`, version 2.7.7, ([ANTLR parser generator](http://www.antlr.org))
    6.  `commons-collections.jar`, version 3.2.1, ([Apache Commons
       Collections](http://commons.apache.org/collections/))
    7.  `oro.jar`, version 2.0.8, ([Apache Jakarta ORO](http://jakarta.apache.org/oro/))
    8.  `jing.jar`, version 2003-01-31, ([Jing - A RELAX NG validator in
       Java](http://www.thaiopensource.com/relaxng/jing.html))
    9.  `xerces.jar`, version 2.7.1, ([Apache Xerces2 Java
       Parser](http://xml.apache.org/xerces2-j/index.html))
    10.  `jena.jar`, version 2.3, ([Jena - A Semantic Web Framework for Java](http://jena.sourceforge.net/))
    11.  `icu4j.jar`, version 3.4.4, ([International Components for Unicode for
       Java](http://www.ibm.com/developerworks/oss/icu4j/))
    12.  `concurrent.jar`, version 1.3.4,
       ([util.concurrent](http://gee.cs.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/intro.html))
    13.  `trove.jar`, version 2.0.4, ([GNU Trove](http://trove4j.sourceforge.net/))
    14.  `tmapi.jar`, version 2.0 a2, ([TMAPI - Common Topic Maps Application Programming
       Interface](http://www.tmapi.org/2.0/))

*  Vizigator:
    1.  `touchgraph.jar`, built from source with patches,
       ([TouchGraph](http://touchgraph.sourceforge.net/))

*  Web Editor Framework:
    1.  `velocity.jar`, version 1.3, ([Apache Velocity](http://velocity.apache.org/))
    2.  `commons-fileupload.jar`, version 1.3, ([Apache Commons
       FileUpload](http://commons.apache.org/fileupload/))

*  Full-text Integration:
    1.  `lucene.jar`, version 1.4.3, ([Apache Lucene](http://lucene.apache.org/java/docs/))

*  DB2TM:
    1.  `opencsv.jar`, version 1.8, ([opencsv](http://opencsv.sourceforge.net/))

*  TMRAP (AXIS+SOAP support):
    1.  Ontopia uses AXIS2 version 1.2, ([Apache Web Services - Axis](http://ws.apache.org/axis/)), to
       provide support for SOAP. The jar files used can be found in
       `${basedir}/apache-tomcat/webapps/tmrap/WEB-INF/lib`.

*  RDBMS Backend Connector:
    1.  `commons-pool.jar`, version 1.3, ([Apache Commons Pool](http://commons.apache.org/pool/))
    2.  `commons-dbcp.jar`, version 1.2.2, ([Apache Commons DBCP](http://commons.apache.org/dbcp/))
    3.  `jgroups-all.jar`, version 2.6.10 GA, ([JGroups - A Toolkit for Reliable Multicast
       Communication](http://www.jgroups.org/))

The Query Engine requires no jar files beyond the ones needed for the Topic Map
Engine.

### Appendix 2: How to set Java properties ###

Java system properties are set by using a command-line argument to the `java` command when it is
run. The command `java -D<prop>=<value> ...` will set Java system property `<prop>` to
`<value>`.

Tomcat is usually started using the Tomcat startup scripts, and so one needs to modify these in
order to be able to pass command-line arguments to the Java runtime. Below is explained how to do
this on Windows and Unix:

Windows
:   Edit the `${basedir}/apache-tomcat/bin/catalina.bat` file and insert the line below near the top of
    it.
:   `set CATALINA_OPTS="-D<prop>=<value>"`

Unix
:   Edit the `${basedir}/apache-tomcat/bin/catalina.sh` file and insert the line below near the top of
    it.
:   `CATALINA_OPTS="-D=<prop>=<value>"`

> **Warning**
> *Note that by default there should be no need to specify any system properties to get Ontopia
> working.*

### Appendix 3: Setting JAVA_HOME ###

This section explains how to set the `JAVA_HOME` environment variable on Windows.

If you start tomcat from the command-line you can give the command `set JAVA_HOME=c:\path\to\java`
before starting the application server. Please replace `c:\path\to\java` with the path to your Java
installation (for example `c:\jdk1.5.0_13`), make sure that you have not accidentally put the `bin`
sub directory into the `JAVA_HOME` environment variable.

On Windows NT, 2000, and XP you can also go into the control panel, select 'System', then click the
'Advanced' tab, then click 'Environment variables'. This gives you a window where you can enter a
new environment variable named 'JAVA_HOME', the value of which must be the directory where you
installed Java.

On Windows 95/98/ME you can also edit the `AUTOEXEC.BAT` file in the root directory of your startup
drive (usually `C:`) and insert a line like the following into it: `set JAVA_HOME=c:\path\to\java`.
You will then need to reboot your computer before the change takes effect.


