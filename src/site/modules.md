Ontopia Modules
===============

<p class="introduction">
This page provides an overview of the Maven modules present in the Ontopia code base.
</p>

### Maven ###

The Ontopia project is structed as a [Maven](https://maven.apache.org/) project. It is split into
functional blocks, represented by maven sub modules, as listed below.

#### Usage ####

You can use one of the modules as a dependency in your project by including the following snippet in 
your POM:

````xml
<dependency>
	<groupId>net.ontopia</groupId>
	<artifactId> <!-- one of the modules --> </artifactId>
	<version>5.3.0</version>
</dependency>
````

### Modules ###

`ontopia-classify`
:    Contains the [automated classification](classify/dev-guide.html) tools.

`ontopia-contentstore`
:    Contains an implementation of an binary content store connected to Ontopia.

`ontopia-db2tm`
:    Contains the [Database to Topic Maps](db2tm/user-guide.html) tools.

`ontopia-deprecated-utils`
:    Contains deprecated code until it is removed in a future release, **Deprecated**

`ontopia-distribution-tomcat`
:    Contains the resource and build logic to create the distribution package.

`ontopia-engine`
:    The core engine of Ontopia. This is the smallest package required to work with Ontopia as a
     dependency.

`ontopia-jdbcspy7`
:    Contains the JDBC Spy Driver that allows debugging of Ontopia RDBMS queries for Java 7.

`ontopia-lucene`
:    Contains the full text implementation based on Apache Lucene.

`ontopia-navigator`
:    Contains Navigator Framework code that allows Topic Map JSP tags to be used.

`ontopia-rdf`
:    Contains code that allows Ontopia to read and write RDF.

`ontopia-realm`
:    Contains required classes for using Ontopia as authenticator in a JAAS realm.

`ontopia-rest`
:    Contains the Ontopia REST API implementation.

`ontopia-tmprefs`
:    Contains code that allows a Topic Map to serve as Java Preferences API store.

`ontopia-tmrap`
:    Contains the core functionality of the [TMRAP protocol](tmrap/dev-guide.html).

`ontopia-tmrap-aar`
:    The TMRAP code packaged as an Apache Axis archive (AAR).

`ontopia-vizigator`
:    Contains the core code of the [Vizigator](vizigator/userguide.html) and Vizlet.

`ontopoly-editor`
:    Contains the core code of the [Ontopoly Topic Maps editor](ontopoly/user-guide.html).

`webapp-accessctl`
:    A sample application that shows the [Userman ontology](navigator/userman.html) in action.

`webapp-i18n`
:    A sample application that shows the `i18n.ltm` Topic Map in a browsable form.

`webapp-manage`
:    The Topic Map sources management application.

`webapp-omnigator`
:    An application that lets you load and browse any topic map, including your own.

`webapp-ontopoly`
:    Ontopia’s self-configuring, ontology-driven Topic Maps editor.

`webapp-ontopoly-standalone`
:    Ontopoly wrapped in a self contained package.

`webapp-root`
:    The distribution start page.

`webapp-tmrap`
:    Exposes the TMRAP protocol as a webapplication.

`webapp-xmltools`
:    An example application used in the [navigator developer's guide](navigator/navguide.html).


