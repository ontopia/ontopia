The RDBMS Backend Connector
===========================

A Developer's Guide
-------------------

<p class="introduction">
This document is a guide to using the Ontopia Topic Maps Engine with the RDBMS Backend Connector. It
assumes that the reader is already familiar with the Topic Maps Engine, and wishes to use it with a
relational database backend.
</p>

<span class="version">5.1 2010-06-09</p>

### Introduction ###

The RDBMS Backend Connector adds relational database persistence to the Ontopia Topic Maps Engine.
The persistence is transparent and users of the topic map interfaces don't have to take any
additional steps in order to persist topic maps.

Note that only topic map objects that originate from the
[net.ontopia.topicmaps.impl.rdbms](../api/net/ontopia/topicmaps/impl/rdbms/package-summary.html)
package can be persisted with this backend. This means that any such object must be created by the
TopicMapFactoryIF used by the store implementation in this package.

Before you can develop applications with the RDBMS Backend Connector make sure that you've completed
the installation procedure described in *[Ontopia RDBMS Backend Connector, Installation
Guide](install.html)*. See also the [Ontopia RDBMS Backend Connector, Javadoc
API](../api/net/ontopia/topicmaps/impl/rdbms/package-summary.html) for a complete overview of the
APIs.

### The RDBMS Backend Connector API ###

All public classes provided by the RDBMS Backend Connector can be found in the
[net.ontopia.topicmaps.impl.rdbms](../api/net/ontopia/topicmaps/impl/rdbms/package-summary.html)
package. Only a few classes in this package are public, and most of those classes are
implementations of interfaces in `net.ontopia.topicmaps.core` and
`net.ontopia.topicmaps.core.index`. This means that there is actually very little new code that have
to be understood in order to use it.

Topic maps can be accessed via the RDBMS Backend Connector using instances of the
`RDBMSTopicMapStore` class, but all access to the store should be done through the interface that it
implements: `TopicMapStoreIF`.

The RDBMS Backend Connector makes use of database transactions, so make sure that you commit or roll
back your changes. Call `TopicMapStoreIF.commit()` when you want to persist your changes and
`TopicMapStoreIF.abort()` if you want them to be rolled back.

There are two important features that need to be considered when accessing topic maps through the
RDBM Backend Connector. The first one is the ability to *share cached data* between store instances
referencing the same topic map. The second feature is *topic map store pooling*. Both of these
features can greatly improve performance in an application. The situation where both are useful is
when there needs to be multiple instances of stores referencing the same topic map. In other words
it is when there are multiple application users accessing the topic map concurrently. This is very
common in multi-user applications, so make sure that both store pooling and a shared cache is used
in your application.

In order to make use of these two features you must retrieve your topic map store through a topic
map reference (via a topic map source). The are currently three implementations of the
`TopicMapSourceIF` interface in the RDBMS Backend Connector: `RDBMSTopicMapSource`,
`RDBMSSingleTopicMapSource` and `RDBMSPatternSingleTopicMapSource`.

The best way to do this is to create the tm-sources.xml file and place it on the classpath. The
topic maps referenced through the sources given in this configuration will then be available through
the `TopicMaps.createStore(String topicmapId, boolean readOnly)` method. This approach is also less
intrusive and will make your application a lot easier to test as all topic map access is done
through the same access point.

> __Important__ If you get hold of your RDBMSTopicMapStore instances through either
> ImportExportUtils or create them yourself you will not be able to take advantage of a shared cache
> and store pooling. Use the TopicMapSourceIF implementations, or access the topic maps stores through
> the mechanisms in the Navigator Framework instead. 

> It is very important that you close the
> TopicMapStoreIF instance when you are done with it, i.e. call the TopicMapStoreIF.close() method.
> You must do this, because otherwise the system will run out of resources. This means that the if you
> have enabled store pooling the topic map stores will actually not reclaimed by the garbage
> collector, but rather returned to the topic map store pool, so that it can be reused the next time
> somebody asks for a topic map store.

The Navigator and Web Editor Frameworks always use store pooling because they are implemented on top
of a TopicMapRepository that is retrieved through the `TopicMaps` class in the
`net.ontopia.topicmaps.entry` package. The frameworks will also make use of a shared cache by
default, unless it has been disabled by a database property. They will retrieve a store for each
request and return it to the pool when the request is over. The Web Editor Framework will also make
sure that the transactions are committed or rolled back as appropriate.

