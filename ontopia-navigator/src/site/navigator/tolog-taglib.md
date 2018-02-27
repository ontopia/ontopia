The Navigator Tag Libraries
===========================

Reference Documentation
-----------------------

<span class="version">Ontopia 5.1 2010-06-09</p>

### Introduction ###

The general principle behind this tag library is that there is at all times an execution context,
which contains a number of variable definitions. The values of variables are always collections of
objects. The tags can set new variable values, extract information from old ones, and do simple
conditionals and loops.

The framework consists of the following tag libraries:

*  The tolog tag library is heavily based on tolog, which is used for all extraction of information
   from the topic map, and tolog variables are made available as tag library variables, and vice versa.
   (Note that this tag library entirely replaces the old logic, tm, value, and output tag
   libraries.)
*  The template tag library provides a simple page templating mechanism that makes it possible to
   create single HTML templates for an entire application and have each page insert content into named
   slots in this template.

The tags are embedded in an HTML document according to ordinary JSP usage and the output-producing
ones write their output directly into the template. For a tutorial introduction to the tag
libraries, see *[The Ontopia Navigator Framework: Developer's
Guide](navguide.html)*.

> **Note**
> The tolog tag library obsoletes several of [the old tag libraries](taglib2.html). Their use is now
> strongly discouraged, but they will continue to be supported as long as they have
> users.

### General ###

