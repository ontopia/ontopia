The Ontopia Navigator Framework
===============================

Configuration Guide
-------------------

<p class="introduction">
This document is a guide to configuring the Ontopia Navigator Framework. The Navigator Framework
offers a large number of configuration options, and this document explains where they can be found
and what they do. By looking in the actual configuration files you can find additional advanced
configuration options not documented here.
</p>

<span class="version">Ontopia 5.1 2010-06-09</p>

### Introduction ###

The only configuration you must have in order for the framework to function is a `tm-sources.xml`
file, which tells the framework where to find the topic maps. (How the `tm-sources.xml` file works
is explained in [tm-sources.xml](#tm-sources.xml).)

The default is that the `tm-sources.xml` file is loaded from the classpath. So for this to work, all
you need to do is to put such a file on the classpath somewhere. On Tomcat, a good location is in
`apache-tomcat/common/classes`.

If you just want to get the framework running you can stop here, because this is all you need for
that. The rest of this document explains alternative approaches for cases where you want to control
the setup more.

### tm-sources.xml ###

The Navigator Framework uses this file to find topic maps and enter them into the topic map
repository. The file basically contains a collection of sources, where each source can produce
references to topic maps. The next sections describe different types of sources that can be
configured in the file.

#### The path sources ####

These sources look in a specific directory of the file system for files with a particular suffix.
Those found will be made available to the Navigator Framework, which may then choose to load them.
Each source class expects files with a different syntax.

Below is shown an example configuration element for the XTM path source. The `path` parameter tells
the source what directory to look in. The `${CWD}` is a variable replaced by the path of the
directory where the `tm-sources.xml` file was found. The `suffix` parameter tells the source what
suffixes to look for on files.

**Example configuration**

````xml
    <source class="net.ontopia.topicmaps.xml.XTMPathTopicMapSource">
      <param name="path" value="${CWD}/../xml"/>
      <param name="suffix" value=".xtm"/>
    </source>
````

The full list of path source classes is:

net.ontopia.topicmaps.xml.XTMPathTopicMapSource
:    This source reads XTM 1.0 and 2.0 files.

net.ontopia.topicmaps.utils.ltm.LTMPathTopicMapSource
:    This source reads LTM files.

net.ontopia.topicmaps.xml.TMXMLPathTopicMapSource
:    This source reads [TM/XML](http://www.ontopia.net/topicmaps/tmxml.html) files.

net.ontopia.topicmaps.utils.ltm.RDFPathTopicMapSource
:    This source reads RDF files in the RDF/XML syntax, but can be adjusted to also read RDF files in
     other syntaxes.


There is also a larger set of parameters to these sources than just the `path` and `suffix` ones.
Below is a complete list of the parameters, together with an overview over which sources support
which parameters.

| Parameter | Values | Purpose | Sources | 
|---|---|---|---|
| path | Path | Path to the directory where the source will look for topic map files. The token ${CWD} can be used to refer to the directory of the source file to specify relative paths. | xtm ltm rdf tmxml | 
| suffix | String | The file name extension of files in the path directory which the source will make topic maps from. | xtm ltm rdf tmxml | 
| maintainFulltextIndexes | true/false | If true a full-text index for the topic map will be created and automatically kept up to date with changes in the topic map. Setting this to true for sources containing Ontopoly topic maps is highly recommended. | xtm ltm rdf tmxml | 
| indexDirectory | Path | Path to the directory where the source will create the full-text index for topic maps from this source. The token ${CWD} can be used to refer to the directory of the source file to specify relative paths. If not specified, the index directory defaults to the current directory of the Java process. | xtm ltm rdf tmxml | 
| maintainFulltextIndexes | true/false | If true a full-text index for the topic map will be created and automatically kept up to date with changes in the topic map. Setting this to true for Ontopoly topic maps is highly recommended. | xtm ltm rdf tmxml | 
| title | String | The name of the source, to be displayed in the drop-down list on the Ontopoly start screen where the user is asked to specify which source to create the new topic map in. (This list is only displayed if more than one source has supportsCreate set to true.) | xtm ltm rdf tmxml | 
| id | String | Unique identifier for this source within the repository. Used by the Ontopoly user interface. If an ID is needed, and none is set explicitly, the source will generate one automatically. | xtm ltm rdf tmxml | 
| duplicateSuppression | true/false | If 'true' will cause duplicate information to be removed from the topic map once it has finished loading. | xtm ltm rdf tmxml | 
| followTopicRefs | true/false | The default is true, which means that `topicRef` elements pointing to external files will cause those files to be loaded and merged in. If set to false no action is taken. | xtm | 
| validation | true/false | If 'true' any XTM files loaded through this source will be validated against the XTM 1.0 DTD. | xtm | 
| mappingFile | Path | References an RDF file which contains the RDF mapping statements to be used when loading RDF files with this source. The `${CWD}` token can be used here. | rdf | 
| supportsCreate | true/false | Sets whether or not the source allows new topic maps to be created within the source. This is used when creating new topic maps in Ontopoly. | xtm tmxml | 
| supportsDelete | true/false | Sets whether or not the source allows topic maps coming from the source to be deleted. This is used when deleting topic maps in Ontopoly. | xtm ltm rdf tmxml | 
| syntax | "RDF/XML"/"N3"/"N-TRIPLE" | The name of the RDF syntax to assume RDF files loaded with this source are written in. If not specified defaults to RDF/XML. | rdf | 
| generateNames | true/false | If "true" the RDF source will generate names for topics in the topic map which have no names. The names will be based on the URIs of the topics in the original RDF file. | rdf | 
| lenient | true/false | If "true" the RDF source allow errors in the RDF mapping that would be reported if the value were "false". | rdf | 

#### The RDBMSTopicMapSource ####

If you have the RDBMS Database Backend product you can use this class to make the Navigator
Framework find topic maps in your relational database. This source understands these
parameters:

propertyFile
:   This is the filename of the property file containing the RDBMS backend configuration settings. This
    file is documented in *The RDBMS Backend Connector: Installation Guide*.

baseAddress
:   This property overrides the base address of the topic map. This property must be a URI and the
    notation is assumed to be 'URI'. See the API documentation for `TopicMapStoreIF.getBaseAddress()`
    for more information.

Below is shown an example configuration element.

**Example configuration**

````xml
    <source class="net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapSource">
      <param name="propertyFile" value="/usr/local/ontopia/jdbc.prop"/>
      <param name="baseAddress" value="http://www.ontopia.net/topicmaps/foo.ltm"/>
    </source>
````

#### The RDBMSSingleTopicMapSource ####

If you have the RDBMS Database Backend product you can use this class to make the Navigator
Framework find an individual topic map in your relational database. This source understands these
parameters:

topicMapId
:   This is the id of the topic map as it is stored in the relational database. The id must be a
    number.

title
:   This is the title of the topic map. The title will be used in the Omnigator unless the topic map is
    reified and assigned a name.

referenceId
:   This property specifies the id that you can retrieve the topic map by in your navigator
    application.

propertyFile
:   This is the filename of the property file containing the RDBMS backend configuration settings. This
    file is documented in *The RDBMS Backend Connector: Installation Guide*.

baseAddress
:   This property overrides the base address of the topic map. This property must be a URI and the
    notation is assumed to be 'URI'. See the API documentation for `TopicMapStoreIF.getBaseAddress()`
    for more information.

Below is shown an example configuration element.

**Example configuration**

````xml
    <source class="net.ontopia.topicmaps.impl.rdbms.RDBMSSingleTopicMapSource">
      <param name="topicMapId" value="5001"/>
      <param name="title" value="The Foo Topic Map"/>
      <param name="referenceId" value="foo"/>
      <param name="propertyFile" value="/usr/local/ontopia/jdbc.prop"/>
      <param name="baseAddress" value="http://www.ontopia.net/topicmaps/foo.ltm"/>
    </source>
````

#### RDBMSPatternSingleTopicMapSource ####

A source producing a single topic map with a defined reference ID (Omnigator ID) by searching the
RDBMS for a matching topic map. If multiple topic maps match the given search pattern the one with
the highest ID is used. This source is useful when you have a topic map in the RDBMS which is
frequently deleted and re-imported. You can then use this source to avoid having to change
`tm-sources.xml` every time the topic map is reimported.

These are the parameters supported by the source:

id
:   The ID of the source. Must not be the same as any other source ID in the repository, but can
    otherwise be anything at all.

referenceId
:   The Omnigator ID of the topic map produced by the source. This is the ID used in your code to refer
    to the topic map.

propertyFile
:   The RDBMS properties file.

match
:   Which field in the database to match. It is best to set this to `title`.

pattern
:   The value to search for.

Below is an example configuration for this source:

````xml
<source class="net.ontopia.topicmaps.impl.rdbms.RDBMSPatternSingleTopicMapSource">
  <param name="id" value="pattern"/>
  <param name="referenceId" value="mytopicmap.xtm"/>
  <param name="propertyFile" value="rdbms.properties"/>
  <param name="match" value="title"/>
  <param name="pattern" value="mytopicmap.xtm"/>
</source>
````

If, when importing topic maps from file, using the following command will ensure that the title of
the topic map is set correctly, so that the source as configured above will pick it
up.

````
java net.ontopia.topicmaps.cmdlineutils.RDBMSImport \
  --title mytopicmap.xtm \
  rdbms.properties whateverfile.xtm
````

#### The URLTopicMapSource ####

This source will read a single topic map from a given URL rather than from the local file system. It
can be used to get a topic map from a remote web site, for example. It supports exactly the same
parameters as the XTM topic map source, but adds a few extra parameters. Below is an example of how
to use this source.

**Example configuration**

````xml
    <source class="net.ontopia.topicmaps.entry.URLTopicMapSource">
      <param name="title" value="Free XML Tools"/>
      <param name="referenceId" value="xmltools"/>
      <param name="url" value="http://www.garshol.priv.no/download/xmltools/xmltools-tm.xml"/>
      <param name="syntax" value="XTM"/>
    </source>
````

This example would load the Free XML Tools topic map from the [Free XML Tools web
site](http://www.garshol.priv.no/download/xmltools/). Note the required `syntax` parameter, which
tells the source what syntax to expect. Possible values for this parameter are XTM, LTM, HyTM, RDF,
RDF/XML, N3, and N-TRIPLE. The reference id is set to 'xmltools', so you can retrieve the topic map
in your navigator application using this id.

#### Relationship with the API ####

This section explains how the `tm-sources.xml` file corresponds to the API. Basically, the file
contains entries that are used to create `TopicMapSourceIF` objects (see the `entry` package
javadoc), which can find topic maps by various means and make them available to the Navigator
Framework.

In this file, each `source` element creates an instance of the class named in its `class` attribute.
The class must implement the `TopicMapSourceIF` interface. The `source` elements contain `param`
elements, which give configuration information to the source objects using Java bean
introspection.

The advantage of this approach is that with this configuration file one can easily make the
navigator find topic maps in the file system, in a database, or by any other imaginable means. Each
section below documents some `TopicMapSourceIF` implementations.

Note that if you wish to use this functionality in your own applications you can use the
`net.ontopia.topicmaps.entry.XMLConfigSource` class.

### Log4j logging ###

Ontopia uses [log4j](http://jakarta.apache.org/log4j/docs/manual.html) for its logging. You can also
find a little more information on log4j and how Ontopia uses it in the *[The Ontopia Topic Maps
Engine - Developer's Guide](../devguide.html)*. If you want to configure the log4j logging in
Navigator Framework applications, just put a `log4j.properties` file on the classpath, and Ontopia
will automatically pick it up.

### Sharing topic maps ###

There are three main ways of connecting an application with a repository of topic maps created by a
`tm-sources.xml` file:

*  The default approach, described in [Introduction](#introduction).
*  Using JNDI. This requires quite a bit of configuration, and is usually quite painful. We do not
   recommend this approach, and it is only supported for historical reasons. It is documented in
   [Sharing topic maps with JNDI](#sharing-topic-maps-with-jndi).
*  Using the `source_config` parameter, which refers directly to the `tm-sources.xml` file, and makes
   the application use its own repository of topic maps. (Described in
   [web.xml](#web.xml).)

In general, we strongly recommend using the default approach. If you want to use more than one
`tm-sources.xml` file, give the file a different name, or point to it directly, this is all
possible. In the standard `web.xml` configuration file you can specify the `topicmaps_repository_id`
parameter. This parameter references the `tm-sources.xml` file to be used in one of two
ways:

*  Using a file URL in the normal way, as in `file:/Users/larsga/config/tm-sources.xml`. This will
   cause that particular file to be loaded. All applications which reference the same file will share a
   repository of topic maps.
*  Using a classpath URL, as in `classpath:tm-sources.xml`, which is the default setting. This causes a
   file named `tm-sources.xml` to be loaded from the classpath. All applications which use the same
   reference will share a repository of topic maps.

#### Sharing topic maps with JNDI ####

> **Note**
> This approach is obsolete, and we do not recommend that you use it. The default approach is much
> better.

In order to use JNDI your web server must support JNDI and be configured to use it. How this is done
is server-dependent. Any number of named shared topic map repositories can be set up, and each web
application can choose whether to use its own repository or to use a named shared
repository.

To share topic maps between applications there are two steps to be performed:

 1.  Configure the web server to make set up the shared repository and make it available via JNDI. Note
    that all this does is enable the repository; it will not be used before the configurations of
    individual applications are changed.
 2.  Configure each web application you want to use the shared repository to use the repository instead
    of setting up its own. It's possible to have three applications in the same server where two use the
    shared repository and one does not, so each application must be configured
    separately.

The two sections below describe how to perform the two steps.

##### Setting up one or more shared repositories #####

This section describes how to set up a shared repository as a JNDI resource.

In Tomcat, shared repositories are set up by adding the following to the `server.xml` file, inside
the `Host` element.

*Tomcat 5.0.x:*

````xml
    <DefaultContext override="true">

      <Resource name="OmnigatorRegistry" auth="Container"
                type="net.ontopia.topicmaps.entry.SharedStoreRegistry"/>
 
      <ResourceParams name="OmnigatorRegistry">
        <parameter>
          <name>factory</name>
          <value>org.apache.naming.factory.BeanFactory</value>
        </parameter>
        <parameter>
          <name>registryName</name>
          <value>OmnigatorRegistry</value>
        </parameter>
      </ResourceParams>

    </DefaultContext>
````

*Tomcat 5.5.x:*

````xml
  <GlobalNamingResources>
    ...
    <Resource name="OmnigatorRegistry" auth="Container"
              type="net.ontopia.topicmaps.entry.SharedStoreRegistry"
              factory="org.apache.naming.factory.BeanFactory" />

  </GlobalNamingResources>
````

The name given to the resources in this example is *OmnigatorRegistry*. This name is the name used
to refer to the shared repository.

The shared repository will be configured by reading `tm-sources.xml` from the CLASSPATH as visible
to the application server internals. Be aware that there usually are several locations on which one
can put resources to make them visible for the application servers' class loaders. In Tomcat one
should normally put the `tm-sources.xml` file in the `common/classes` directory. In most situations
one would put the file in the same class loader context as the ontopia.jar
file.

You can make the shared repository read its configuration from another file by specifying the
`sourceLocation` parameter.

*Tomcat 5.0.x:*

````xml
      <ResourceParams name="OmnigatorRegistry">
        ...
        <parameter>
          <name>sourceLocation</name>
          <value>/tmp/tm-sources.xml</value>
        </parameter>
      </ResourceParams>
````

*Tomcat 5.5.x:*

````xml
  <GlobalNamingResources>
    ...
    <Resource name="OmnigatorRegistry" auth="Container"
              type="net.ontopia.topicmaps.entry.SharedStoreRegistry"
              factory="org.apache.naming.factory.BeanFactory" 
              sourceLocation="/tmp/tm-sources.xml" />

  </GlobalNamingResources>
````

Alternatively you can tell the SharedStoreRegistry resource what the resource name of the source
configuration file to load from the CLASSPATH is. This can be done by specifying the `resourceName`
parameter. The default value is `tm-sources.xml`. If you would like to set up multiple shared
repositories where more than one of them is to read their source configuration from the CLASSPATH
then the `resourceName` parameter will have to be given for all except one of
them.

Here is an example of how you can set up two shared repositories that both loads their source
configuration from the CLASSPATH:

*Tomcat 5.0.x:*

````xml
      <ResourceParams name="MyRegistry1">
        ...
        <parameter>
          <name>resourceName</name>
          <value>tm-sources1.xml</value>
        </parameter>
      </ResourceParams>

      <ResourceParams name="MyRegistry2">
        ...
        <parameter>
          <name>resourceName</name>
          <value>tm-sources2.xml</value>
        </parameter>
      </ResourceParams>
````

*Tomcat 5.5.x:*

````xml
  <GlobalNamingResources>

    <Resource name="MyRepository1" auth="Container"
              type="net.ontopia.topicmaps.entry.SharedStoreRegistry"
              factory="org.apache.naming.factory.BeanFactory" 
              resourceName="tm-sources1.xml" />

    <Resource name="MyRepository2" auth="Container"
              type="net.ontopia.topicmaps.entry.SharedStoreRegistry"
              factory="org.apache.naming.factory.BeanFactory" 
              resourceName="tm-sources2.xml" />

  </GlobalNamingResources>
````

With *Tomcat 5.5.x* one also has to relate the global naming source with individual web
applications. This is how it has been set up for the Omnigator web application:

````xml
  <Context path="/omnigator">
    <ResourceLink name="OmnigatorRegistry"
              global="OmnigatorRegistry"
              type="net.ontopia.topicmaps.entry.SharedStoreRegistry"/>
  </Context>
````

The `<Context>` elements can be added just right before the `</Host>` end-tag. You will need to add
one `<Context>` element per web application. Also make sure that the `path` attribute point to the
correct web application.

Note that any number of shared repositories can be specified. If you set up more than a single
shared repository make sure that you give them unique resource names and unique 'registryName'
parameters. You would also have to make sure that they load their configuration from different
locations. See the description of the `sourceLocation` parameter above.

Setting up a shared repository in the [Resin](http://www.caucho.com/products/resin/) application
server is done by adding the following to the `resin.conf` file, inside the `host`
element:

````xml
  <resource-ref>
    <res-ref-name>OmnigatorRegistry</res-ref-name>
    <res-type>net.ontopia.topicmaps.entry.SharedStoreRegistry</res-type>
  </resource-ref>
````

##### Making an application use the shared repository #####

In order to make a web application use a shared repository, edit the `web.xml` file of that
application to remove the `source_config` parameter, and replace it by the
following:

````xml
  <context-param>
    <param-name>jndi_repository</param-name>    
    <param-value>OmnigatorRegistry</param-value>
  </context-param>
````

The value of the parameter is the JNDI resource name of the shared repository, as given in the
`server.xml`/`resin.conf` file.

### web.xml ###

This file is part of the Java Servlet system and contains information used by the servlet system, as
well as simple name/value configuration properties for individual servlet
applications.

The following configuration settings are used by the Navigator Framework:

source_config
:   Tells the navigator where to find the configuration file used to find out how to locate topic maps.
    See [tm-sources.xml](#tm-sources.xml).

app_config
:   Tells the navigator where to find the application configuration file, which is documented in
    [application.xml](#application.xml). The default value is
    `WEB-INF/config/application.xml`.

plugins_rootdir
:   Tells the navigator in what directory the plug-ins are located, if you want to change it from the
    default.

topicmaps_repository_id
:   References the topic maps repository to be used, as described in [Sharing topic
    maps](#sharing-topic-maps).

jndi_repository
:   This setting is used instead of `source_config` when topic maps are shared across web applications
    with JNDI (see [Sharing topic maps with JNDI](#sharing-topic-maps-with-jndi)). This setting is
    obsolete.

### application.xml ###

This configuration file stores the configuration for a Navigator web application, and by modifying
this file it is possible to control many aspects of how the application
behaves.

> **Note**
> Note that this file is obsolete. You do not need it.

#### Autoloads ####

The Navigator uses the information in the source.xml file ([tm-sources.xml](#tm-sources.xml)) to
populate the topic map registry with topic map references. It does not load any of these topic maps
before the user requests it through the manage page, however. The autoloads section can be used to
request that some topic maps be loaded when the Navigator starts up, without the user having to
explicitly request it.

Below is shown the default autoloads section:

**Autoloads**

````xml
  <autoloads>
    <autoload topicmapid="opera.ltm" />
    <!-- autoload topicmapid="xmltools-tm.xtm" /-->
  </autoloads>
````

The topic map IDs used here are the file names of the topic maps, including the suffix. The special
ID `~all` can be used to make the system automatically load all topic maps it finds. (Note that this
can cause slow startups, and if you have too many large topic maps it may cause the server to run
out of memory during startup.)

#### Model/View/Skin ####

With this section you can control what models, views, and skins are to be made available to users
(including ones that you develop yourself), and you can also set the default model, view, and skin.
The `model`, `view`, and `skin` elements in the `mvs` element each make available a model, a view,
or a skin on the customize page. They can have the following attributes:

name
:   The ID of the model, view, or skin, used in the file names and also as a general identifier for the
    model/view/skin.

title
:   The title to be displayed to the user.

default
:   If set to `yes` it makes this model/view/skin the default.

#### Properties ####

In this section one can set a number of configuration options that control how the Navigator
Framework behaves. These options are all simple named string values. The ones currently in use
are:

defaultCharacterEncoding
:   This property controls the character encoding in which the Navigator Framework writes all its
    output. The default value is 'utf-8'. If you get problems with international characters you most
    likely need to change this value to the name of your national character encoding. If you have
    problems with this, please contact
    [support@ontopia.net](mailto:support@ontopia.net).

defaultContentType
:   This property controls the HTTP content type which the Navigator Framework declares on its output
    pages via the `framework:response` tag. The default value is 'text/html', and as long as you are
    only making ordinary web applications you do not need to change it. If you want to make web
    applications which output WML or XML you should change this property to make sure that user agents
    treat the output correctly.

allowLoadOnRequest
:   Controls whether topic maps are autoloaded when requested or not. If this property is set to `true`
    topic maps that have not previously been loaded through the manage page will be loaded automatically
    when someone tries to access them.
:   This differs from the autoload section in that topic maps listed there will be loaded when the
    server starts, before anyone has requested them. If this option is on, any topic maps that have not
    been loaded but which are requested by a user will be loaded automatically. If this option is turned
    off the topic maps in the autoloads section will still be loaded on startup.

pluginsOrder_foo
:   This property defines the order of the plug-ins in a particular group (the group being the ID
    appearing after the underscore in the property name). The value is a whitespace-separated list of
    plug-in IDs.

nameStringNonExistent
:   This is the string displayed by `output:name` for topics which have no basenames.

nameStringNullValue
:   This is the string displayed by `output:name` when the most appropriate name of a topic has the
    value `null`.

nameStringEmptyValue
:   This is the string displayed by `output:name` when the most appropriate name of a topic has an empty
    string as its value.

##### Obsolete properties #####

> **Warning**
> There are also some properties used by the old Navigator Framework tag libraries that are now
> deprecated. These are listed below, but note that these apply only to tags that are
> deprecated.

maxListLength
:   The `logic:foreach` tag stops after the number of iterations set here (default 500) in order to keep
    the framework from producing near-endless lists. By changing this value you can change the cutoff
    point.

checkForChangedModules
:   If set to `true` the `logic:include` tag will check if the referenced module file has been updated
    since the last time the tag was executed every time it is executed. If it has changed the module
    will be reloaded. If set to `false` the module will be loaded on startup, but never again. The
    default is `false`.

occurrenceEmptyValue
:   This is the string displayed by `output:content` for occurrences whose value is an empty string.

occurrenceNullValue
:   This is the string displayed by `output:content` for occurrences whose value is `null`.

#### Class shortnames ####

In several places in the tag libraries classes are referred to. Often the same classes have to be
referred to in many different places, and it can quickly get tedious to repeat the same long
classnames many times throughout the source code. To help solve this problem we've added a section
to the `application.xml` file called the classmap. Using this shortnames for classes can be defined,
and classes can then be referred to in the JSP pages using these shortnames. Below is shown an
example of a minimal classmap section, which should show how it is used.

````xml
  <classmap>

    <class shortcut="topicComparator"
           fullname="net.ontopia.topicmaps.nav.utils.comparators.TopicComparator"/>

  </classmap>
````