The rest of this section is a walkthrough of the various public classes of the RDBMS Backend
Connector. The API is very simple, since it only contains single implementations of
`TopicMapStoreIF` and two implementations of the `TopicMapSourceIF` interface.

#### RDBMSTopicMapStore ####

This `TopicMapStoreIF` implementation is used to access existing topic maps and to create new topic
maps in a relational database. Note that you should only create instances of this class when you
only want a single store instance to the same topic map, and when you only have a single user (no
concurrent access).

It has four public constructors:

*  `RDBMSTopicMapStore()`, creates a new topic map object in the database. The database property
   filename is assumed specified in the `net.ontopia.topicmaps.impl.rdbms.PropertyFile` system
   property.
*  `RDBMSTopicMapStore(long topicmap_id)` retrieves an existing topic map with the specified object id.
   The database property filename is assumed specified in the
   `net.ontopia.topicmaps.impl.rdbms.PropertyFile` system property.
*  `RDBMSTopicMapStore(String propfile)`, creates a new topic map object in the database. The database
   configuration is read from the specified property file.
*  `RDBMSTopicMapStore(String propfile, long topicmap_id)` retrieves an existing topic map with the
   specified object id. The database configuration is read from the specified property
   file.

You can choose among a variety of constructors for the topic map store. The simplest is the default
constructor. This constructor requires that you have set the
`net.ontopia.topicmaps.impl.rdbms.PropertyFile` system property to reference the property file. See
the [installation guide](install.html) for a complete description of database properties. You can
also use a constructor that takes the filename of the property file. The property file will be
attempted loaded from the file system first. If not found there it will be loaded through the class
loader. If the access must be explicit then the property file name can be prefixed by 'file:' or
'classpath:'.

All source instances that do not specify a topicmap id will cause a *new* topic map object to be
created. Here is an example of how you can use the default constructor to create a new topic map in
your relational database (the database property file will be found through the system property given
above):

````text/x-java
  TopicMapStoreIF store = new RDBMSTopicMapStore();
````

After you've created the `TopicMapStoreIF` instance you access it via the interfaces in the same way
as you would do in the in-memory implementation. Remember to always close the store with
`TopicMapStoreIF.close()` when you are done with it.

If you want to access an *existing* topic map you must also specify the `topicmap_id` argument in
the constructor. The following example creates a store that allows you to access the topic map with
the id 1500:

````text/x-java
  TopicMapStoreIF store = new RDBMSTopicMapStore("propfile.xml", 1500);
````

#### RDBMSTopicMapSource ####

This `TopicMapSourceIF` implementation returns a collection of `TopicMapReferenceIF`s for all topic
maps stored in the database. If you access your topic map stores through the same instance of this
topic map source implementation you will always get the advantage of store pooling and a shared
cache (enabled by default).

Use the default constructor to create an instance of the topic map source. After creating an
instance you must specify some more bean properties:

*  `setId(String id)`, gives the topic map source an id. (optional)
*  `setTitle(String title)`, gives the topic map source a title. (optional)
*  `setPropertyFile(String filename)`, tells the topic map source which database property file it
   should use. If you don't specify this the source will use the system property instead. The property
   will be loaded either through the file system or through the classpath.
   (optional)
*  `setBaseAddress(String uri)`, specifies the base address of the topic map. You would normally want
   to set this property so that tolog can resolve it's relative locators against it.
   (optional)
*  `setSupportsCreate(boolean supportsCreate)`, specifies whether the topic map source should allow
   creating new topic maps. Default is false. (optional)
*  `setSupportsDelete(boolean supportsDelete)`, specifies whether the topic map source should allow
   deleting topic maps. Default is false. (optional)

The topic map id for each topic map will be the id of the topic maps source plus '-' and the numeric
identifier of the topic map. If the source id is not given the topic map id will be 'RDBMS-' plus
the numeric identifier of the topic map.

Most applications should set up the topic map source in tm-sources.xml:

````xml
<?xml version="1.0"?>
<repository>
  <source class="net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapSource">
    <param name="propertyFile" value="grove.postgresql.props"/>
    <param name="id" value="postgresql"/>
    <param name="title" value="PostgreSQL database"/>
    <param name="supportsCreate" value="true"/>
    <param name="supportsDelete" value="true"/>
  </source>
</repository>
````

The topic map can then be accessed like this::

