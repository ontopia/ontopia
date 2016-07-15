Ontopia
=======

What's new - Ontopia 1.x
------------------------

<p class="introduction">
This document describes what has changed in Ontopia between releases, both at a higher level, and in
more detail.
</p>

<span class="version">5.3.0 2013-08-01</p>

### Changes from 1.4.1 to 1.4.2 ###

This release is purely a bug fix release.

#### Omnigator ####

We have fixed the following bugs in the Omnigator:

*  [Bug #661](http://www.ontopia.net/bugs/showbug.cgi?id=661): Associations where current topic plays
   multiple roles displayed incorrectly.

#### Navigator Framework ####

The following bugs have been fixed in the Navigator Framework:

*  [Bug #663](http://www.ontopia.net/bugs/showbug.cgi?id=663): Setup servlets did not work with Tomcat
   4.1 and shared repositories.
*  [Bug #659](http://www.ontopia.net/bugs/showbug.cgi?id=659): Role players were disregarded when
   sorting association roles.
*  [Bug #658](http://www.ontopia.net/bugs/showbug.cgi?id=658): ContextTag caching caused problems in
   Oracle 9i Application Server and WebLogic.

#### Topic Map Engine ####

The following bugs have been fixed in the Topic Map Engine:

*  [Bug #657](http://www.ontopia.net/bugs/showbug.cgi?id=657): Base names sometimes lost when merging
   topics.
*  [Bug #664](http://www.ontopia.net/bugs/showbug.cgi?id=664): File URLs were normalized incorrectly.
*  [Bug #660](http://www.ontopia.net/bugs/showbug.cgi?id=660): In XTM, having a topicRef and a
   subjectIndicatorRef to the same URI within the subjectIdentity element caused an
   exception.

In addition, `NameIndexIF` has now been made auto-updating.

#### Query Engine ####

The following bugs have been fixed in the Query Engine:

*  [Bug #655](http://www.ontopia.net/bugs/showbug.cgi?id=655): Queries failed on unary associations.
*  [Bug #662](http://www.ontopia.net/bugs/showbug.cgi?id=662): Bug(s) in merging of results from
   inference rules.

### Changes from 1.4 to 1.4.1 ###

This release is a bug fix release, released primarily to fix some embarrassing problems in the
Omnigator. There are bug fixes in other parts of the OKS as well, however. The LTM Emacs mode is now
distributed with the OKS, as is one additional topic map: `support-kb.ltm`.

#### Omnigator ####

We are now bundling Tomcat 4.1.18 with the Omnigator, rather than 4.0.6, which we used for 1.4. The
front page has been cleaned up a little, and a link to a new "Index of Individuals", showing all the
topics that are not typing topics, has been added to the topic map page. In addition, we have fixed
the following bugs:

*  [Bug #656](http://www.ontopia.net/bugs/showbug.cgi?id=656): Class hierarchy loops caused Omnigator
   to freeze.
*  [Bug #648](http://www.ontopia.net/bugs/showbug.cgi?id=648): Couldn't display topic maps when the
   superclass-subclass topic (with role topics) was defined, but there were no associations of that
   type.
*  [Bug #578](http://www.ontopia.net/bugs/showbug.cgi?id=578): Refreshing application configuration
   failed to work for some configuration settings.

#### Navigator Framework ####

The following bugs have been fixed in the Navigator Framework:

*  [Bug #605](http://www.ontopia.net/bugs/showbug.cgi?id=605): Improved error message when
   `tm:associated` gets input that does not only consist of topics.
*  [Bug #634](http://www.ontopia.net/bugs/showbug.cgi?id=634): `template:put` did not call
   BodyContent.clearBody().

In addition the documentation has been improved in places.

#### Topic Map Engine ####

The following bugs have been fixed in the Topic Map Engine:

*  [Bug #654](http://www.ontopia.net/bugs/showbug.cgi?id=654): Exporter assigns duplicate IDs.
*  [Bug #598](http://www.ontopia.net/bugs/showbug.cgi?id=598): NullPointerException when loading topic
   map.

In addition the documentation has been improved in places.

#### Query Engine ####

The following bugs have been fixed in the Query Engine:

*  [Bug #643](http://www.ontopia.net/bugs/showbug.cgi?id=643): Queries of the form related($A : term,
   $A : term) did not work.
*  [Bug #593](http://www.ontopia.net/bugs/showbug.cgi?id=593): Selecting the same variable twice
   crashed rather than provide a proper error message.
*  [Bug #615](http://www.ontopia.net/bugs/showbug.cgi?id=615): Order by in tolog did not use sort
   names.

In addition the documentation has been improved in places.

### Changes from 1.3.4 to 1.4 ###

This release adds several major new features to the OKS, the most important of which are:

*  A beta version of the Web Editor Framework,
*  Improvements to the Navigator Framework to make it more powerful and easier to use,
*  An implementation of tolog designed for use with the RDBMS backend, and

Each of these are described in more detail below. In addition, this version has a large number of
bug fixes, as well as numerous minor improvements to the documentation.

#### Omnigator ####

A plug-in for providing feedback to Ontopia has been added to the Omnigator, there are now links to
the topics that represent role types from the pages about association types, subject addresses are
being displayed, and it is now distributed with Tomcat 4.1. The following bugs have been
fixed:

*  [Bug #601](http://www.ontopia.net/bugs/showbug.cgi?id=601): The Tomcat batch scripts set environment
   variables incorrectly on Windows in some cases.
*  [Bug #618](http://www.ontopia.net/bugs/showbug.cgi?id=618): Reloading unloaded topic maps on the
   Manage page caused an error.
*  [Bug #584](http://www.ontopia.net/bugs/showbug.cgi?id=584): Topics were sorted by base name rather
   than sort name.

#### Navigator Framework ####

The most important changes to the Navigator Framework functionality are:

*  The `logic:if` tag can now be used inside `logic:set` since it will now pass values produced in the
   then/else branches upwards. This makes it possible to write much more powerful recursive
   functions.
*  The `tm:tolog` tag now has an attribute `select` for producing a collection of values rather than a
   map.
*  The `logic:set` tag will now produce an empty collection if it has no content, thus making it easier
   to produce empty collections.
*  The `tm:associated` tag has a new attribute `produce`, which can be used to tell it to produce
   associations rather than topics.
*  The `tm:subjectAddress` tag has been added.

In addition, a number of bugs have been fixed in the Navigator Framework:

*  [Bug #634](http://www.ontopia.net/bugs/showbug.cgi?id=634): A bug in the ontojsp JSP tag interpreter
   made functions work poorly in Tomcat 4.1.
*  [Bug #624](http://www.ontopia.net/bugs/showbug.cgi?id=624): The `tm:name` tag would crash if given
   the wrong kind of collection in its `basenameScope` attribute.
*  [Bug #583](http://www.ontopia.net/bugs/showbug.cgi?id=583): The Navigator Framework did not work
   correctly in the BEA WebLogic application server.
*  [Bug #575](http://www.ontopia.net/bugs/showbug.cgi?id=575): The `tm:name` tag produced collections
   containing `null` when used on a topic which had no name. This would cause problems for other
   tags.

#### Topic Map Engine ####

The following change has been made to the topic map engine:

*  The `SubjectUtils` class has been deprecated since it was badly broken (it did not distinguish
   between subject addresses and subject indicators), and because it did not perform any useful
   function.

The only changes to the topic map engine in this release are the following bug fixes:

*  [Bug #535](http://www.ontopia.net/bugs/showbug.cgi?id=535): The `SubjectIdentityDecider` did not
   distinguish subject addresses from subject identifiers. Nor did any of the many classes that used
   it.
*  [Bug #623](http://www.ontopia.net/bugs/showbug.cgi?id=623): The LTM parser provided poor error
   messages when the ID of the topic map collided with that of a topic.
*  [Bug #614](http://www.ontopia.net/bugs/showbug.cgi?id=614): The LTM parser would loop forever if
   presented with an LTM file which had an internal occurrence that was not terminated with
   `]]`.
*  [Bug #582](http://www.ontopia.net/bugs/showbug.cgi?id=582): The XTM importer ignored the XML
   namespace of elements and used the XML 1.0 element type name to look for XTM elements. Now accepts
   any element in the XTM namespace.
*  [Bug #522](http://www.ontopia.net/bugs/showbug.cgi?id=522): In XTM documents containing more than
   one topic map references to the same external document from multiple topic maps will only be loaded
   for the first topic map.
*  [Bug #538](http://www.ontopia.net/bugs/showbug.cgi?id=538): A mistake in the test code for the
   module encrypter caused the test suite to fail on Windows due to differences in line
   endings.

#### The Schema Tools ####

In this version a number of problems in the documentation of the OSL schema language have been
fixed, and some bugs in the tools themselves have been fixed.

*  [Bug #557](http://www.ontopia.net/bugs/showbug.cgi?id=557): When a topic was an instance of two
   different classes the validator would not merge the constraints on the two classes
   correctly.
*  [Bug #629](http://www.ontopia.net/bugs/showbug.cgi?id=629): The OSL importer did not handle
   superclass references to classes defined further down in the file.
*  [Bug #614](http://www.ontopia.net/bugs/showbug.cgi?id=614): The LTM parser would loop forever if
   presented with an LTM file which had an internal occurrence that was not terminated with
   `]]`.
*  [Bug #582](http://www.ontopia.net/bugs/showbug.cgi?id=582): The XTM importer ignored the XML
   namespace of elements and used the XML 1.0 element type name to look for XTM elements. Now accepts
   any element in the XTM namespace.
*  [Bug #522](http://www.ontopia.net/bugs/showbug.cgi?id=522): In XTM documents containing more than
   one topic map references to the same external document from multiple topic maps will only be loaded
   for the first topic map.

#### The Query Engine ####

A new implementation of the tolog query language designed especially for the RDBMS backend has been
added. This implementation converts tolog queries directly to SQL queries, which are then executed
by the RDBMS. This makes evaluation of tolog queries enormously much faster when running on the
RDBMS backend.

The `net.ontopia.topicmaps.query.utils.QueryUtils` class has also been added to the API. Developers
are recommended to use this class to create instances of the `QueryProcessorIF` interface, as this
class will create different processors depending on what topic map implementation is
used.

In addition, a method for loading rules from Java `Reader` objects has been added to the
`QueryProcessorIF`.

#### The RDBMS Backend ####

The only changes to the RDBMS backend have been a tiny optimization of some queries, and a single
bug fix:

*  [Bug #627](http://www.ontopia.net/bugs/showbug.cgi?id=627): In some cases constraints in the RDBMS
   schema would be violated when collection properties were modified.

### Changes from 1.3.3 to 1.3.4 ###

This release is primarily a bug fix release, though some optimizations have also been performed, and
some APIs have been cleaned up. In addition, there are some minor improvements here and
there.

#### Omnigator ####

A number of minor adjustments have been made in the Omnigator's display. In addition, the free
download now has an additional capability.

*  The number of objects in each list is now displayed together with the heading.
*  The association players now have their names selected using the current topic as a theme.
*  The scope of associations is now displayed using a mouse-over.
*  The name chosen for the title of the topic page now tries to use the topic types as themes when
   selecting a name to display.
*  The free download distribution of the Omnigator now includes the fulltext adminstration plug-in.
   This plug-in lets you create and delete fulltext indexes for topic maps.
*  The full-text plug-in 'No index' message is now a link to the full-text administration plug-in
   manage page.
*  The "fulltext-admin" plug-in directory has been renamed to "ftadmin", because the Tomcat JSP
   compiler (Jasper) converts directory names to Java package names and hyphens are not allowed in
   package names.

#### Navigator Framework ####

The tag libraries have seen some internal changes, and a couple of usability improvements.

*  The `tm:tolog` tag now has a `select` attribute that allows it to produce a collection of topics
   rather than maps.
*  The `logic:set` tag has been changed, so that if empty it will still set the named variable, but set
   it to the empty collection. (In previous versions the variable was not set at
   all.)
*  The tag library implementation has been optimized, yielding some performance improvements.
*  The method `net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF.getUserStoreRegistry()` has been
   added and replaces the now deprecated `getStoreRegistry()` and `getTransactionUser()` methods. This
   change renders the transaction user used by the navigator application
   transparent.
*  `net.ontopia.topicmaps.nav2.core.NavigatorTagException` and derived exception classes now support
   nested exceptions, i.e. the cause.
*  Added `net.ontopia.topicmaps.nav2.impl.basic.AbstractFunction`, which is a common base class for
   user defined taglib functions. See the `logic:externalFunction` in the taglib reference for more
   information.
*  Deprecated the following methods in `net.ontopia.topicmaps.nav2.core.FunctionIF`: ` getName()`,
   `getReturnVariableName()`, and `call(PageContext, TagSupport)`.
*  Added `net.ontopia.topicmaps.nav2.core.FunctionIF.execute(PageContext, TagSupport)`, which now takes
   over the role of executing user defined functions. Existing functions will continue to work provided
   they subclass `AbstractFunction`.
*  Added `net.ontopia.topicmaps.nav2.core.NavigatorPageIF.registerFunction(String, FunctionIF)`, which
   replaces the now deprecated `registerFunction(FunctionIF)` method.
*  [Bug #560](http://www.ontopia.net/bugs/showbug.cgi?id=560): First content node inside functions was
   ignored.

#### Topic Map Engine ####

The changes to the engine mainly consist in bug fixes, optimizations, and minor API improvements.

*  The URILocator(File) constructor was added to make it easy to create URI locators from File
   objects.
*  The StoreRegistry.isStoreOpen(Object txnuser, String refkey) method was added so that it was
   possible to figure out if a store for a reference key was open for the specified transaction
   user.
*  The `UserStoreRegistry` class was added to make it easier to use the `StoreRegistry` in situations
   where there was only a single transaction user.
*  The utility class `NullResolvingExternalReferenceHandler` was added, as a default reference handler
   that does not resolve any external references.
*  Added `TopicMapStoreIF.delete()`, which lets you delete a topic map from the store. Note that the
   topic map must be empty, ie. not contain any topics, associations, or facets, before it can be
   deleted.
*  Added `net.ontopia.topicmaps.entry.UserStoreRegistry`, a wrapper class for `StoreRegistry` instances
   and a single transaction user.
*  The scope processing code has been optimized.
*  [Bug #545](http://www.ontopia.net/bugs/showbug.cgi?id=545): An exception was thrown when an XTM
   <topicRef> URI didn't not contain a fragment ("#"). This has now been fixed.
*  [Bug #533](http://www.ontopia.net/bugs/showbug.cgi?id=533): The tm-sources.xml now produces better
   error messages for certain errors.

#### Full-text Integration ####

*  The full-text integration has been upgraded to use Lucene 1.2 final. The Lucene change log is
   available
   [here](http://cvs.apache.org/viewcvs/jakarta-lucene/CHANGES.txt?rev=HEAD&content-type=text/vnd.viewcvs-markup).

#### Query Engine ####

*  Added `net.ontopia.topicmaps.query.core.QueryResultIF.close()` which lets query results release its
   underlying resources.
*  Added `net.ontopia.topicmaps.query.utils.QueryUtils.getQueryProcessor(TopicMapIF)`, which makes it
   possible to get a query processor optimized for the given topic map.

#### RDBMS Backend Connector ####

*  `RDBMSTopicMapReference.delete()` now delegates to `RDBMSTopicMapStore.delete()`, which allows
   TopicMapIF instances to be deleted if they contain no data.
*  The syntax of the URI locator returned by `RDBMSTopicMapStore.getBaseAddress()` was wrong. This has
   now been fixed. The correct syntax is 'x-tm:rdbms:12345', where 12345 is the id of the topic
   map.
*  Added `net.ontopia.topicmaps.impl.rdbms.RDBMSSingleTopicMapSource`, which lets you reference
   individual RDBMS topic maps.

### Changes from 1.3.2 to 1.3.3 ###

This release is almost purely a bugfix release. It corrects a bug in the test suite, as well as an
important bug in the RDBMS backend. We recommend that RDBMS users upgrade. Other users need not
bother.

#### Navigator Framework ####

The functionality has been extended/changed in the following ways:

*  The generation of topic map IDs in links is now improved. It no longer depends on the
   `logic:context` tag.
*  The `getTopicMapById(Object txnuser, String topicmapId)` method on `NavigatorApplicationIF` has been
   deprecated. It will eventually be removed.
*  The `getTopicMapRefId` method on `NavigatorApplicationIF` has been added.

#### Topic Map Engine ####

There has been one API change; a new constructor for the `URILocator` class has been added:
`URILocator(File file)`.

The following bugs were fixed:

*  [Bug #538](http://www.ontopia.net/bugs/showbug.cgi?id=538): A weakness in `EncryptionWriter`, an
   internal class used only for building releases, caused the test suite to fail on Windows and on
   platforms where the default encoding was not ISO 8859-1. This class has now been replaced by one
   which does not have this problem.
*  Some unreported problems with the `importInto` method in `XTMTopicMapReader` and `LTMTopicMapReader`
   have been fixed.

#### RDBMS Backend Connector ####

The following bugs have been fixed:

*  [Bug #540](http://www.ontopia.net/bugs/showbug.cgi?id=540): The positions of the two parameters to
   lookup by source locator were reversed in `queries.xml`, which caused all such lookups to fail with
   PostgreSQL databases. Subject address lookups had the same problem and failed with all databases.
   This is a serious bug, and has been fixed in this release. Upgrades are
   recommended.
*  [Bug #539](http://www.ontopia.net/bugs/showbug.cgi?id=539): The developer's guide had some
   out-of-date code exaaples.

### Changes from 1.3.1 to 1.3.2 ###

Previous versions shipped with a built-in license key that would expire 90 days after the version
was built. In version 1.3.2 this is different. The software comes with no default license key, and
will not run before it gets one. Read section 3.2 of the install guide to see how the software finds
its license key.

#### Omnigator ####

Some new features have been added, as listed below. Note also that the interpretation of XTM
documents has changed, as described in [Topic Map Engine](#topic-map-engine).

*  If the topic map has a reifying topic with a description occurrence (using [the Ontopia description
   PSI](http://psi.ontopia.net/xtm/occurrence-type/description)) that description is now displayed on
   the topic map start page.
*  The tolog plug-in now lets you specify inference rules.
*  The Omnigator now displays the source locators of topics at the bottom of the page.
*  The Omnigator now produces stable links to topics, by using the symbolic IDs from the source files
   instead of object IDs.
*  The Omnigator now displays its version number, build details, and expiry date on the welcome page.

#### Navigator Framework ####

The functionality has been extended/changed in the following ways:

*  The `output:id` tag has been added. This tag makes it possible for web applications to produce
   stable links. This is related to [bug
   #515](http://www.ontopia.net/bugs/showbug.cgi?id=515).

In addition, some reference diagrams have been added to the tag library documentation, to make it
easier to see how to traverse topic map information using the tag libraries.

A number of bugs have also been fixed:

*  [Bug #513](http://www.ontopia.net/bugs/showbug.cgi?id=513): The `output:treediagram` tag did not use
   the user context filter when labelling nodes in the tree.
*  [Bug #489](http://www.ontopia.net/bugs/showbug.cgi?id=489): The `tm:occurrences` tag accepted
   illegal values in the `type` attribute.

#### Topic Map Engine ####

As part of the fix to [bug #523](http://www.ontopia.net/bugs/showbug.cgi?id=523) the behaviour of
the XTM importer with respect to entities has changed. In earlier versions, one could refer from
`tm.xtm` to the topic `foo` in the external entity `tm.ent` using `#foo`, but now it is necessary to
use URIs of the form `tm.ent#foo`. Relative URIs inside `tm.ent` will now be resolved relative to
the URI of that entity, rather than relative to the URI of the document entity as before. We realize
that this may break existing documents, but [the XML Base
specification](http://www.w3.org/TR/xmlbase/#granularity) is very clear on what the correct
behaviour is in this case.

Other improvements are:

*  The `OntopiaRuntimeException` now provides more information, which leads to better error messages in
   a number of situations.
*  The `XTMContentHandler` has been optimized somewhat, resulting in faster XTM imports. The exact
   speed-up depends on the topic map in question, but on average imports seem to be about 15%
   faster.
*  The engine (and the rest of the OKS) has been upgraded to the new log4j 1.2 release. This should
   have no impact on users in any way.

There have also been a number of API changes:

*  The `TopicMapBuilderIF` interface has two new methods for creating topics, which allow a single
   topic type or a collection of topic types to be added to the topic directly.
*  The `net.ontopia.topicmaps.entry` package has been considerably reworked and expanded, making it
   much more powerful and usable than it was before.
*  The `DefaultTopicMapSource` class now has methods for adding and removing topic map references. The
   `TopicMapReferenceIF` has a number of new methods added to it, making it considerably more powerful
   than it was before. The same applies to `TopicMapSourceIF`. The `StoreFactoryReference` and
   `StoreRegistry` classes and the `TopicMapRepositoryIF` interface have been
   added.
*  The `TopicStringifiers` utility class now has a method that lets the user specify desired base name
   and variant name scope.

The following bugs were fixed:

*  [Bug #524](http://www.ontopia.net/bugs/showbug.cgi?id=524): In some cases subject indicator merges
   when importing from XTM would result in broken topic maps.
*  [Bug #516](http://www.ontopia.net/bugs/showbug.cgi?id=516): Empty `xml:base` attributes caused XTM
   loading to fail.
*  [Bug #457](http://www.ontopia.net/bugs/showbug.cgi?id=457): When importing XTM topic maps, merged-in
   topic maps would add their source locators to that of the master topic map, which was wrong, and
   caused problems with reification.
*  [Bug #511](http://www.ontopia.net/bugs/showbug.cgi?id=511): Using scope on an occurrence or
   association in LTM would give a parse error if an association followed directly after. This was
   solved by increasing lookahead in the parser.
*  [Bug #523](http://www.ontopia.net/bugs/showbug.cgi?id=523): relative URIs inside XML entities in XTM
   documents are now resolved relative to the URI of the entity.
*  [Bug #533](http://www.ontopia.net/bugs/showbug.cgi?id=533): Bad error message when entering file as
   path in `tm-sources.xml`.
*  [Bug #530](http://www.ontopia.net/bugs/showbug.cgi?id=530): Bad error message from LTM parser when
   syntax name in `#MERGEMAP` not quoted.

#### Full-text Integration ####

The only change in this version is that the command-line `LuceneIndexer` tool now supports the use
of `x-tm:rdbms` URIs to refer to topic maps stored in the RDBMS backend. This means it can be used
to index RDBMS topic maps.

#### Query Engine  ####

In this release, the only change was a bug fix:

*  [Bug #476](http://www.ontopia.net/bugs/showbug.cgi?id=476): Association predicates where the same
   role type was used more than once gave wrong results.

#### Schema Tools ####

The documentation of the `player` element type in OSL was extended somewhat. The only other change
was a bug fix:

*  [Bug #494](http://www.ontopia.net/bugs/showbug.cgi?id=494): NullPointerException on undefined
   otherClasses.

#### RDBMS Backend Connector ####

Two new command-line utilities were added in this version:
`net.ontopia.topicmaps.cmdlineutils.rdbms.RDBMSImport` and
`net.ontopia.topicmaps.cmdlineutils.rdbms.RDBMSExport`. These allow users to import and export topic
maps into and out of the RDBMS backend from the command-line.

A minor optimization of the lookup queries was made for the PostgreSQL RDBMS server.

Otherwise, the major change was the introduction of support for virtual collections. What this means
is when calling say `getTopics()` on `TopicMapIF` the topics will only be loaded as needed. This
ensures that the JVM does not run out on memory when methods that return large numbers of objects
are called. This change fixed bugs [507](http://www.ontopia.net/bugs/showbug.cgi?id=507) and
[509](http://www.ontopia.net/bugs/showbug.cgi?id=509).

### Changes from 1.3 to 1.3.1 ###

#### Omnigator ####

Some new features have been added or changed:

*  The export plug-in can now generate OSL schemas from existing topic maps.
*  The merge plug-in no longer does name-based merging by default. The option now has to be turned on
   explicitly.

The following bug was fixed:

*  Handling of associations with empty (un-specified) roleType fixed. See [bug
   413](http://www.ontopia.net/bugs/showbug.cgi?id=413).

#### Navigator Framework ####

The behaviour of one tag has changed:

*  The logic:if tag now treats non-existent variables as being false. This makes it much easier to use
   the if tag.

The following bugs were fixed:

*  In some cases equals comparisons done with logic:if would give incorrect results. Now fixed. See
   [Bug #503](http://www.ontopia.net/bugs/showbug.cgi?id=503)

#### Topic Map Engine ####

The following bugs were fixed:

*  Runtime license no longer expire after 24 hours. See [bug
   488](http://www.ontopia.net/bugs/showbug.cgi?id=488).
*  Class loader license key problem fixed. See [bug
   484](http://www.ontopia.net/bugs/showbug.cgi?id=484).
*  Compiling the OKS under JDK 1.4 caused problems with running it on 1.3. This is now fixed.
*  Security exceptions thrown by the getProperty() calls in applets are now caught. See [bug
   500](http://www.ontopia.net/bugs/showbug.cgi?id=500).

### Changes from 1.2.5 to 1.3 ###

#### Omnigator ####

New features:

*  The subject index now shows the class hierarchy structure.
*  The class hierarchy structure can also be displayed as an image with links.
*  A new default stylesheet has been introduced.
*  Two new plug-ins were added: the tolog query plug-in, and the OSL schema validator plug-in.

The following bugs were fixed:

*  [Bug #354](http://www.ontopia.net/bugs/showbug.cgi?id=354): Statistics printer didn't use user
   context.
*  [Bug #399](http://www.ontopia.net/bugs/showbug.cgi?id=399): The CSS stylesheets now uses relative
   fonts sizes.
*  [Bug #427](http://www.ontopia.net/bugs/showbug.cgi?id=427): The statistics plug-in incorrectly
   counted topic types with no names.
*  [Bug #448](http://www.ontopia.net/bugs/showbug.cgi?id=448): The statistics plug-in didn't sort
   occurrences by type correctly.
*  [Bug #459](http://www.ontopia.net/bugs/showbug.cgi?id=459): The statistics plug-in association
   structure summary failed when association types had no name.

#### Navigator Framework ####

Changes:

*  The Acme GIF encoder has been replaced by better JGME encoder.
*  The TreeDiagramTag now produces transparent GIFs.
*  Multiple values are now allowed for object ids in context tag ([bug
   #330](http://www.ontopia.net/bugs/showbug.cgi?id=300)). The navigator test framework has been
   extended to support this.
*  "`function`" elements can now have return values. This can be specified using the "`return`"
   attribute. The attribute should contain the name of a context variable whose value is to be
   returned. See [bug #336](http://www.ontopia.net/bugs/showbug.cgi?id=336).

The following bugs were fixed:

*  [Bug #458](http://www.ontopia.net/bugs/showbug.cgi?id=354): `framework:setcontext` would throw a
   `NullPointerException` when there was no user object in the session.
*  [Bug #455](http://www.ontopia.net/bugs/showbug.cgi?id=354): `logic:if` would silently accept testing
   on undefined variables. It now throws an error in this case.
*  [Bug #464](http://www.ontopia.net/bugs/showbug.cgi?id=354): `logic:foreach` would throw a
   `NullPointerException` when there was no user object in the session.
*  [Bug #353](http://www.ontopia.net/bugs/showbug.cgi?id=353): The "`logic:set`" tag didn't take the
   user context into account when sorting.
*  [Bug #363](http://www.ontopia.net/bugs/showbug.cgi?id=363): It was not possible to override the
   default comparator.
*  [Bug #449](http://www.ontopia.net/bugs/showbug.cgi?id=449): Maximum list lengths can now be
   specified in the navigator configuration file.

#### Topic Map Engine ####

Notable changes in this release are:

*  All Ontopia Knowledge Suite components now support Java2 version 1.4.
*  Memory consumption has been reduced by, on average, 30%.
*  Support for LTM version 1.2 has been added. This new version contains some changes that break
   backwards compatibility. A [python](http://www.python.org) script for updating LTM topic maps to the
   new version is included in the distribution (`${basedir}/utils/ltmfix.py`). Usage: `python ltmfix.py
   <oldfile> <newfile>`
*  Added support for the ".hytm" file suffix in `net.ontopia.topicmaps.utils.ImportExportUtils`,
   because ISO recently renamed its old HyTime-based topic maps syntax to *HyTM*. See [bug
   419](http://www.ontopia.net/bugs/showbug.cgi?id=419).
*  Data URLs used in HyTM topic maps are now being resolved into inline occurrences on import. HyTM
   export map inline occurrences back into data URLs. Note that this only apply to data URLs with the
   `text/plain` content type. See [bug
   355](http://www.ontopia.net/bugs/showbug.cgi?id=355).
*  TopicTreeBuilder has been made more robust.
*  The test suite has been upgraded to use JUnit version 3.7.
*  The `ReificationUtils` class has two new methods: `getReifyingTopics` and `getReifiedObjects`.
*  The `AssociationBuilder` class now has new methods for building ternary associations.
*  The `OntopiaRuntimeException` has a new method `getCause`, which replaces the now deprecated
   `getException`, in order to be more in line with JDK 1.4 exceptions.
*  The `TopicMapBuilderIF` has a new method `makeAssociationRole`, which can also set the player of the
   role.

The following bugs were fixed:

*  [Bug #269](http://www.ontopia.net/bugs/showbug.cgi?id=269): The developer guide did not include
   documentation of topic map builders and factories.
*  [Bug #300](http://www.ontopia.net/bugs/showbug.cgi?id=300): The ordering of topic map objects in XTM
   canonicalization in some cases caused object to have the same sort key. The canonicalization
   specification was updated (version 1.1) to guarantee consistent ordering of objects with different
   properties. The canonicalizer has been updated according to the new
   specificiation.
*  [Bug #432](http://www.ontopia.net/bugs/showbug.cgi?id=432): <topicRef> elements occurring inside
   <subjectIdentity> and referring to external topic maps didn't cause those topic maps to be loaded.
   They should since the XTM specification clearly states that they should.
*  [Bug #435](http://www.ontopia.net/bugs/showbug.cgi?id=435): Resolving relative URI locators
   sometimes failed.
*  [Bug #439](http://www.ontopia.net/bugs/showbug.cgi?id=439): Error messages produced by the LTM
   processor has been made more readable.
*  [Bug #461](http://www.ontopia.net/bugs/showbug.cgi?id=461): The test suites produced lots of
   namespace-prefix related errors. These errors are now being filtered out by default, since they are
   of no importance.
*  [Bug #442](http://www.ontopia.net/bugs/showbug.cgi?id=442): The documentation of the
   `MergeUtils`.`shouldMerge` method was inaccurate. This has now been cleared up.

#### Full-text Integration ####

Changes:

*  The Lucene integration can now work with instances of `org.apache.lucene.store.Directory` directly.
   This will for example let you to create and search indexes in-memory using the `RAMDirectory`
   class.
*  The default token stream analyzer is now `org.apache.lucene.analysis.standard.StandardAnalyzer`,
   replacing `StopAnalyzer`.
*  Token stream analyzers can now be configured for use with the Lucene fulltext engine. This will let
   you plug your own analyzers. See the classes `LuceneIndexer` and `LuceneSearcher` for more
   information.

The following bugs have been fixed:

*  [Bug #198](http://www.ontopia.net/bugs/showbug.cgi?id=198): The IndexerIF.delete() method has been
   added. It lets you delete a fulltext index.
*  [Bug #436](http://www.ontopia.net/bugs/showbug.cgi?id=436): The fulltext plug-in didn't take the
   user context into account when displaying topic names.
*  [Bug #437](http://www.ontopia.net/bugs/showbug.cgi?id=437): The fulltext plug-in didn't process
   requests with extended characters properly.
*  [Bug #440](http://www.ontopia.net/bugs/showbug.cgi?id=440): The fulltext plug-in displayed long
   traceback when invalid queries was issued.

#### RDBMS Backend Connector ####

The following bugs have been fixed:

*  [Bug #387](http://www.ontopia.net/bugs/showbug.cgi?id=387): Adding objects that had been deleted
   earlier caused some of the object values to not be included.
*  [Bug #317](http://www.ontopia.net/bugs/showbug.cgi?id=317): Big imports ran out of memory.

### Changes from 1.2.4 to 1.2.5 ###

#### Navigator Framework ####

The main change in the navigator in this version is that we now bundle it with Tomcat 4.0, something
that caused us to upgrade certain parts of the code. In addition we have added some tags, and fixed
a number of bugs.

A number of changes were made in the navigator framework itself:

*  The ontojsp JSP execution environment has been updated to support JSP 1.2.
*  The web application and tag library descriptors were updated to conform to the Servlets 2.3 and JSP
   1.2 specifications.
*  The `<framework:setcontext>` and `<framework:getcontext>` tags were added.
*  The methods `getModule` and `getRootNode` in `net.ontopia.topicmaps.nav2.core.FunctionIF` were
   deprecated.

The following bugs were fixed in the 1.2.5 version of the Navigator Framework:

*  [Bug #405](http://www.ontopia.net/bugs/showbug.cgi?id=405): fixing the behaviour of the
   `<tm:superclasses>` and `<tm:subclasses>` tags.
*  [Bug #408](http://www.ontopia.net/bugs/showbug.cgi?id=408): the `sequence-first` and `sequence-last`
   variables were not set correctly by the `<logic:foreach>` tag.
*  [Bug #376](http://www.ontopia.net/bugs/showbug.cgi?id=376): order of roles within each association
   in a list of associations was inconsistent.
*  [Bug #238](http://www.ontopia.net/bugs/showbug.cgi?id=238): fixed in September 2001, now officially
   closed.

In addition, some bugs were also fixed in the Omnigator:

*  [Bug #376](http://www.ontopia.net/bugs/showbug.cgi?id=376): in n-ary associations, topics playing
   the same role as the current topic were not displayed.
*  [Bug #407](http://www.ontopia.net/bugs/showbug.cgi?id=407): associations with no type caused page
   display to crash.
*  [Bug #304](http://www.ontopia.net/bugs/showbug.cgi?id=304): order of roles within each association
   in a list of associations was inconsistent.
*  [Bug #406](http://www.ontopia.net/bugs/showbug.cgi?id=406): binary associations where the same topic
   plays both roles were displayed as empty.

#### Topic Map Engine ####

There were few significant changes in the engine in this version.

*  Added the `getSubclasses(class, level)` and `getSuperclasses(class, level)` methods on
   `net.ontopia.topicmaps.utils.TypeHierarchyUtils`.
*  Added the `getBaseAddress` and `setBaseAddress` methods on the
   `net.ontopia.topicmaps.entry.AbstractPathTopicMapSource` class.

Three bugs were fixed in this release:

*  [Bug #300](http://www.ontopia.net/bugs/showbug.cgi?id=300): in some cases canonicalization of topic
   maps would give topics the same sort keys, thus giving random ordering of some
   topics.
*  [Bug #410](http://www.ontopia.net/bugs/showbug.cgi?id=410): when importing LTM files which had a
   topic with the sort name PSI and when that topic itself had a sort name the LTM parser would produce
   bad topic map object structures.
*  [Bug #397](http://www.ontopia.net/bugs/showbug.cgi?id=397): ISO exporter would fail when exporting
   association roles with no player.

#### Full-text Integration ####

The following bugs have been fixed:

*  [Bug #378](http://www.ontopia.net/bugs/showbug.cgi?id=378): Fulltext testsuite failed on default
   install (Windows only)
*  [Bug #390](http://www.ontopia.net/bugs/showbug.cgi?id=390): The *Ontopia Full-text Integration - A
   Developer's Guide* document has been updated to include information about the RDBMS URL
   scheme.

#### RDBMS Backend Connector ####

The following public class has been added::

*  RDBMSTopicMapReader

The following bug has been fixed:

*  [Bug #390](http://www.ontopia.net/bugs/showbug.cgi?id=390): The *RDBMS Backend Connector - A
   Developer's Guide* has been updated to include information about the RDBMS URL
   scheme.

### Changes from 1.2.3 to 1.2.4 ###

#### Navigator Framework ####

This release has seen a number of minor improvements in the navigator framework. The changes in the
tag libraries are:

*  The <output:treediagram> tag no longer uses Batik, but instead uses an open source GIF encoder. This
   makes the distribution a lot smaller.
*  The <tm:names> tag now rejects duplicate name objects.

In addition, the navigator developer's guide has been extended with more detailed information on how
to use the <tm:lookup> tag.

#### Topic Map Engine ####

This release was primarily made to satisfy customer requests for functionality in the full-text
integration. Changes in the engine are limited to a few new method, and a minor extension to the
engine developer's guide.

The following methods has been added:

*  net.ontopia.topicmaps.utils.TopicTreeRendrer.renderImage()
*  net.ontopia.topicmaps.utils.ImportExportUtils.getReader(String propfile, String filename_or_url)
*  net.ontopia.topicmaps.utils.ImportExportUtils.getReader(Map properties, String filename_or_url)

#### Full-text Integration ####

The following features has been added:

*  The LuceneIndexer command line tool can now index topic maps via the RDBMS Backend Connector. Such
   topic maps can be referenced using URLs of the following type "x-tm:rdbms:<topicmapid>", e.g.
   "x-tm:rdbms:5001".
*  The LuceneIndexer now accepts the --props=propfile option, which references the topic map store
   properties file.

#### RDBMS Backend Connector ####

The following constructors has been added - all to make it possible to pass topic map store
properties as a Map instance:

*  RDBMSTopicMapStore(Map properties)
*  RDBMSTopicMapStore(Map properties, long topicmap_id)
*  RDBMSTopicMapSource(Map properties)

### Changes from 1.2.2 to 1.2.3 ###

#### Navigator Framework ####

This release adds some new tags, and fixes a large number of bugs. The changes in the tag libraries
are:

*  The <tm:topics> tag can now have other tags as content.
*  The <tm:lookup> tag has two new attributes: basename and variant.
*  The <output:name> tag has a new attribute 'stringifier', which can be used to control the rendering
   of names.
*  The <logic:if> tag has a new attribute 'sizeEquals', which can be used to test the size of a
   collection.

The following bugs were fixed in the Navigator Framework in this release:

*  [Bug #371](http://www.ontopia.net/bugs/showbug.cgi?id=371): <tm:lookup> does not work with relative
   URIs.
*  [Bug #369](http://www.ontopia.net/bugs/showbug.cgi?id=369): <logic:foreach> does not set up context
   correctly.

The following bugs were fixed in the Omnigator in this release:

*  [Bug #352](http://www.ontopia.net/bugs/showbug.cgi?id=352): Main subjects overdoes it for topic
   maps.
*  [Bug #356](http://www.ontopia.net/bugs/showbug.cgi?id=356): Incorrect order in "Subject indexes".

#### Topic Map Engine ####

The following method has been added:

*  net.ontopia.topicmaps.utils.ClassInstanceUtils.resolveAssociations()

One old bug has been fixed:

*  [Bug #67](http://www.ontopia.net/bugs/showbug.cgi?id=67): Class-instance associations not
   recognized

#### RDBMS Backend Connector ####

The following features has been added:

*  Support for JDBC batch writing. This option boosts performance with large transactions and in
   environments where network lag is an issue, since the number of network requests needed is
   significantly lower and the database may provide optimizations for performing lots of similar
   database modifications in batches.
*  JDBC Connection pooling. Note that this features requires the JDBC 2.0 Optional Package API to be
   installed. See the requirements section in the installation guide for more
   information.
*  Ability to disable conforming queries via configuration property, i.e. no longer guaranteeing that
   database query results matches changes in the current transaction. In this case there may be
   outstanding transaction changes that have not been written to the database to the time the query is
   issued. Be careful when disabling conforming queries.
*  Subject identity lookup in the database can be disabled via a configuration property. Warning: This
   option should only be used when it is known that topic map objects in the database with requested
   identities does not exist.

Changes in the database schema:

*  The following changes were done in the database schema because of the column name 'value' sometimes
   conflicting with the 'VALUE' SQL keyword. If you have database instances using the old database
   schema you need to perform the following changes the database schema:
*  Rename `TM_BASE_NAME.value` to `TM_BASE_NAME.content`
*  Rename `TM_VARIANT_NAME.value` to `TM_VARIANT_NAME.content`
*  Rename `TM_OCCURRENCE.value` to `TM_OCCURRENCE.content`

### Changes from 1.2.1 to 1.2.2 ###

#### Navigator Framework ####

This release adds some new tags, and fixes a large number of bugs. The changes in the tag libraries
are:

*  The new <tm:sourceLocators> tag for retrieving the source locators of a topic map object has been
   added.
*  The new <tm:reified> tag for retrieving the object reified by a topic has been added.
*  The <tm:filter> tag has a new attribute 'is', which can be used to filter objects according to
   whether they are topics, base names, occurrences, associations, or something
   else.
*  The <tm:filter> tag can now use instances of `DeciderIF` and not just `NavigatorDeciderIF`.
*  The 'sequence-index' variable set by the <logic:foreach> tag now begins with 1, rather than 0, as it
   used to.

The following bugs were fixed in the Navigator Framework in this release:

*  [Bug #334](http://www.ontopia.net/bugs/showbug.cgi?id=334): <logic:call> does not work inside
   modules.
*  [Bug #335](http://www.ontopia.net/bugs/showbug.cgi?id=335): <logic:externalFunction> should not call
   the function, just register it.
*  [Bug #339](http://www.ontopia.net/bugs/showbug.cgi?id=339): <output:name> and <tm:name> do not
   support variant name selection.
*  [Bug #342](http://www.ontopia.net/bugs/showbug.cgi?id=342): Navigator framework should support more
   than one content-type in the same web application.
*  [Bug #350](http://www.ontopia.net/bugs/showbug.cgi?id=350): Plug-ins are hardcoded to 'omnigator'
   directory.

The following bugs were fixed in the Omnigator in this release:

*  [Bug #320](http://www.ontopia.net/bugs/showbug.cgi?id=320): Reified topic map objects not displyed
   on topic.
*  [Bug #349](http://www.ontopia.net/bugs/showbug.cgi?id=349): Make Omnigator display better in
   Netscape 4.

#### Topic Map Engine ####

The following method has been added:

*  net.ontopia.topicmaps.core.TopicMapTransactionIF.getBuilder()

### Changes from 1.2 to 1.2.1 ###

#### Navigator Framework ####

Added the <framework:response> JSP tag, which sets response headers according to its attribute
values or the application defaults.

#### What's new in version 1.2 ####

Version 1.2 of the Ontopia Navigator Framework and the Ontopia Omnigator is a complete rewrite of
the entire navigator framework, and also of the Omnigator, which has now been implemented using the
new framework. The old navigator is still included in the distribution, but the documentation has
been taken out.

We have replaced the old tag libraries with a completely new set of tag libraries, which are
designed according to different principles. We think that the new tag libraries are enormously much
easier to learn than the previous ones, and that they are also much more flexible. The Omnigator has
been completely re-implemented (making the current version the fifth implementation from scratch)
using the new tag libraries.

Since the entire navigator package has been entirely replaced by a new package there is no list of
changes. Note that we expect changes to the navigator from this point on to be backwards-compatible,
except where current features are marked as experimental in the documentation.

#### Topic Map Engine ####

There were few changes in this new release. Some new functionality has been added, and some problems
have been fixed, but in general very little has happened with the engine.

Two improvements have been made:

*  when importing XTM documents, unknown elements in the XTM namespace are now warned about, and
*  MergeUtils did not copy source locators from source objects to target objects, but this is now
   fixed.

Four new classes have been added:

*  net.ontopia.topicmaps.utils.ImportExportUtils,
*  net.ontopia.topicmaps.utils.ReificationUtils,
*  net.ontopia.topicmaps.utils.AssociationBuilder, and
*  net.ontopia.topicmaps.utils.DuplicateSuppressionUtils.

Only one bugs were fixed in this release:

*  [Bug #259](http://www.ontopia.net/bugs/showbug.cgi?id=259); misspelling the name of the `topicMap`
   element causes `NullPointerException`s.
*  [Bug #305](http://www.ontopia.net/bugs/showbug.cgi?id=305); topic map ID sometimes set to file URI
   on export.
*  [Bug #308](http://www.ontopia.net/bugs/showbug.cgi?id=308); LTM reader did not handle duplicate
   subject indicators.

### Changes from 1.1.2 to 1.1.3 ###

#### Navigator Framework ####

Reviewed the classes in package nav.utils.comparators; BaseNameComparator deprecated, NameComparator
takes sort variant into account if available.

nav.context.UserFilterContextStore extended to also store variant name scoping themes (basides base
name scope)

#### Topic Map Engine ####

Occurrence.toString() now prints contents of inline occurrences.

CollectionUtils.getRandom() now delivers a really random entry.

Four bugs have been fixed:

*  [Bug #237 fixed](http://www.ontopia.net/bugs/showbug.cgi?id=237); export of empty and null strings
   generally improved,
*  [Bug #241 fixed](http://www.ontopia.net/bugs/showbug.cgi?id=241); XTMContentHandler and internal
   class ContentHandler of XMLConfigSource now use qualified name of element instead of local name in
   methods startElement and endElement,
*  [Bug #244 fixed](http://www.ontopia.net/bugs/showbug.cgi?id=244); subject indicator references from
   topics to reified topic map objects are no longer broken on export to XTM format,
   and
*  [Bug #245 fixed](http://www.ontopia.net/bugs/showbug.cgi?id=245); the ISO export can now handle the
   case where the PSI for display or sort name appears as a source locator instead of as a subject
   indicator.

### Changes from 1.1.1 to 1.1.2 ###

#### Navigator Framework ####

A new plug-in has been added: the "LTM add" plug-in, which lets you add content to already loaded
topic maps using LTM syntax.

A part of the documentation relating to how connect the navigator to the RDBMS backend was updated.

#### Topic Map Engine ####

The `LTMTopicMapReader` class now also implements the `TopicMapImporterIF` interface.

The `NameGrabber` class can now grab names appearing in a specific scope defined by a collection of
topics.

Some problems with the test suite, both on Windows and generally, have been fixed.

Two bugs have been fixed:

*  [Bug #231 (XTM element IDs not preserved on
   export)](http://www.ontopia.net/bugs/showbug.cgi?id=231), and
*  [Bug #230 (xml:base-related problem)](http://www.ontopia.net/bugs/showbug.cgi?id=230).

### Changes from 1.1 to 1.1.1 ###

#### Navigator Framework ####

The major change in this release is that we have upgraded the bundled version of tomcat, closing a
security hole. Other than that the main changes are bug fixes.

*  The tomcat version bundled with the navigator was upgraded from version 3.2.1 to 3.2.3. This makes
   the navigator start much faster, and also closes a serious security hole in
   tomcat.
*  [Bug #225 (Plug-ins don't work in Netscape 4.x)](http://www.ontopia.net/bugs/showbug.cgi?id=225) has
   been fixed.
*  [Bug #226 (Statistics table display problems in Netscape
   4.x)](http://www.ontopia.net/bugs/showbug.cgi?id=226) has been fixed.
*  The `ConfigReader` class has been improved to make it much more robust in cases where files are
   missing, configuration settings are missing or configuration files are screwed
   up.

#### Topic Map Engine ####

Only very minor changes have been made to the engine. These are:

*  [Bug #219 (Obscure merging bug)](http://www.ontopia.net/bugs/showbug.cgi?id=219) has been fixed.
*  [Bug #222 (Problem with null role players in
   merging)](http://www.ontopia.net/bugs/showbug.cgi?id=222) has been fixed.
*  The XMLConfigSource class was made more robust. It now gives much better error messages when
   problems occur.
*  Some new test cases have been added. Some of these test for the fixed bugs, others test new things.

### Changes from 1.0.x to 1.1 ###

#### Navigator Framework ####

##### Overview #####

Since version 1.1 the code of the Navigator Framework has been further improved through
optimizations, some internal refactoring of the code, and also a number of bug fixes. The result is
a version of the framework that is decidedly more mature and performs better. In particular, loading
topic maps is now substantially faster than in previous versions.

In addition to the improvements to the framework, the Omnigator has been revisited and its user
interface further improved. Some bugs have also been fixed in the Omnigator, making it able to
handle even more topic maps.

This release also contains substantially improved and extended documentation compared to the 1.0.x
releases.

The main improvements, however, fall in the category of extended functionality. The following are
the main extensions:

*  The Navigator framework has been extended to support a user context filter, which users can use to
   apply scope filtering to topic maps, in order to filter out unnecessary information or, for example,
   switch the language they are navigating in. The user context filter is described in more detail in
   *The Ontopia Omnigator User Guide*.
*  The Navigator has been extended with the concept of plug-ins, which are encapsulated functionality
   that can be dropped into Navigator applications. This concept is described in the *Ontopia Navigator
   Plug-ins Developer's Guide*.
*  The Navigator now has native support for the textual LTM topic map format (described in a separate
   technology note), which means that LTM files can now be loaded directly into Navigator
   applications.

The log4j package used by the engine to provide logging of actions has been upgraded from version
1.0.4 to version 1.1.3, which has given better performance in some cases.

##### Detailed changelog #####

The `application.xml` file has been extended to allow users to specify the default model, view, and
skin. It is also possible to extend the lists of models and views. Skins are now found by scanning a
specified directory for CSS stylesheets.

The `superTypes` tag now has an `excludeTypes` argument in the `args` attribute, which can be used
to keep the types of the current topic out of the list of supertypes.

The following new tags have been added:

*  DescriptionOccurrencesTag
*  FacetValueTypesTag
*  IndexScopesTag
*  ResourceFacetValuesTag
*  SubjectIndicatorsParentTopicsTag

See *The Ontopia Navigator Tag Library Reference* for more information on these tags.

#### Topic Map Engine ####

##### Overview #####

In general, the changes to the Topic Map Engine itself have not been very substantial. The main
changes have been made externally, such as the addition of an RDBMS backend, modifications to the
Navigator, integration of the Fulltext Search capability, and so on.

A number of optimizations have been done, however, resulting in markedly improved performance for
some operations, like importing XML topic maps. A number of minor bugs have also been fixed, and XML
import is now more robust, and also gives better error messages than before.

The `URILocator` class has been rewritten from scratch. The original was based on the
`java.net.URL`, which meant that it couldn't handle non-standard URI protocols or characters in URIs
that were not in the platform default character set. It was also too slow. The new implementation
solves all these problems.

The engine now has native support for the textual LTM topic map format (described in a separate
technology note), which means that LTM files can now be imported directly into the object model.
This support is found in the `net.ontopia.topicmaps.utils.ltm` package.

The log4j package used by the engine to provide logging of actions has been upgraded from version
1.0.4 to version 1.1.3, which has given better performance in some cases.

The test suite has been extended with about 150 new test cases, making it substantially more
comprehensive.

##### Detailed changelog #####

The main API changes have been in the `net.ontopia.topicmaps.utils` package, where the changes
listed below have been made. Some methods have also been deprecated and added; see the javadoc for
these. Generally the API changes have been quite limited.

*  Added the `NameGrabber` class in the package. This is a generally useful class used to pick out the
   in some sense most appropriate name from a topic.
*  Added the `TopicCharacteristicsGrabbers` class as a generalization of `DisplayNameGrabber`.
   `DisplayNameGrabber` was deprecated accordingly.
*  Added the `UnconstrainedScopeDecider` class as an easy way to make other scope deciders accept all
   objects in the unconstrained scope.


