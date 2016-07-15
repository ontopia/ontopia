Ontopia
=======

What's new - Ontopia 2.x
------------------------

<p class="introduction">
This document describes what has changed in Ontopia between releases, both at a higher level, and in
more detail.
</p>

<span class="version">5.3.0 2013-08-01</p>

### OKS 2.2.4 release notes ###

This is mainly a bug fix release, but also includes a number of improvements.

The following improvements have been made:

*  The dynamic association predicate now does prefetching when all role player arguments are unbound.
*  An existing log4j system can now be used by web applications. This can be done by not specifying the
   `log4j_config` context parameter in web.xml.
*  Web Editor tags webed:button, webed:checkbox, webed:field, webed:file, webed:form, webed:link and
   webed:list now take the `class` attribute which maps directly to an HTML class
   attribute.
*  The user-interface language used in the Vizigator and the Vizlet is now configurable. The Vizigator
   takes an applet parameter called `lang` and the Vizlet takes a command line argument with the same
   name. The default value is `en`.
*  Vizlet controls can now be hidden when the applet is loaded. This can be done by setting the
   `controlsVisible` applet parameter to `false`. Note that it is possible to get the controls back by
   selecting the appropriate entry in the context menu.

The following bugs have been fixed:

*  [Bug #1719](http://www.ontopia.net/bugs/showbug.cgi?id=1719): The webed:actionid registered the
   current value incorrectly.
*  [Bug #1554](http://www.ontopia.net/bugs/showbug.cgi?id=1554): Deadlock problem in TouchGraph fixed.
*  Fixed Vizigator bug that caused a NullPointerException to be thrown when association roles did not
   have a role player.
*  NoSuchElementExceptions where constructed and thrown too eagerly internally inside one of the RDBMS
   Backend collection implementations. This is no longer done and leads to better
   performance.
*  Database connections were not always returned to connection pool when topic map store was
   invalidated. In some situations this caused the connection pool to run out if connections quickly,
   while in others it would take a long time.

### OKS 2.2.3 release notes ###

This is a bug fix and optimization release. A number of bug fixes and performance improvements have
been made to the RDBMS backend. If you are not using the RDBMS backend, you can ignore this release.
If you are using the RDBM backend we *strongly recommend* that you upgrade to this
version.

In this release, only the OKS code and the database schemas/scripts have been updated. To upgrade an
existing installation, all you need to do is replace the `oks-*.jar` file.

The following improvements have been made:

*  A memory leak in the RDBMS caches has been fixed.
*  Data sources retrieved through JNDI must now have absolute JNDI names, not relative as in previous
   releases. The OKS will now also explicitly set the `autoCommit` flag to false, overriding any
   defaults.
*  The representation of object identities has been optimized, so that it now takes much less memory.
*  A number of optimizations have been made in the backend, some of which dramatically improve
   performance for multiple simultaneous transactions. A side-effect is that the RDBMS backend may now
   more JDBC connections than in previous releases.
*  The XTM importer has been optimized so that, when importing into the RDBMS backend, creation of
   topics is delayed, in order to see whether the topic already exists. This avoids merging in a number
   of cases, and can dramatically improve performance when importing new data from XTM into an existing
   database.
*  Some modifications have been made to the default RDBMS schemas, such as making foreign key
   constraints deferrable, and some improvements to the default indexes.
*  The database drop script now also drops constraints.

### OKS 2.2.2 release notes ###

This is a bug fix release.

#### Query Engine ####

The following bugs have been fixed:

*  [Bug #1507](http://www.ontopia.net/bugs/showbug.cgi?id=1507): Query with role-player and type gives
   no results
*  The dynamic association predicate failed with a NullPointerException in some situations where the
   player of an association role was null.

#### RDBMS Backend ####

The following bugs have been fixed:

*  [Bug #1509](http://www.ontopia.net/bugs/showbug.cgi?id=1509): OutOfMemoryExceptions on large
   imports
*  Importing large topic maps caused OutOfMemoryExceptions. This effectively prevented one from running
   imports larger than the available memory.

### OKS 2.2.1 release notes ###

This is a bug fix release.

#### Engine ####

The following improvements have been made:

*  The LTM importer now makes sure that older topics are preferred over newer topics when merging. This
   change leads to better performance in most situations as older topics usually have more
   characteristics than newer ones.

The following bugs have been fixed:

*  [Bug #1339](http://www.ontopia.net/bugs/showbug.cgi?id=1339): RDF import does not support
   rdf:nodeID

#### Vizigator ####

The following bugs have been fixed:

*  [Bug #1464](http://www.ontopia.net/bugs/showbug.cgi?id=1465): Collapse leaves isolated nodes
*  [Bug #1428](http://www.ontopia.net/bugs/showbug.cgi?id=1428): Hide node can split graph

#### Navigator Framework ####

The following improvements have been made to the Navigator Framework:

*  The JAAS authentication module now provides role names to the application container. The unscoped
   user group names are provided as role names.
*  The JAAS authentication module classes are now provided in a separate jar file:
   `$TOMCAT_HOME/server/lib/oks-realm.jar`.
*  A few new utility methods were added to make it easier to use the framework in servlets: an
   overloaded version of `run` in `TreeWidget` and of `getSingleValue` in
   `ContextUtils`.

#### Web Editor Framework ####

The following improvements have been made to the Web Edit Framework:

*  User form data will now expire after a set interval, even if the session as a whole has not expired,
   and even if the form has not been closed. This reduces the memory usage with large forms and many
   concurrent users.

The following bugs have been fixed:

*  [Bug #1471](http://www.ontopia.net/bugs/showbug.cgi?id=1471): Data in other actions not available
   when triggered action is exclusive

#### RDBMS Backend ####

The following improvements have been made to the RDBMS backend:

*  The database creation scripts now include a number of new indexes that greatly improve performance
   with the new queries used by the pre-fetching code.
*  All database connections are now validated before starting new topic map transactions. The shared
   cache is also able to detect that the database is down and attempt to acquire a new connection. This
   change means that the shared cache, or JVM, does not have to be restarted when the backing database
   has been reset.
*  The trove4j library has been upgraded (`trove.jar`).

The following bugs have been fixed:

*  [Bug #1466](http://www.ontopia.net/bugs/showbug.cgi?id=1466): Role type cache crashes when no shared
   cache
*  JDBCSpy would in some cases produce a driver even if the JDBC URL did not match.

### OKS 2.2 release notes ###

This release adds substantial new functionality, primarily significant performance enhancements in
the RDBMS backend, but also support for LTM 1.3, the JAAS module, and the accessctl web application.
More detail on these is provided below. There are some violations of backwards compatibility, so
please read the release notes carefully.

#### Omnigator ####

The Omnigator now supports exporting topic maps to LTM format.

The following bugs have been fixed:

*  [Bug #1278](http://www.ontopia.net/bugs/showbug.cgi?id=1278): Encoding problem in Omnigator's Add
   plug-in
*  [Bug #1420](http://www.ontopia.net/bugs/showbug.cgi?id=1420): Omnigator's Query plug-in garbles
   non-ASCII characters
*  [Bug #1423](http://www.ontopia.net/bugs/showbug.cgi?id=1423): Omnigator's Query plug-in doesn't
   reuse the QueryProcessor object

#### Navigator Framework ####

The following improvements have been made to the Navigator Framework:

*  A JAAS module for user authentication in application servers against user information stored in the
   topic map has been added. A web application for maintaining user information has also been added.
   Documentation for these two components is provided.
*  The configuration of shared repositories has been significantly simplified. In particular, the need
   to directly reference the `tm-sources.xml` file has been reduced.
*  The methods NavigatorApplicationIF.getConfigurations(), NavigatorApplicationIF.getName() and
   NavigatorApplicationIF.getInstanceOf(String) has been marked INTERNAL.

The following bugs have been fixed:

*  [Bug #1416](http://www.ontopia.net/bugs/showbug.cgi?id=1416): tolog:set unwraps collection value in
   non-OKS scope
*  [Bug #1404](http://www.ontopia.net/bugs/showbug.cgi?id=1404): Cannot have tolog:set tags with
   implicit var attribute

#### Vizigator ####

The following improvements have been made to the Vizigator:

*  Loading big topic maps in VizDesktop is now much easier, as a dialog box showing progress is shown,
   and a start topic can be chosen without having to view the entire topic map. Building of the topic
   map graph is now done "on demand", which means that with a start topic getting started with a large
   topic map is much faster.
*  The VizDesktop now supports setting name scopes to control which names are shown on topics. There is
   a corresponding configuration parameter in the Vizlet, and the setting is stored in the
   configuration.
*  The file size of the Vizlet jar file has been reduced by about 30%, which should reduce the Vizlet
   startup time.
*  If two topics are related by two associations the associations will no longer appear on top of each
   other so that only one can be seen, but will instead curve out to avoid this.

The following bugs have been fixed:

*  [Bug #1302](http://www.ontopia.net/bugs/showbug.cgi?id=1302): Hiding or collapsing a node can make a
   ghost node
*  [Bug #1392](http://www.ontopia.net/bugs/showbug.cgi?id=1392): Error message because log4j.properties
   not found
*  [Bug #1415](http://www.ontopia.net/bugs/showbug.cgi?id=1415): Map view menu choice enabled when in
   map view

#### Web Editor Framework ####

The following improvements have been made to the Web Editor Framework:

*  The `forwardBehaviour` element is no longer allowed in `actions.xml`. This because the machinery for
   multi-frame applications has been signficantly changed.
*  The documentation of the `AssignRolePlayer` action has been signficantly extended.
*  Documentation of on how to make the `ProcessServlet` produce log output has been added, and the
   logging itself improved.
*  The `actionGroup` attribute has been *removed* from the `webed:actionid` tag.

The following bugs have been fixed:

*  [Bug #1412](http://www.ontopia.net/bugs/showbug.cgi?id=1412): Incorrect handling of button icon
   paths
*  [Bug #1401](http://www.ontopia.net/bugs/showbug.cgi?id=1401): Poor error message when no
   <forwardDefault> provided
*  [Bug #1400](http://www.ontopia.net/bugs/showbug.cgi?id=1400): Locking forwarding not done correctly
*  [Bug #1356](http://www.ontopia.net/bugs/showbug.cgi?id=1356): Action data expires too easily
*  [Bug #1396](http://www.ontopia.net/bugs/showbug.cgi?id=1396): `trim` attribute on `webed:field` does
   not work

#### Query Engine ####

The following improvements have been made to the Query Engine:

*  The query engine now checks queries for type conflicts (incorrect types of arguments to predicates)
   and will report any type conflicts as errors. This may cause existing queries to fail, but always
   because there is a bug in the query.
*  Looking up occurrences via their string values, using either the `value` predicate or dynamic
   occurrence predicates is now significantly optimized.
*  A number of optimizations have been added to the in-memory query processor. In addition, the
   in-memory query processor will now prefetch information not in the cache from the RDBMS, which
   significantly improves performance when using the RDBMS backend. Dynamic association predicates
   should also be much faster.

The following bugs have been fixed:

*  [Bug #1143](http://www.ontopia.net/bugs/showbug.cgi?id=1143): Lookahead error in tolog parser
*  [Bug #1453](http://www.ontopia.net/bugs/showbug.cgi?id=1453): subject-locator predicate binds to
   null

#### Topic Map Engine ####

The following new functionality has been added to the engine:

*  The `getRolesByType(TopicIF)` and `getRolesByType(TopicIF, TopicIF)` methods has been added to the
   `TopicIF` interface. This can be convenient in some cases, and is used to improve the performance of
   the RDBMS backend.
*  The support for [TMAPI](http://www.tmapi.org/) has now been finalized. The OKS now has full support
   for TMAPI 1.0.
*  The LTM parser has been updated to support LTM 1.3.
*  The `net.ontopia.topicmaps.xml.CanonicalXTMWriter` class has been added, with support for the
   2004-11-01 draft of Canonical XTM.
*  The `net.ontopia.topicmaps.utils.ltm.LTMTopicMapWriter` class has been added, with support for
   exporting topic maps to LTM 1.3.

The following bugs have been fixed:

*  [Bug #1418](http://www.ontopia.net/bugs/showbug.cgi?id=1418): data URLs do not support non-ASCII
   characters

#### RDBMS Backend ####

The following improvements have been made to the RDBMS backend:

*  The RDBMS backend will now prefetch information not in the cache in a number of situations, which
   significantly improves performance. The difference is likely to be most noticeable when using
   in-memory tolog.
*  Index lookups (through the API or via tolog) would always cause two SQL queries in the database.
   This has now been optimized to only do a single SQL query.
*  Documentation of how to use P6Spy to do SQL profiling has been added, together with a script for
   producing reports from the P6Spy logs.

The following bugs have been fixed:

*  [Bug #1398](http://www.ontopia.net/bugs/showbug.cgi?id=1398): NullPointerException when closing
   RDBMS store

### OKS 2.1.1 release notes ###

This release is almost exclusively a bug fix release.

#### Omnigator ####

The following changes have been made to the Omnigator:

*  The user interface of the RDF2TM plug-in has been improved slightly.
*  The full-text indexer now considers digits parts of tokens, which it did not previously. This means
   old indexes should be recreated.

The following bugs have been fixed:

*  Not all plug-ins closed the topic map store the way they should; this has now been fixed.

#### Navigator Framework ####

Only one change has been made here: the "oks" JSP variable is now set in request scope instead of
page scope.

#### Vizigator ####

Two bugs have been fixed in the Vizigator:

*  [ Bug #1369](http://www.ontopia.net/bugs/showbug.cgi?id=1369): NPE on bad topic maps
*  [ Bug #1376](http://www.ontopia.net/bugs/showbug.cgi?id=1376): Right-click does not work on MacOS X

#### Web Editor Framework ####

The following changes have been made to the Web Editor Framework:

*  The `trim` attribute has been added to the `webed:field` tag to control whether whitespace is
   removed from its contents or not. (Bug #1374.)
*  The `runIfNoChanges` attribute has been added to the `webed:invoke` tag to control whether the
   invoked action is to be run if no other actions run.

The following bugs have been fixed:

*  [Bug #1373](http://www.ontopia.net/bugs/showbug.cgi?id=1373): Problems with certain webed tags and
   `jsp:include`.
*  NPE in ActionSignature in certain cases.
*  NoSuchElement exception in EvaluateLTM action on topics with no source locator.

#### Query Engine ####

The RDBMS tolog implementation now makes better use of the optimizers developed for the in-memory
implementation, which should improve performance in some cases.

One bug has been fixed:

*  [Bug #1378](http://www.ontopia.net/bugs/showbug.cgi?id=1378): NPE when sorting on topics with
   null-value names

#### Topic Map Engine ####

The following API changes have been made:

*  The `clear` method has been added to the `TopicMapReferenceIF` interface.
*  The `delete` method on the `TopicMapStoreIF` interface has been deprecated.

#### RDBMS Backend ####

One bug has been fixed:

*  The `delete` method on `RDBMSTopicMapStore` would in some cases leave orphan rows in the `*_SCOPE`
   tables.

### OKS 2.1 release notes ###

This release adds major new functionality, substantial internal changes, as well an overhaul of the
API where many packages and classes are made INTERNAL or deprecated. You are recommended to read
this section carefully before installing this version.

#### Omnigator ####

The Omnigator now has two new plug-ins:

*  VizPlugin, which provides a visual view of the surroundings of a given topic in the topic map, using
   Vizigator.
*  RDF2TM, which can be used to create RDF-to-topic map mappings using a graphical interface.

The following bugs have been fixed:

*  [Bug #1224](http://www.ontopia.net/bugs/showbug.cgi?id=1224): Merge button causes error when no
   topic map selected

#### Navigator Framework ####

The `tm`, `logic`, `output`, and `value` tag libraries have now been deprecated, in favour of the
new `tolog` tag library. The old tag libraries and their documentation are still included with the
OKS, but they are now officially deprecated, and no new functionality should be developed using
them. They will not be removed from the product as long as there are users for
them.

There are also some important changes in how the Navigator Framework works with the RDBMS backend;
these are documented in section 3.5 of the Navigator Framework Developer's Guide, and all users are
encouraged to read this section.

Parts of the Navigator Framework API have been marked as INTERNAL or been deprecated. A few methods
that have been deprecated for a long time have been removed altogether. The following class has also
been added to the API.

*  The `net.ontopia.topicmaps.nav2.utils.TreeWidget` class has been added.

#### Vizigator ####

The Vizigator is now out of beta, and officially part of the OKS. A substantial number of bugs have
been fixed, and new functionality added. We do not describe the details here; for a description of
the functionality, see the Vizigator User's Guide.

#### Web Editor Framework ####

The `params` attribute in the Web Editor Framework tag library now supports tolog URI prefixes
declared with the new `tolog:declare` tag. This makes it possible to refer to topics without
creating framework variables for them first.

The action implementations included in the Web Editor Framework now perform much stricter checking
on the parameters passed to them, which means that they provide much more useful error messages, but
also that in certain cases they will complain about parameters they would earlier
allow.

The `AddSubjectIndicator`, `RemoveSubjectIndicator`, `SetSubjectIndicator`, `AddSourceLocator`,
`RemoveSourceLocator`, and `SetSourceLocator` actions have been updated to accept string parameters
as well as locator parameters.

Parts of the API have been marked as INTERNAL or been deprecated in this version. In addition, the
`cloneAndOverride` method has been added to the `ActionParametersIF` interface to make it possible
to trigger existing actions from within an action.

The following bugs have been fixed:

*  [Bug #1289](http://www.ontopia.net/bugs/showbug.cgi?id=1289): Certain exceptions in actions would
   make the OKS go into a non-responsive state.
*  [Bug #1252](http://www.ontopia.net/bugs/showbug.cgi?id=1252): Actions would always be triggered in
   multiselect lists
*  [Bug #1251](http://www.ontopia.net/bugs/showbug.cgi?id=1251): Actions nested within buttons would
   not be invoked

#### Query Engine ####

The tolog query language has been extended with comparator predicates (that is, the normal `<`, `>`,
`=`, `<=`, `>=` operators). In addition, the `object-id` predicate has been
added.

The query engine has been reworked somewhat internally, adding some new optimizations, as well as
type analysis on variables. This is not obvious to external code, but improves
performance.

An API for preparsing declarations (as opposed to queries) and storing the resulting context so that
later queries can be parsed in a particular declaration context has been added.

The following bugs have been fixed:

*  [Bug #1264](http://www.ontopia.net/bugs/showbug.cgi?id=1264): Exception thrown when second parameter
   to dynamic occurrence predicate of wrong type
*  [Bug #1293](http://www.ontopia.net/bugs/showbug.cgi?id=1293): Incorrect result in dynamic
   association predicate query with inconsistent association signatures
*  [Bug #1290](http://www.ontopia.net/bugs/showbug.cgi?id=1290): subject-locator predicate cannot start
   from URL

#### Schema Tools ####

Parts of the API have been marked as INTERNAL or been deprecated.

#### Full-text Integration ####

The `oracle_text` platform must now be listed on the `net.ontopia.topicmaps.impl.rdbms.Platforms`
database property for Oracle Text support to be enabled, e.g. to enable Oracle Text support with
Oracle 10g set the property to `oracle_text,oracle10g,oracle,generic`. This change will break
backward compatibility, but will allow Oracle users to use the default rdbms fulltext support using
the LIKE operator instead of using Oracle Text. See the fulltext developers guide for more
information.

The `LuceneIndexer` class now has a new command line option `--analyzer` which takes the class name
of an analyzer.

Parts of the API have been marked as INTERNAL or been deprecated.

#### Topic Map Engine ####

The core interfaces of the engine have now changed substantially, for the first time since the
release of version 1.0. Concepts left over from the HyTM syntax have now officially been deprecated,
such as facets, facet values, mnemonics, effective scope, and scope on topics and topic maps. If you
didn't know these things existed: don't worry, the effect on your code is likely to be nil. In
addition, a large number of non-core interfaces, classes, and methods have been marked as INTERNAL
or been deprecated.

In addition, the following API changes have been made:

*  The `close` method has been added to the `TopicMapRepositoryIF` interface.
*  The `lenient` property on the RDF path topic map source has been added, making it possible to ignore
   some errors in the RDF mappings.
*  The `DuplicateSuppressionUtils` class has a new method called `removeDuplicateAssociations` which
   will remove all duplicate associations of a particular topic.

Some performance optimizations have been made in import and export:

*  The LTM and XTM importers are now faster when the imported files cause topic merges, because they
   have been modified to avoid merges in situations where it can be avoided.
*  Export to XTM should now be quite a bit faster, as the performance of XML generation has been
   improved.

The `net.ontopia.topicmaps.entry` package has been reworked a bit, and the following changes have
been made:

*  The `TopicMapReferenceIF` interface now has two new methods: `open` and `isDeleted`.
*  The `TopicMapReferenceIF` interface now throws a `StoreDeletedException` if the topic map has been
   deleted.
*  Topic map references can now be reopened after they have been closed.
*  Most implementations of `TopicMapReferenceIF` no longer delete the file in the file system when
   `delete` is called unless the `deleteFiles` property is set to true.

Locators output by XTM, CXTM and HyTM serializers are now written in its external form and no longer
the internal form.

The following bugs have been fixed:

*  [Bug #1247](http://www.ontopia.net/bugs/showbug.cgi?id=1247):
   InternalAssociation.getAssociationTypeId throws NPE
*  [Bug #1325](http://www.ontopia.net/bugs/showbug.cgi?id=1325): ClassInstanceIndex contains
   unconnected topics
*  [Bug #1317](http://www.ontopia.net/bugs/showbug.cgi?id=1317): RDF mapping file topics included in
   TM

#### RDBMS Backend ####

The internals of the RDBMS backend have been substantially reworked to provide better support for
concurrent transactions. A number of RDBMS properties have been added to control the internal
workings of this. To find out what these are, please consult the RDBMS Backend Installation Guide
and the Developer's Guide. Note also that some behaviours that never were allowed, but which used
not to cause problems now *will* cause problems. Caution, and careful testing, should be
applied.

The two main new features in the RDBMS Backend are shared cache and topic map store pooling. The
first lets topic map store instances share information so that the network traffic to the database
is reduced and thus performance improved. The second feature makes it possible to reuse topic map
store instances across transactions. This improves scalability in multi-user access to the database
and will also improve resource management in applications.

A few new database properties has been added. All the properties are related to either shared cache
settings or topic map store pooling. See the RDBMS Installation Guide for more in-depth information
on the supported database properties.

*  net.ontopia.topicmaps.impl.rdbms.Cache.shared
*  net.ontopia.topicmaps.impl.rdbms.Cache.identitymap.lru
*  net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.srcloc.lru
*  net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.subind.lru
*  net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.subloc.lru
*  net.ontopia.topicmaps.impl.rdbms.StorePool.MinimumSize
*  net.ontopia.topicmaps.impl.rdbms.StorePool.MaximumSize
*  net.ontopia.topicmaps.impl.rdbms.StorePool.SoftMaximum

The following properties are no longer supported/used, so if you use them in your database
properties file consider removing them:

*  net.ontopia.topicmaps.impl.rdbms.ConnectionPool.InitialSize
*  net.ontopia.topicmaps.impl.rdbms.ConnectionPool.Timeout
*  net.ontopia.topicmaps.impl.rdbms.ConnectionPool.UserTimeout
*  net.ontopia.topicmaps.impl.rdbms.ConnectionPool.SkimmerFrequency
*  net.ontopia.topicmaps.impl.rdbms.ConnectionPool.ShrinkBy
*  net.ontopia.topicmaps.impl.rdbms.ConnectionPool.TransactionIsolationLevel
*  net.ontopia.topicmaps.impl.rdbms.MappingFile
*  net.ontopia.topicmaps.impl.rdbms.QueriesFile

The following properties are only used for database platforms for which there are no built-in
support, and should therefore not normally be specified in database properties:

*  net.ontopia.persistence.query.sql.InMaxElements
*  net.ontopia.topicmaps.impl.rdbms.KeyBlockSize
*  net.ontopia.topicmaps.impl.rdbms.HighLowKeyGenerator.SelectSuffix
*  net.ontopia.topicmaps.impl.rdbms.Platforms

The property net.ontopia.topicmaps.impl.rdbms.Platforms is now inferred from the
net.ontopia.topicmaps.impl.rdbms.Database property value and should therefore normally not be
specified.

The SQL IN-operator used by RDBMS tolog no longer uses a predefined max elements size, but retrieves
the setting from the net.ontopia.persistence.query.sql.InMaxElements database property instead.
Default is that there now is no limit to the maximum number of elements.

Poolman is no longer supported and has been dropped from the distribution. Jakarta Commons DBCP is
now the default connection pool implementation. Connection pooling is now enabled by
default.

Prepared statement pool is enabled by default by the DBCP connection pool.

URI locators stored in the RDBMS did not get externalized properly in getExternalForm and
resolveAbsolute. This has now been fixed.

The RDBMSDelete command line utility has been added.

The following bugs have been fixed:

*  [Bug #1283](http://www.ontopia.net/bugs/showbug.cgi?id=1283): Identity not found in repository
*  [Bug #1271](http://www.ontopia.net/bugs/showbug.cgi?id=1271): Generic database platform should be
   implicit
*  [Bug #1295](http://www.ontopia.net/bugs/showbug.cgi?id=1295): Transaction is not active and store
   already open
*  [Bug #1294](http://www.ontopia.net/bugs/showbug.cgi?id=1294): StoreNotOpenException is thrown
*  [Bug #1270](http://www.ontopia.net/bugs/showbug.cgi?id=1270): instance-of slow when class have many
   superclasses

### OKS 2.0.7 release notes ###

This release resolves a couple of Java 1.3 compatibility issues and two issues regarding the use of
locators.

#### Topic Map Engine ####

`'|'` characters in file URLs were incorrectly escaped by `URILocator.getExternalForm()`.

#### Navigator Framework ####

`<output:locator>` now uses `LocatorIF.getExternalForm()` instead of `LocatorIF.getAddress()` to
output locator addresses.

### OKS 2.0.6 release notes ###

This release is mainly a bug fix release. It resolves a couple of issues in the RDBMS backend. There
are also improvements in the XTM import utility.

#### Topic Map Engine ####

The way merging is done during XTM import has been improved. The importer code now avoids creating
temporary topics in the cases where the temporary object later would be merged with an existing
object. Temporary object creation is avoided where possible. This should lead to improved
performance in certain situations.

#### RDBMS Backend ####

The following bugs have been fixed:

*  [Bug #1062](http://www.ontopia.net/bugs/showbug.cgi?id=1062): dynamic occurrence predicate not
   properly mapped to SQL
*  [Bug #1261](http://www.ontopia.net/bugs/showbug.cgi?id=1261): misleading directory reference

### OKS 2.0.5 release notes ###

This release is a bug fix release. It resolves a couple of Web Editor bugs and a bug in the RDBMS
Backend.

#### Web Editor ####

The following bugs have been fixed:

*  [Bug #1251](http://www.ontopia.net/bugs/showbug.cgi?id=1251): Nested actions within <webed:button>
   never invoked
*  [Bug #1252](http://www.ontopia.net/bugs/showbug.cgi?id=1252): <webed:list> actions always executed
   when type was multiselect

#### RDBMS Backend ####

Some debugging statements were written to the console in the previous release. These have now been
removed.

### OKS 2.0.4 release notes ###

This release is a bug fix release. It also adds previews of the RDF2TM Omnigator plug-in and the
Vizigator. The RDF2TM plug-in is turned off by default, and not yet much documented. The Vizigator
has separate documentation included.

#### Topic Map Engine ####

Prototypical support for [TMAPI](http://www.tmapi.org) has been added. This does not quite conform
to the final version, and will have to be updated.

The RDF importer now supports a new `generateNames` property, which enables it to automatically
generate names for nameless topics created by the mapping based on their URLs.

The following bugs have been fixed:

*  [Bug #1142](http://www.ontopia.net/bugs/showbug.cgi?id=1142): File names in non-ASCII encodings
   cause errors
*  [Bug #1079](http://www.ontopia.net/bugs/showbug.cgi?id=1079): Can't resolve references to XTM files
   with spaces in URI
*  [Bug #1024](http://www.ontopia.net/bugs/showbug.cgi?id=1024): Associations with no roles get
   exported to invalid XTM

#### Query engine ####

The RDBMS implementation of tolog will now produce queries that in some situations are
*significantly* faster, especially in PostgreSQL.

The following bugs have been fixed:

*  [Bug #1096](http://www.ontopia.net/bugs/showbug.cgi?id=1096): Searches for keywords cause errors
*  [Bug #1032](http://www.ontopia.net/bugs/showbug.cgi?id=1032): Ordering by null value crashes
*  [Bug #972](http://www.ontopia.net/bugs/showbug.cgi?id=972): Incorrect results when same topic plays
   multiple roles

#### Navigator Framework ####

The interpretation of the `index` attribute on the `tm:fulltext` tag has changed. See the tag
library reference documentation for details.

#### Web Editor Framework ####

The `webed:field` tag now supports hidden fields.

### OKS 2.0.3 release notes ###

This release is a bug fix release, but also contains some optimizations and minor additions of new
functionality, about which, more below. The main extension is the experimental support for the new
[Canonical XTM](http://www.isotopicmaps.org/sam/cxtm/) syntax proposed for inclusion in the ISO
13250 standard.

#### Omnigator ####

The following improvements have been made:

*  For scoping topics (themes) the Topic Page now displays lists of names, occurrences and types of
   associations that are scoped by the current topic. (Note: This functionality is experimental and may
   be removed or changed if it turns out to be too expensive in terms of
   performance.)
*  Unary associations are now displayed separately from binary and n-ary associations.
*  The display of occurrences whose resources have topics representing them has been improved.
*  The export plug-in now supports the new [Canonical XTM](http://www.isotopicmaps.org/sam/cxtm/)
   syntax proposed for inclusion in the ISO 13250 standard.
*  The validator plug-in now displays better error messages in several cases.

The following bugs have been fixed:

*  [Bug #1007](http://www.ontopia.net/bugs/showbug.cgi?id=1007): Number of occurrences being shown was
   incorrect

#### Navigator Framework ####

The following bugs have been fixed:

*  [Bug #994](http://www.ontopia.net/bugs/showbug.cgi?id=994): `rulesfile` attribute on `tm:tolog` was
   broken

#### Web Editor Framework ####

The following improvements have been made:

*  The `webed:invoke` tag has been added. This tag can be useful for actions that always need to be
   invoked when a form is submitted.
*  The `webed:form`, `webed:field`, `webed:list`, and `webed:button` tags now all have an `id`
   attribute, which can be used to attach JavaScript actions to the HTML form controls they
   produce.
*  The `AssignRolePlayer` action has been extended to support use from checkboxes, by making it accept
   an additional argument.

The following bugs have been fixed:

*  [Bug #640](http://www.ontopia.net/bugs/showbug.cgi?id=640): No proper error message when user
   session expires

#### Query Engine ####

The tolog query engine has been modified to do extensive type inferencing internally, in order to
work out the possible types that can be assigned to each variable. This information is now used to
optimize away some predicates that can never succeed because of type conflicts.

*  [Bug #987](http://www.ontopia.net/bugs/showbug.cgi?id=987): The `value-like` predicate was sensitive
   to predicate order
*  [Bug #1005](http://www.ontopia.net/bugs/showbug.cgi?id=1005): tolog SQL compiler optimized OR
   clauses too eagerly
*  [Bug #955](http://www.ontopia.net/bugs/showbug.cgi?id=955): The `value-like` predicate misbehaved
   with empty search strings

#### Engine ####

The following improvements have been made:

*  An experimental implementation of the standard Canonical XTM format has been added.
*  The `getExternalForm` method has been added to the `LocatorIF` interface. This in order to allow
   URIs to be correctly escaped on output.
*  The `setDuplicateSuppression` method was added to the `RDFTopicMapReader` class.
*  The RDF topic map source now supports setting the RDF syntax as well as mapping files external to
   the RDF file being read.
*  The `TopicVariantNameGrabber` class has been added in order to make it possible to grab variant
   names from topics.

The following bugs have been fixed:

*  [Bug #1006](http://www.ontopia.net/bugs/showbug.cgi?id=1006): ImportExportUtils.getReader fails in
   applets
*  [Bug #1000](http://www.ontopia.net/bugs/showbug.cgi?id=1000): ConcurrentModificationException thrown
   in DuplicateSuppressionUtils
*  [Bug #981](http://www.ontopia.net/bugs/showbug.cgi?id=981): The LTM #MERGEMAP directive caused
   name-based merging

#### RDBMS Backend ####

The following improvements have been made:

*  The `queries.xml` and `mapping.xml` files are no longer read from the current directory by default,
   but instead from the .jar file. Their location can still be overridden using the properties
   file.
*  Added database scripts for MS SQL Server and Firebird.
*  The old 'oracle' platform has been split into 'oracle8' and 'oracle9i', with 'oracle' as the generic
   fallback.
*  The database scripts for PostgreSQL now create columns of the type `TEXT` (instead of `VARCHAR`),
   which gives unlimited-length strings.

### Changes from 2.0.1 to 2.0.2 ###

This release contains mainly bug fixes and improvements. One important change in this release is
that the version number is included in the $OKS_HOME directory name. This was introduced, so that
one can easily keep different versions apart and also avoid versions being copied on top of each
other.

#### Engine ####

Two improvements have been made:

*  URIs for topic maps stored in a relational database can now reference the object id of the topic map
   object. Example: x-ontopia:tm-rdbms:M1
*  The methods ImportExportUtils.getReader(String uri) and ImportExportUtils.getReader(LocatorIF uri)
   now support URIs like x-ontopia:tm-rdbms:M1.

These bugs were fixed in the topic map engine:

*  [Bug #924](http://www.ontopia.net/bugs/showbug.cgi?id=924): Topic reader did not resolve DTDs when
   referenced by URL.
*  [Bug #863](http://www.ontopia.net/bugs/showbug.cgi?id=863): The way the sanity checker reported
   topics without a name was broken.

#### Query Engine ####

One improvement has been made:

*  Handling of nots for large result sets has been optimized.

In addition the following bug was fixed in the tolog engine:

*  [Bug #903](http://www.ontopia.net/bugs/showbug.cgi?id=903): NullPointerException given when tolog
   engine could not determine type of variable.

#### Fulltext ####

One change has been made:

*  Upgraded to Lucene version 1.3 final.

#### Navigator Framework ####

The following improvements have been made:

*  The object id of a topic is now being displayed on the Omnigator topic page.
*  All navigator framework JSP tag attributes now allow runtime expressions.

#### Web Editor Framework ####

One improvement has been made:

*  The action configuration file (actions.xml) is now validated against the DTD when loaded.

In addition the following bug were fixed:

*  [Bug #883](http://www.ontopia.net/bugs/showbug.cgi?id=883): The tolog delete action only supported a
   single expression.
*  [Bug #882](http://www.ontopia.net/bugs/showbug.cgi?id=882): The tolog evaluate LTM action threw a
   NullPointerException when it didn't get any parameters.

#### RDBMS Backend ####

The following improvements have been made:

*  String comparison in MySQL has been made case-sensitive. In previous releases they were
   case-insensitive. This was considered a bug.
*  Fixed a bug in rdbms tolog that caused query parameters not to be passed through to the generated
   SQL query.
*  The rdbms test suite now cleans up after itself in the database.
*  Added the RDBMSTopicMapReader(long topicmap_id) constructor.

### Changes from 2.0 to 2.0.1 ###

This release contains mainly bug fixes and improvements.

#### Engine ####

New functionality has been added to the RDF support:

*  The RDF source now supports an external mapping file.
*  The RDF source now supports the duplicateSuppression parameter.

In addition the following bug was fixed in the engine test suite:

*  [Bug #865](http://www.ontopia.net/bugs/showbug.cgi?id=865): The engine test suite reported three
   failures on certain versions of Windows. The problem was a bug in the test suite itself, not the
   engine.

#### Navigator Framework ####

Two improvements have been made:

*  The reporting of out-of-memory errors has been improved.
*  The configuration guide has been updated with all tm-sources.xml details.

#### Web Editor Framework ####

One improvement has been made:

*  User objects now use a least-recently used algorithm, which reduces the risk of out-of-memory
   errors.

#### RDBMS Backend ####

The following changes have been made:

*  MySQL database creating script now uses InnoDB tables so that transactions can be used.

### Changes from 1.4.2 to 2.0 ###

This is the first OKS release for quite some time, and so there are substantial changes, many bug
fixes, and some new features. The changes are so substantial that some minor changes are not listed,
and some minor bug fixes omitted.

#### Omnigator ####

The following functionality has been added to the Omnigator:

*  Support for displaying association types which form hierarchies using a collapsible tree view,
   provided they use [Techquila's PSIs for hierarchies](http://www.techquila.com/psi/hierarchy/). Also
   shows the position of each topic within the hierarchies formed by these association
   types.
*  Now displays metadata about each topic map on the topic map page.
*  Now will display a name (and topic link) for an occurrence when there exists a topic representing
   the resource the occurrence links to.
*  XTM topic maps are now validated against the XTM DTD when loaded and validation errors cause the
   loading to be aborted.
*  There is a new index on the topic map page: index of nameless topics, which lists all topics which
   don't have a name. These are often caused by bad cross-references within a topic
   map.
*  Better error messages are displayed when topic map loading is stopped because of errors in an XML
   document, whether HyTM or XTM.

The following bugs have been fixed in the Omnigator:

*  [Bug #718](http://www.ontopia.net/bugs/showbug.cgi?id=718): Topic maps imported from LTM and
   exported using the export plug-in as XTM would get an .ltm file extension.
*  [Bug #700](http://www.ontopia.net/bugs/showbug.cgi?id=700): The `startup.bat` batch script would not
   work on Windows 98.
*  [Bug #669](http://www.ontopia.net/bugs/showbug.cgi?id=669): Full-text searches would fail whenever
   the ID of a topic map contained a whitespace.

#### Navigator Framework ####

The following changes have been made to the Navigator Framework:

*  There is now support for Servlets 2.2 and JSP 1.1. Note that to use JSP 1.1 the special TLD files
   supplied for that purpose in `${basedir}/config/jsp-11` must be used.
*  The `rulesfile` attribute was added to the `tm:tolog` tag. This attribute makes it possible to load
   tolog rules files in order to make the queries in them accessible to
   applications.
*  The `parameter` attribute was added to the `tm:lookup` tag. This attribute makes it possible to look
   up a topic map object from a request parameter.
*  The `to` attribute was added to the `tm:associated` tag. This attribute makes it easier to find the
   associations between two topics.
*  The `order` attribute was added to the `logic:foreach` tag. This attribute makes it possible to
   reverse the order in which the loop traverses the collection being iterated
   over.
*  The `value:sequence` tag has been added, which makes it possible to build collections in any desired
   order.
*  The `template:split` tag has been added, in order to support more advanced uses of templates.
*  Added the `stringifier` attribute to the `output:content` tag.
*  Added the `output:debug` tag.
*  Added the `NonExistentObject` exception, which is now thrown whenever an application is passed an
   invalid topic map object ID to a page. This allows applications to route the error to a page that
   displays the error in the way they want.
*  Added the `NoEscapeStringifier` class, which is useful for outputting strings containing special
   XML/HTML characters without escaping.

The following bugs have been fixed in the Navigator Framework:

*  [Bug #541](http://www.ontopia.net/bugs/showbug.cgi?id=541): Non-ASCII characters would not work in
   topic IDs.

#### Web Editor Framework ####

The Web Editor Framework is now out of beta and is now ready for use in applications. The Framework
has changed substantially since version 1.4.2, and has also been extended considerably. A large
number of bugs have been fixed, and in general the use of the Framework has been simplified
substantially.

The changes are sufficiently comprehensive that we have decided not to document them in detail. In
general, it can not be expected that applications developed with 1.4.2 will run with
2.0.

#### Engine ####

The following changes have been made to the Topic Map Engine:

*  Added the `net.ontopia.topicmaps.utils.rdf` package. This package provides support for RDF import
   and export. (See separate documents in the distribution for details.)
*  The `getStore` method has been added to the `TopicMapIF` interface.
*  Added `getAddIds` and `setAddIds` methods to the `XTMTopicMapWriter` class, in order to make it
   possible to control whether or not id attributes should be generated for elements which do not
   represent reified topic map constructs.
*  Added the `URLTopicMapStore` class, which supports downloading an individual topic map from a
   specified URL.
*  Added a `mergeInto` method to the `MergeUtils` class which merges a topic from one topic map into a
   different topic map.
*  Added the `remove` method to the `DeletionUtils` class. This method removes a topic map object from
   the containing topic map, regardless of what type of topic map object it might
   be.
*  Added the `removeTopic(TopicIF topic, boolean removeAssociations)` method to the `DeletionUtils`
   class. This method makes it possible to control whether associations the topic participates in are
   deleted or not.
*  Added the `clearTopicMap` method to the `DeletionUtils` class. This method removes the contents of a
   topic map.
*  Added the `deleteTopicMap` method to the `DeletionUtils` class. This method removes an entire topic
   map.
*  The `net.ontopia.products.license.LicenseInfo` command-line tool has been added. This tool shows
   where the OKS looks for the license key, and also where it finds the one it uses (if indeed it does
   find one).
*  Added a series of static `toString` methods to the `TopicStringifiers` class, which produce a name
   for a topic with various possible scope settings.
*  Added the `TMDeciderUtils` class, which can easily create various kinds of useful deciders.
*  The `getByType` method has been added to the `CharacteristicUtils` class. The method returns the
   first characteristic that is of a given type within a collection.
*  There have been more changes to the APIs, but these are considered to minor to be worthy of mention
   here.

The following bugs have been fixed in the Topic Map Engine:

*  [Bug #834](http://www.ontopia.net/bugs/showbug.cgi?id=834): The `getInstancesOf` method in
   `ClassInstanceUtils` was broken.
*  [Bug #827](http://www.ontopia.net/bugs/showbug.cgi?id=827): The `URILocator` class allowed '#'
   characters inside fragments.
*  [Bug #813](http://www.ontopia.net/bugs/showbug.cgi?id=813): The `mergeInto` method in `MergeUtils`
   that merges topics in the same topic map did not remove duplicates.
*  [Bug #750](http://www.ontopia.net/bugs/showbug.cgi?id=750): In certain rare cases `mergeMap`
   elements in XTM files could cause loading to fail.
*  [Bug #738](http://www.ontopia.net/bugs/showbug.cgi?id=738): The HyTM importer would lose source
   locators in cases where the same topic was represented by many `topic`
   elements.
*  [Bug #679](http://www.ontopia.net/bugs/showbug.cgi?id=679): '+' characters in file names would turn
   into spaces.
*  [Bug #672](http://www.ontopia.net/bugs/showbug.cgi?id=672): The `removeTopic` method in
   `DeletionUtils` would sometimes throw concurrent modification exceptions.

#### Query Engine ####

The following changes have been made to the Query Engine:

*  Now upgraded to support tolog 1.0. (See the tolog tutorial for information on the new functionality
   in tolog 1.0.)
*  Added the `execute(String query, Map params)` method to the `QueryProcessorIF` interface. This
   provides support for dynamic parameters to preparsed queries.
*  Added the `execute(Map params)` method to the `ParsedQueryIF` interface. This provides support for
   dynamic parameters to preparsed queries.
*  A number of optimizations have been introduced, which causes certain classes of queries to run
   dramatically much faster.

The following bugs in the Query Engine have been fixed:

*  [Bug #809](http://www.ontopia.net/bugs/showbug.cgi?id=809): The `count` aggregate function would
   also count duplicate results in the in-memory implementations.
*  [Bug #671](http://www.ontopia.net/bugs/showbug.cgi?id=671): Queries on n-ary associations with
   repeated roles would not work in the in-memory implementation.
*  [Bug #564](http://www.ontopia.net/bugs/showbug.cgi?id=564): The results of symmetric query
   predicates was not defined.

#### Schema Tools ####

The only changes to the Schema Tools have been the following bug fixes:

*  [Bug #721](http://www.ontopia.net/bugs/showbug.cgi?id=721): Validator would crash on references to a
   non-existent superclasses in schemas.

#### The RDBMS Backend ####

The RDBMS tolog implementation has been updated to support the full tolog 1.0. The following bugs
has been fixed:

*  [Bug #758](http://www.ontopia.net/bugs/showbug.cgi?id=758): Exceptions thrown in some rare
   situations because of problem with cache update.
*  [Bug #680](http://www.ontopia.net/bugs/showbug.cgi?id=680): Association role cache were sometimes
   out of sync with data store.
*  [Bug #692](http://www.ontopia.net/bugs/showbug.cgi?id=692): Transaction flushing was reentrant and
   caused infinite loop.
*  [Bug #681](http://www.ontopia.net/bugs/showbug.cgi?id=681): Transaction flusing was not
   synchronized.
*  [Bug #677](http://www.ontopia.net/bugs/showbug.cgi?id=677): Non-materialized fields written to
   database when they should not be.

### Older release notes ###

* [Ontopia 1.x release notes](whatsnew-1.html)