````text/x-java
// create read-only transaction
TopicMapStoreIF store = TopicMaps.createStore("postgresql-1", true);
try {
  ...
} finally {
  store.close();
}
````

Following is an example of how you would you would use the `RDBMSTopicMapSource` in your application
programmatically. This approach has several disadvantages which are described
above.

````text/x-java
  // create an instance
  RDBMSTopicMapSource source = new RDBMSTopicMapSource();
  // give the source an id
  source.setId("mydb");
  // give the source a title
  source.setTitle("My RDBMS Topic Maps");
  // specify the rdbms propertyfile to use
  source.setPropertyFile("db.postgresql.props");

  // get hold of the underlying topic map reference
  Collection refs = source.getReferences();
  Iterator iter = refs.iterator();

  while (iter.hasNext()) {
    TopicMapReferenceIF ref = (TopicMapReferenceIF)iter.next();
    System.out.println("Found topic map reference: " + ref.getId());

    // ...
  }
````

#### RDBMSSingleTopicMapSource ####

This `TopicMapSourceIF` implementation is very similar to `RDBMSTopicMapSource`, but lets you refer
to a *single* topic map in the database. If you access your topic map stores through the same
instance of this topic map source implementation you will always get the advantage of store pooling
and a shared cache (enabled by default).

Use the default constructor to create an instance of the topic map source. After creating an
instance you must specify some more bean properties:

*  `setTopicMapId(long topicmap_id)`, this is the id of the topic map we want to access.
*  `setId(String id)`, gives the topic map source an id. (optional)
*  `setTitle(String title)`, gives the topic map source a title. The persistent title of the topic map
   will be used if no title given here. (optional)
*  `setReferenceId(String refid)`, gives the topic map source an id to use for its single topic map
   reference. (optional)
*  `setPropertyFile(String filename)`, tells the topic map source which database property file it
   should use. If you don't specify this the source will use the system property instead.
   (optional)
*  `setBaseAddress(String uri)`, specifies the base address of the topic map. You would normally want
   to set this property so that tolog can resolve it's relative locators against it.
   (optional)

Note that this topic map source implemenation will always return a single `TopicMapReferenceIF` from
`getReferences()`.

The topic map id for each topic map will be the referenceIdid as given, otherwise 'RDBMS-' plus the
numeric identifier of the topic map.

Most applications should set up the topic map source in tm-sources.xml:

````xml
<?xml version="1.0"?>
<repository>
  <source class="net.ontopia.topicmaps.impl.rdbms.RDBMSSingleTopicMapSource">
    <param name="propertyFile" value="db.postgresql.props"/>
    <param name="topicMapId" value="M1"/>
    <param name="referenceId" value="mytopicmap"/>
    <param name="title" value="My Topic Map"/>
  </source>
</repository>
````

The topic map can then be accessed like this::

````text/x-java
// create read-write transaction
TopicMapStoreIF store = TopicMaps.createStore("mytopicmap", false);
try {
  ...
  store.commit();
} catch (Exception e) {
  store.abort();
} finally {
  store.close();
}
````

Following is an example of how you would you would use the `RDBMSSingleTopicMapSource` in your
application. This approach has several disadvantages which are described above.

````text/x-java
  // create an instance
  RDBMSSingleTopicMapSource source = new RDBMSSingleTopicMapSource();
  // give the source an id
  source.setId("mytm");
  // give the source a title
  source.setTitle("My RDBMS Topic Map");
  // specify the rdbms propertyfile to use
  source.setPropertyFile("db.postgresql.props");
  // specify the base address, so tolog can resolve it's locators relative to it
  source.setBaseAddress("file:/tmp/mytopicmap.ltm")
  // specify the id of the topic map
  source.setTopicMapId(5001);

  // get hold of the underlying topic map reference
  TopicMapReferenceIF ref = (TopicMapStoreIF)source.getReferences().iterator().next();

  TopicMapStoreIF store = null;
  try {
    // tell the reference to create a new store instance
    store = ref.createStore(false);

    //  ...

  } finally {
    // close the store since we're done
    if (store != null && store.isOpen()) store.close();
  }
````

#### RDBMSPatternSingleTopicMapSource ####

This `TopicMapSourceIF` implementation is very similar to `RDBMSSingleTopicMapSource`, but lets you
refer to a *single* topic map in the database via its title (TM_TOPIC_MAP.title) or comment
(TM_TOPIC_MAP.comments) instead of its physical object id. If multiple topic maps have the same
value then the newest topic map will be used. This feature can be used to swap to newer topic maps
at runtime by refreshing the topic map repository.

