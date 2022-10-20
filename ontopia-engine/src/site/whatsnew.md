Ontopia
=======

What's new - Ontopia 5.x
------------------------

<p class="introduction">
This document describes what has changed in Ontopia between releases, both at a higher level, and in
more detail.
</p>

<span class="version">5.4.0 2022-10-20</p>

### Ontopia 5.4.0 release notes ###

The following changes have been made:

*  Migrated from GoogleCode to [GitHub](https://github.com/ontopia/ontopia)
*  Upgraded to Java 8
*  The distribution is now based on Tomcat 9
*  Enabled [Codacy integration](https://app.codacy.com/gh/ontopia/ontopia/) to improve the code quality
*  Made Ontopia packages available in the [Maven Central repository](https://search.maven.org/search?q=net.ontopia)
*  Remade the documentation
*  Removed antiquated XML parsers
*  Removed the use of `LocatorIF` as file and classpath resource reference.
   Many IO related interfaces have changed to use `File`, `URL` or `InputStream` as data sources.
*  Aligned reification handling by merging all logic into `DuplicateReificationException`
*  Added `RDBMSTopicMapSource.setQueryfile` to allow additional SQL queries to be provided for expert use
*  Added `ImportExportServiceIF` that allows extension to provide alternative topicmap readers and writers
*  Moved RDF related code into `ontopia-rdf` module
*  Added `FulltextImplementationIF` that allows extensions to provide alternative full-text indexing methods
*  Deprecated and moved OSL Schema files into separate module
*  Moved JDBC Spy files into separate module
*  Improved cross-transaction merge handling
*  `&lt;template:get&gt;` tag now supports fallback value
*  Applied `AutoClosable` interface on `TopicmapStoreIF`, `TopicMapReferenceIF`, `TopicMapSourceIF` and `QueryResultIF` 
*  Added `OccurrenceIndexIF.getOccurrences` methods based on value and type
*  Added `NameIndexIF.getTopicNames` methods based on value and type
*  A lot of code quality improvements
*  Added Ontopia REST module
*  Added `ClassInstanceIndexIF.getRolesByType` based on roletype and associationtype
*  Removed `TopicMapImporterIF`
*  Removed `Locators`
*  Removed antiquated concurrency dependency
*  Added `ClassInstanceIndexIF.getAllTopicNames`, `ClassInstanceIndexIF.getAllVariantNames` and `ClassInstanceIndexIF.getAllOccurrences`
*  Added `IdentifierIndexIF`
*  Replaced Ontopia specific utility classes with java functional classes where possible. Moved the replaced files into 
   a separate [ontopia-deprecated-utils module](https://github.com/ontopia/ontopia/tree/master/ontopia-deprecated-utils). The following replacements have been performed:
    *  `DeciderIF` &rarr; `Predicate`
    *  `GrabberIF` &rarr; `Function`
    *  `StringifierIF` &rarr; `Function`, with `String` result
*  Switched from log4j1 to reload4j to fix log4shell vulnerabilities
*  Improved default RDBMS connection pooling settings and added automatic returning of abandoned connections
*  Disabled autoCommit on read only connections
*  Refactored how non-transactional reading works and added cleaning of connections used for this
*  Switched to TMAPI tests 2.1.1


#### The following bugs have been fixed:
*  [Issue 7: Avoid duplicating test data](https://github.com/ontopia/ontopia/issues/7)
*  [Issue 50: Making ontopia.jar available in public Maven repositories](https://github.com/ontopia/ontopia/issues/50)
*  [Issue 94: Document how to log into user administration](https://github.com/ontopia/ontopia/issues/94)
*  [Issue 112: XTM 1.0 importer: Wrong handling of xml:base](https://github.com/ontopia/ontopia/issues/112)
*  [Issue 161: Omnigator full-text uses hard-wired path to indexes](https://github.com/ontopia/ontopia/issues/161)
*  [Issue 206: Omnigator does not load sources with German umlauts](https://github.com/ontopia/ontopia/issues/206)
*  [Issue 225: net.ontopia.topicmaps.impl.tmapi2.LocatorImpl.toExternalForm NullPointerException](https://github.com/ontopia/ontopia/issues/225)
*  [Issue 235: maven pom.xml log4j](https://github.com/ontopia/ontopia/issues/235)
*  [Issue 273: Crimson dependency should be removed](https://github.com/ontopia/ontopia/issues/273)
*  [Issue 317: Cannot load topic maps if Ontopia directory contains non-ascii characters](https://github.com/ontopia/ontopia/issues/317)
*  [Issue 347: Topic delete fails with StackOverflowError](https://github.com/ontopia/ontopia/issues/347)
*  [Issue 348: NPE in DB2TM for topic maps without base locator](https://github.com/ontopia/ontopia/issues/348)
*  [Issue 382: Tolog:out using JSP attributes has an incorrect test](https://github.com/ontopia/ontopia/issues/382)
*  [Issue 410: Ontopoly editor code does not conform to maven standards](https://github.com/ontopia/ontopia/issues/410)
*  [Issue 424: Tolog does not allow 'ë' in a subject identifier](https://github.com/ontopia/ontopia/issues/424)
*  [Issue 484: Checksum mismatches for 5.3.0 artifacts](https://github.com/ontopia/ontopia/issues/484)
*  [Issue 485: Release 5.3.0 is missing javadoc jars in maven repository](https://github.com/ontopia/ontopia/issues/485)
*  [Issue 486: Review if jstl and standard taglibs are needed as compile dependencies for navigator](https://github.com/ontopia/ontopia/issues/486)
*  [Issue 489: Review the use of backport concurrent dependency](https://github.com/ontopia/ontopia/issues/489)
*  [Issue 492: The value-like predicate causes are memory based lucene index if a SearcherIF is not present](https://github.com/ontopia/ontopia/issues/492)
*  [Issue 493: LTMPathTopicMapSource.createTopicMap creates an XTM file with LTM content](https://github.com/ontopia/ontopia/issues/493)
*  [Issue 494: LTMTopicMapWriter fails to write RMDBS topicmap with empty valued occurrences](https://github.com/ontopia/ontopia/issues/494)
*  [Issue 495: XTMTopicMapReader fails with NPE on validation error, hiding the real error](https://github.com/ontopia/ontopia/issues/495)
*  [Issue 498: Outdated docs published on ontopia.net](https://github.com/ontopia/ontopia/issues/498)
*  [Issue 499: Merging causes database inconsistenties](https://github.com/ontopia/ontopia/issues/499)
*  [Issue 500: Incorrect query for RDBMSSearcher when using Oracle](https://github.com/ontopia/ontopia/issues/500)
*  [Issue 503: DeletionUtils.removeDependencies(TopicIF): docs vs implementation](https://github.com/ontopia/ontopia/issues/503)
*  [Issue 504: tolog DynAssocPred: Useless check for assoc type](https://github.com/ontopia/ontopia/issues/504)
*  [Issue 505: tolog: Bug in Pair.equals implementation](https://github.com/ontopia/ontopia/issues/505)
*  [Issue 508: tolog: Typo in RolePlayerPredicateTest](https://github.com/ontopia/ontopia/issues/508)
*  [Issue 509: tolog: basic.TopicPredicate holds reference to an unused index](https://github.com/ontopia/ontopia/issues/509)
*  [Issue 510: ClassInstanceIndex implementations differ](https://github.com/ontopia/ontopia/issues/510)
*  [Issue 511: tolog: value-like impl.: Variable is never used](https://github.com/ontopia/ontopia/issues/511)
*  [Issue 515: IteratorIterator fails on empty iterator](https://github.com/ontopia/ontopia/issues/515)
*  [Issue 516: Commit 2d931be385cced9458a615f04283894d1fc4aab3 breaks omnigator](https://github.com/ontopia/ontopia/issues/516)
*  [Issue 517: Xml parser removal, fixing #273](https://github.com/ontopia/ontopia/issues/517)
*  [Issue 519: All collections returned by basic.ClassInstanceIndex should be unmodifiable](https://github.com/ontopia/ontopia/issues/519)
*  [Issue 522: Omnigator fulltext indexes not working](https://github.com/ontopia/ontopia/issues/522)
*  [Issue 525: OnDemandValue is never unreleased, could trigger exception](https://github.com/ontopia/ontopia/issues/525)
*  [Issue 526: Read only topicmap stores cause leaking connections in combination with concurrency](https://github.com/ontopia/ontopia/issues/526)
*  [Issue 535: JDBC Connections not returned to pool on store close](https://github.com/ontopia/ontopia/issues/535)
*  [Issue 536: ClassInstanceIndexIF.usedAs* methods return true for a null value](https://github.com/ontopia/ontopia/issues/536)
*  [Issue 539: OccurrenceIndexIF.getOccurrencesLessThanOrEqual RDBMS query misses equals](https://github.com/ontopia/ontopia/issues/539)
*  [Issue 551: Ontopia gives error on build](https://github.com/ontopia/ontopia/issues/551)
*  [Issue 554: DB2TM test case FullRescanEventTest depends on previous test data](https://github.com/ontopia/ontopia/issues/554)
*  [Issue 557: Topicmaps.org is using https, causing DTD resolving to fail](https://github.com/ontopia/ontopia/issues/557)

#### The following new features have been added:
*  [Issue 45: Use Findbugs in build process](https://github.com/ontopia/ontopia/issues/45)
*  [Issue 175: Write a Selenium test suite for Ontopoly](https://github.com/ontopia/ontopia/issues/175)
*  [Issue 319: tolog:set request parameter does not resolve PSI's ](https://github.com/ontopia/ontopia/issues/319)
*  [Issue 491: Ontopia should upgrade to support java 7](https://github.com/ontopia/ontopia/issues/491)
*  [Issue 512: The &lt;template:get&gt; tag could use a fallback value.](https://github.com/ontopia/ontopia/issues/512)
*  [Issue 513: Ontopia on Github?](https://github.com/ontopia/ontopia/issues/513)
*  [Issue 514: Transform docbook documentation into markdown](https://github.com/ontopia/ontopia/issues/514)
*  [Issue 523: Ontopia should upgrade to Java 8](https://github.com/ontopia/ontopia/issues/523)
*  [Issue 524: Make it compile on JDK 1.8 (generics fixes)](https://github.com/ontopia/ontopia/issues/524)
*  [Issue 527: Make ontopia compilable in java 8](https://github.com/ontopia/ontopia/issues/527)
*  [Issue 528: Add Travis-CI for Ontopia](https://github.com/ontopia/ontopia/issues/528)
*  [Issue 529: Add ClassInstanceIndexIF.getRolesByType(rt, at)](https://github.com/ontopia/ontopia/issues/529)
*  [Issue 533: Refactor IO to avoid using LocatorIF as resource](https://github.com/ontopia/ontopia/issues/533)
*  [Issue 534: Apply apache commons where possible](https://github.com/ontopia/ontopia/issues/534)
*  [Issue 549: Switch from Github Services to webhooks](https://github.com/ontopia/ontopia/issues/549)
*  [Issue 553: Test GitHub actions](https://github.com/ontopia/ontopia/issues/553)


#### The following dependencies have been upgraded:
*  [Issue 502: Upgrade JGroups dependency](https://github.com/ontopia/ontopia/issues/502)
*  [Issue 501: Upgrade commons-collections dependency](https://github.com/ontopia/ontopia/issues/501)
*  [Issue 496: Ontopia should upgrade to support servlet specifications 3.0](https://github.com/ontopia/ontopia/issues/496)
*  [Issue 497: Ontopia should upgrade to Tomcat 7](https://github.com/ontopia/ontopia/issues/497)
*  [Issue 518: Upgraded to Tomcat 7, fixes 497.](https://github.com/ontopia/ontopia/issues/518)
*  [Issue 530: Modularize Lucene and upgrade to newer version](https://github.com/ontopia/ontopia/issues/530)
*  [Issue 531: Upgrade to Lucene 6.5.0](https://github.com/ontopia/ontopia/issues/531)
*  [Issue 532: Upgrade to Jena 3.2.0](https://github.com/ontopia/ontopia/issues/532)
*  [Issue 548: [Security] Upgrade PDFBox to 1.8.16](https://github.com/ontopia/ontopia/issues/548)
*  [Issue 547: [Security] Upgrade PDFBox to 1.8.12](https://github.com/ontopia/ontopia/issues/547)
*  [Issue 546: [Security] Upgrade to Lucene 7.1.0](https://github.com/ontopia/ontopia/issues/546)
*  [Issue 550: [Security] Upgrade to commons-fileupload 1.3.3](https://github.com/ontopia/ontopia/issues/550)
*  [Issue 552: Bump pdfbox from 1.8.16 to 2.0.15](https://github.com/ontopia/ontopia/issues/552)
*  [Issue 559: Bump junit from 4.12 to 4.13.1](https://github.com/ontopia/ontopia/issues/559)
*  [Issue 560: Bump poi from 3.12 to 3.17](https://github.com/ontopia/ontopia/issues/560)
*  [Issue 561: Bump commons-io from 2.5 to 2.7](https://github.com/ontopia/ontopia/issues/561)
*  [Issue 562: Bump pdfbox from 2.0.15 to 2.0.24](https://github.com/ontopia/ontopia/issues/562)
*  [Issue 567: Bump poi-scratchpad from 3.12 to 5.2.1](https://github.com/ontopia/ontopia/issues/567)
*  [Issue 568: Bump poi from 3.17 to 4.1.1](https://github.com/ontopia/ontopia/issues/568)
*  [Issue 569: Bump jgroups from 3.4.4.Final to 4.0.0.Final](https://github.com/ontopia/ontopia/issues/569)
*  [Issue 570: Bump axis2 from 1.6.2 to 1.6.3](https://github.com/ontopia/ontopia/issues/570)
*  [Issue 571: Add a Codacy badge to README.md](https://github.com/ontopia/ontopia/issues/571)
*  [Issue 572: Bump axis2.version from 1.6.2 to 1.6.3](https://github.com/ontopia/ontopia/issues/572)


### Ontopia 5.3.0 release notes ###

This release is a bug fix and cleanup release and adds the new `NameIF` interface.

The following changes have been made:

*  Added `NameIF` interface to support operations that can return any name.
*  More generics were added to the Ontopia interfaces
*  The dependencies to patched artifacts hosted by the Ontopia repository (jing, trove and touchgraph)
   have been refactored to their respected pulic counterparts. This means the Ontopia repository is no
   longer present in the POM.
*  Removed the `PoolableSetFactoryIF`, `PoolableSetIF` and `SetPoolIF` interfaces and their
   implementing classes.
*  Improved the closing of `TopicMapSourceIF` objects when closing a `TopicMapRepositoryIF` object
   fixing possible memory leaks and clustering issues.
*  Added missing implementations for persisting reference title and base address changes in an RDBMS
   environment
*  Added functionality to LTM parser that allows reification based on a prefixed PSI
*  Added functionality to LTM parser that allows predefined PSI prefixes to be used when exporting a
   topic map
*  A long standing issue regarding difference of topic map loading in Navigator and Engine has been
   resolved. (issue 392)
*  Added fallback-value functionality to `tolog:id`, `tolog:oid` and `tolog:oid`
*  Improved tag pool handling in navigator tags, also added tests for this.
*  Fixed the export of topic maps within Ontoploy (issue 466)
*  Fixes missing classes in the Vizlet (issue 450)
*  Upgraded to SLF4J 1.7.5
*  Fixed issues in the TMRAP-webapp that were blocking the Axis SOAP service. Note that the presence of
   Crimson on the classpath will still stop TMRAP from working as a SOAP service.

The following bugs have been fixed:

*  [Issue 56: Automate distribution uploading](https://github.com/ontopia/ontopia/issues/56)
*  [Issue 66: Remove dependency to patched 3rd-party
   libs](https://github.com/ontopia/ontopia/issues/66)
*  [Issue 70: Testing after installation failures](https://github.com/ontopia/ontopia/issues/70)
*  [Issue 224: Ontopia could show version number](https://github.com/ontopia/ontopia/issues/224)
*  [Issue 232: Execute RDBMS tests during build](https://github.com/ontopia/ontopia/issues/232)
*  [Issue 392: NavigatorApplication.getTopicMapRepository() returns different repository object then
   TopicMaps.getTopicMapRepository() for the same sources
   file](https://github.com/ontopia/ontopia/issues/392)
*  [Issue 406: Cant build ontopia from scratch](https://github.com/ontopia/ontopia/issues/406)
*  [Issue 422: ontopia/pom.xml contains duplicate maven-javadoc-plugin
   dependency](https://github.com/ontopia/ontopia/issues/422)
*  [Issue 432: Error in NextPreviousOptimizer](https://github.com/ontopia/ontopia/issues/432)
*  [Issue 446: Export in Ontopoly produces 0 byte file, Export in Ominigator produces correct
   result](https://github.com/ontopia/ontopia/issues/446)
*  [Issue 450: the failure when use "Visualize"](https://github.com/ontopia/ontopia/issues/450)
*  [Issue 452: LTM reification should support prefix:suffix
   notation](https://github.com/ontopia/ontopia/issues/452)
*  [Issue 454: LTMTopicMapWriter should support user defined
   prefixes](https://github.com/ontopia/ontopia/issues/454)
*  [Issue 457: Cannot create symmetric associations with the browse
   dialog](https://github.com/ontopia/ontopia/issues/457)
*  [Issue 460: Omnigator documentation specifies incorrect path for topicmaps
   location](https://github.com/ontopia/ontopia/issues/460)
*  [Issue 465: Upgrade slf4j to 1.6 or higher](https://github.com/ontopia/ontopia/issues/465)
*  [Issue 466: TMRAP webapp fails to start due to missing axis
   class](https://github.com/ontopia/ontopia/issues/466)
*  [Issue 467: tolog:out, :oid and :id tags should support fallback
   value](https://github.com/ontopia/ontopia/issues/467)
*  [Issue 468: RDBMSTopicMapSource should implement close()
   method](https://github.com/ontopia/ontopia/issues/468)
*  [Issue 469: TopicMapSourceIF could use close()
   method](https://github.com/ontopia/ontopia/issues/469)
*  [Issue 470: RDBMSTopicMapReference.setTitle is not
   persisted](https://github.com/ontopia/ontopia/issues/470)
*  [Issue 471: RDBMSTopicMapReference should implement methods to get and set base
   address](https://github.com/ontopia/ontopia/issues/471)
*  [Issue 473: RDBMSTopicMapSource.refresh might leave references
   open](https://github.com/ontopia/ontopia/issues/473)
*  [Issue 474: Documentation pages are missing tables of contents and section
   numbering](https://github.com/ontopia/ontopia/issues/474)
*  [Issue 475: RDBMS clustering throws exception when shared cache is
   off](https://github.com/ontopia/ontopia/issues/475)
*  [Issue 476: JGroups fails to include sub-dependency
   commons-logging](https://github.com/ontopia/ontopia/issues/476)
*  [Issue 477: SimpleClassifier holds files open](https://github.com/ontopia/ontopia/issues/477)
*  [Issue 479: Ability to build Ontopia without need for port
   8080](https://github.com/ontopia/ontopia/issues/479)
*  [Issue 480: PoolableSet[Factory][IF] and SetPool[IF] classes are unused and should be
   removed](https://github.com/ontopia/ontopia/issues/480)
*  [Issue 481: XTMTopicMapWriter does not add xlink
   namespace](https://github.com/ontopia/ontopia/issues/481)
*  [Issue 482: Ontopia distribution contains duplicate
   libraries](https://github.com/ontopia/ontopia/issues/482)
*  [Issue 483: Revision r2338 exposes error in tolog SetTag when used with tag
   pooling](https://github.com/ontopia/ontopia/issues/483)

### Ontopia 5.2.2 release notes ###

This release is a bug fix release and introduces some new API methods for `TopicIF`.

The following changes have been made:

*  `TopicIF` now has the new optimized methods `getOccurrencesByType()`, `getTopicNamesByType()`,
   `getAssociations()` and `getAssociationsByType()`. The RDBMS implementation uses SQL queries for
   these methods to increase performance.
*  A long standing issue regarding merging of occurrence and name types in RDBMS topicmaps has been
   fixed (issue 409)
*  Statistics of a topicmap can now be obtained from the new `StatisticsIndexIF` index
*  `TMXMLWriter` now supports setting prefixes beforehand

The following bugs have been fixed:

*  [Issue 447: The PostgreSQL specific searcher implementation is broken due to deprecated function
   use](https://github.com/ontopia/ontopia/issues/447)
*  [Issue 448: Topicmap statistics should be available though
   API](https://github.com/ontopia/ontopia/issues/448)
*  [Issue 449: TMXMLWriter should support user defined
   prefixes](https://github.com/ontopia/ontopia/issues/449)
*  [Issue 411: TopicIF should have a getOccurrencesByType(TopicIF type)
   method](https://github.com/ontopia/ontopia/issues/411)
*  [Issue 409: MergeUtils fails to merge names of name and occurrences types that do not have a subject
   identifier](https://github.com/ontopia/ontopia/issues/409)

### Ontopia 5.2.1 release notes ###

This release is a minor bug fix release, which includes the fixed vizigator and omnigator vizlet.

The following bugs have been fixed:

*  [Issue 438: Vizigator is not starting on Mac!](https://github.com/ontopia/ontopia/issues/438)
*  [Issue 439: TopicNameComparator might throw
   NullPointerException](https://github.com/ontopia/ontopia/issues/439)
*  [Issue 440: bat file pointing to noneixisting jar file in
   5.2.0](https://github.com/ontopia/ontopia/issues/440)

### Ontopia 5.2.0 release notes ###

This is the first release in the new Maven structure. It includes the modularization of Ontopia
along with bug fixes along with some new functionality.

The following changes have been made:

*  Ontopia is now divided into Maven modules based functionality. For developers working with Ontopia
   as a dependency this means that there is a more controlled way of including parts of Ontopia as a
   dependency. This change does not affect Ontopia distribution users.
*  The distribution has been updated to include Tomcat version 6.
*  The DB2TM functionality has been extended and improved.
*  Ontopoly had several outstanding bugs. Support for exporting TM/XML and schema without data was
   added.
*  Tolog now supports negative integer values and some basic numeric operations through the numbers
   module.
*  Ontopia now uses Lucene 2.9.4 (up from 2.2.0).

The following bugs have been fixed:

*  [Issue 41: CVS tags in source code](https://github.com/ontopia/ontopia/issues/41)
*  [Issue 59: Modularize the product](https://github.com/ontopia/ontopia/issues/59)
*  [Issue 60: Tweak the directory-structure to add initial Maven2
   support](https://github.com/ontopia/ontopia/issues/60)
*  [Issue 152: Document access control in Ontopoly](https://github.com/ontopia/ontopia/issues/152)
*  [Issue 189: Query text box in omnigator could be
   larger](https://github.com/ontopia/ontopia/issues/189)
*  [Issue 285: URIUtils.getURI to support resources](https://github.com/ontopia/ontopia/issues/285)
*  [Issue 307: Omnigator doesn't show correct "untyped" name for
   topics](https://github.com/ontopia/ontopia/issues/307)
*  [Issue 325: Ontopoly date and datetime fields don't allow user
   input](https://github.com/ontopia/ontopia/issues/325)
*  [Issue 339: export topicmap without data](https://github.com/ontopia/ontopia/issues/339)
*  [Issue 344: CanonicalXTMWriterTestGenerator is not included in test configuration + testdata
   baseline is outdated](https://github.com/ontopia/ontopia/issues/344)
*  [Issue 363: Document DB2TM changelog improvement](https://github.com/ontopia/ontopia/issues/363)
*  [Issue 368: Ontopoly 5.1.3 fails to add Subject identifier Identity for Topic
   classes](https://github.com/ontopia/ontopia/issues/368)
*  [Issue 369: Ontopoly - Removing fields from topic type
   definition](https://github.com/ontopia/ontopia/issues/369)
*  [Issue 378: Apache Tomcat should be updated to
   5.5.33](https://github.com/ontopia/ontopia/issues/378)
*  [Issue 380: Class.forName uses wrong classloader](https://github.com/ontopia/ontopia/issues/380)
*  [Issue 381: StreamUtils.readString might return incorrect
   result](https://github.com/ontopia/ontopia/issues/381)
*  [Issue 383: Not all TMAPI2 test are ran in the trunk, and fail when
   enabled](https://github.com/ontopia/ontopia/issues/383)
*  [Issue 384: LTM export of RDBMS readonly topicmap causes
   ClassCastException](https://github.com/ontopia/ontopia/issues/384)
*  [Issue 385: tolog subject-locator predicate returns wrong costs iff topic is bound and IRI is
   unbound](https://github.com/ontopia/ontopia/issues/385)
*  [Issue 386: tolog reifies predicate returns wrong
   costs](https://github.com/ontopia/ontopia/issues/386)
*  [Issue 387: Docs "The Built-in tolog Predicates" - Wrong example for the reifies
   predicate](https://github.com/ontopia/ontopia/issues/387)
*  [Issue 389: coalesce() predicate in ontopia 5.1.3 doesn't allow unbound second
   argument](https://github.com/ontopia/ontopia/issues/389)
*  [Issue 389: coalesce() predicate in ontopia 5.1.3 doesn't allow unbound second
   argument](https://github.com/ontopia/ontopia/issues/389)
*  [Issue 390: Allow CTM topic map sources should be able to have their full-text indexes maintained
   just like XTM and LTM topic
   maps](https://github.com/ontopia/ontopia/issues/390)
*  [Issue 391: QueryUtils circumvents query processor caching for non-tolog
   languages](https://github.com/ontopia/ontopia/issues/391)
*  [Issue 393: JFlex is an unnecessary compile/runtime
   dependency](https://github.com/ontopia/ontopia/issues/393)
*  [Issue 395: Test-specific methods in FileUtils](https://github.com/ontopia/ontopia/issues/395)
*  [Issue 397: Documentation for Ontopia Web Editor framework is
   broken.](https://github.com/ontopia/ontopia/issues/397)
*  [Issue 399: Build date is wrong](https://github.com/ontopia/ontopia/issues/399)
*  [Issue 407: Ontopia distribution doesn't contain
   xerces.jar](https://github.com/ontopia/ontopia/issues/407)
*  [Issue 408: Cannot sort on numbers](https://github.com/ontopia/ontopia/issues/408)
*  [Issue 412: Ontopoly instances page takes very long to load on types with a lot of
   intsances](https://github.com/ontopia/ontopia/issues/412)
*  [Issue 413: The Tomcat download link returns a 404](https://github.com/ontopia/ontopia/issues/413)
*  [Issue 415: Build Ontopia from tomcat.zip, not
   tomcat.tar.gz](https://github.com/ontopia/ontopia/issues/415)
*  [Issue 417: Ontopoly access right 'NONE' gives a user read-write
   rights](https://github.com/ontopia/ontopia/issues/417)
*  [Issue 418: StringUtils.compare() should be
   case-sensitive](https://github.com/ontopia/ontopia/issues/418)
*  [Issue 434: NumbersModuleTest fails due to locale](https://github.com/ontopia/ontopia/issues/434)

### Ontopia 5.1.3 release notes ###

This is a minor bugfix release that mostly fixes bugs, but also adds some fairly minor new
functionality.

The following changes have been made:

*  The `TopicMapFragmentWriterIF` interface has been added, providing a common interface to fragment
   exporters.
*  An RDF/XML fragment exporter has been added.
*  Major performance improvement in the retrieval of possible role players in Ontopoly.
*  The reordering of dynamic association predicates in tolog has been improved.
*  An Omnigator plugin for running DB2TM synchronization has been added.
*  Error reporting when DB2TM functions fail has been improved.
*  Several new utility methods have been added to the `KeyGenerator` and `MergeUtils` classes.

The following bugs have been fixed:

*  [Issue 316: No events for new role players](https://github.com/ontopia/ontopia/issues/316)
*  [Issue 313: No events when associations are deleted](https://github.com/ontopia/ontopia/issues/313)
*  [Issue 334: Incorrect encoding of characters in
   URIs](https://github.com/ontopia/ontopia/issues/334)
*  [Issue 343: Multiple conformance errors in CTM
   parser](https://github.com/ontopia/ontopia/issues/343)
*  [Issue 344: CXTM conformance tests were disabled](https://github.com/ontopia/ontopia/issues/344)
*  [Issue 356: The literal "1.0" not allowed in CTM](https://github.com/ontopia/ontopia/issues/356)
*  [Issue 360: CTM parser doesn't accept fractional seconds and
   timezones](https://github.com/ontopia/ontopia/issues/360)

### Ontopia 5.1.2 release notes ###

This is a minor bugfix release that fixes two issues in Ontopoly.

*  [Issue 320: Geotagging in Ontopoly: failed searches give no
   response](https://github.com/ontopia/ontopia/issues/320)
*  [Issue 322: Ontopoly: list of "other topic maps" in random
   order](https://github.com/ontopia/ontopia/issues/322)
*  [Issue 323: Ontopoly: association search popup
   broken](https://github.com/ontopia/ontopia/issues/323)

### Ontopia 5.1.1 release notes ###

The main new feature in this release is the support for geotagging in Ontopoly. Briefly, if the
longitude and latitude occurrences are added to a topic type, all topics of that type will get a
button to open a geotagging window. In this window a position on a map can be chosen, and this
position will be written into the occurrences.

Further, pluggable support for authorization and authentication was added to Ontopoly, as described
[in the wiki](https://github.com/ontopia/ontopia/wiki/OntopolyHowTo).

Beyond that this release features mainly the following bug fixes:

*  [Issue 48: Ontopoly repository out of sync with the underlying topic map
   repository](https://github.com/ontopia/ontopia/issues/48)
*  [Issue 105: Instance list in Ontopoly no longer displayed as tree when there is no
   tree](https://github.com/ontopia/ontopia/issues/105)
*  [Issue 240: Problem with character " (quotation mark) in
   Ontopoly](https://github.com/ontopia/ontopia/issues/240)
*  [Issue 259: XTM 2.1 export is now available in
   Ontopoly](https://github.com/ontopia/ontopia/issues/259)
*  [Issue 275: LTM add-fragment support in TMRAP was
   broken](https://github.com/ontopia/ontopia/issues/275)
*  [Issue 277: Fix to handling of equal parameters to
   rules](https://github.com/ontopia/ontopia/issues/277)
*  [Issue 283: RDBMS schema no longer allows NULLs](https://github.com/ontopia/ontopia/issues/283)
*  [Issue 287: tolog updates in Omnigator had no effect on
   RDBMS](https://github.com/ontopia/ontopia/issues/287)
*  [Issue 292: Topic selection in hierarchy didn't work in
   Ontopoly](https://github.com/ontopia/ontopia/issues/292)
*  [Issue 294: tolog did not optimize rule sets](https://github.com/ontopia/ontopia/issues/294)
*  [Issue 302: Cost estimator in value-like predicate caused
   errors](https://github.com/ontopia/ontopia/issues/302)
*  [Issue 305: Merging away sort and display topics in LTM caused
   errors](https://github.com/ontopia/ontopia/issues/305)
*  [Issue 318: Save icon visible with RDBMS backend](https://github.com/ontopia/ontopia/issues/318)

### Ontopia 5.1.0 release notes ###

This release introduces some major new functionality in Ontopia:

*  The automated classification is now available through public APIs and a command-line client. See the
   automatic classification developer's guide in the documentation index for more
   details.
*  The `INSERT`, `UPDATE`, `DELETE`, and `MERGE` statements have been added to the tolog query
   language, allowing it to also perform updates. See the section on updates in the tolog language
   tutorial for more information.
*  Support for XTM 2.1 has been added, together with some new classes to support it. Note that the
   `XTMVersion` class has been introduced to provide identifiers for the different
   versions.
*  Support for [JTM 1.0](http://www.cerny-online.com/jtm/1.0/) import and export has been added.
*  The CTM importer has now been updated to the final CTM specification.
*  Ontopoly can now be embedded in other applications using jQuery, as shown in the
   `examples/embedded-jquery.jsp` example in the Ontopoly distribution.
*  The query plugin in Omnigator now supports pluggable query language implementations, and also
   supports tolog updates.

In addition, some API changes have been made:

*  The core API now uses generics, making it much easier to use.
*  `TopicNameIF.getType()` never returns null any more, since [the default name
   type](http://www.isotopicmaps.org/sam/sam-model/#d0e2429) now is always represented by a
   topic.
*  The `coalesce` predicate has been added to tolog (see [the predicate
   reference](http://www.ontopia.net/doc/5.1.0/doc/query/predicate-reference.html#p-coalesce)).
*  Some new methods have been added to the `QueryWrapper` class in the
   `net.ontopia.topicmaps.query.utils` package.
*  The `makeKey(ReifiableIF r)` method has been added to the `KeyGenerator` class in the
   `net.ontopia.topicmaps.utils` package.
*  `mergeInto` methods for all `TMObjectIF`s have been added to the `MergeUtils` class in the
   `net.ontopia.topicmaps.utils` package.
*  The `getFastSortNameStringifier` method has been added to the `TopicStringifiers` class in the
   `net.ontopia.topicmaps.utils` package.
*  The TMRAP `add-fragment` request now supports CTM input.

The following bugs are fixed in this release:

*  [Issue 135](https://github.com/ontopia/ontopia/issues/135): Omnigator: NullPointerException in
   getScopeDecider
*  [Issue 264](https://github.com/ontopia/ontopia/issues/264): Query plugin in Omnigator doesn't handle
   non-latin-1 characters correctly
*  [Issue 254](https://github.com/ontopia/ontopia/issues/254): tolog "reifies" predicate returns error
   on finding refiers of a topic
*  [Issue 244](https://github.com/ontopia/ontopia/issues/244): DB2TM schema disallows iid/slo prefixes,
   but they actually work
*  [Issue 238](https://github.com/ontopia/ontopia/issues/238): Exception on instance topic in Ontopoly
*  [Issue 237](https://github.com/ontopia/ontopia/issues/237): Interning of locators caused out of
   memory error in the engine even when there was more memory left
*  [Issue 231](https://github.com/ontopia/ontopia/issues/231): TopicType not serializable exception in
   Ontopoly
*  [Issue 228](https://github.com/ontopia/ontopia/issues/228): TMAPI TopicMap/TopicMapSystem.close()
   doesn't commit current transaction
*  [Issue 214](https://github.com/ontopia/ontopia/issues/214): TMAPI ScopedIndex throws
   NullPointerException
*  [Issue 209](https://github.com/ontopia/ontopia/issues/209): TMAPI Variants allow empty scope
*  [Issue 208](https://github.com/ontopia/ontopia/issues/208): Ontopoly should not clear field when
   cardinality is 0-1 or 1-1 and field has multiple values
*  [Issue 193](https://github.com/ontopia/ontopia/issues/193): DB2TM synchronization strips topic type
*  [Issue 184](https://github.com/ontopia/ontopia/issues/184): Cannot disassociate second topic type of
   an instance in Ontopoly
*  [Issue 176](https://github.com/ontopia/ontopia/issues/176): Omnigator: ClassNotFoundException when
   exporting to RDF
*  [Issue 132](https://github.com/ontopia/ontopia/issues/132): Copy assotiation type does not work
   correctly in Ontopoly
*  [Issue 121](https://github.com/ontopia/ontopia/issues/121): Ontopoly: field list updated incorrectly
   when subtyping
*  [Issue 99](https://github.com/ontopia/ontopia/issues/99): tolog should not count null values
*  [Issue 93](https://github.com/ontopia/ontopia/issues/93): tolog reordering optimizer makes query
   much slower
*  [Issue 238](https://github.com/ontopia/ontopia/issues/238): InvalidQueryException when viewing
   certain topics in Ontopoly
*  [Issue 216](https://github.com/ontopia/ontopia/issues/216): DB2TM makePSI function strips out chars
   9 and z
*  [Issue 208](https://github.com/ontopia/ontopia/issues/208): Problem with hierarchy-walker
   optimization in tolog
*  [Issue 207](https://github.com/ontopia/ontopia/issues/207): tolog topic and association predicates
   do not filter correctly
*  [Issue 143](https://github.com/ontopia/ontopia/issues/143): Bad syntax error reporting in LTM
   reader
*  [Issue 111](https://github.com/ontopia/ontopia/issues/111): XTM 1.0 importer uses non-standard PSIs
*  [Issue 267](https://github.com/ontopia/ontopia/issues/267): Broader/narrower association type names
   in the wrong direction in MyThesaurus.xtm
*  [Issue 252](https://github.com/ontopia/ontopia/issues/252): Add update-topic method to SOAP version
   of TMRAP

Note that a minor modification has been made to the Ontopia database schema on MySQL in order to fix
a failing test. See [issue 163](https://github.com/ontopia/ontopia/issues/163) for a description of
the problem and how to upgrade older databases.

### Ontopia 5.0.2 release notes ###

This is a bug fix release, specifically released in order to make the fix in [revision 486
(23e327d29029f653729374a1f3118fef8441191e in
github)](https://github.com/ontopia/ontopia/commit/23e327d29029f653729374a1f3118fef8441191e)
available to a customer who needed this fix. The fix solves performance problems with tolog
inference rules parsed into `DeclarationContextIF` objects.

A few other very minor fixes are also included in this version, which is otherwise very similar to
5.0.1.

### Ontopia 5.0.1 release notes ###

This is a micro release, containing bug fixes and minor improvements throughout the product. Please
consult the lists below to see the actual changes.

The following changes have been made:

*  [Support for tolog.properties added](https://github.com/ontopia/ontopia/issues/13) ([blog
   post](http://ontopia.wordpress.com/2009/08/31/tolog-properties/))
*  [Refactored Vizigator to simplify embedding](https://github.com/ontopia/ontopia/issues/72)
*  [Dependency on Apache ORO removed](https://github.com/ontopia/ontopia/issues/81)
*  [Ontopoly can now be built as a .jar file](https://github.com/ontopia/ontopia/issues/114)
*  [Ontopia now runs on Google
   AppEngine](http://ontopia.wordpress.com/2009/08/21/ontopia-on-google-appengine/), thanks to two
   small fixes ([118](https://github.com/ontopia/ontopia/issues/118),
   [114](https://github.com/ontopia/ontopia/issues/114))
*  Minor improvements in [TologSpy](https://github.com/ontopia/ontopia/wiki/TologTips) to make reports
   more readable
*  [tolog can now count to zero](https://github.com/ontopia/ontopia/issues/80)
*  [tolog plug-in in Omnigator can show query plan without running
   query](https://github.com/ontopia/ontopia/issues/104)
*  New version of Italian Opera topic map.

The following bugs have been fixed:

*  [Synchronization problem in DB2TM fixed](https://github.com/ontopia/ontopia/issues/30)
*  [XTM 1.0 crash on duplicate non-topic objects](https://github.com/ontopia/ontopia/issues/77)
*  [XTM 2.0 crash on duplicate non-topic objects](https://github.com/ontopia/ontopia/issues/91)
*  [NullPointerException on names in TMAPI 2.0](https://github.com/ontopia/ontopia/issues/85)
*  [TMSync doesn't synchronize reifying topics](https://github.com/ontopia/ontopia/issues/88)
*  [Reification of sub-topic maps carred into main TM in XTM
   2.0](https://github.com/ontopia/ontopia/issues/117)
*  [Cannot log in to new installation](https://github.com/ontopia/ontopia/issues/15)

### Ontopia 5.0.0 release notes ###

The biggest change with this release is a business change: the product is now open source, and the
name has changed from the Ontopia Knowledge Suite to just Ontopia. A consequence of this change is
that the license key restrictions are now completely gone from the product. You no longer need a
valid license key to run Ontopia.

The following changes have been made:

*  Ontopia now requires Java 1.5.
*  The old TMAPI 1.0 support has been removed, and support for TMAPI 2.0 added in its place.
*  The tolog query engine now has a new optimizer. See [the
   blog](http://ontopia.wordpress.com/2009/06/18/new-reordering-optimizer/) for more
   details.
*  The `net.ontopia.topicmaps.query.utils.TologSpy` class has been added, providing a jdbcspy-like
   query profiler for tolog queries.
*  The `net.ontopia.topicmaps.utils.QNameRegistry` and `QNameLookup` classes have been added, providing
   convenient lookup of topics using qnames.
*  Ontopia now uses the [Simple Logging Facade for Java (SLF4J)](http://www.slf4j.org/), which makes it
   easy to switch logging tools, if desired.
*  The old net.ontopia.product classes have been removed, in favour of a single new class named
   `net.ontopia.Ontopia`.
*  The old net.ontopia.infoset.fulltext API is no longer supported and will be removed soon. The
   full-text search in tolog will continue to work.
*  The command-line tool `net.ontopia.topicmaps.cmdlineutils.Canonicalizer` has been updated to output
   ISO CXTM instead of Ontopia's old non-standard canonical format.

The following bugs have been fixed:

*  [Issue 2:](https://github.com/ontopia/ontopia/issues/2) template definitions crash CTM parser
*  [Issue 3:](https://github.com/ontopia/ontopia/issues/3) Name typing breaks on import into Ontopoly
*  [Issue 9:](https://github.com/ontopia/ontopia/issues/9) Nontopoly model in Omnigator broken
*  [Issue 17:](https://github.com/ontopia/ontopia/issues/17) Exception thrown in Ontopoly when clicking
   Add on "Add or remove topic type" when no topic type selected
*  [Issue 20:](https://github.com/ontopia/ontopia/issues/20) Exporting topic maps without schema broken
   in Ontopoly
*  [Issue 28:](https://github.com/ontopia/ontopia/issues/28) Merging lost item identifiers if they were
   the same as some subject identifier
*  [Issue 36:](https://github.com/ontopia/ontopia/issues/36) Ontopoly export without schema loses
   description occurrence
*  [Issue 37:](https://github.com/ontopia/ontopia/issues/37) TMSync does not synchronize reification
*  [Issue 58:](https://github.com/ontopia/ontopia/issues/37) VizDesktop doesn't work with Sun 1.5 JRE
   under Gnome

### Older release notes ###

* [Ontopia 4.x release notes](whatsnew-4.html)
* [Ontopia 3.x release notes](whatsnew-3.html)
* [Ontopia 2.x release notes](whatsnew-2.html)
* [Ontopia 1.x release notes](whatsnew-1.html)
