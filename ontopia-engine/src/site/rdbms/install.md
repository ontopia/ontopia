The RDBMS Backend Connector
===========================

Installation Guide
------------------

<p class="introduction">
This document is a guide to installing and configuring the Ontopia Topic Maps Engine RDBMS Backend
Connector. It assumes that the reader is familiar with relational databases and has some knowledge
of how to configure them.
</p>

<span class="version">5.1 2011-04-10</p>

### Quickstart ###

> **Note**
> This is a short summary of the installation process for the impatient. If you want more detail,
> jump to [the introduction](#introduction) and read on from there.

In order to use the RDBMS backend you need:

*  A database with the Ontopia Topic Maps schema set up. SQL scripts to create the tables are in
   `rdbms/setup`.
*  A JDBC driver for your particular database. In the default installation this goes in
   `apache-tomcat/common/lib`.
*  A property file describing how to connect to the database. There are examples in `rdbms/config`. Put
   it in `apache-tomcat/common/classes`.
*  To make Ontopia look for topic maps in the database you must also add an RDBMS source to the
   `apache-tomcat/common/classes/tm-sources.xml` file. You'll find examples commented out at the bottom
   of the file.

That's it!

### Introduction ###

The RDBMS Backend Connector adds relational database persistence support to the Ontopia Topic Maps
Engine. The persistence is transparent and users of the topic map interfaces don't have to take any
additional steps in order to persist topic maps. Applications that uses the RDBMS Backend Connector
must properly demarcate transactions and follow the general rules in a transactional system. This
includes managing the life-cycle of topic map stores and transactions.

### Installation ###

Before you can install and start using the RDBMS Backend Connector you must have an RDBMS server
installed, and you must have created a database inside it. (The database can have any name since
Ontopia finds the database name from the JDBC URL.) You also need a JDBC 2.0 driver, and to have set
everything up so that you can connect to the database through the JDBC driver. If you want to use
batch writing or connection pooling you will also have to install the JDBC 2.0 optional package.
Once this is done, you are ready to start.

#### Requirements ####

A JDBC 2.0 driver for your database must be available on your CLASSPATH. Your database vendor should
be able to provide you with an appropriate JDBC driver.

If you want to use connection pooling you need the JDBC 2.0 Optional Package API (the javax.sql
packages) on your CLASSPATH. This API can be downloaded from [Sun
Microsystems](http://java.sun.com/products/jdbc/download.html).

#### Unicode support ####

Ontopia fully supports Unicode, but for the RDBMS Connector to support Unicode the database must
support storing Unicode text. Most databases do not enable this by default, and require special
options to be set when creating the database. This section documents these options for various
databases. Note that you need only do this if you actually need to store Unicode characters in your
database.

If you don't do this, the tests in the test suite that verify Unicode support will fail. This will
give failure reports like the ones shown below.

````
There were 4 failures:
1) testFile(net.ontopia.topicmaps.xml.test.AbstractCanonicalTests$CanonicalTestCase) "test file unicode.xtm canonicalized wrongly"
2) testFile(net.ontopia.topicmaps.xml.test.AbstractCanonicalTests$CanonicalTestCase) "test file unicode.iso canonicalized wrongly"
3) testExport(net.ontopia.topicmaps.xml.test.AbstractCanonicalExporterTests$CanonicalTestCase) "test file unicode.xtm canonicalized wrongly"
4) testExport(net.ontopia.topicmaps.xml.test.AbstractCanonicalExporterTests$CanonicalTestCase) "test file unicode.iso canonicalized wrongly"
````

##### PostgreSQL #####