The root tag must always be the [tolog:context](#tolog-context) tag.

All variables are set within a scope, that is, a region on the page in which they are accessible.
The page contains a hierarchy of such scopes, with the one established by the
[tolog:context](#tolog-context) tag as the root. Each [tolog:foreach](#tolog-foreach),
[tolog:if](#tolog-if), [tolog:when](#tolog-when), and [tolog:otherwise](#tolog-otherwise) tag
creates a new scope inside itself. Variables are visible from where they are bound down to the end
of the containing scope.

Variable names are required to match the following regular expression: `[A-Za-z_][A-Za-z0-9_-]*`.

### The tolog tags ###

#### Index ####

* [tolog:choose](#p-tolog:choose)
* [tolog:context](#p-tolog:context)
* [tolog:declare](#p-tolog:declare)
* [tolog:foreach](#p-tolog:foreach)
* [tolog:id](#p-tolog:id)
* [tolog:if](#p-tolog:if)
* [tolog:oid](#p-tolog:oid)
* [tolog:otherwise](#p-tolog:otherwise)
* [tolog:out](#p-tolog:out)
* [tolog:query](#p-tolog:query)
* [tolog:set](#p-tolog:set)
* [tolog:when](#p-tolog:when)

#### <a name="p-tolog:choose">`tolog:choose`</a> ####

Container for [tolog:when](#tolog-when) and [tolog:otherwise](#tolog-otherwise) tags; used for
complex tests.

##### Description #####

This tag executes its children until the first [tolog:when](#tolog-when) child succeeds; if none of
the [tolog:when](#tolog-when) children succeed the [tolog:otherwise](#tolog-otherwise) is executed,
if present. At least one [tolog:when](#tolog-when) or [tolog:otherwise](#tolog-otherwise) child must
be present. The `tolog:choose` tag does not introduce a new lexical scope.

##### Attributes #####

This tag has no attributes.

##### Examples #####

The example below links to different JSP pages depending on the type of the topic in the `topic`
variable. This sort of construct is used on general search pages and suchlike.

**Using tolog:choose**

````application/x-jsp
<%-- 'topic' is already set --%>
<tolog:choose>
  <tolog:when query="instance-of(%topic%, composer)?">
    <a href="composer.jsp?id=..."><tolog:out var="topic"/></a>
  </tolog:when>
  <tolog:when query="instance-of(%topic%, opera)?">
    <a href="opera.jsp?id=..."><tolog:out var="topic"/></a>
  </tolog:when>
  <tolog:when query="instance-of(%topic%, country)?">
    <a href="country.jsp?id=..."><tolog:out var="topic"/></a>
  </tolog:when>
  <tolog:otherwise>
    <tolog:out var="topic"/> <%-- no link here --%>
  </tolog:otherwise>
</tolog:choose>
````


#### <a name="p-tolog:context">`tolog:context`</a> ####

Creates the context the other tags need to run.

##### Description #####

Sets up the Navigator Framework ready for execution. It may load a topic map, if necessary, as well
as the application configuration. Finally, it establishes a lexical scope, but does not set any
variables.

##### Attributes #####

| Name | Required | Values | Description | 
|---|---|---|---|
| topicmap | false | topic map ID | The ID of the topic map to work on | 

##### Example #####

Below is a trivial example of the use of this tag.

**Using tolog:context**

````application/x-jsp
<tolog:context topicmap="opera.ltm">
  <%-- use other tags to produce content here --%>
</tolog:context>
````


#### <a name="p-tolog:declare">`tolog:declare`</a> ####

Used to make tolog declarations available to all queries in the same
[tolog:context](#tolog-context).

##### Description #####

This tag makes it possible to declare tolog URI prefixes, module imports, and inference rules once
in a single page, and then reuse them in other queries below their first declaration. Note that the
declarations will be visible throughout the entire [tolog:context](#tolog-context) tag in which the
tag occurs below the point at which it occurs.

The `tolog:declare` tag does not introduce a new lexical scope.

##### Attributes #####

This tag has no attributes.

##### Examples #####

The example below first defines a URI prefix, then makes use of it in a query below.

**Using tolog:declare**

````application/x-jsp
<tolog:declare>
using xtm for i"http://www.topicmaps.org/xtm/1.0/#"
</tolog:declare>

<p>The <tolog:out var="class"/> class has the following
subclasses:</p>

<ul>
<tolog:foreach query="
  xtm:superclass-subclass(%class% : xtm:superclass, $SUB : xtm:subclass)?">
  <li><tolog:out var="SUB"/></li>
</tolog:foreach>
</ul>
````


#### <a name="p-tolog:foreach">`tolog:foreach`</a> ####

Runs a tolog query and iterates over the result.

##### Description #####

The tag runs a tolog query, and executes its contents once for each row in the result set. For each
iteration, the values of the tolog variables in that row are made available as navigator variables
inside the tag. (The tag creates a new lexical scope for these variables.)

The tag also supports grouping (such as when displaying all countries, and for each a list of the
cities in each country), which is done by having two tolog:foreach tags nested within each other.
The outermost one will have a `query` attribute and a `groupBy` attribute indicating which variable
to group by (in the example, country). It will iterate over the groups (in the example, the
countries). The innermost foreach tag will not have any attributes, and will iterate over the
elements in each group (in the example, the cities).

It is possible to have more than one level of grouping, which is achieved by having intermediate
foreach elements which have a `groupBy` attribute, but no `query` attribute. So if we were to list
all countries, with their provinces, and the cities within each province, the outermost foreach
would have a query and group on country, while the next foreach would have no query and group on
province, while the innermost foreach would have no attributes at all.

The foreach tag sets three variables on each iteration, described below. These variables are
independent of any grouping done by the tag.

sequence-first
:   This variable will be true if this is the first iteration of the foreach tag, otherwise it will be
    false.

sequence-last
:   This variable will be true if this is the last iteration of the foreach tag, otherwise it will be
    false.

sequence-number
:   This variable will contain the number of this iteration of the foreach tag, starting with 1.

##### Attributes #####

| Name | Required | Values | Description | 
|---|---|---|---|
| query | No | tolog query | The query to run (or the name of a query declared with [tolog:query](#tolog-query)). | 
| separator | No | string | A string to be inserted between each iteration. | 
| groupBy | No | variable name(s) | The name(s) of the variable(s) to group on. If not present the tag will not do grouping. The variable names are separated by whitespace only. | 

##### Examples #####

The example below shows a list of all composer topics in the topic map.

**Simple use of tolog:foreach**

````application/x-jsp
<ul>
<tolog:foreach query="instance-of($composer, composer)?">
  <%-- the composer variable is now set by the :foreach tag --%>
  <li><tolog:out var="composer"/></li> 
</tolog:foreach>
</ul>
````

The next example shows a list of composers, and for each composer all the operas he<span
class="footnote">The Italian Opera Topic Map only contains male composers.</span> has composed. This
is done in a single query using grouping.

**Using tolog:foreach with groupBy**

````application/x-jsp
<ul>
<tolog:foreach query="composed-by($COMPOSER : composer, $OPERA : opera)
                      order by $COMPOSER?" 
               groupBy="COMPOSER">

  <%-- the COMPOSER variable is now set by the :foreach tag,
       but not OPERA; repeated once for each COMPOSER --%>

  <li><tolog:out var="COMPOSER"/>
    <ul>
      <tolog:foreach>
        <%-- OPERA is now set; repeated once for each OPERA with the
             current COMPOSER --%>
        <li><tolog:out var="OPERA"/>
      </tolog:foreach>
    </ul>
  </li>
</tolog:foreach>
</ul>
````

The next example lists all operas composed by Puccini with commas in between their names, except
between the last two, which are separated by "and". In addition, the number of each opera in the
sequence is added in parentheses behind its name.

**Using the sequence-* variables**

````application/x-jsp
<tolog:foreach query="composed-by(puccini : composer, $OPERA : work)?">
  <tolog:choose>
     <tolog:when var="sequence-first"></tolog:when>
     <tolog:when var="sequence-last"> and </tolog:when>
     <tolog:otherwise>, </tolog:otherwise>
  </tolog:choose>
  <tolog:out value="OPERA"/> (<tolog:out value="sequence-number"/>)
</tolog:foreach>
````


#### <a name="p-tolog:id">`tolog:id`</a> ####

Outputs a symbolic ID of a topic map object.

##### Description #####

This tag gets a value either by running a query or picking it out of a variable and then outputs its
symbolic ID, provided it is a topic map object. It is an error if the value is not a topic map
object. The tag must be empty.

The symbolic ID is found by searching for a source locator for the object of the form `foo#bar`
where `foo` is the same URI as the base address of the topic map store. The ID will then be `bar`,
which would correspond to the ID of the topic/object in an XTM/LTM file. If no matching source
locator is found the object ID will be used instead.

If the `query` attribute is used it is an error for the query to return a result set with more than
one column. If the query produces more than one row only the first row is used.

If the `var` attribute is set to a variable that either is not set, does not contain anything, or
contains null it is an error.

##### Attributes #####

| Name | Required | Values | Description | 
|---|---|---|---|
| query | No | tolog query | The query to run (or the name of a query declared with [tolog:query](#tolog-query)). Either this or `var` must be set. | 
| var | No | variable name or JSP attribute | The name of the variable whose value is to be output. Note that if the variable contains more than one value a random value is picked. If no variable is found, a JSP attribute will be looked up, and bean properties accessed. (Either this attribute or `query` must be set.) | 
| fallback | No | string | A fallback value to be displayed if the `query` result is empty or the `var` variable is not set. | 

##### Examples #####

The example below uses the already bound `composer` variable to create a link to the topic in that
variable.

**Using tolog:id in a link**

````application/x-jsp
<p>
This opera was composed by <a href="composer.jsp?id=<tolog:id
var="composer"/>"><tolog:out var="composer"/></a>.</p>
````


#### <a name="p-tolog:if">`tolog:if`</a> ####

Executes its contents depending on the value of a variable or the result of a query.

##### Description #####

This tag either tests the value of a variable or the result of a query. The variable is considered
true if it is set and contains values other than null. The query is considered true if it produces
at least one result row. If a query is run, navigator variables for each tolog variable bound in the
first row will be set. Later rows, if any, will be ignored.

The `tolog:if` tag introduces a new lexical scope.

##### Attributes #####

| Name | Required | Values | Description | 
|---|---|---|---|
| query | No | tolog query | The query to run (or the name of a query declared with [tolog:query](#tolog-query)). Either this or `var` must be set. | 
| var | No | variable name | The name of the variable whose value is to be tested. (Either this attribute or `query` must be set.) | 

##### Examples #####

The example below loops over all operas in the topic map, outputting for each the name and, if set,
the premiere date.

**Using tolog:if with a variable**

````application/x-jsp
<ul>
<tolog:foreach query="instance-of($opera, opera),
                      { premiere-date($opera, $date) }?">
  <%-- 'opera' and 'date' will be set here, but 'date' may be null --%>
  <li><tolog:out var="opera"/>
    <tolog:if var="date">(<tolog:out var="date"/>)</tolog:if>
  </li> 
</tolog:foreach>
</ul>
````

The example below does the same thing, except that it runs a separate query to check the premiere
date for each opera.

**Using tolog:if with a query**

````application/x-jsp
<ul>
<tolog:foreach query="instance-of($opera, opera)?">
  <%-- 'opera' will be set here --%>
  <li><tolog:out var="opera"/>
    <tolog:if query="premiere-date(%opera%, $date)?">
      <%-- if there is no date we won't get here; if we get here
           'date' will be set --%>
      <tolog:out var="date"/>
    </tolog:if>
  </li> 
</tolog:foreach>
</ul>
````


#### <a name="p-tolog:oid">`tolog:oid`</a> ####

Outputs the object ID of a topic map object.

##### Description #####

This tag gets a value either by running a query or picking it out of a variable and then outputs its
object ID, provided it is a topic map object. It is an error if the value is not a topic map object.
The tag must be empty.

Note that the object ID of a topic map object is *not* the same as its ID in an XTM or LTM file. The
ID in a file is likely to be meaningful, but the object ID is a meaningless identifier assigned
automatically by the topic map engine. (At present this will be a number.)

If the `query` attribute is used it is an error for the query to return a result set with more than
one column. If the query produces more than one row only the first row is used.

If the `var` attribute is set to a variable that either is not set, does not contain anything, or
contains null it is an error.

##### Attributes #####

| Name | Required | Values | Description | 
|---|---|---|---|
| query | No | tolog query | The query to run (or the name of a query declared with [tolog:query](#tolog-query)). Either this or `var` must be set. | 
| var | No | variable name or JSP attribute | The name of the variable whose value is to be output. Note that if the variable contains more than one value a random value is picked. If no variable is found, a JSP attribute will be looked up, and bean properties accessed. (Either this attribute or `query` must be set.) | 
| fallback | No | string | A fallback value to be displayed if the `query` result is empty or the `var` variable is not set. | 

##### Examples #####

The example below uses the already bound `composer` variable to create a link to the topic in that
variable.

**Using tolog:oid in a link**

````application/x-jsp
<p>
This opera was composed by <a href="composer.jsp?id=<tolog:oid
var="composer"/>"><tolog:out var="composer"/></a>.</p>
````


#### <a name="p-tolog:otherwise">`tolog:otherwise`</a> ####

Used within [tolog:choose](#tolog-choose) at the end for a clause that will be executed if no
[tolog:when](#tolog-when) tags are executed.

##### Description #####

Used within [tolog:choose](#tolog-choose) (and only there) at the end as a kind of 'else' or
'default branch', which will be executed if none of the [tolog:when](#tolog-when) tags in the
[tolog:choose](#tolog-choose) are executed.

The `tolog:otherwise` tag introduces a new lexical scope.

##### Attributes #####

This tag has no attributes.

##### Examples #####

The example below links to different JSP pages depending on the type of the topic in the `topic`
variable. This sort of construct is used on general search pages and suchlike. Note the
tolog:otherwise at the end, which is used to just display the name of the topic with no link if it's
not of one of the recognized types.

**Using tolog:choose**

````application/x-jsp
<%-- 'topic' is already set --%>
<tolog:choose>
  <tolog:when query="instance-of(%topic%, composer)?">
    <a href="composer.jsp?id=..."><tolog:out var="topic"/></a>
  </tolog:when>
  <tolog:when query="instance-of(%topic%, opera)?">
    <a href="opera.jsp?id=..."><tolog:out var="topic"/></a>
  </tolog:when>
  <tolog:when query="instance-of(%topic%, country)?">
    <a href="country.jsp?id=..."><tolog:out var="topic"/></a>
  </tolog:when>
  <tolog:otherwise>
    <tolog:out var="topic"/> <%-- no link here --%>
  </tolog:otherwise>
</tolog:choose>
````


#### <a name="p-tolog:out">`tolog:out`</a> ####

Outputs a value from a variable or a query.

##### Description #####

This tag gets a value either by running a query or picking it out of a variable and then outputs it.
The table below shows what is output for different kinds of values. The tag must be
empty.

| Type | Output | 
|---|---|
| string | output as is | 
| locator | external form is output | 
| topic | most suitable named picked and output | 
| basename | string value is output | 
| variant | string value or external form of locator is output | 
| occurrence | string value or external form of locator is output | 
| other | result of calling `toString` is output | 

If the `query` attribute is used it is an error for the query to return a result set with more than
one column. If the query produces more than one row only the first row is used.

If the `var` attribute is set to a variable that either is not set, does not contain anything, or
contains null it is an error.

##### Attributes #####

| Name | Required | Values | Description | 
|---|---|---|---|
| query | No | tolog query | The query to run (or the name of a query declared with [tolog:query](#tolog-query)). Either this or `var` must be set. | 
| var | No | variable name or JSP attribute | The name of the variable whose value is to be output. Note that if the variable contains more than one value a random value is picked. If no variable is found, a JSP attribute will be looked up, and bean properties accessed. (Either this attribute or `query` must be set.) | 
| scope | No | variable name | Specifies the scope to be used when selecting names for topics; has no effect on other objects. | 
| fallback | No | string | A fallback value to be displayed if the `query` result is empty or the `var` variable is not set. | 
| escape | No | true &#124; false | If true (which is the default) the output has any reserved XML/HTML characters escaped; this can be turned off by setting the value to false. | 

##### Examples #####

The example below shows a list of all composer topics in the topic map using the `var` attribute.

**Simple use of tolog:out**

````application/x-jsp
<ul>
<tolog:foreach query="instance-of($composer, composer)?">
  <%-- the out tag will pick the best name for the composer topic and
       output it --%>
  <li><tolog:out var="composer"/></li> 
</tolog:foreach>
</ul>
````

The example below shows how the `query` attribute can be used to pick the value to output. The query
counts the number of topics in the topic map, and the `tolog:out` tag will print this
number.

**Using tolog:out with a query**

````application/x-jsp
<p>
This topic map contains 
<tolog:out query="select count($T) from topic($T)?"/>
topics.</p>
````

The example below shows how to use the `fallback` attribute to output a fallback value if a `query`
result is empty or a `var` variable is not set.

**Using a fallback value for tolog:out**

````application/x-jsp
<p>
<tolog:out var="person"/>'s date of birth is 
<tolog:out query="p:date-of-birth(%person%, $dob)" fallback="not set"/>.</p>
<%-- returns: <p>John's date of birth is 1974-08-12.</p>
          or: <p>Mick's date of birth is not set.</p> --%>
````


#### <a name="p-tolog:query">`tolog:query`</a> ####

Defines a named query which can later be referenced from other tolog tags.

##### Description #####

This tag captures the output from its contents and stores it as a named tolog query in the page
context. The query can later be referenced by name from other tolog tags in their `query`
attributes.

The `tolog:query` tag does not introduce a new lexical scope.

##### Attributes #####

| Name | Required | Values | Description | 
|---|---|---|---|
| name | yes | tolog query name | The name under which the query is to be stored. | 

##### Examples #####

The example below uses a named query to avoid having to repeat the same query twice. The query lists
all cities within a country, and is used first to check if the country has any cities, in which case
a list of them is written out.

**Reusing a named query**

````application/x-jsp
<tolog:query name="q-cities">
located-in(%country% : container, $CITY : containee),
instance-of($CITY, city)
order by $CITY?
</tolog:query>

<tolog:if query="q-cities">
<p><tolog:out var="country"> contains these cities:</p>

<ul>
  <tolog:foreach query="q-cities">
    <li><tolog:out var="CITY"/></li>
  </tolog:foreach>
</ul>
</tolog:if>
````


#### <a name="p-tolog:set">`tolog:set`</a> ####

Used to set navigator framework variables.

##### Description #####

Sets the value of a navigator framework variable. This is generally used when working with the Web
Editor Framework, or to interact with non-Ontopia tags and code. The tolog:set set tag must always
be empty.

If a tolog query is used it must produce exactly one column in the result set. The variable set will
be given the name in the `var` attribute (if present) or the name of the tolog variable (if `var` is
not present). Each row in the result set will contribute one element to the collection to be bound
to the variable.

The `reqparam` attribute can be used to get topics from HTTP request parameters. The request
parameter must contain an object ID or a symbolic ID, which will be looked up in the topic map, and
the corresponding object set as the value of the variable. If the `reqparam` attribute is used the
`var` attribute must be present. (If the request parameter has multiple values the same procedure is
applied to each value.)

The `scope` attribute can be used to set values as JSP container attributes instead of as Navigator
Framework variables. See the documentation for this attribute for details.

The `value` attribute can be used instead of the `query` attribute to compute the value to be set.
This attribute takes a [JSP
expression](http://java.sun.com/j2ee/1.4/docs/tutorial/doc/JSPIntro7.html) as its value, and the JSP
expression computes the value of the variable. See the attribute documentation for
details.

Finally, the tag can be used with only the `var` attribute and the variable will then be set to the
string value of the content of the tag. If the tag is empty it the variable will be set to the empty
collection.

The `tolog:set` tag does not introduce a new lexical scope.

##### Attributes #####

| Name | Required | Values | Description | 
|---|---|---|---|
| query | no | tolog query | The query which produces the value put into the variable (or the name of a query declared with [tolog:query](#tolog-query)). | 
| var | no | variable name | The name of the variable to set. If not given the name will be that of the variable in the tolog query. | 
| reqparam | no | HTTP request parameter name | If given, the variable set will be set to the topic map object whose ID is the value of the named HTTP request parameter to the page. If the request parameter is not given the variable will be bound to an empty value. | 
| scope | no | application &#124; session &#124; request &#124; page &#124; ontopia | Controls where the variable is set. If value is `ontopia` (which is the default) the variable is set as a Navigator Framework variable. If not, it is set as a JSP container attribute, and not as a Navigator Framework variable at all. The values other than `ontopia` indicate which scope in the JSP container the attribute is to be set in. | 
| value | no | [JSP expression](http://java.sun.com/j2ee/1.4/docs/tutorial/doc/JSPIntro7.html) | The JSP expression computes the value to be set. Note that when scope is `ontopia` values that are arrays will be converted to collections, collections will stay as collections, and anything else (including null) will become singleton collections. (This is done because Navigator Framework variables are always bound to collections and never to single values.) | 

Note that either `query`, `value`, or `reqparam` must be set, or the tag must have content, which
will become the string value of the variable.

##### Examples #####

The example below sets the variable 'composers' to the collection of all composer topics in the
topic map.

**Using tolog:set with many values**

````application/x-jsp
<tolog:set var="composers"
           query="instance-of($COMPOSER, composer)?"/>
````

The next example sets the 'composer' topic into the variable 'composer'.

**Using tolog:set with one value**

````application/x-jsp
<tolog:set query='source-locator($composer, "#composer")?'/>
````


#### <a name="p-tolog:when">`tolog:when`</a> ####

Conditional test, almost identical to [tolog:if](#tolog-if), but only used within
[tolog:choose](#tolog-choose).

##### Description #####

Used within [tolog:choose](#tolog-choose) (and only there) for conditional testing. The first
tolog:when within a [tolog:choose](#tolog-choose) whose test evaluates to true will have its
children executed; after this, no more tolog:whens will be tested.

tolog:when has the same attributes as [tolog:if](#tolog-if) and the same rules are used to establish
whether the test evaluates to true or false.

The `tolog:when` tag introduces a new lexical scope.

##### Attributes #####

| Name | Required | Values | Description | 
|---|---|---|---|
| query | No | tolog query | The query to run (or the name of a query declared with [tolog:query](#tolog-query)). Either this or `var` must be set. | 
| var | No | variable name | The name of the variable whose value is to be tested. (Either this attribute or `query` must be set.) | 

##### Examples #####

The example below links to different JSP pages depending on the type of the topic in the `topic`
variable. This sort of construct is used on general search pages and suchlike.

**Using tolog:choose**

````application/x-jsp
<%-- 'topic' is already set --%>
<tolog:choose>
  <tolog:when query="instance-of(%topic%, composer)?">
    <a href="composer.jsp?id=..."><tolog:out var="topic"/></a>
  </tolog:when>
  <tolog:when query="instance-of(%topic%, opera)?">
    <a href="opera.jsp?id=..."><tolog:out var="topic"/></a>
  </tolog:when>
  <tolog:when query="instance-of(%topic%, country)?">
    <a href="country.jsp?id=..."><tolog:out var="topic"/></a>
  </tolog:when>
  <tolog:otherwise>
    <tolog:out var="topic"/> <%-- no link here --%>
  </tolog:otherwise>
</tolog:choose>
````


### The templating tags ###

These tags are used to implement JSP page templating, which can be used to avoid having to repeat
the page layout structure in every single JSP page. They can also be used to implement a full
Model-View-Skin system.

#### Index ####

* [get](#p-get)
* [insert](#p-insert)
* [put](#p-put)
* [split](#p-split)

#### <a name="p-get">`get`</a> ####

Used in template pages to define slots into which content will be inserted by pages using the
template.

##### Context interaction #####

This tag makes no assumption about its ancestors.

This tag may have content if it outputs a slot from a `put` tag that had a `split` child. If not it
must be empty.

##### Attributes #####

| Name | Required | Values | Description | 
|---|---|---|---|
| name | Yes | String | The name of the slot whose contents are to be inserted to replace this tag when the template is used. | 
| fallback | No | Boolean | Indicates whether the tag body should be used as fallback value if no corresponding `put` tag is found (see example below).  | 

##### Execution #####

When the template is used the contents of the named slot (as produced by the using page) are looked
up and inserted in place of this tag. If no content has been filled into the slot and no `fallback`
is given, the tag will not output anything.

If the slot referred to was from a `put` tag that had a `split` child the start tag for the `get`
will output the slot up to the split point, then output the result of evaluating its content, and
finally the end tag will output the slot after the split point.

##### Example #####

See the example given for the `insert` tag.

##### Example with fallback value #####

The example below defines a template that injects a date of last change for each page that uses the
template. The fallback value is included in the template to avoid broken content if a page fails to
define a `put` for `date-last-change`.

**Template with fallback value**

````application/x-jsp
  <div class="date-last-change">
    This page was last changed at 
    <template:get name="date-last-change" fallback="true">
      an unknown date
    </template:get>
  </div>
````

The result of this template is e.g. *'This page was last changed at december 2nd 2001'* for a page
that includes a `put` tag with name `date-last-change`, or *'This page was last changed at an
unknown date'* for pages that lack such a tag.

The body of a `get` tag with `fallback` can include any kind of JSP content, even including other
`get` tags, if needed.


#### <a name="p-insert">`insert`</a> ####

References the template JSP page to be used by the current page.

##### Context interaction #####

This tag does not make any assumptions about its ancestors.

This tag should contain `template:put` tags, which assign contents to the slots defined by the
template page.

##### Attributes #####

| Name | Required | Values | Description | 
|---|---|---|---|
| template | Yes | Path | The path is relative to the referencing page. | 

##### Execution #####

This tag is used in JSP pages to say what template the page should use. Execution is done by
executing the contents of the tag, which stores the contents written into the various page slots.
The template page is then output with the contents of the various slots inserted in the right
places.

##### Example #####

Below is an example of the use of how the three templating tags are used together. The `demo.jsp`
requested by the client uses the template (as defined in `template.jsp` as listed a bit further
down).

**Referring to the template with the insert tag
(demo.jsp)**

````application/x-jsp
<template:insert template="template.jsp">

  <template:put name="title">An example page</template:put>

  <template:put name="content">
    <p>This page shows you all the ...</p>
  </template:put>

  <template:put name='footer' content='footer.html'/>

</template:insert>
````

The JSP page above produces content in the named slots, which will be filled into the template shown
below (which is the `template.jsp` page) in the named locations.

**Defining the slots (template.jsp)**

````application/x-jsp
<html>
<head>
  <link rel=stylesheet type="text/css" href="default.css">
  <title><template:get name='title'/></title>
</head>

<body>
  <h1><template:get name='title'/></h1>

  <template:get name="content"/>
  <template:get name="footer"/>
</body>
</html>
````

**Example for external reused data
(footer.html)**

````application/x-jsp
  <hr>
  <address>
    Ontopia A/S
  </address>
````


#### <a name="p-put">`put`</a> ####

Used in pages to put produced content into a named slot in the template used by that page.

##### Context interaction #####

Must have an `insert` tag among its ancestors.

The tag makes no assumptions about its children, whose output it will capture and store in order to
fill it into the template. It may contain one `split` tag as well any number of `put` tags. The
`split` tag allows the named slot to be output by a nested `get` tag.

##### Attributes #####

| Name | Required | Values | Description | 
|---|---|---|---|
| name | Yes | String | The name of the slot in the template to write the content produced by the children of this tag into. | 
| content | No | String &#124; Path | Gives the content of the named slot either as a string (if `direct` is true) or as a reference to a file which holds the contents (if `direct` is false).  | 
| direct | No | true &#124; false | If set to "true", will insert the contents of the `content` attribute as the slot content. | 

##### Execution #####

The children of the tag are executed, and their output captured. That output is then taken by the
`insert` tag and inserted into the correct slot in the template, which is
output.

##### Example #####

See the example given for the `insert` tag.


#### <a name="p-split">`split`</a> ####

Used in template pages inside `put` tags to split the slot so that it can be output by a nested
`get` tag.

##### Context interaction #####

This tag must have a `put` tag among its ancestors.

This tag must be empty.

##### Attributes #####

This tag has no attributes.

##### Execution #####

The tag informs its nearest ancestor `put` tag that this is the split point for this slot. No other
actions are performed.

##### Example #####

Below is an example of how the `split` tag is used. The `demo.jsp` requested by the client uses the
template (as defined in `template.jsp` as listed a bit further down).

**Referring to the template with the insert tag
(demo.jsp)**

````application/x-jsp
<template:insert template="template.jsp">

  <template:put name="title">An example page</template:put>

  <template:put name="content">
    <form action="demo.jsp" method="post">

      <template:split/>

      <template:put name="menu">
        <input type=button name=button1 value="Button1">
        <input type=button name=button2 value="Button2">
      </template:put>

      <template:put name="body">
        <input type=text name=field>
      </template:put>

    </form>
  </template:put>

  <template:put name='footer' content='footer.html'/>

</template:insert>
````

The JSP page above produces a web form that contains a menu (as a template slot), consisting of a
row of buttons that submit the form, and the form body (as another template
slot).

**Defining the slots (template.jsp)**

````application/x-jsp
<html>
<head>
  <link rel=stylesheet type="text/css" href="default.css">
  <title><template:get name='title'/></title>
</head>

<body>
  <h1><template:get name='title'/></h1>

  <template:get name="content">
    <table>
    <tr><td><template:get name="menu"/>

    <tr><td><template:get name="body"/>

    <tr><td><template:get name="menu"/>
    </table>
  </template:get>

  <template:get name="footer"/>
</body>
</html>
````

In this template the `get` start tag for the 'content' slot outputs the form start tag (the
'content' slot up to the `split` tag), then the content of the `get` tag is output with the slots
inserted, and finally the end tag outputs the form end tag, like this:

**Result of template with split tag**

````application/x-jsp
<html>
<head>
  <link rel=stylesheet type="text/css" href="default.css">
  <title>An example page</title>
</head>

<body>
  <h1>An example page</h1>

  <form action="demo.jsp" method="post">
    <table>
    <tr><td>
        <input type=button name=button1 value="Button1">
        <input type=button name=button2 value="Button2">

    <tr><td>
        <input type=text name=field>

    <tr><td>
        <input type=button name=button1 value="Button1">
        <input type=button name=button2 value="Button2">
    </table>
  </form>

  <hr>
  <address>
    Ontopia A/S
  </address>
</body>
</html>
````



