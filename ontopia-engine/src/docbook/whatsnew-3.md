Ontopia
=======

What's new - Ontopia 3.x
------------------------

<p class="introduction">
This document describes what has changed in Ontopia between releases, both at a higher level, and in
more detail.
</p>

<span class="version">5.3.0 2013-08-01</p>

### OKS 3.4.5 release notes ###

This release is a bug fix release.

> **Warning**
> If you get the following error message when starting one of the web applications, then you must
> remove the DOCTYPE declaration from the top of the `actions.xml` file.

> `org.xml.sax.SAXParseException: Relative URI "actionConfig.dtd"; can not be resolved without a
> base URI.`

> This error occurs because of the XML parser cannot retrieve the DTD through the class loader.

#### General ####

The following change has been made:

*  [Bug #1962:](http://www.ontopia.net/bugs/showbug.cgi?id=1962) Reification support not implemented in
   TMXMLWriter.

#### Ontopoly ####

The following change has been made:

*  [Bug #2117:](http://www.ontopia.net/bugs/showbug.cgi?id=2117) Cannot remove role field with
   interface control not set

#### Omnigator ####

The following change has been made:

*  [Bug #1403:](http://www.ontopia.net/bugs/showbug.cgi?id=1403) Visualization of n-ary associations
   not intuitive

#### Navigator Framework ####

The following change has been made:

*  The resources pointed to by the `log4j_config`, `app_config` and `action_config` web.xml context
   parameters will be attempted loaded through the class loader if not found on the file system. One
   can also use the `file:` and `classpath:` URI schemes directly.
*  Module files referred to from `<logic:include>` will be loaded through the class loader if found
   there. Otherwise they will be loaded through the file system as before.

#### Web Editor ####

The following change has been made:

*  `<tolog:foreach>` tags failed on weblogic container when nested three or more levels. This has now
   been fixed.

#### RDBMS ####

The following change has been made:

*  The SQL statement used for full-text searches on MySQL was semantically incorrect and caused an
   incorrect result to be returned.

### OKS 3.4.4 release notes ###

This release is a bug fix release.

#### General ####

The following change has been made:

*  [Bug #2111:](http://www.ontopia.net/bugs/showbug.cgi?id=2111) Images in the documentation did not
   show up in Internet Explorer

#### Query Engine ####

The following change has been made:

*  The query processor will now write warnings as stack traces to the log4j log if the time and memory
   spent executing a query exceed 3 seconds or 5 MB. This is done in the DEBUG logging
   level.

#### Ontopoly ####

The following changes have been made:

*  [Bug #2035:](http://www.ontopia.net/bugs/showbug.cgi?id=2035) The link button did not work with URLs
   containing ampersands.
*  [Bug #2036:](http://www.ontopia.net/bugs/showbug.cgi?id=2036) Instances of abstract topic types can
   now been seen.
*  [Bug #2111:](http://www.ontopia.net/bugs/showbug.cgi?id=2111) Some of the sample topic maps were
   missing hierarchical declarations.
*  [Bug #2114:](http://www.ontopia.net/bugs/showbug.cgi?id=2114) Topic maps with no name did not show
   up on start page.

#### RDBMS Backend ####

The following changes have been made:

*  Improved the performance of the tolog predicates direct-instance-of($X,foo) and instance-of($X,foo).
   This was done by using a less expensive SQL query.
*  The RDBMSPatternSingleTopicMapSource now returns empty list of references instead of a collection
   with a null element when no topic map was found.

### OKS 3.4.3 release notes ###

This release is a primarily a bug fix release, but it also introduces a much requested extension to
the event model in the topic map engine.

#### Engine ####

The following changes have been made:

*  The Topic Maps engine now has support for topic modification events. The `TopicMapListenerIF`
   interface now has a new method called `objectModified(TMObjectIF)` which will be called when a topic
   has been modified.
*  A base topic map listener class called `AbstractTopicMapListener` has been introduced. This abstract
   class contains default implementations of all the callback methods. The class was primarily
   introduced so that listener implementations extending it can continue to work even though new
   methods get added to the listener interface in the future.

#### Navigator ####

The `omnigator`, `manage` and `i18n` web applications was updated to use the new tag library URIs.
This means that they will no longer use the `.tld` files in the `WEB-INF/jsp`
directories.

#### Ontopoly ####

The following bugs have been fixed:

*  The create button was duplicated on each row on fields with repeated values. This was changed so
   that the create button now only shows up on the last row.
*  Fixed bug that caused create buttons to show up even though hideCreateButtons property was set.
*  Specified the max-width CSS property on drop-down lists to 500px to prevent very wide pages.
*  Fixed bug that caused the last association in a many-cardinality field to be replaced when selecting
   a topic through the search or browse popup windows. The correct behaviour is to instead add a new
   association.
*  [Bug #2080:](http://www.ontopia.net/bugs/showbug.cgi?id=2080) Illegal occurrence fields cannot be
   deleted.

#### Classify ####

The following bugs have been fixed:

*  Improved presentation performance by changing one of the tolog queries used by the implementation.
   The user-interface should now be much more responsive.
*  Fixed bug that caused association types to not be shown on subtypes of the type that declared the
   association field.

#### Vizigator ####

The following bugs have been fixed:

*  [Bug #2076:](http://www.ontopia.net/bugs/showbug.cgi?id=2076) Graphs do not draw correctly when
   double click operation is done on nodes.
*  [Bug #2106:](http://www.ontopia.net/bugs/showbug.cgi?id=2106) Runtime Exception message on Java
   console in Vizlet

### OKS 3.4.2 release notes ###

This release is a bug fix release.

#### Admin console ####

The following bugs have been fixed:

*  [Bug #2101:](http://www.ontopia.net/bugs/showbug.cgi?id=2101) JSP compile error on Manage page

#### Ontopoly ####

The following bugs have been fixed:

*  [Bug #2045:](http://www.ontopia.net/bugs/showbug.cgi?id=2045) Create type and instance buttons on
   topic type config are hidden if the topic type is locked

#### Topic Maps Engine ####

The following constructors of `XTMTopicMapWriter` have been changed so that they are now declared to
throw `IOException`: `XTMTopicMapWriter(File file, String encoding)` and `XTMTopicMapWriter(Writer
writer, String encoding)`. Note that this may cause compilation errors.

The following bugs have been fixed:

*  [Bug #2098:](http://www.ontopia.net/bugs/showbug.cgi?id=2098) Writers close streams passed in from
   outside

#### Query Engine ####

The following bugs have been fixed:

*  [Bug #2102:](http://www.ontopia.net/bugs/showbug.cgi?id=2102) Incorrect type inferencing in
   comparison predicates

#### RDBMS backend ####

The experimental `RDBMSPatternSingleTopicMapSource` class was added in the
`net.ontopia.topicmaps.impl.rdbms` package. This source can be used to swap topic maps used by an
implementation at runtime.

The `--title` and `--comment` options for setting the title and comments of a topic map were added
to the `RDBMSImport` command-line utility.

### OKS 3.4.1 release notes ###

This is a bug fix release. If you are using Ontopoly then it is important that you do an upgrade as
this release fixes a serious problem with character encoding of form data in Ontopoly. This release
includes a few more bug fixes as well.

#### Ontopoly ####

The following bugs have been fixed:

*  [Bug #2096](http://www.ontopia.net/bugs/showbug.cgi?id=2096): Ontopoly obfuscates Unicode
   characters.
*  [Bug #2094](http://www.ontopia.net/bugs/showbug.cgi?id=2094): Export plugin closes JSP output stream
   closed too early.

#### Omnigator ####

The following bugs have been fixed:

*  [Bug #2094](http://www.ontopia.net/bugs/showbug.cgi?id=2094): Export plugin closes JSP output stream
   closed too early.

#### Engine ####

The following bugs and improvements have been made:

*  [Bug #2095](http://www.ontopia.net/bugs/showbug.cgi?id=2095): `MergeUtils` does not replace name
   types.
*  `MergeUtils` did not always compare name objects correctly. This could lead to duplicate names in
   some situations.
*  Added `TopicMapSourceIF.supportsDelete()` method, which must be true for it to be valid to call
   `TopicMapReferenceIF.delete()`. This replaces the old deleteFiles property on topic maps
   sources.
*  Added `TopicMapRepositoryIF.createStore(String, boolean)` as a convenience method.
*  `TopicMapSynchronizer` class no longer produces duplicate characteristics on synchronized topics.

### OKS 3.4 release notes ###

The main change in this version of the OKS is various upgrades to Ontopoly, especially the improved
locking support, and the new approach to sharing topic maps between web
applications.

There is also a new OKS distribution, called Ontopoly Runtime, which is essentially OKS Samplers
with the RDBMS backend and support for authentication. This means that the new distribution can be
used by customers which want to use Ontopoly to create topic maps without developing new custom
applications. The price also reflects this. See [the OKS price
list](http://www.ontopia.net/solutions/pricelist.html) for more information.

#### Ontopoly ####

The following improvements have been made:

*  Some changes have been made to the Ontopoly schema. The new schema version is 1.2, so Ontopoly will
   ask you to upgrade your existing topic maps.
*  Users can now manually unlock topics locked by other users, and they can see who has locked the
   topic and when, making it easier to decide if they can safely unlock the topic.
*  Ontopoly will now automatically unlock the currently edited topic in many more cases than it did
   previously. It is no longer necessary to submit the form to unlock a topic; just following normal
   links will also unlock the current topic.
*  Ontopoly now displays a formatting hint for date values, to make it easier for users to remember
   what the date format is.
*  The instance editing page now has a "Copy" button for creating an identical copy of the current
   topic. This can be convenient when creating many similar topics.
*  It is now possible to specify whether the search dialog used for finding topics to create new
   associations should open in search mode or browse mode.
*  The default locale used by the web application is now `en` instead of relying on the fallback locale
   given by the web browser.

#### Omnigator ####

The following improvements have been made:

*  A new topic map index plug-in has been added, so that there is now always a link back to the topic
   map index page from all other pages.
*  The Plug-ins plug-in now displays the plug-ins menu (yes, it is true).

#### Auto-classification ####

The recognition of compound terms (terms consisting of more than one word, like "Topic Maps") has
been improved, as has the scoring of these terms.

The following bugs have been fixed:

*  [Bug #2082](http://www.ontopia.net/bugs/showbug.cgi?id=2082): Norwegian stop words with special
   characters not recognized

#### Navigator Framework ####

The Navigator Framework has been changed to use a new and much simpler API for sharing topic maps
between applications. Please see the *Navigator Framework Configuration Guide* for details. The API
has also been simplified, and a number of classes made INTERNAL.

The following bugs have been fixed:

*  The JAAS topic maps module was missing some prefix declarations in one of its tolog queries, causing
   it to throw an exception in some situations as the query was sensitive to the base address of the
   topic map used.
*  The JNDI lookup of shared topic maps on OC4J was broken.

#### Web Editor Framework ####

The `readonly` attribute has been introduced on many tags to allow applications to control whether
or not forms and form controls should be read only.

#### Topic Maps Engine ####

The following improvements have been made:

*  A new class called `TopicMaps` has been added to the `net.ontopia.topicmaps.entry` package. This
   class can be used to access topic maps via `tm-sources.xml` files in a simple and straightforward
   way. Please see the javadoc and the *Navigator Framework Configuration Guide* for
   details.
*  The `XTMFragmentExporter` class is now PUBLIC.
*  The `XMLConfigSource` class (and effectively tm-sources-xml) now uses `<repository>` as the document
   element as this is more consistent with the naming of the topic maps repository. See the included
   tm-sources.xml for an example. The old syntax is still supported.

The following bugs have been fixed:

*  [Bug #2031](http://www.ontopia.net/bugs/showbug.cgi?id=2031): Exporting and reimporting via XTM
   breaks reification

#### Vizigator ####

The performance of the Vizlet has been improved substantially by changing how the Vizlet loads Topic
Maps fragments from the server. The result is less network traffic and a more responsive
Vizlet.

The following bugs have been fixed:

*  There was a bug in the test suite that failed on the first attempt, but worked on subsequent
   invocations. This was caused by a directory not having been created initially.
*  [Bug #2081](http://www.ontopia.net/bugs/showbug.cgi?id=2081): Go to topic does not work on topics
   with special characters in name
*  [Bug #2073](http://www.ontopia.net/bugs/showbug.cgi?id=2073): Node cannot be expanded after
   collapse

#### Query Engine ####

The `source-locator` predicate has been renamed to `item-identifier`. The old predicate will
continue to work, but it is no longer documented.

#### RDBMS Backend ####

The following improvements have been made:

*  The database properties file will now be attempted loaded from the class loader if not found on the
   file system. Loading files through the class loader is much more portable and is recommended for
   most appliations. In practice this means that the database properties file can be copied to one of
   the directories on the CLASSPATH, and that the reference to it can be just the name of the
   file.
*  The connection URLs in the database properties files `db.sqlserver.props` and `db.mysql.props` have
   been updated to reflect the latest JDBC driver releases.
*  Most of the more rare and mostly unused properties have been removed from the default database
   properties files. Note that the properties can still be used; they are just not included in the
   files, so that the default values is used instead.
*  The RDBMS Backend Installation Guide now mentions that the Row Versioning and Snapshot Isloation
   features must be enabled on Microsoft SQL Server 2005. These two features are essential for good
   performance in multi-user applications.
*  The columns `TM_OCCURRENCE.content`, `TM_BASE_NAME.content`, `TM_VARIANT_NAME.content` and
   `TM_TOPIC_MAP.comments` have been extended from 2000 to 4000 characters on Microsoft SQL Server 2005
   as this is the maximum length supported on `nvarchar` columns.

#### DB2TM ####

The following improvements have been made:

*  DB2TM now looks up relations qualified by schema name before using the default schema in JDBC
   datasources. This change adds support for explicit database schemas.

### OKS 3.3 release notes ###

The main new addition in this release is the autoclassification module, which is still experimental.
For more about this, see [Automatic classification](#automatic-classification). There are also some
new features in the Vizigator and a new string module in tolog, but beyond that the changes are
mostly bug fixes.

#### Engine ####

The following changes have been made to the Engine:

*  commons-dbcp.jar has been upgraded to version 1.2.2.
*  All methods relating to nested transactions have been deprecated as none of the current Topic Maps
   implementations actually support nested transactions.
*  [Bug #2034](http://www.ontopia.net/bugs/showbug.cgi?id=2034): jena.jar includes log4j.properties

#### Query Engine ####

The following changes have been made to the Query Engine:

*  tolog has been extended with a module containing predicates for string operations. This provides
   operations for working with substrings, concatenating strings, etc. See the tolog predicate
   reference for more information.

#### Navigator Framework ####

The following changes have been made to the Navigator Framework:

*  Upgraded Apache Tomcat to version 5.5.23.
*  Web applications will now use default shared-in-JVM topic map repository if neither
   'jndi_repository' nor 'source_config_file' parameters are specified in web.xml. This was done to
   lower the chances of running with multiple repositories by mistake. It also makes configuration
   easier as no configuration is required by default. We recommend that you remove the parameters from
   all your existing web applications.
*  Improved error message thrown when accessing variables outside of <tolog:context>.
*  [Bug #2058](http://www.ontopia.net/bugs/showbug.cgi?id=2058): sequence-last not always correctly set
   in <tolog:foreach>
*  [Bug #2037](http://www.ontopia.net/bugs/showbug.cgi?id=2037): AXIS reports error in tomcat.log on
   startup
*  Upgraded bundled Axis2 version to 1.2.

#### RDBMS Backend ####

*  Improved detection of deleted object when iterating over collections. This means that
   IdentityNotFoundException is no longer thrown that often while iterating over collections of topic
   map objects.

#### Omnigator ####

The following changes have been made to the Omnigator:

*  XTM validation is now enabled by default.
*  No longer supports refresh topic map registry as this is no longer supported in the framework. Use
   refresh sources instead.
*  Added back the plugins plug-in that seems to have been lost in an older release.
*  [Bug #2039](http://www.ontopia.net/bugs/showbug.cgi?id=2039): Names types incorrectly displayed

#### Ontopoly ####

The following changes have been made:

*  Support for automatic classification has been added, as a plug-in on the instance editing page. See
   the Ontopoly User's Guide for more information, as well as [Automatic
   classification](#automatic-classification).
*  The display of the add, create, and search buttons on the instance page has been made more
   consistent and intuitive. The functionality is the same as before.
*  [Bug #1854](http://www.ontopia.net/bugs/showbug.cgi?id=1854): Some topics become both types and
   instances on import

#### Vizigator ####

The following new features have been added to the Vizigator:

*  The filter controls previously only found in VizDesktop can now also be accessed in Vizlet.
*  A new applet parameter `wallpaper_image` has been added, which makes it possible to display a
   background image in the Vizlet.
*  Both VizDesktop and Vizlet now support undo and redo.
*  [Bug #2060](http://www.ontopia.net/bugs/showbug.cgi?id=2060): Opening non-existent file causes NPE
*  [Bug #2061](http://www.ontopia.net/bugs/showbug.cgi?id=2061): Association type filter doesn't show
   state

#### DB2TM ####

*  Added servlet `net.ontopia.topicmaps.db2tm.SynchronizationServlet` that can be used to schedule
   regular DB2TM synchronizations. See the javadoc for more information.

#### Automatic classification ####

This module has been added, but in an experimental state. It is only accessible through a plug-in to
Ontopoly, and is described in the Ontopoly User's Guide. There is no publicly available API at the
moment, but an API exists, and will be made available once we have received more feedback regarding
customer requirements. Supported languages are English and Norwegian, but support for more languages
can be added.

### OKS 3.2.4 release notes ###

This release is a bug fix release.

#### Engine ####

*  Added `net.ontopia.topicmaps.utils.ltm.LTMTemplateImporter` class that makes it possible to import
   LTM fragments outside of the Web Editor Framework.
*  [Bug #2028](http://www.ontopia.net/bugs/showbug.cgi?id=2028): Fixed NullPointerException issue in
   XTM exporter
*  [Bug #2034](http://www.ontopia.net/bugs/showbug.cgi?id=2034): Removed log4j.properties file from
   jena.jar

#### Query Engine ####

*  Tolog query parser will now throw `net.ontopia.topicmaps.query.core.BadObjectReferenceException`
   instead of `net.ontopia.topicmaps.query.core.InvalidQueryException` when object references in the
   query cannot be resolved.
*  [Bug #2029](http://www.ontopia.net/bugs/showbug.cgi?id=2029): Recursive rule gave wrong result

#### DB2TM ####

*  Non-primary topic entities in relations now get all their matching characteristic values cleared
   when the row is removed. In practice this means that changelog synchronization is not possible with
   non-primary topic entities that are stored in multiple rows.

### OKS 3.2.3 release notes ###

This release is a bug fix release. If you are using the Web Editor Framework or DB2TM then an
upgrade is recommended.

#### Engine ####

*  Two new methods have been added to the TopicMapStoreIF interface. The first one is
   `getProperty(String)` which returns the value of the given property. This method can be used to get
   hold of topic map store property values in an implementation independent manner. The second method
   is `getImplementation()` that returns a constant that identifies the topic map implementation. This
   is useful to easily figure out if the topic map is stored in-memory or in an
   rdbms.
*  The third party jar-files antlr.jar and commons-collections.jar have been upgraded to versions 2.7.7
   and 3.2 respectively.
*  [Bug #2009](http://www.ontopia.net/bugs/showbug.cgi?id=2005): XTMFragmentExporter did not respect
   filter settings.

#### Query Engine ####

*  [Bug #2019](http://www.ontopia.net/bugs/showbug.cgi?id=2019): NullPointerException thrown from rule
   inliner

#### Navigator ####

*  log4j logging pattern in tomcat.log is now the same as in ontopia.log. This means that timestamps
   are now included in both log files.

#### Web Editor ####

*  [Bug #2013](http://www.ontopia.net/bugs/showbug.cgi?id=2013): lack of synchronization in user
   object
*  [Bug #2021](http://www.ontopia.net/bugs/showbug.cgi?id=2021): Uniqueness of action ids not
   guaranteed

#### Omnigator ####

*  [Bug #1788](http://www.ontopia.net/bugs/showbug.cgi?id=1788): Full-text search not available on
   RDBMS topic maps

#### Ontopoly ####

*  [Bug #2002](http://www.ontopia.net/bugs/showbug.cgi?id=2002): Error message given if topic map was
   not reified

#### DB2TM ####

*  Improved detection of discrepancies between changelog table and data in actual table.
*  Fixed bug that caused some columns to be null when read through changelog table.
*  Rescan will now replace the topic type(s) instead of just adding a new one(s).
*  Added --force-rescan command line option to db2tm.Execute.

### OKS 3.2.2 release notes ###

This release is a small bug fix release.

#### Engine ####

*  Most of the no-argument builder methods in TopicMapBuilderIF have been deprecated because they were
   no longer useful.
*  [Bug #2009](http://www.ontopia.net/bugs/showbug.cgi?id=2009): XTMFragmentExporter did not respect
   filter settings.

#### Web Editor ####

*  [Bug #1997](http://www.ontopia.net/bugs/showbug.cgi?id=1997): Bad error message when topic had no
   type.

#### DB2TM ####

*  Rescan now updates the synchronization state if a changelog is declared.
*  Fixed problem with NullPointerException when attempting to run duplicate suppression on deleted
   topics.
*  [Bug #2007](http://www.ontopia.net/bugs/showbug.cgi?id=2007): Changelog query triggered exception in
   older Oracle versions.

### OKS 3.2.1 release notes ###

This release is primarily a bug fix and improvements release, but some new functionality have been
added. It contains a couple of important bug fixes, so an upgrade is recommended. Java 1.6 support
has been verified.The main new features are the new management web application and native full-text
support for PostgreSQL and Microsoft SQL Server.

#### Query Engine ####

*  The dynamic association predicate has been optimized. Tests have indicated a 15% performance
   increase.
*  Tolog now supports locale specific sorting. This feature is disabled by default, but it can be
   enabled by specifying the property `net.ontopia.topicmaps.query.core.QueryProcessorIF.locale`. The
   properties file will be used by default, but it can also be specified as a system property. The
   value consists of three parts, e.g. `no_NO_NO`, where the language, country and variant codes are
   separated by the underscore character. The ordering is implemented in terms of a
   `java.text.Collator`.
*  [Bug #1991](http://www.ontopia.net/bugs/showbug.cgi?id=1991): Nested `tolog:foreach` with `groupBy`
   attribute triggered exception.
*  [Bug #1992](http://www.ontopia.net/bugs/showbug.cgi?id=1992): occurrence predicate had side-effects
   when used in OR branch.
*  [Bug #2001](http://www.ontopia.net/bugs/showbug.cgi?id=2001): NullPointerException thrown in dynamic
   association predicate method.
*  [Bug #2003](http://www.ontopia.net/bugs/showbug.cgi?id=2003): topicmap predicate failed with RDBMS
   tolog.

#### RDBMS Backend ####

*  [Bug #2004](http://www.ontopia.net/bugs/showbug.cgi?id=2004): Potential deadlock in finalizer
   method.

#### Engine ####

*  `DuplicateSuppressionUtils` now does prefetching of characteristics when applicable. This change
   leads to a huge performance improvement, especially with large topic maps.
*  Reduced memory footprint when doing duplicate suppression.
*  Prefetching is no longer performed when the shared cache is disabled.
*  `getTopicMap(ServletRequest request)` method has been added to the ContextUtils class. This makes it
   easy to get hold of the current topic map object from the parent `tolog:context`
   tag.
*  [Bug #1996](http://www.ontopia.net/bugs/showbug.cgi?id=1996): Subject indicators sometimes lost
   during XTM import.

#### DB2TM ####

*  Improved reuse of topic maps objects when tuples change. This leads to huge performance improvements
   when synchronizing.
*  Defaulting of the `primary` attribute did not work as intented.
*  A new FAQ section has been added to the User's Guide document.
*  [Bug #1989](http://www.ontopia.net/bugs/showbug.cgi?id=1989): DB2TM crashed if row didn't have
   enough column values
*  [Bug #2000](http://www.ontopia.net/bugs/showbug.cgi?id=2000): Reuse of occurrence objects failed
   when changing from external to internal occurrences (and vice versa).

#### Navigator ####

*  The manage plugin in the Omnigator has been refactored into a new web application. All management of
   resources is now done through this application. This new web application introduces a couple of new
   features including JDBC profiling, topic map statistics and shared cache
   statistics.
*  [Bug #1994](http://www.ontopia.net/bugs/showbug.cgi?id=1994): Omnigator did not display typed names
   correctly.

#### Full-text ####

*  Native full-text support has been added for PostgreSQL using tsearch2. The support can be enabled by
   setting the `net.ontopia.infoset.fulltext.impl.rdbms.RDBMSSearcher.type` property to
   `postgresql`.
*  Native full-text support has been added for Microsoft SQL Server. The support can be enabled by
   setting the `net.ontopia.infoset.fulltext.impl.rdbms.RDBMSSearcher.type` property to
   `sqlserver`.

#### Vizigator ####

*  Neighbouring circle feature has been added. This is a visual effect to draw the attention to a node
   and its immediate neighbours.
*  [Bug #1769](http://www.ontopia.net/bugs/showbug.cgi?id=1769): Increment/decrement of locality
   changed visible links.
*  [Bug #1771](http://www.ontopia.net/bugs/showbug.cgi?id=1771): Vizlet graph placement dependent on
   browser window size.
*  [Bug #1984](http://www.ontopia.net/bugs/showbug.cgi?id=1984): Association node could not be
   collapsed.

#### Ontopoly ####

*  [Bug #1999](http://www.ontopia.net/bugs/showbug.cgi?id=1999): Could not change the height and width
   of occurrence type fields..

### OKS 3.2 release notes ###

The main change in OKS 3.2 is that Ontopoly has been significantly extended with new functionality,
described below, and that the DB2TM module now supports updating the topic map with changes from the
source database. In addition, there is a long list of minor improvements and bug fixes to various
parts of the product suite.

#### Ontopoly ####

The modifications to Ontopoly are quite extensive, and have required changes to the Ontopoly
meta-ontology. This means that all old topic maps have to be upgraded in order to make them editable
in the new Ontopoly version. Ontopoly can handle this by itself, however. The main improvements
are:

*  Extension of the search popup dialog to also support browsing lists of all instances of a particular
   topic type for topics to create associations to. The dialog also allows the user to create multiple
   associations in a single operation.
*  Support for hierarchical associations, and using these to create hierarchies for topics of a
   particular type. This means that Ontopoly now supports taxonomies, but also hierarchies of other
   types can also be created. The hierarchies can be seen both on the page listing all instances of a
   particular type, as well as in the search popup dialog.
*  Ontopoly can now also treat ontology topics (that is, topic types, association types, etc) as though
   they were normal topics. This means that new fields can be added for these topics, and they can be
   edited in the instance editor. This makes it possible to use Ontopoly for ontology
   annotation.
*  A number of performance optimizations have been made, to ensure that Ontopoly scales to large data
   sets. Users have also been given more control over this, via the "Large instance set" property on
   topic types, which turns off the ability to list all instances of these types.
*  The Ontopoly instance editor is now localizable (meaning that it can be translated to any
   language).
*  Ontopoly can now export topic maps to the TM/XML syntax.
*  Ontopoly now uses Navigator Framework plug-ins for many of the user interface elements, which means
   that
*  Ontopoly now has two new privileges: instance-reader and ontology-reader, which allows read-only
   users to be created.

The following bugs have been fixed in Ontopoly:

*  [Bug #1971](http://www.ontopia.net/bugs/showbug.cgi?id=1971): Couldn't leave export page after doing
   export
*  [Bug #1939](http://www.ontopia.net/bugs/showbug.cgi?id=1939): Search doesn't work on topicType.ted
*  [Bug #1849](http://www.ontopia.net/bugs/showbug.cgi?id=1849): Search for empty string finds all
   topics
*  [Bug #1740](http://www.ontopia.net/bugs/showbug.cgi?id=1740): Search returns duplicates
*  [Bug #1906](http://www.ontopia.net/bugs/showbug.cgi?id=1906): Search crashes if string contains *
*  [Bug #1904](http://www.ontopia.net/bugs/showbug.cgi?id=1904): Search dialog box too small on MSIE
*  [Bug #1884](http://www.ontopia.net/bugs/showbug.cgi?id=1884): Description page shows no warning when
   locked
*  [Bug #1843](http://www.ontopia.net/bugs/showbug.cgi?id=1843): System topics can be found by search
   and edited

#### Vizigator ####

The Vizigator has been extended slightly with two new pieces of functionality, described below. Note
that it was also substantially extended in the OKS 3.1.1 release; you may want to check
[Vizigator](#vizigator) as well.

*  There is now support for stopping graphs which vibrate endlessly.
*  There is now support for making all nodes sticky (or unsticky) in a single operation.

The following bugs have been fixed in the Vizigator:

*  [Bug #1930](http://www.ontopia.net/bugs/showbug.cgi?id=1930): Search only finds hits in visible
   names

#### Omnigator ####

The "No name topics" choice on the topic map page now always appears, and without a count of
nameless topics. This is a small step backwards in terms of functionality, but this query is very
expensive, and has made it difficult to use the Omnigator with large topic maps. This problem is now
gone.

The following bugs have been fixed in Omnigator:

*  [Bug #1894](http://www.ontopia.net/bugs/showbug.cgi?id=1894): Stylesheet indentation problem
*  [Bug #1853](http://www.ontopia.net/bugs/showbug.cgi?id=1853): Tomcat does not work with JRE on
   Windows

#### Query Engine ####

The tolog query engine now has support for plugging in external full-text search predicates that
behave much like the current built-in "value-like" predicate. To create a new full-text search
predicate, one only needs to implement two Java interfaces. The new predicate will then be available
in a module which can be imported into queries that need it.

The query engine now also supports getting a trace from a specific query as a string, instead of
logging it to log4j. This support is available at the API level, and also in the Omnigator query
plug-in. This makes it much easier to debug slow tolog queries.

The following bugs have been fixed in the query engine:

*  [Bug #1893](http://www.ontopia.net/bugs/showbug.cgi?id=1893): Incorrect sort order with typed names

#### DB2TM ####

The main extension to DB2TM is the support for synchronizing the topic map with new information from
the database. This makes it easy to keep the topic map up to date as the source database changes.
See the documentation for more details on how this works.

The DB2TM module has been extended with support for mapping values in the database to new values
using either a mapping table or a Java method. These mappings can now be declared in the XML mapping
file.

#### Web Editor Framework ####

The `webed:link` and `webed:form` tags now have a new attribute, `target`, which tells the framework
in which window/frame to open the next page.

The following bugs have been fixed in the Web Editor Framework:

*  [Bug #1500](http://www.ontopia.net/bugs/showbug.cgi?id=1500): `param` must be set on `webed:list`
   for multiple select lists
*  [Bug #1776](http://www.ontopia.net/bugs/showbug.cgi?id=1776): Evaluate LTM action crashes if no
   topic map parameter given
*  [Bug #1760](http://www.ontopia.net/bugs/showbug.cgi?id=1760): No output when `webed:field` is empty
*  [Bug #1756](http://www.ontopia.net/bugs/showbug.cgi?id=1756): Crash when `params` attribute contains
   whitespace at beginning or end
*  [Bug #1869](http://www.ontopia.net/bugs/showbug.cgi?id=1869): actions attached to `webed:list` with
   `unspecified=none` run even if no changes made
*  [Bug #1857](http://www.ontopia.net/bugs/showbug.cgi?id=1857): package names for actions missing in
   documentation
*  [Bug #1848](http://www.ontopia.net/bugs/showbug.cgi?id=1848): poor error message on missing
   actions.xml file

#### Navigator Framework ####

The `TMLoginModule` now exposes user-groups/roles names and privilege names as role principals. This
means that applications can make use of the `HttpServletRequest.isUserInRole(String rolename)`
method to check privileges and user-group memberships.

#### Topic Maps Engine ####

The TMSync implementation has been extended so that it now supports using filter on the source topic
map as well, to filter the characteristics to be included in the
synchronization.

The `DeletionUtils` class now has a new method for deleting a collection of topics in a single
operation. This is much faster than deleting the topics individually.

The TM/XML exporter now accepts a filter that lets you control which parts of the topic map should
be exported.

The `TopicMapSourceIF` interface now has a new method `createTopicMap`, which allows new topic maps
to be created within the source. Some of the source implementations support this method, and there
is a `getSupportsCreate` method which can be used to query for such support.

The XTM path source now has a boolean property to control whether topic references to external
documents should cause those documents to be merged in. This is implemented using a new class
`net.ontopia.topicmaps.utils.NoFollowTopicRefExternalReferenceHandler` which tells the XTM importer
to not follow external topic references, but to still load any topic maps referenced by the
`mergeMap` element.

The `Merger` command-line utility now has an option to enable name-based merging (which used to be
enabled, because of a bug), and an option to do duplicate suppression on the resulting topic
map.

The following bugs have been fixed in the Topic Maps Engine:

*  [Bug #1933](http://www.ontopia.net/bugs/showbug.cgi?id=1933): TM/XML namespace prefixes not
   consistent
*  [Bug #1864](http://www.ontopia.net/bugs/showbug.cgi?id=1864): Collection concurrency exceptions not
   documented
*  [Bug #1827](http://www.ontopia.net/bugs/showbug.cgi?id=1827): LTM exporter creates numeric IDs
*  [Bug #1850](http://www.ontopia.net/bugs/showbug.cgi?id=1850): LTM importer can crash on subject
   identifier/source locator collision
*  [Bug #1868](http://www.ontopia.net/bugs/showbug.cgi?id=1868): XTM reader crashes on empty topic
   references

#### RDBMS Backend ####

A new property `net.ontopia.topicmaps.impl.rdbms.Cache.shared.identitymap.lru` has now been added,
which can be used to control the size of the identity map LRU for the shared cache independently
from the LRUs of the individual transactions.

Another new property is `net.ontopia.infoset.fulltext.impl.rdbms.RDBMSSearcher.type` which is used
to control how full-text searching in the RDBMS backend is done.

A new option has been added to the `RDBMSImport` command-line tool, to control whether external
references are followed or not.

The RDBMS backend now always sets the transaction isolation level, which it only did before when
using a connection pool.

### OKS 3.1.2 release notes ###

This release primarily consists of some new functionality and a couple of bug-fixes for Vizigator.

#### Vizigator ####

The following functionality has been added to Vizigator:

*  It is now possible to hide the search bar by setting the `search.bar` menufile property to `off`.

For more information, please consult the Vizigator User's Guide.

The following bugs have been fixed in Vizigator:

*  [Bug #1890](http://www.ontopia.net/bugs/showbug.cgi?id=1890): Hide edge menu couldn't be controlled
*  [Bug #1891](http://www.ontopia.net/bugs/showbug.cgi?id=1891): Disappearing popup menu

#### Web Editor Framework ####

A problem regarding passing java.util.Map instances as parameters to web editor actions was
resolved.

### OKS 3.1.1 release notes ###

This release primarily consists of some new functionality for Vizigator, and support for Microsoft
SQL Server. There is also improved support for TM/XML in Omnigator.

#### Topic Maps Engine ####

The following bugs have been fixed:

*  [Bug #1795](http://www.ontopia.net/bugs/showbug.cgi?id=1795): TMAPI implementation bug
*  The server-config.wsdd file in the TMRAP web application contained an absolute file reference. This
   has now been replaced by a relative file reference.
*  One of the examples in the TMRAP Developer's Guide used an incorrect request parameter.

#### Vizigator ####

The following functionality has been added to Vizigator:

*  A new (optional) locality algorithm has been added.
*  The popup menu items in VizLet have been made configurable.
*  The locality settings (initial setting, and maximum value) in VizLet have been made configurable.
*  On topic nodes there is now a new "Copy name" menu item.

For more information, please consult the Vizigator User's Guide.

The following bugs have been fixed in Vizigator:

*  [Bug #1870](http://www.ontopia.net/bugs/showbug.cgi?id=1870): Some edges not shown

#### RDBMS backend ####

The RDBMS backend has seen substantial internal reworkings that cause it to need fewer database
connections per transaction, and which reduce lock contention in the database quite significantly.
As a result of these changes the RDBMS backend now supports Microsoft SQL Server. A sample database
properties file and database scripts can be found in `${OKS_HOME}/rdbms`.

#### Omnigator ####

It's now possible to drop TM/XML files with the extension .tmx into the `topicmaps` directory and
have Omnigator detect them. It is also possible to topic maps to TM/XML in Omnigator using the
export plug-in.

### OKS 3.1.0 release notes ###

This release adds significant new functionality, primarily the DB2TM module and the new TMRAP
version. However, the Vizigator has also been significantly improved, support for the TM/XML format
has been added throughout the OKS, and an event API has been added to the engine. The improvements
are described in more detail below.

Documentation of the two new modules (DB2TM and TMRAP) can be found as part of the OKS
documentation.

#### Vizigator ####

The Vizigator has been extended with support for a default topic and association type configuration,
as well as for controlling the rendering of topics which have more than one
type.

The following bugs have been fixed in the Vizigator:

*  [Bug #1767](http://www.ontopia.net/bugs/showbug.cgi?id=1767): Filter changes are very slow
*  [Bug #1768](http://www.ontopia.net/bugs/showbug.cgi?id=1768): Hide node only hides incident edges

#### Topic Maps Engine ####

The main improvements to the Topic Maps engine are the addition of support for the TM/XML Topic Maps
syntax and the event API. The event API is documented in the javadoc for the package
`net.ontopia.topicmaps.core.events`.

The following bugs have been fixed in the Topic Maps engine:

*  [Bug #1797](http://www.ontopia.net/bugs/showbug.cgi?id=1797): RDF exporter crashes on null
   occurrences

#### The RDBMS backend ####

A number of performance improvements have been made to the RDBMS backend. The RDBMS backend now
distinguishes between read-only and read-write transactions, where the former can be shared by
several users (because no changes can be made), thus reducing the memory consumption. In addition,
the use of synchronization has now been streamlined to allow better throughput with many concurrent
transactions. The garbage collector now has an easier job because the soft references in the shared
cache are more fine-grained, making it possible to collect smaller amounts of data on each
iteration.

The default tolog implementation when using the RDBMS backend is now the in-memory implementation,
and not the RDBMS implementation. Note that this change might affect the performance of existing
installations. Existing installations may want to override the tolog implementation in the database
properties file.

The following bugs have been fixed in the RDBMS backend:

*  [Bug #1818](http://www.ontopia.net/bugs/showbug.cgi?id=1818): Query cache corruption when shared
   cache disabled
*  [Bug #1792](http://www.ontopia.net/bugs/showbug.cgi?id=1792): Shared cache corruption
*  [Bug #1764](http://www.ontopia.net/bugs/showbug.cgi?id=1764): Clearing cache causes error
*  [Bug #1777](http://www.ontopia.net/bugs/showbug.cgi?id=1777): XTM export causes
   OutOfMemoryException

#### Navigator Framework ####

Two new methods have been added to the `NavigatorUtils` class: `getTopicMapRepository(PageContext
pageContext)` and `getTopicMapRepository(ServletContext servletContext)`.

The code which finds the label to display for a topic has been replaced with a new implementation,
which is much simpler, and much faster, thus resulting in improved performance.

The following bugs have been fixed in the Navigator Framework:

*  [Bug #1809](http://www.ontopia.net/bugs/showbug.cgi?id=1809): Nested template tags do not work

#### Web Editor Framework ####

The `TologDelete` action now accepts objects of any types as parameters, and not just topics.

The following bugs have been fixed in the Web Editor Framework:

*  [Bug #1821](http://www.ontopia.net/bugs/showbug.cgi?id=1821): The `webed:actionid` tag does not work
   correctly in read-only mode

#### Ontopoly ####

The following bugs in Ontopoly have been fixed:

*  [Bug #1842](http://www.ontopia.net/bugs/showbug.cgi?id=1842): Ontopoly crashes if more than one
   missing topic map
*  [Bug #1806](http://www.ontopia.net/bugs/showbug.cgi?id=1806): Ontopoly importer doesn't handle
   symmetric associations
*  [Bug #1808](http://www.ontopia.net/bugs/showbug.cgi?id=1808): Ontopoly importer doesn't handle
   reified topic maps
*  [Bug #1785](http://www.ontopia.net/bugs/showbug.cgi?id=1785): Download choice on export has no
   effect
*  [Bug #1787](http://www.ontopia.net/bugs/showbug.cgi?id=1787): Read-only topic types produce
   validation errors
*  [Bug #1784](http://www.ontopia.net/bugs/showbug.cgi?id=1784): International characters lost on
   export
*  [Bug #1772](http://www.ontopia.net/bugs/showbug.cgi?id=1772): Topic Maps sources without ID and
   title don't show up in Ontopoly

### OKS 3.0.2 release notes ###

This release is a bug fixing release for the Vizigator. The last few versions of Vizigator have been
somewhat buggy and unstable, and the behaviour of the Vizigator has not really been entirely
consistent. This release fixes the known bugs, and makes the Vizigator behaviour much more
consistent.

The following bugs have been fixed:

*  [Collapse node doesn't work on focus node](http://www.ontopia.net/bugs/showbug.cgi?id=1733)
*  [Expand node creates edges with no node on the other
   side](http://www.ontopia.net/bugs/showbug.cgi?id=1739)
*  [Focus node does not reappear when filtered back
   in](http://www.ontopia.net/bugs/showbug.cgi?id=1741)
*  [Filtering in associations creates edges with only one
   node](http://www.ontopia.net/bugs/showbug.cgi?id=1731)
*  [Locality miscomputed with cycles](http://www.ontopia.net/bugs/showbug.cgi?id=1751)
*  [Nodes at locality 2 removed in locality 2](http://www.ontopia.net/bugs/showbug.cgi?id=1750)

### OKS 3.0.1 release notes ###

This a bug-fix release that includes fixes to three bugs.

**Important:**

If you are using the RDBMS Backend you should upgrade to this new version as there is a critical bug
in the jdbcspy driver shipped with earlier OKS releases. This bug only occurs when using the jdbcspy
driver, but to be on the safe side an upgrade is recommended. If you cannot upgrade then you must
not use jdbcspy (disabled by default).

#### Topic Map Engine ####

The following bugs have been fixed:

*  [Bug #1438](http://www.ontopia.net/bugs/showbug.cgi?id=1438): The in-memory class-instance index had
   a bug that gave an incorrect result with null types.

#### RDBMS backend ####

The following bugs have been fixed:

*  [Bug #1757](http://www.ontopia.net/bugs/showbug.cgi?id=1757): The database scripts included a
   reference to a non-existent `reference_id` column.
*  [Bug #1761](http://www.ontopia.net/bugs/showbug.cgi?id=1761): The jdbcspy driver contained a bug
   that caused transactions not to be rolled back in some situations.

### OKS 3.0 release notes ###

This release is a major release that has seen changes to most parts of the OKS, as well as the
inclusion of a major new component, the Ontopoly Topic Maps editor. Also, the distribution now
includes Tomcat 5.5, instead of Tomcat 5.0.

**Important:**

If you are using the RDBMS Backend you will have to make a few changes to the database schema before
proceeding. A description of how to do the upgrade can be found in the section called [RDBMS
backend](#rdbms-backend) in this document.

#### Web Editor Framework ####

A new policy for error handling in the Web Editor Framework has been added, with support for
distinguishing between critical and non-critical errors. The only change is really that it is now
possible to report non-critical errors to the user.

The form tag library has been extended with better support for accessing JSP attributes, which
greatly simplifies using the form tag library together with other tag
libraries.

The `pattern` attribute has been added to the `webed:field` tag, providing support for validating
user input against a regular expression.

The Velocity macros used by the Web Editor Framework tags have been moved into the OKS .jar file. It
is still possible to use custom Velocity macros, but by default the macros used will be those in the
.jar file. This greatly simplifies upgrades to new OKS versions. Existing applications will continue
to use the Velocity macros in the file system, which they have been configured to use. We recommend
that applications switch to using the .vm files in the .jar file.

The TLD files are now also stored in the OKS .jar file, and the tag libraries have been given
identifying URIs that can be used to load these. We recommend that applications start using these
URIs and remove local copies of the TLD files.

The following new actions have been added to the Framework:

*  `tmobject.OSLValidate` is an action which can take any number of topics, association, and topic maps
   and validate them against the OSL schema registered for the current topic map.
*  `topic.SetSubjectLocator` is an action for setting the subject locator of a topic.
*  In addition, there are the `occurrence.SetValue2`, `occurrence.SetLocator2`, and
   `topic.SetSubjectIndicator2` actions, all of which are slight modifications of existing actions that
   behave differently when given an empty string (they delete the value being operated
   on).

The following bugs have been fixed in the Web Editor Framework:

*  [Bug #1719](http://www.ontopia.net/bugs/showbug.cgi?id=1719): webed:actionid does not record current
   value correctly.
*  [Bug #1701](http://www.ontopia.net/bugs/showbug.cgi?id=1701): Velocity context clashes between web
   applications.

#### Navigator Framework ####

The tolog tag library has been extended with better support for accessing JSP attributes, which
greatly simplifies using the tolog tag library together with other tag libraries. In addition, the
`tolog:set` tag now supports setting JSP attributes in any scope (in addition to setting OKS
variables), through the new `scope` attribute.

The `tolog:out` tag now has an attribute named `escape`, which can be used to control whether or not
the output string is to be HTML-escaped.

The TLD files are now stored in the OKS .jar file, and the tag libraries have been given identifying
URIs that can be used to load these. We recommend that applications start using these URIs and
remove local copies of the TLD files.

The `tm-sources.xml` can now be loaded from the classpath, and in the distribution this is where it
is now loaded from.

#### Vizigator ####

The Vizigator has been extended with support for filtering associations by scope. It also now has
support for localization, and has been localized to Japanese and German.

*  [Bug #1690](http://www.ontopia.net/bugs/showbug.cgi?id=1690): Opening non-existent file causes
   crash.

#### Topic Map Engine ####

The engine now supports typed names, in accordance with the TMDM.

The engine now supports automatically keeping the full-text index for a topic map up to date as
changes are made to a topic map. This is primarily of interest with in-memory topic
maps.

The topic map repository framework has now been extended so that topic map sources can create new
topic maps. New methods have been added to the `TopicMapSourceIF` interface to support this. The
`XMLConfigSource` class has also been extended with new methods to allow the configuration to be
loaded from the classpath.

The following new additions have been made to the API:

*  The `setFollowTopicRefs` method has been added to the `XTMTopicMapReader`, making it easier to
   prevent the XTM reader loading other XTM documents referred to by `topicRef`
   elements.
*  The `XTMTopicMapWriter` can now be given a `DeciderIF` which will be used to filter the topic map
   during export.

The following bugs have been fixed in the engine:

*  [Bug #1726](http://www.ontopia.net/bugs/showbug.cgi?id=1726): RDF2TM inserts unwanted topics.

#### RDBMS backend ####

##### Upgrading the database schema #####

There has been a few minor changes to the database schema in this release. In order to upgrade your
database instance you will have to execute the SQL statements listed below in your
database.

If you do not have an existing database instance you should not perform the upgrade shown below, but
instead follow the standard database installation procedure described in *The RDBMS Backend
Connector - Installation Guide*.

##### PostgreSQL #####

Use the `psql` command line tool to execute the statements below, or any other database access tool
of your choice.

	
	  alter table TM_BASE_NAME add column type_id integer null;
	  alter table TM_TOPIC_MAP add column base_address varchar(512) null;
	  alter table TM_TOPIC_MAP add column title varchar(128) null;
	  create index TM_BASE_NAME_IX_myi on TM_BASE_NAME(topicmap_id,type_id,id);
	

If foreign keys are enabled the following command should also be issued:

	
	  alter table TM_BASE_NAME add constraint FK_TM_BASE_NAME_3 foreign key (type_id) references TM_TOPIC (id);
	

##### Oracle #####

Use the `sqlplus` command line tool to execute the statements below, or any other database access
tool of your choice.

	
	   alter table TM_BASE_NAME add type_id integer null;
	   alter table TM_TOPIC_MAP add base_address varchar2(512) null;
	   alter table TM_TOPIC_MAP add title varchar2(128) null;
	   create index TM_BASE_NAME_IX_myi on TM_BASE_NAME(topicmap_id,type_id,id);
	

If foreign keys are enabled the following command should also be issued:

	
	   alter table TM_BASE_NAME add constraint FK_TM_BASE_NAME_3 foreign key (type_id) references TM_TOPIC (id);
	

##### Other changes in this release #####

The RDBMS backend schema has changed to support typed names.

The RDBMS backend schema has changed to store the base address of the topic map in the database.
This solves the long-standing problem with porting ID-based topic maps applications from the
in-memory store to the RDBMS backend, since the base address is now set on import (using the
`RDBMSImport` command-line tool) ID-based references will continue to work.

The `RDBMSImport` command-line tool can now produce a SQL profiling report to show where the time is
spent during an import.

The `RDBMSImport` command-line tool will now automatically disable the shared cache, so that topic
map documents of any size can be imported.

The `RDBMSTopicMapStore` now has a new method that can dump the identity map, for debugging and
performance monitoring purposes.

The `net.ontopia.topicmaps.impl.rdbms.StorePool.SoftMaximum` database property was being incorrectly
set, effectively making impossible to set an upper boundary on the store pool size. This is now
fixed.

### Older release notes ###

* [Ontopia 2.x release notes](whatsnew-2.html)
* [Ontopia 1.x release notes](whatsnew-1.html)