The title or comment of a topic map can be specified via the --title and --comments options on the
`RDBMSImport` command line tool.

If you access your topic map stores through the same instance of this topic map source
implementation you will always get the advantage of store pooling and a shared cache (enabled by
default).

Use the default constructor to create an instance of the topic map source. After creating an
instance you must specify some more bean properties:

*  `setId(String id)`, gives the topic map source an id. (optional)
*  `setTitle(String title)`, gives the topic map source a title. The persistent title of the topic map
   will be used if no title given here. (optional)
*  `setReferenceId(String refid)`, gives the topic map source an id to use for its single topic map
   reference. (required)
*  `setPropertyFile(String filename)`, tells the topic map source which database property file it
   should use. If you don't specify this the source will use the system property instead.
   (optional)
*  `setBaseAddress(String uri)`, specifies the base address of the topic map. You would normally want
   to set this property so that tolog can resolve it's relative locators against it.
   (optional)
*  `setPattern(String pattern)`, specifies the title or comment value used to look up the topic map.
   (required)
*  `setMatch(String match)`, specifies where to apply the pattern. Allowed values are `title` or
   `comment`. The default value is `title`. (optional)

Note that this topic map source implemenation will always return a single `TopicMapReferenceIF` from
`getReferences()`.

Here is an example of how tm-sources.xml can be set up using this topic map source:

````xml
<?xml version="1.0"?>
<repository>
  <source class="net.ontopia.topicmaps.impl.rdbms.RDBMSPatternSingleTopicMapSource">
    <param name="propertyFile" value="db.postgresql.props"/>
    <param name="referenceId" value="mytopicmap"/>
    <param name="match" value="title"/>
    <param name="pattern" value="My Topic Map"/>
  </source>
</repository>
````

#### XMLConfigSource ####

Both of the preceeding `TopicMapSourceIF` implementations require quite a bit of code to work. An
alternative method is to use the `XMLConfigSource` class. This class has a convenient static utility
method, `getRepository(String config_file)`that reads a `tm-sources.xml` file and creates a
`TopicMapRepositoryIF` object. Through this object instance you that you can access and manage the
topic map references.

````text/x-java
  // read tm-sources.xml and create a topic map repository
  TopicMapRepositoryIF rep = XMLConfigSource.getRepository("/tmp/tm-sources.xml");

  // get hold of the topic map reference
  TopicMapReferenceIF ref = rep.getReferenceByKey("mytm");

  TopicMapStoreIF store = null;
  try {
    // tell the reference to create a new store instance
    store = ref.createStore(false);

    //  ...

  } finally {
    // close the store since we're done
    if (store != null && store.isOpen()) store.close();
  }

  // close the repository when we are done
  rep.close();
````

