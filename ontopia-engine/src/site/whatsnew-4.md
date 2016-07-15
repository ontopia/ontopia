Ontopia
=======

What's new - Ontopia 4.x
------------------------

<p class="introduction">
This document describes what has changed in Ontopia between releases, both at a higher level, and in
more detail.
</p>

<span class="version">5.3.0 2013-08-01</p>

### OKS 4.1.0 release notes ###

This release contains a major upgrade of Ontopoly, the Topic Maps editor. The internals of the
editor has to a major extent been rewritten and modularized. Most of the old functionality is still
there, and we have also added a lot new features:

*  The ontology model used by the editor is now represented in a way that allows it to be edited by
   Ontopoly itself. All editing of topic maps data, including the Ontopoly schema, now happen through
   the same editor components. The rationale for doing this has been to make it easier to extend the
   editor and repurpose its components in new contexts. Improving the embeddability of the editor has
   been an important goal of this release.
*  Fields are now first class citizens, and this makes it possible to say things about them. The fields
   are now represented as topics in the ontology model. This makes the model a lot more flexible as one
   can now say things about the fields and ask questions about them more easily.
*  Support for field views has been added. This lets you assign a field to one or more views, so that
   if you edit a topic in a particular view, only the fields declared on the topic type in that view
   are shown. This can be specified on the "Part of views" field on the name field, identity field,
   occurrence field, or role field topics.
*  One can now customize how a field is rendered in the different views, e.g. it can be read-only in
   one view, but editable in another. Links to topics can be traversable in some views, not not others,
   and so on. This can be specified on the "View mode" field on the name field, identity field,
   occurrence field, or role field topics. The available options are "Embedded", "Hidden", "Not
   traversable", and "Read-only".
*  Topics in association fields can now be edited in embedded mode. This lets you edit the referenced
   topics on the same page as the current one. There are no restrictions on how many nested embedded
   topics there can be. This can be configured per field and view. This feature has been used
   extensively in the Ontopoly ontology editor. All the grey boxes with borders are actually embedded
   field values, and thus topics in the topic map.
*  Given a field in a particular view, it is possible to specify the view referenced by the links to
   other topics. This allows one to change the view as the user navigates from topic to topic in the
   editor. This is specified through the "Value view" field on the role field
   topic.
*  Reordering fields in the topic type fields editor is now done through drag-and-drop.
*  Binary association field values are now sortable. The user can reorder field values using
   drag-and-drop. This can be enabled by checking the "Sortable" field on the role field
   topic.
*  One can now specify the create and selection policies on association fields. This lets one restrict
   how topics are selected for the field, and also how they are created and deleted. This is done
   through the "Edit modes" field on the role field topic. The options are "Existing values only", "New
   values only", "No edit", "Normal", "Owned values"
*  One can now define what happens when a new player topic is created on a field. This is done through
   the "Create actions" field on the role field topic. The options are: "Edit new topic in popup
   window", "Go to new topic", and "None".
*  It is possible to fully customize what topic types one can select or create instances of in a given
   context for a given field. This is done through the "Players query", "Players search query", and
   "Players types query" fields on the role field topic.

Other minor changes in this release are:

*  Ordering by tolog variable that contains objects of different types will now order topics relatively
   by name, the same behavour as when ordering a topic-only variable. The old behavior in this case was
   to order by object id. This has been done to reduce the confusion that occurs when topics suddenly
   appear unordered even though one said that they should be ordered.
*  The tolog query tracer is now properly thread safe and will keep a query trace stack per thread.
   This prevents multiple threads to interfere with each others query traces.
*  `jgroups-all.jar` has been upgraded to the JGroups 2.6.10.merge release.

### OKS 4.0.5 release notes ###

This release fixes several bugs and adds support for importing CTM files (ISO 13250-6: CTM (Compact
Notation)). The following bugs have been fixed:

*  [Bug #1963:](http://www.ontopia.net/bugs/showbug.cgi?id=1963) TMXMLWriter does not seem to support
   multityped topics.
*  [Bug #2161:](http://www.ontopia.net/bugs/showbug.cgi?id=2161) Broken encoding when exporting from
   Ontopoly
*  [Bug #2162:](http://www.ontopia.net/bugs/showbug.cgi?id=2162) Ontopoly User Guide: broken link
*  [Bug #2167:](http://www.ontopia.net/bugs/showbug.cgi?id=2167) MergeUtils overwrites existing topic
   map reifier
*  [Bug #2168:](http://www.ontopia.net/bugs/showbug.cgi?id=2168) Merging causes "Cannot reassign
   object" error
*  [Bug #2171:](http://www.ontopia.net/bugs/showbug.cgi?id=2171) Symmetric association types broken
*  [Bug #2173:](http://www.ontopia.net/bugs/showbug.cgi?id=2173) Instances tab should be default
*  [Bug #2175:](http://www.ontopia.net/bugs/showbug.cgi?id=2175) IndexOutOfBoundsException on
   association type config
*  [Bug #2178:](http://www.ontopia.net/bugs/showbug.cgi?id=2178) Multiple changes to same topic not
   handled correctly

### OKS 4.0.4 release notes ###

This release is a security release.

> **Warning**
> There is a security hole in earlier releases that made it possible to upload web archive files
> (.war) files remotely. Please upgrade to releases 3.4.7 or 4.0.4 as these do not have this
> problem.

> As an alternative to upgrading one can delete the directory
> `$OKS_HOME/apache-tomcat/server/webapps/manager` and the files
> `$OKS_HOME/apache-tomcat/conf/Catalina/localhost/manager.xml` and
> `$OKS_HOME/apache-tomcat/conf/tomcat-users.xml` before restarting the Apache Tomcat
> server.

*  [Bug #2166:](http://www.ontopia.net/bugs/showbug.cgi?id=2166) Tomcat allows remote exceution of
   arbitrary code

### OKS 4.0.3 release notes ###

This release is a bug fix release.

*  [Bug #2146:](http://www.ontopia.net/bugs/showbug.cgi?id=2146) Symmetric checkbox is disabled on
   unary association
*  [Bug #2147:](http://www.ontopia.net/bugs/showbug.cgi?id=2147) Omnigator export plug-in exports topic
   maps with the name null.xml
*  [Bug #2149:](http://www.ontopia.net/bugs/showbug.cgi?id=2149) Ordering by multi-typed variables
   fails with ClassCastException
*  [Bug #2150:](http://www.ontopia.net/bugs/showbug.cgi?id=2150) jing.jar is broken on Java 6, MacOSX
*  [Bug #2151:](http://www.ontopia.net/bugs/showbug.cgi?id=2151) TopicIF.remove() does not remove
   scoped objects
*  [Bug #2152:](http://www.ontopia.net/bugs/showbug.cgi?id=2152) Oracle Text full-text query fails
   because of datatype issue
*  [Bug #2158:](http://www.ontopia.net/bugs/showbug.cgi?id=2158) Oracle CLOB indexes does not work
   properly
*  [Bug #2160:](http://www.ontopia.net/bugs/showbug.cgi?id=2160) Negation fails with a
   NullPointerException
*  Apache Tomcat has been upgraded to version 5.5.27.

### OKS 4.0.2 release notes ###

This release is a bug fix release.

*  [Bug #2110:](http://www.ontopia.net/bugs/showbug.cgi?id=2110) Vizlet "further-associations boxes"
   not displayed when Untyped topics filtered out
*  [Bug #2134:](http://www.ontopia.net/bugs/showbug.cgi?id=2134) Selections on export screen not
   respected
*  [Bug #2135:](http://www.ontopia.net/bugs/showbug.cgi?id=2135) Default focus on instance page
*  [Bug #2136:](http://www.ontopia.net/bugs/showbug.cgi?id=2136) Modal browse page and
   InstanceTypesPage not hierarchical
*  [Bug #2137:](http://www.ontopia.net/bugs/showbug.cgi?id=2137) Enter keypress should trigger search
   button
*  [Bug #2138:](http://www.ontopia.net/bugs/showbug.cgi?id=2138) Omnigator Edit plug-in pointed to old
   Ontopoly
*  [Bug #2140:](http://www.ontopia.net/bugs/showbug.cgi?id=2140) Incorrect display of image fields in
   read-only mode
*  [Bug #2141:](http://www.ontopia.net/bugs/showbug.cgi?id=2141) LTMTopicMapWriter does not export
   external occurrences correctly
*  [Bug #2142:](http://www.ontopia.net/bugs/showbug.cgi?id=2142) Should not be able to create instances
   of read-only topic types
*  [Bug #2143:](http://www.ontopia.net/bugs/showbug.cgi?id=2143) Read-only association type not
   respected on instance page
*  [Bug #2144:](http://www.ontopia.net/bugs/showbug.cgi?id=2144) Vizlet cannot display rdbms topics
   without identifiers
*  [Bug #2145:](http://www.ontopia.net/bugs/showbug.cgi?id=2145) ClassCastException thrown when
   retrieving large read-only occurrence value

A new feature in Ontopoly is that the create button now opens the new topic in a modal window. This
was done to prevent navigating away from the current topic.

Note that the `backport-util-concurrent.jar` jar-file has been added to the distribution, so an
upgrade of an OKS application must include this file.

### OKS 4.0.1 release notes ###

This release is a bug fix release.

*  [Bug #2127:](http://www.ontopia.net/bugs/showbug.cgi?id=2127) Stack overflow in snapshot code
*  [Bug #2128:](http://www.ontopia.net/bugs/showbug.cgi?id=2128) Invalid action in old Ontopoly
   instance
*  [Bug #2129:](http://www.ontopia.net/bugs/showbug.cgi?id=2129) applications.xml is mandatory
*  [Bug #2130:](http://www.ontopia.net/bugs/showbug.cgi?id=2130) Ontopoly throws exception when
   association type has no roles
*  [Bug #2131:](http://www.ontopia.net/bugs/showbug.cgi?id=2131) Dynamic occurrence predicate filters
   by xsd:string and xsd:anyURI datatypes
*  [Bug #2132:](http://www.ontopia.net/bugs/showbug.cgi?id=2132) Value predicate returns values of
   datatype xsd:anyURI
*  [Bug #2133:](http://www.ontopia.net/bugs/showbug.cgi?id=2133) No match for association field

### OKS 4.0.0 release notes ###

This is a major new release. The main changes are complete support for the TMDM and XTM 2.0
specifications, clustering support and an Ontopoly reimplementation. Note that there are also
changes made to the public APIs that are not backward compatible. See below for more information on
how to handle this.

Other than the changes described below all other existing components of the OKS should continue to
work like before.

> **Warning**
> As the new TMDM support required substantial API changes this release does not guarantee backward
> compatibility. This means that existing applications that use the Java APIs must be recompiled
> before deployed with OKS 4.0.

> **Warning**
> Because the RDBMS database schema contains a lot of changes existing topic maps must be exported
> from the old schema and reimported into the new schema. Use the XTM 1.0 format to do this. (If
> you're upgrading from the 4.0 beta release you'll have to upgrade the database schema before using
> the final release.)

> **Warning**
> The OKS now requires Java 1.4 or newer.

#### New features ####

*  OKS now fully supports the standards `ISO 13250-2: Topic Maps - Data Model (TMDM)`, `ISO 13250-3:
   Topic Maps - XML Syntax (XTM 2.0)`, and `ISO 13250-4: CXTM (Canonicalization)`.
*  The OKS can now be clustered. It uses JGroups to notify members in the cluster about cache
   invalidation events. This allows different OKS instances accessing the same topic map to be run on
   multiple nodes in a cluster. Web-Editor locks can be clustered with Terracotta. See the `The RDBMS
   Backend Connector - Installation Guide` and `The Web Editor Framework - Developer's Guide` documents
   for more information on how to set it up.
*  Ontopoly has been reimplemented on top of the Apache Wicket framework, a component framework. The
   new version is more interactive as it uses AJAX technology to update parts of the user-interface
   instead of rerendering the page on every change. All changes made through the interface are
   immediately reflected on the server-side, so the old "Confirm" and "Reset" buttons are now history.
   The main rationale for reimplementing Ontopoly has been to make it more flexible and extensible. The
   old version used JSP and became difficult to maintain. New features are: support for HTML and image
   occurrence datatypes and that instances can be turned into types and vice-versa. Note that the user
   guide is not yet up to date with the new user interface.
*  XTM 2.0 import and export support has been added. XTM import will auto-detect the version used, and
   will support both versions transparently. XTM export defaults to XTM 2.0.
*  Support for datatypes on variant names and occurrences has been added. The datatype can now be
   specified when the value is being set. Any datatype can be used in the model, but note that few
   datatypes will not be explicitly supported by all Topic Maps software. API methods that refer to
   String and LocatorIF, but specifies no datatype, uses the datatypes
   `http://www.w3.org/2001/XMLSchema#string` and `http://www.w3.org/2001/XMLSchema#anyURI`
   respectively.
*  The most common data types can be found as constants in the DataTypes class.
*  DB2TM now supports arbitrary datatypes.
*  Occurrences and variant names can now have values of arbitrary length. Use the
   OccurrenceIF.setReader(Reader, long, LocatorIF) and VariantNameIF.setReader(Reader, long, LocatorIF)
   methods to set really long values. Binary values will have to be string encoded, e.g. using
   Base64.
*  The NameIndexIF index has been updated to support variants and their datatypes.
*  The OccurrenceIndexIF index has been updated to support occurrence datatypes.
*  The TopicMapBuilderIF interface has been changed so that invalid objects cannot be constructed. This
   was done as the TMDM specification states that partial objects are not valid. See the javadoc API
   documentation for more information.
*  The TopicMapBuilderIF and mutable metods in the object model now perform rigorous checks to verify
   that object models don't get polluted by cross-topic map references. Also, removed objects cannot be
   reattached to properties in the object model. In practice this new behaviour should prevent a whole
   series of possible bug scenarios.
*  The ReifiableIF interface has been added and objects that can be reified now implements this
   interface. Reification is now explicit in the object model. The old implicit reification (matching
   item identifiers and subject identifiers) is not explictly supported, except in XTM 1.0 import and
   export, which will translate between the new and old refication models. Use the TopicIF.getReified()
   method to see which object a topic reifies.
*  All LocatorIFs are now restricted to be of notation `URI`. The object model will throw a
   ConstraintViolationException if a non-URI locator is used. Other locator notations should be
   represented as datatypes instead.
*  Source locators are now called item identifiers in the model.
*  Subject addresses are now called subject locators in the model. Topics can now have multiple subject
   locators. The ConflictingSubjectHandlerIF interface has been removed because of
   this.
*  The BaseNameIF interface has been renamed to TopicNameIF. All methods using the name 'base name' has
   been changed accordingly.
*  AssociationRoleIF.setPlayer(TopicIF), TypedIF.setType(TopicIF) (not TopicNameIF) and the value
   setter methods on TopicNameIF, VariantNameIF and OccurrenceIF does not support null values and
   datatypes. Note that the XTM 1.0 and LTM importer will create a topic with the PSI
   `http://psi.ontopia.net/xtm/1.0/null-topic` to represent null values.
*  The new method TMObjectIF.remove() replaces the old TopicMapIF.removeTopic(TopicIF),
   TopicMapIF.removeAssociation(AssociationIF), TopicIF.removeBaseName(BaseNameIF),
   TopicIF.removeOccurrenceIF(OccurrenceIF), TopicNameIF.remove and VariantName(VariantNameIF)
   methods.
*  TopicMapIF.clear() replaces DeletionUtils.clearTopicMap(TopicMapIF).
*  The tolog query language will continue to work as before. There are no changes to the public
   interface. The `datatype` predicate has been added to allow one to query on the datatype of variants
   and occurrences.
*  Added method AssociationBuilder.makeAssociation(TopicIF, TopicIF, TopicIF, TopicIF) for creating
   associations with four roles.
*  The RDBMS database schema has been updated to support the new object model. Note that you will have
   to transision between the new and old schemas by exporting and importing the topic
   maps.

#### Removed features ####

*  The HyTM format is no longer supported.
*  Facets, facet values, mnemonics and effective scope are no longer part of the topic map engine model
   as these features are no longer part of the Topic Maps standards. They are also not seen as
   neccessary for the development of Topic Maps, hence their removal from the
   software.
*  The MultiTypedIF interface has been removed as it was considered superflous.
*  Most methods and classes that were marked as deprecated in earlier releases have been removed from
   the distribution. Make sure that you don't use any of the remaining once as these may be removed at
   a later time.
*  The TopicMapTransactionIF interface has been removed as all the useful methods have been moved to
   either TopicMapStoreIF and TopicMapIF.
*  The IndexManagerIF interface has been removed. Use the TopicMapIF.getIndex(String) method to
   retrieve the indexes instead. As all indexes are automatically kept up to date automatically the old
   interface was superflous.
*  The IndexIF interface is still there, but now only works as a marker interface as the old methods
   have been removed.
*  The TopicMapFactoryIF interface has been removed and replaced by the modified TopicMapBuilderIF
   interface.
*  The class DeletionUtils has been removed as the methods TMObjectIF.remove() and TopicMapIF.clear()
   replaces it.
*  The class ReificationUtils has been removed as the method TopicIF.getReified() and the ReifiableIF
   interface replaces it.
*  The LocatorIndexIF index has been removed. It is replaced by methods in NameIndexIF and
   OccurrenceIndexIF.
*  Support for the Topic Naming Constraint has been removed.
*  TopicMapIF.getObjectByItemIdentifier(LocatorIF) replaces
   TopicMapIF.getObjectBySourceLocator(LocatorIF).
*  TopicMapIF.getTopicBySubjectLocator(LocatorIF) replaces TopicMapIF.getTopicBySubject(LocatorIF).
*  TopicMapIF.getTopicBySubjectIdentifier(LocatorIF) replaces
   TopicMapIF.getTopicByIndicator(LocatorIF).
*  TMObjectIF.getItemIdentifiers(), TMObjectIF.addItemIdentifier(LocatorIF) and
   TMObjectIF.removeItemIdentifier(LocatorIF) replaces TMObjectIF.getSourceLocators(),
   TMObjectIF.addSourceLocator(LocatorIF) and
   TMObjectIF.removeSourceLocator(LocatorIF).
*  TopicIF.getSubjectLocators(), TopicIF.addSubjectLocator(LocatorIF),
   TopicIF.removeSubjectLocator(LocatorIF) replaces TopicIF.getSubject() and
   TopicIF.setSubject(LocatorIF).
*  TopicIF.getSubjectIdentifiers(), TopicIF.addSubjectIdentifier(LocatorIF) and
   TopicIF.removeSubjectIdentifier(LocatorIF) replaces TopicIF.getSubjectIndicators(),
   TopicIF.addSubjectIndicator(Locator) and
   TopicIF.removeSubjectIndicator(LocatorIF).

#### Other changes ####

Bug fixes:

*  [Bug #2120:](http://www.ontopia.net/bugs/showbug.cgi?id=2120) Ordering by length of a string causes
   exception
*  [Bug #2123:](http://www.ontopia.net/bugs/showbug.cgi?id=2123) NullPointerException in comparison
   query

The Web Editor framework actions have seen a few changes because of the changes to the object model.
The changes are:

*  AddExtOccurrence: the type parameter is now required.
*  AddIntOccurrence: the type parameter is now required.
*  RemoveType: now only works with TopicNameIF objects.
*  SetPlayer: the player parameter is now required.

### Older release notes ###

* [Ontopia 3.x release notes](whatsnew-3.html)
* [Ontopia 2.x release notes](whatsnew-2.html)
* [Ontopia 1.x release notes](whatsnew-1.html)
