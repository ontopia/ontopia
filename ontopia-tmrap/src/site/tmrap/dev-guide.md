TMRAP
=====

Developer's Guide
-----------------

<p class="introduction">
This document introduces the TMRAP web services interface to Ontopia and shows how to develop
solutions using it. It introduces both the plain HTTP interface and the SOAP
interface.
</p>

<span class="version">Ontopia 5.1 2010-06-09</p>

### Introduction ###

TMRAP is a web service interface to Ontopia which makes it possible to retrieve Topic Maps fragments
from a remote Topic Maps server and also to modify the topic maps stored on the server. The
interface consists of a number of methods which can be accessed either using plain HTTP or using
SOAP. The functionality is the same in both cases; the only difference is in how the methods are
accessed.

The methods provided by TMRAP are:

get-topic
:    Returns a Topic Maps fragment describing a single topic. This is useful to get more information
     about specific topics with known identities.

get-topic-page
:    Returns information about web pages for viewing/editing a given topic on the server. This is useful
     for creating links to topics in an application that runs on a different server.

get-tolog
:    Allows tolog queries to be run on the server, and returns the query results either as structured XML
     or as a Topic Maps fragment.

tolog-update
:    Runs a tolog update statement on the server, modifying one topic map.

add-fragment
:    Adds a Topic Maps fragment to a topic map on the server.

delete-topic
:    Deletes a specific topic from a topic map on the server.


The next part of this guide describes the methods in detail, and is followed by a section that
describes how to use the two different TMRAP interfaces (SOAP and plain HTTP).