To create a database which stores text in UTF-8, give the `-E UNICODE` option to `createdb`, as in
`createdb -E UNICODE topicmaps`. More information can be found on the
[PostgreSQL](http://www.postgresql.org/docs) website.

##### MySQL #####

To create a database which stores text in UTF-8 and uses case-insensitive collation, use the
following DDL command:

````sql
CREATE DATABASE topicmaps DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_bin;
````

##### Oracle #####

To modify a database to store text in UTF-8, execute the following commands. More information is
available on [Oracle
technet](http://download-west.oracle.com/docs/cd/B10501_01/server.920/a96529/ch10.htm#1009580).

````sql
STARTUP MOUNT;
ALTER SYSTEM ENABLE RESTRICTED SESSION;
ALTER SYSTEM SET JOB_QUEUE_PROCESSES=0;
ALTER DATABASE OPEN;
ALTER DATABASE CHARACTER SET UTF8;
SHUTDOWN NORMAL;
````

#### Creating the database schema ####

The next step is to create the topic map database schema in the database. This is done by executing
the SQL scripts provided in one of the `*.create.sql` files, found in the
`$ONTOPIA_HOME/rdbms/setup` directory. If there is no script for the database you are using, use the
`generic.create.sql` script.

There are also scripts for dropping the database schema in the same directory.

These scripts create the tables with primary keys and indexes in the database. They also initialize
the `TM_ADMIN_SEQUENCE` table by inserting a single row. The `TM_ADMIN_SEQUENCE` table contains the
sequence counters used to generate object identities.

In later versions we will provide a command-line tool which initializes the database for you
automatically. In the meantime, if you run into problems when initializing the database, please send
an email to [support@ontopia.net](mailto:support@ontopia.net).

The next step is to create a properties file that contains the information necessary to connect to
the database.

##### Microsoft SQL Server 2005 #####

Only SQL Server 2005 or newer versions are supported. Older versions may work, but there will be
contention in the database if used in a concurrent environment.

In order to enable the snapshot isolation and row versioning features (both needed by Ontopia) the
following two statements need to be executed in the database. Replace the `topicmaps` name with the
name of your database. Note that this will not work with the `master` database as the
`read_committed_snapshot` is not supported in that database. Use a custom database
instead.

````sql
alter database topicmaps set allow_snapshot_isolation on;
alter database topicmaps set read_committed_snapshot on;
````

More information about the database features can be found
[here](http://msdn2.microsoft.com/en-us/library/tcbchxcb%28VS.80%29.aspx).

#### Setting up the properties file ####

Before we can connect to the database we need to tell the backend how to connect to the database.
This is done by writing a Java properties file that provides the necessary information. Below are
listed the properties that need to be defined. You can put the properties file anywhere you like,
but it is recommended to place it together with other configuration files. For Ontopia to find this
file you have to point to it from the `tm-sources.xml` file. (See the Navigator Configuration guide
for details, but by default this file is located in
`apache-tomcat/common/classes`.)

The default properties file for use in applications can be designated by setting the
`net.ontopia.topicmaps.impl.rdbms.PropertyFile` system property to the property filename. Note that
the default can always be overridden through the APIs.

A collection of sample database properties files can be found in the `$ONTOPIA_HOME/rdbms/config`
directory.

##### Database connection #####

net.ontopia.topicmaps.impl.rdbms.Database
:   Type: token, Required: yes
:   This property specifies which database it is running against. The RDBMS Backend Connector uses this
    information to adjust the SQL it generates, and also to do optimizations.
:   Possible values are: `oracle8`, `oracle9i`, `oracle10g`, `postgresql`, `mysql`, `h2` `sqlserver` and
    `generic`. If you don't find your database in this list try using the `generic`
    setting.

net.ontopia.topicmaps.impl.rdbms.ConnectionString
:   Type: string, Required: yes
:   This is the JDBC connection string, which must be specified. The value will depend on which database
    you are using and how you have configured it.

net.ontopia.topicmaps.impl.rdbms.DriverClass
:   Type: string (class name), Required: yes
:   This is the fully qualified class name of the JDBC driver class to use. It must be set. Note that
    this must be a JDBC 2.0 driver.

net.ontopia.topicmaps.impl.rdbms.UserName
:   Type: string, Required: yes
:   This is the user name of the database user.

net.ontopia.topicmaps.impl.rdbms.Password
:   Type: string, Required: yes
:   This is the password of the database user.

net.ontopia.topicmaps.impl.rdbms.BatchUpdates
:   Type: boolean, Default: false
:   If set to `true` all database modifications will be performed through JDBC batch updates.
:   This option boosts performance with large transactions and in environments where network lag is an
    issue, since the number of network requests needed is significantly lower and the database may
    provide optimizations for performing lots of similar database modifications in
    batches.
:   Note that some JDBC drivers do not support batch updates. Please verify that your JDBC driver
    actually supports batch updates before enabling it.

##### Clustering #####

If multiple instances of the RDBMS Backend Connector are to access the same topic map(s) the
clustering feature *must* be enabled to prevent inconsistencies. As each instance holds an internal
data cache any modifications to the data needs to be replicated across all nodes in the cluster.
This must be done to prevent any of the nodes from holding stale data.

net.ontopia.topicmaps.impl.rdbms.Cluster.id
:   Type: string, Default: none
:   This property specifies the id of the cluster that the instance should be a member of. All instances
    that access the same topic map should be members of the same cluster. Specifying this property will
    make the instance connect to to the cluster on startup.
:   The cluster identifier must start with `jgroups:`, e.g. `jgroups:cluster:my-topicmaps`. JGroups is
    currently the only clustering implementation for cache invalidation.

net.ontopia.topicmaps.impl.rdbms.Cluster.properties
:   Type: string, Default: none
:   This property lets one pass in the name of a file containing configuration properties used by the
    clustering implementation. If the property is not specified the default properties will be used. The
    file will be first attempted loaded from the file system, then from the
    classpath.

Note that there is a critical bug in pre-1.6 JVMs related to IPv6 network support that prevents them
from being used with JGroups out of the box. To make JGroups use IPv4 make sure that you pass the
following system property to the JVM on startup:

````
-Djava.net.preferIPv4Stack=true
````

##### Caching #####

The RDBMS Backend Connector retrieves all its data from the backing relational database. The
communication with the database is relatively fast, but can introduce a big performance hit when the
network traffic is high. In order to offer high performance Ontopia provides a number of
client-caches that makes it possible to avoid repeated reads from the database.

net.ontopia.topicmaps.impl.rdbms.Cache.shared
:   Type: boolean, Default: true
:   Because the RDBMS Backend Connector supports concurrent transactions the database communication
    needs to be effective and non-redundant. Ontopia provides a cache implementation that transactions
    can share, so that redundant database access is avoided. This cache is enabled by default when using
    the Navigator and Web Editor Frameworks. Note that when using the Topic Maps Engine API one must
    access the topic map store through one of the topic map source implementations in the RDBMS Backend
    to make use of the shared cache. Warning: If one does not use the shared cache in a multi-user
    environment the performance hit can be very high.
:   The only situation where you should consider disabling this cache is when you have one long-running
    transaction. In this case it is best to disable the shared cache because it adds an overhead and
    can, if the transaction is big, also cause the transaction to run out of memory. Without a shared
    cache there is no limit on the size of transactions.
    
    > **Warning** Do not disable the shared cache when there are concurrent transactions updating the
    > same topic map as this would mean that the transactions might not see each other's committed
    > changes.

net.ontopia.topicmaps.impl.rdbms.Cache.identitymap.lru
:   Type: integer (# items in cache), Default: 300
:   Each transaction maintains a mapping between the object ids of topic map objects and the topic map
    objects themselves. This data structure holds soft references to the topic map objects and the
    objects can therefore be garbage collected when there are no other hard references to them. This is
    a mechanism that lets the Java Virtual Machine get rid of objects when it needs to reclaim memory in
    order to not run out of memory. This datastructure is also extremely useful for the transaction
    itself because it uses it to avoid having to create object instances too often.
:   This property tells Ontopia that it should hold hard references to the most recently used objects
    and for how many objects it should do so. Note that the property specifies the number of hard
    references per transaction, so when adjusting this number take into account how many concurrent
    transactions you might have and how much memory is available.

net.ontopia.topicmaps.impl.rdbms.Cache.shared.identitymap.lru
:   Type: integer (# items in cache), Default: 5000
:   This property is similiar to the previous one, but this one is used to define the size of the lru of
    the shared identity map. The shared identity map is shared amongst all read-only transactions. In
    general this one should be bigger than the previous one as there are usually a lot more read-only
    transactions in the system than read-write ones.

net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.srcloc.lru
:   Type: integer (# items in cache), Default: 2000
:   Each transaction maintains cache that is a mapping between source locators and the topic map objects
    that have those identities. This cache is there so that repeated lookups can be fast. The cache uses
    soft references and this property lets you specify how many hard references there should be to the
    most recently used entries.
:   

net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.subind.lru
:   Type: integer (# items in cache), Default: 1000
:   Each transaction maintains cache that is a mapping between subject identities (subject identifiers)
    and the topics that have those identities. This cache is there so that repeated lookups can be fast.
    The cache uses soft references and this property lets you specify how many hard references there
    should be to the most recently used entries.

net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.subloc.lru
:   Type: integer (# items in cache), Default: 100
:   Each transaction maintains cache that is a mapping between subject locators and the topics that have
    those identities. This cache is there so that repeated lookups can be fast. The cache uses soft
    references and this property lets you specify how many hard references there should be to the most
    recently used entries.

net.ontopia.topicmaps.impl.rdbms.Cache.rolesbytype.lru
:   Type: integer (# items in cache), Default: 1000
:   This setting is used to control the minimum size of a cache used by the
    `TopicIF.getRolesByType(TopicIF roleType)`. This cache is also used by the tolog query engine to
    improve association traversal performance.

net.ontopia.topicmaps.impl.rdbms.Cache.rolesbytype2.lru
:   Type: integer (# items in cache), Default: 1000
:   This setting is used to control the minimum size of a cache used by the
    `TopicIF.getRolesByType(TopicIF roleType, TopicIF associationType)`. This cache is also used by the
    tolog query engine to improve association traversal performance.

##### JDBC connection pooling #####

The RDBMS Backend Connector uses JDBC connections to access the relational database. Being able to
reuse connections across interactions with the database is important because creating connections is
expensive. The RDBMS Backend Connector supports different JDBC connection pool implementations. Use
the following properties to enable and configure connection pooling:

net.ontopia.topicmaps.impl.rdbms.ConnectionPool
:   Type: boolean, 'dbcp' or 'jndi', Default: dbcp
:   If set to `true`, or any of the named collection pool implementations, connection pooling will be
    used. The default connection pool implementation is 'dbcp'. It is recommended that one use
    connection pooling if one needs to open more than a single topic map
    transaction.

net.ontopia.topicmaps.impl.rdbms.ConnectionPool.MinimumSize
:   Type: integer (# connections), Default: 0
:   The minimum number of idle connections in the pool. The number of connections in the pool will never
    shrink below this size.

net.ontopia.topicmaps.impl.rdbms.ConnectionPool.MaximumSize
:   Type: integer (# connections), Default: 50
:   The maximum number of active connections in the pool. The number of connections will never grow larger
    than this number, unless the soft maximum is enabled.

net.ontopia.topicmaps.impl.rdbms.ConnectionPool.MaximumIdle
:   Type: integer (# connections), Default: 20
:   The maximum number of idle connections in the pool. When there are more idle connections in the pool,
    the pool will start closing them.

net.ontopia.topicmaps.impl.rdbms.ConnectionPool.IdleTimeout
:   Type: integer (# miliseconds), Default: 300000 (5m)
:   The maximum time an idle connection stays in the pool. If exceeded and there are more than `MaximumIdle`
    connections in the pool, the connection is closed. Setting the timeout to -1 means idle connections are
    never closed.

net.ontopia.topicmaps.impl.rdbms.ConnectionPool.SoftMaximum
:   Type: boolean, Default: false
:   If the maximum pool size is reached but there are outstanding requests for connections emergency
    connections will be created if this property is true. This will temporarily increase the size of the
    pool, but the pool will shrink back to an acceptable size when the activity is lower. If this setting is
    false, requests will block until a connection is available. Using this option can lead the exceeding
    the maximum number of connections the database supports.

net.ontopia.topicmaps.impl.rdbms.ConnectionPool.WhenExhaustedAction
:   Type: String (`grow`, `block`, `fail`), Default: block
:   What to do when the pool is exhausted but a new connection is request. When set to `grow`: the pool will
    temporarly grow (see `SoftMaximum`). When set to `fail`: requesting a connection when the pool is exhausted will
    lead to an exception. When set to `block`: the request for a connection will block until a connection is available.
    Note: overrides the `SoftMaximum` setting.

net.ontopia.topicmaps.impl.rdbms.ConnectionPool.UserTimeout
:   Type: integer (# miliseconds), Default: 10000
:   When `SoftMaximum` is set to false, or `WhenExhaustedAction` is set to block, this is the maximum
    time that the connection request will block. If exceeded an exception is thrown. If set to -1, the
    request will block indefinitely.

net.ontopia.topicmaps.impl.rdbms.ConnectionPool.PoolStatements
:   Type: boolean, Default: true
:   The dbcp connection pool makes it possible to pool and reuse prepared statements created by the
    application. The RDBMS Backend uses prepared statements and enabling this feature should generally
    have a positive effect on performance. If the connection pool is dbcp then this feature is enabled
    by default. You should only set this property to false if you have a good reason to do
    so.

net.ontopia.topicmaps.impl.rdbms.ConnectionPool.JNDIDataSource
:   Type: string (the JNDI name), Default: none
:   Specifies the JNDI name of the JDBC data source. Note that this property is only useful when you are
    accessing the JDBC connections via JNDI. This usually only happens inside an J2EE application
    server. The property must be specified if the 'jndi' connection pool implementation has been
    specified. If you do not use JNDI to configure and set up your JDBC connections then do not set this
    property.

##### Topic map store pooling #####

Ontopia supports pooling of topic map stores in the same way as you can pool JDBC connections with
the provided connection pool implementations. The topic map store pooling is currently only
available via the topic map reference implementation in the RDBMS Backend Connector. When using the
RDBMS Backend Connector with the Navigator and Web Editor frameworks the topic map store pooling is
always enabled. Note that when using the Topic Maps Engine API one must access the topic map stores
through one of the topic map source implementations in the RDBMS Backend to make use store pooling.
These topic map source implementations always use store pooling.

The main reason for using topic map store pooling is that it greatly improves performance in
transactional environements. This is possible because topic map objects, topic map data, cached
information and other state information can be reused across transaction
boundaries.

You can override the store pool defaults by setting the following properties:

net.ontopia.topicmaps.impl.rdbms.StorePool.MinimumSize
:   Type: integer (# stores), Default: 0
:   The minimum number of stores in the pool. The number of stores in the pool will never shrink below
    this size.

net.ontopia.topicmaps.impl.rdbms.StorePool.MaximumSize
:   Type: integer (# stores), Default: 8
:   The maximum number of stores in the pool. The number of stores will never grow larger than this
    number, unless the soft maximum is enabled.

net.ontopia.topicmaps.impl.rdbms.StorePool.SoftMaximum
:   Type: boolean, Default: false
:   If the maximum pool size is reached but there are outstanding requests for stores emergency stores
    will be created if this property is true. This will temporarily increase the size of the pool, but
    the pool will shrink back to an acceptable size when the activity is lower. If this setting is
    false, requests will block until a connection is available.

##### Tolog queries #####

The RDBMS Backend Connector has its own tolog implementation which it will use if it finds that it
is possible to translate the tolog query into SQL. In most of the cases the entire tolog query can
be translated fully into a SQL query, but for some more complex tolog queries only parts or none of
it can be translated. Sometimes it makes sense for a topic map application to not use the RDBMS
tolog implementation. These applications can change the default tolog implementation by setting the
following property:

net.ontopia.topicmaps.query.core.QueryProcessorIF
:   Type: 'in-memory' or 'rdbms', Default: 'in-memory'
:   Specifies which tolog implementation the RDBMS Backend Connector should use by default.

##### Fulltext queries #####

The tolog query language with its value-like predicate uses an underlying Fulltext integration. At
the moment there are two options. The first is 'generic', which means that the search terms is
turned into a SQL expression using the LIKE operator with wild cards on both sides. The second is
'oracle_text' which translates the search into an Oracle Text search expression. It is possible to
specify exactly which Fulltext integration that should be used:

net.ontopia.infoset.fulltext.impl.rdbms.RDBMSSearcher.type
:   Type: 'generic' or 'oracle_text', Default: 'generic'
:   Specifies which fulltext implementation the RDBMS Backend Connector should use by default.

Note that this property will also have an effect with direct use of the
`net.ontopia.infoset.fulltext.impl.rdbms.RDBMSSearcher` class.

##### Sample file #####

**Sample properties file (Oracle)**

````properties
net.ontopia.topicmaps.impl.rdbms.Database=oracle10g
net.ontopia.topicmaps.impl.rdbms.ConnectionString=jdbc:oracle:thin:@127.0.0.1:1521:TOPICMAPS
net.ontopia.topicmaps.impl.rdbms.DriverClass=oracle.jdbc.driver.OracleDriver
net.ontopia.topicmaps.impl.rdbms.UserName=scott
net.ontopia.topicmaps.impl.rdbms.Password=tiger
````

> **Warning**
> Please note that the backslash character (`\`) is used as an escape character in Java property
> files. If you need to write a backslash (for example in DOS paths), make sure you write them as
> `\\`. Lines ending in a single backslash will cause the following property to be
> ignored.

#### Native RDBMS full-text ####

Instead of using the Lucene integration with the RDBMS Backend Connector one can take advantage of
the native full-text support that the database may have. This support can range from simple LIKE
operator queries to advanced full-text indexing support.

In the `net.ontopia.infoset.fulltext.impl.rdbms` package there is a class called `RDBMSSearcher`
that implements the `SearcherIF` interface. Pass your RDBMS `TopicMapIF` instance to the single
constructor of this class and you have a searcher instance that let you search for topic name,
variant name, and occurrences using the native pattern language.

The actual queries that are issued and the syntax that are supported is usually database specific,
but the default behaviour is to use the LIKE operator. This means that you can use the '%' symbol to
specify wildcards in your queries. Most relational databases support the LIKE operator, and
therefore the default should be ok for most databases. Note that the actual query will make use of
the `lower` function to make it a case-insensitive query.

In addition to the generic RDBMS full-text support Ontopia comes with support for several
database-specific full-text implementations. They are all described below.

Note that enabling native RDBMS full-text through the
`net.ontopia.infoset.fulltext.impl.rdbms.RDBMSSearcher.type` database property will also enable it
for the `value-like` tolog predicate.

##### Oracle Text #####

Recent versions of Oracle comes with full-text support included. The component is called Oracle
Text. In older versions Oracle Text was an optional component and had to be purchased and installed
separately.

To enable use of Oracle Text in Ontopia you will have to set the
`net.ontopia.infoset.fulltext.impl.rdbms.RDBMSSearcher.type` database property to `oracle_text`. You
will also have to make sure that the columns `TM_TOPIC_NAME.content`, `TM_VARIANT_NAME.content`, and
`TM_OCCURRENCE.content` are setup to be indexed by Oracle Text.

Here are some sample statements to set this up for Oracle. Note that they require the Oracle Context
Option, which for Oracle 8.x needs to be installed separately, but for Oracle 9i and later it should
be installed by default.

````sql
create index TEXT_TOPIC_NAME on TM_TOPIC_NAME(content) 
  indextype is ctxsys.context 
  parameters ('sync(on commit) datastore ctxsys.default_datastore filter ctxsys.null_filter section group ctxsys.auto_section_group');
create index TEXT_OCCURRENCE on TM_OCCURRENCE(content) 
  indextype is ctxsys.context 
  parameters ('sync(on commit) datastore ctxsys.default_datastore filter ctxsys.null_filter section group ctxsys.auto_section_group');
create index TEXT_VARIANT_NAME on TM_VARIANT_NAME(content) 
  indextype is ctxsys.context 
  parameters ('sync(on commit) datastore ctxsys.default_datastore filter ctxsys.null_filter section group ctxsys.auto_section_group');
````

These indexes will be kept up to date on every commit. If you don't need to have them synced that
often, consider changing the sync parameter.

An error message will be issued by the Oracle RDBMS if one attempts to use the Oracle Text query
when the columns have not been full-text indexed, so if this happens you will have to run the create
index statements above or similar ones.

Please consult your database documentation for more information on Oracle Text.

##### PostgreSQL full-text #####

The full-text support in PostgreSQL is implemented through a module called
[tsearch2](http://www.sai.msu.su/~megera/postgres/gist/tsearch/V2/). This module will usually have
to be installed manually before it can be used. This is typically done by logging in as the
PostgreSQL administrator user and running the `${PSQL_LIB}/contrib/tsearch2.sql` script in the
database, e.g. using the `psql` command line utility. If you later which to uninstall tsearch2 that
can be done with the `${PSQL_LIB}/contrib/untsearch2.sql` script.

The next step is to grant access to the tsearch2 tables to the database user that is used by
Ontopia. Replace `myuser` with the name of your database user below.

````sql
grant all on pg_ts_cfg to myuser;
grant all on pg_ts_cfgmap to myuser;
grant all on pg_ts_dict to myuser;
grant all on pg_ts_parser to myuser;
````

tsearch2 will then have to be told which of its configurations that are to be used. Ontopia uses a
configuration called `default`. The configurations are mapped to the server encoding. Issue the
following statement to set the locale for the default configration.

````sql
update pg_ts_cfg set locale='en_US.UTF-8' where ts_name='default';
````

If you don't know what the server encoding is then the following query will tell you:

````sql
select setting from pg_settings where name='lc_ctype';
````

The last step is to add a tsearch2 column and an index to each of the three tables that are to be
searched. To keep the index up-to-date a set of triggers will also have to be
added.

````sql
/* add columns */
alter table TM_TOPIC_NAME add column tsearch2 tsvector;
alter table TM_VARIANT_NAME add column tsearch2 tsvector;
alter table TM_OCCURRENCE add column tsearch2 tsvector;
/* create indexes */
create index tsearch2_index_tbn on TM_TOPIC_NAME using gist(tsearch2);
create index tsearch2_index_tvn on TM_VARIANT_NAME using gist(tsearch2);
create index tsearch2_index_to on TM_OCCURRENCE using gist(tsearch2);
/* populate indexed columns */
update TM_TOPIC_NAME set tsearch2=to_tsvector('default', coalesce(content,''));
update TM_VARIANT_NAME set tsearch2=to_tsvector('default', coalesce(content, ''));
update TM_OCCURRENCE set tsearch2=to_tsvector('default', coalesce(content, ''));
/* add triggers */
create trigger tsearch2_update_tbn before update or insert on TM_TOPIC_NAME for each row execute procedure tsearch2(tsearch2, content);
create trigger tsearch2_update_tvn before update or insert on TM_VARIANT_NAME for each row execute procedure tsearch2(tsearch2, content);
create trigger tsearch2_update_to before update or insert on TM_OCCURRENCE for each row execute procedure tsearch2(tsearch2, content);
````

To enable use of tsearch2 in Ontopia you will have to set the
`net.ontopia.infoset.fulltext.impl.rdbms.RDBMSSearcher.type` database property to `postgresql` or
`tsearch2`.

##### Microsoft SQL Server full-text #####

The full-text support in SQL Server is not enabled by default, so it will have to be enabled:

````sql
EXEC sp_fulltext_database 'enable';
````

Then a full-text catalog has to be created and one full-text index added to each of the three tables
that are be indexed. The database server will keep the full-text indexes automatically up to date as
changes occur.

````sql
create fulltext catalog okscatalog as default;
create fulltext index on TM_TOPIC_NAME(content) key index TM_TOPIC_NAME_pkey;
create fulltext index on TM_VARIANT_NAME(content) key index TM_VARIANT_NAME_pkey;
create fulltext index on TM_OCCURRENCE(content) key index TM_OCCURRENCE_pkey;
alter fulltext catalog okscatalog reorganize;
````

Note that the `TM_TOPIC_NAME_pkey`, `TM_VARIANT_NAME_pkey` and `TM_OCCURRENCE_pkey` indexes
referenced above are primary key indexe names, but they may not actually have those names in your
database. You will have to look up the actual names of those three indexes of the given three names
don't work.

To enable use of Microsoft SQL Server full-text in Ontopia you will have to set the
`net.ontopia.infoset.fulltext.impl.rdbms.RDBMSSearcher.type` database property to
`sqlserver`.

#### Setting the CLASSPATH ####

In order for the Java Virtual Machine to be able to load the RDBMS Backend Connector classes you
need to add the ontopia.jar file to your `CLASSPATH` environment variable. See the *Ontopia
Installation and getting started* document for more information.

Note that you also need to put your selected JDBC-driver classes on the `CLASSPATH`.

If you are using batch writing or connection pooling, then you must make sure that the JDBC 2.0
optional package is available on the `CLASSPATH`.

#### Running the test suite ####

Now that you have an empty database we are ready to verify that the system actually works. We do
this by running the test suite on the database. If you don't want to run the test suite, feel free
to skip this section.

Before you can run the RDBMS Backend Connector test suite make sure that you have the Ontopia test
environment correctly set up. This includes having the ontopia.jar and ontopia-test.jar-files on the
CLASSPATH. See the *Ontopia, Installation and getting started* document for more
information.

You can now run the test suite by following the procedure below:

Windows
:   Go to the `${basedir}` directory. Set the environment variable `ONTOPIA_HOME` to the `${basedir}`,
    using the command `set ONTOPIA_HOME=${basedir}`. Then run the `tests\runtests-rdbms.bat` script
    passing the property file as an argument.

Unix
:   Go to the `${basedir}` directory. Then run the `tests/runtests-rdbms.sh` script passing the property
    file as an argument.

The result should be similiar to what is shown below. If it is not, please send an email to
[support@ontopia.net](mailto:support@ontopia.net) with the output of the test run as well as details
about your platform and the database you are using.

````
Ontopia RDBMS Backend Connector [version]
Success: All required classes found.
Running tests
.........................................
.........................................
.........................................
.........................................
.........................................
.........................................
.........................................
..............................
Time: [xxx.yyy]

OK ([zzz] tests)

Asserts: [nnn]
````

### What's next? ###

Congratulations. Now that you've gotten this far it is time to start developing software using the
RDBMS Backend Connector. See the *[Ontopia RDBMS Backend Connector, Developer's
Guide](devguide.html)* for information about how to develop applications using the backend
connector.