See [The Ontopia Navigator Framework Configuration Guide](../navigator/config.html#tm-sourcesxml)
for more information on how to set up the tm-sources.xml file.

### Locating RDBMS topic maps ###

The `net.ontopia.topicmaps.utils.ImportExportUtils` utility class has special support for locating
topic maps using the RDBMS Backend Connector. This is done by passing one of the getter methods in
the utility class the name of the property file and a `x-ontopia:tm-rdbms` URI referencing the topic
map.

The property file contains the backend connection properties and the URL the identifies the topic
map. Note that both these values are required to successfully locate the topic
map.

> **Warning**
> If you get hold of your RDBMSTopicMapStore instances through ImportExportUtils you will *not* be
> able to take advantage of a shared cache and store pooling. Use the `XMLConfigSource` or
> `TopicMapSourceIF`s instead.

#### RDBMS URI syntax ####

The URIs for referencing topic maps managed by the RDBMS Backend Connector uses the following
syntax:

````
x-ontopia:tm-rdbms:<topicmap-id>
````

where `<topicmap-id>` is the numeric identity of the topic map or the topic map object's object id.
The default base address of topic maps are always of this form.

### Command line utilities ###

#### Importing ####

````
java net.ontopia.topicmaps.cmdlineutils.rdbms.RDBMSImport [options] <dbprops> <tmfile1> [<tmfile2>] ...

  Imports topic map files into a topic map in a database.

  Options:
    --logargs=<propfile>  : log4j properties config file
    --loglevel=[DEBUG|INFO|WARN|ERROR|FATAL|NONE]  : the log level to use (verbosity threshold)
    --tmid=<topic map id> : existing TM to import into (creates new TM by default)
    --title=<topic map title> : persistent name of topic map
    --comments=<topic map comments> : persistent comments about topic map
    --validate=true|false : if true topic map document will be validated (default: true)
    --suppress=true|false: suppress duplicate characteristics (default: false)
    --loadExternal=true|false : if true external topic references will be resolved (default: true)
    --jdbcspy=<filename> : write jdbcspy report to the given file

  <dbprops>:   the database configuration file
  <tmfile#>:   the topic map files to import
````

#### Exporting ####

````
java net.ontopia.topicmaps.cmdlineutils.rdbms.RDBMSExport [options] <dbprops> <tmid> <expfile>

  Exports topic maps from RDBMS to file.

  Options:
    --logargs=<propfile>  : log4j properties config file
    --loglevel=[DEBUG|INFO|WARN|ERROR|FATAL|NONE]  : the log level to use (verbosity threshold)

  <dbprops>:   the database configuration file
  <tmid>:      the topic map id
  <expfile>:   the filename of the exported file
````

#### Deleting ####

````
java net.ontopia.topicmaps.cmdlineutils.rdbms.RDBMSDelete [options] <dbprops> <tmid>

  Deletes a topic map from a database.

  Options:
    --logargs=<propfile>  : log4j properties config file
    --loglevel=[DEBUG|INFO|WARN|ERROR|FATAL|NONE]  : the log level to use (verbosity threshold)

  <dbprops>:   the database configuration file
  <tmid>:      the id of the topic map to delete
````

### Sample applications ###

The next few sections contains some sample applications written with the RDBMS Backend Connector.

#### Importing a topic map ####

Source code for this example can be found in: [RdbmsImport.java](RdbmsImport.java)

This application imports an XTM topic map document into the database. You can run the application by
issuing the command, where <propfile> is the database property file and <xtmfile> is the XTM
document to be imported:

````
java RdbmsImport <propfile> <xtmfile>
````

Result:

````
Connecting...
Imported (id M1).
Done.
````

#### Listing the topic maps in a database ####

Source code for this example can be found in: [RdbmsList.java](RdbmsList.java)

This application lists all the topic maps that are stored in the database referenced by the
properties file. The list includes the topic map object ids and the number of topics and
associations in each.

You can run the application by issuing the command, where <propfile> is the database property file:

````
java RdbmsList <propfile>
````

Result:

````
Connecting...
Topic map ID: M602
  Topics: 750
  Associations: 1250
Topic map ID: M5102
  Topics: 2049
  Associations: 8891
Topic map ID: M9402
  Topics: 40
  Associations: 35
Done.
````

#### Exporting a topic map ####

Source code for this example can be found in: [RdbmsExport.java](RdbmsExport.java)

This application exports a topic map stored in the database as an XTM document. You can run the
application by issuing the command, where <propfile> is the database property file and <topicmap-id>
is the database id of the topic map (without the 'M' prefix):

````
java RdbmsExport <propfile> <topicmap-id>
````

Result:

````xml
Connecting...
<?xml version="1.0" encoding="utf-8" standalone="yes"?>
<topicMap xmlns="http://www.topicmaps.org/xtm/1.0/" xmlns:xlink="http://www.w3.org/1999/xlink">
...
</topicMap>
Done.
````

Note that the output is printed to stdout, so you can redirect the output to a file.

### Performance tuning ###

Ontopia comes with a SQL profiler called jdbcspy, which can be used to check the performance of the
SQL queries sent to the database by the database backend. To enable jdbcspy, please add "jdbcspy:"
at the front of your JDBC URL in the `rdbms.properties` file. That is, if the setting
was:

````properties
net.ontopia.topicmaps.impl.rdbms.ConnectionString=jdbc:mysql://localhost/topicmaps  
````

then change it to:

````properties
net.ontopia.topicmaps.impl.rdbms.ConnectionString=jdbcspy:jdbc:mysql://localhost/topicmaps  
````

This is sufficient to make jdbcspy log all SQL queries. To get a report, go to the "Manage" page in
the Ontopia web interface, and click on the jdbcspy report button on the right-hand side. This will
show a report of all SQL queries sent, which can then be used to analyze performance
problems.