More background on the purpose and design of TMRAP can be found in [the TMRA'05
paper](http://dx.doi.org/10.1007/11676904_5) that introduced version 1.0 of TMRAP. [The slides from
the presentation](http://www.informatik.uni-leipzig.de/~tmra05/PRES/LMGa.pdf) may also be
useful.

### Method reference ###

This section presents each of the TMRAP methods in more detail, showing all of the parameters and
behaviours. What is described here applies to both the plain HTTP interface and the SOAP interface.
In order to understand how the methods here are invoked using the two interfaces, see [The HTTP
interface](#the-http-interface) and [The SOAP interface](#the-soap-interface).

For many of the methods it's possible to specify which syntax the result should be returned in. The
identifiers used for the syntaxes supported by Ontopia are given below.

application/x-xtm
:    This is for the XTM 1.0 syntax, which is always the default.

text/x-tmxml
:    This is for the TM/XML syntax, about which more below.

text/x-ltm
:    This is the identifier for the LTM syntax.

text/x-ctm
:    This is the identifier for the CTM syntax.


The TM/XML syntax is an XML syntax for Topic Maps that is easier to process with XSLT than XTM is,
and so may be more convenient to use when the TMRAP client is not using Topic Maps software. Like
TMRAP, TM/XML is described in [a TMRA'05 paper](http://dx.doi.org/10.1007/11676904_19), for which
there is also [slides](http://www.informatik.uni-leipzig.de/~tmra05/PRES/GB.pdf). A short background
and introduction can also be found in [this blog
posting](http://www.garshol.priv.no/blog/18.html).

#### get-topic ####

This method returns a topic map fragment representing the topic identified by the parameters to the
method. The parameters to the method are shown in the table below.

| Parameter | Req.? | Repeat? | Type | Description | 
|---|---|---|---|---|
| item | no | yes | URI | An item identifier of the requested topic. | 
| subject | no | yes | URI | A subject locator of the requested topic. | 
| identifier | no | yes | URI | A subject identifier of the requested topic. | 
| topicmap | no | yes | string | The ID of a topic map being queried. | 
| syntax | no | no | string | The syntax in which to return the topc map fragment. The default is XTM, but TM/XML is also supported (and LTM is not). Note that the values supplied must be the syntax identifiers defined above. | 
| view | no | no | string | The view to use when creating the fragment. The default (and only permitted value in this version) is 'stub'. | 

Fragments can be retrieved in two different ways: if the `topicmap` parameter is given the
identified topic maps are queried, but if it is not the topic maps currently loaded on the server
will be queried. The fragment will represent a single topic formed from all the topics matching one
of the three URI parameters in some queried topic map. (The server may have to merge topics in order
to produce this result, but it will *not* modify the topic maps on the server.)

The fragment returned for a topic contains all its identifiers, names, occurrences, and
associations, but only identifiers are given for topics referenced from these.

#### get-topic-page ####

This method returns a topic map fragment for a topic that describes it in terms useful for linking
to the topic on the TMRAP server from some other web application. For more information on how this
method can be used, see [the original TMRAP
presentation](http://www.ontopia.net/topicmaps/materials/Towards%20Seamless%20Knowledge.ppt). The
parameters to the method are listed below.

| Parameter | Req.? | Repeat? | Type | Description | 
|---|---|---|---|---|
| item | no | yes | URI | An item identifier of the requested topic. | 
| subject | no | yes | URI | A subject locator of the requested topic. | 
| identifier | no | yes | URI | A subject identifier of the requested topic. | 
| topicmap | no | yes | string | The ID of a topic map being queried. | 
| syntax | no | no | string | The syntax in which to return the topic map fragment. The default (and only supported value) is XTM. Note that the values supplied must be the syntax identifiers defined above. | 

The set of topics found is produced in the same way as for the `get-topic` method (see
[get-topic](#get-topic)), but the topics are not merged. Instead, a small topic map describing the
topics and the web pages they can be accessed through is returned. The topic map is best explained
with an example. Let's say we start the Tomcat server in the Ontopia distribution, load `opera.ltm`,
and ask for the topic page for Russia. The result would be the following topic map (except the IDs,
which have been edited to make them more readable).

````ltm
#PREFIX tmrap @"http://psi.ontopia.net/tmrap/"

[ontopia : tmrap:server = "Ontopia local installation"]

[opera : tmrap:topicmap = "The Italian Opera Topic Map"]
   {opera, tmrap:handle, [[opera.ltm]]}
tmrap:contained-in(ontopia :  tmrap:container, opera : tmrap:containee)
tmrap:contained-in(opera : tmrap:container, view : tmrap:containee)
tmrap:contained-in(opera : tmrap:container, edit : tmrap:containee)

[view : tmrap:view-page
   %"http://localhost:8080/omnigator/models/topic_complete.jsp?tm=opera.ltm&id=458"]
[edit : tmrap:edit-page
   %"http://localhost:8080/ontopoly/enter.ted?tm=opera.ltm&id=458"]

[russia = "Russia"
    @"http://www.topicmaps.org/xtm/1.0/country.xtm#RU"]
````

This topic map describes the server, the topic map, the topic, and the pages on which the topic can
be accessed by a user.

#### get-tolog ####

The `get-tolog` method allows you to run a tolog query on the server and receive the result as a
Topic Maps fragment. Later versions of TMRAP will also support getting the query result as a table
structure. The supported parameters are shown below.

| Parameter | Req.? | Repeat? | Type | Description | 
|---|---|---|---|---|
| tolog | yes | no | string | The tolog query. | 
| topicmap | yes | no | string | The ID of the topic map being queried. | 
| syntax | no | no | string | The syntax in which to return the topc map fragment. The default (and only supported value) is XTM. Note that the values supplied must be the syntax identifiers defined above. | 
| view | no | no | string | The view to use when creating topic fragments. The default (and only supported value) is 'stub'. | 

There is one constraint on the tolog queries supported by this method: they must produce a
one-column result consisting only of topics. The topics are then returned in a fragment in the same
way as with the get-topic method (see [get-topic](#get-topic)), except that the topics returned are
not merged.

#### tolog-update ####

The `tolog-update` method runs a tolog update statement on the server against the specified topic
map, modifying it in place. The return value is simply the number of rows modified. The supported
parameters are shown below.

| Parameter | Req.? | Repeat? | Type | Description | 
|---|---|---|---|---|
| tolog | yes | no | string | The tolog statement. | 
| topicmap | yes | no | string | The ID of the topic map being modified. | 

The statement is run against the topic map. The modifications are committed.

#### add-fragment ####

The add-fragment method allows a Topic Maps fragment to be added to a topic map on the server. The
parameters are listed below.

| Parameter | Req.? | Repeat? | Type | Description | 
|---|---|---|---|---|
| topicmap | yes | no | string | The ID of the topic map being added to. | 
| syntax | yes | no | string | The syntax of the fragment to be added. The only supported values are CTM, LTM, and XTM. Note that the values supplied must be the syntax identifiers defined above. | 
| fragment | yes | no | string | The actual fragment. | 

The given fragment is imported into the topic map.

#### delete-topic ####

The delete-topic method allows a topic to be deleted from a topic map on the server. The parameters
are listed below.

| Parameter | Req.? | Repeat? | Type | Description | 
|---|---|---|---|---|
| item | no | yes | URI | An item identifier of the requested topic. | 
| subject | no | yes | URI | A subject locator of the requested topic. | 
| identifier | no | yes | URI | A subject identifier of the requested topic. | 
| topicmap | no | yes | string | The ID of a topic map being queried. | 

The set of topics is found using the same method as with `get-topic` (see [get-topic](#get-topic))
and all topics are deleted from their respective topic maps. The method of deletion is the same as
for the `removeTopic` method of the `net.ontopia.topicmaps.utils.DeletionUtils`
class.

### The HTTP interface ###

In Ontopia distribution the plain HTTP interface is included as a servlet (implemented by the Java
class `net.ontopia.topicmaps.utils.tmrap.RAPServlet`). This servlet can be made available in any web
application by adding the appropriate mappings in the `web.xml` file, and this has been done in the
`/tmrap/` web application in the Ontopia distribution. This means that each TMRAP method is
available in plain HTTP style at the URL
`http://localhost:8080/tmrap/tmrap/method-name`.

The general principle of the mapping is that each method has a separate URL (with the method name as
the last part), and that the parameters to the method are provided as plain HTTP request parameters.
Methods that do not change state on the server are accessed with `GET` while methods that do change
state are accessed with `POST`. So to access method `get-foo` with the parameter `bar` set to `baz`
you would send a `GET` request to
`http://localhost:8080/tmrap/tmrap/get-foo?bar=baz`.

As an example, let's say we've got Ontopia running, and we want information about Russia. We go into
`opera.ltm` using the Omnigator, so that the topic map is loaded on the server. To get information
about Russia using TMRAP we could use the following very simple Python script.

````python
import urllib

BASE = "http://localhost:8080/tmrap/tmrap/"
psi = "http://www.topicmaps.org/xtm/1.0/country.xtm%23RU"

inf = urllib.urlopen(BASE + "get-topic?identifier=" + psi)
print inf.read()
inf.close()
````

Note that in the PSI above the '#' character has been escaped. This is because the fragment part of
a URI is never transmitted to the server, and so we need to escape this to turn it into part of the
request parameter, instead of a fragment to the URI.

Anyway, running this Python script produces the following output (slightly edited to reduce the
length of the lines):

````
[larsga@dhcp-98 larsga]$ python tst.py 
<?xml version="1.0" encoding="utf-8" standalone="yes"?>
<topicMap xmlns="http://www.topicmaps.org/xtm/1.0/" 
          xmlns:xlink="http://www.w3.org/1999/xlink">
  <topic id="id458">
    <instanceOf>
      <subjectIndicatorRef xlink:href="http://psi.ontopia.net/geography/#country"/>
    </instanceOf>
    <subjectIdentity>
      <subjectIndicatorRef xlink:href="http://www.topicmaps.org/xtm/1.0/country.xtm#RU"/>
      <topicRef xlink:href="file:/.../WEB-INF/topicmaps/geography.xtmm#russia"/>
    </subjectIdentity>
    <baseName>
      <baseNameString>Russia</baseNameString>
    </baseName>
  </topic>
  <association>
    <instanceOf>
      <subjectIndicatorRef xlink:href="http://psi.ontopia.net/geography/#located-in"/>
    </instanceOf>
    <!-- ...rest omitted... -->
````

In other words, the plain HTTP interface really is quite plain, and should also be relatively
straightforward to use.

### The SOAP interface ###

The SOAP interface to TMRAP has been created using Apache Axis2, the second-generation SOAP
implementation from the Apache Foundation. This interface is also located in the `/tmrap/` web
application in the Ontopia distribution. You can access this web application with a web browser to
see the available web services and get the WSDL describing the SOAP interface.

To access a SOAP interface you need a SOAP client for your platform. The use of SOAP clients vary so
widely with the platform and specific client used that we make no attempt to document their use
here. The best way to get started is to use your SOAP development framework to generate client stubs
from the WSDL file.

### Security Considerations ###

As it is possible to both modify the topic map and extract any information from it via TMRAP there
are definite security concerns with providing an full TMRAP interface to the open internet. It is
possible to configure the web server in such a way as to block some of the requests, or to require
authentication in order to access some or all of the requests.

The most common requirement is to block the `add-fragment` and `delete-topic` methods, as these
allow modification of the topic map. To do this, simply add the following at the end of the
`web.xml` for the `tmrap` web application:

````xml
  <security-constraint>
    <display-name>Blocked TMRAP requests</display-name>
    <web-resource-collection>
      <web-resource-name>Blocked TMRAP requests</web-resource-name>
      <url-pattern>/tmrap/add-fragment</url-pattern>
      <url-pattern>/tmrap/delete-topic</url-pattern>
    </web-resource-collection>
    <auth-constraint>
      <role-name>user</role-name>
    </auth-constraint>
  </security-constraint>
````

The security role `user` is not defined, but this is not necessary, so long as the purpose is simply
to block access to these requests.


