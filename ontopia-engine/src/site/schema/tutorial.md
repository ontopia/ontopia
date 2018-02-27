The Ontopia Schema Language
===========================

Tutorial
--------

<p class="introduction">
This document provides a tutorial introduction to The Ontopia Schema Language, suitable for learning
the language. For detailed information about the language syntax and semantics, see <em>The Ontopia
Schema Language: Reference Specification</em>. You can also find a DTD for the schema language in
<code>${basedir}/doc/schema/schema.dtd</code>.
</p>

<span class="version">5.1 $Date: 2008/06/13 12:06:19 $</p>

### Introduction ###

The Ontopia Schema Language provides a way to describe and constrain the information in your topic
maps, and this can be made use of in a number of different ways. Some of the most important
are:

*  To verify that the information in your topic maps is consistent and meaningful. That is, that nobody
   has by mistake asserted that a city was born in a composer, forgotten to say that Mozart was a
   composer, or given an opera a birth date.
*  To document the constraints which topic maps processed or displayed by an application must abide by.
   Or, similarly, to document the structure of your topic map.
*  To help topic map editors and other applications provide a better user interface, by making use of
   the information your schema provides about your topic maps.

This tutorial shows you how to write a schema for your topic maps and how to validate the topic maps
against the schema, but does not go into making use of the schema in other
ways.

### The basics of the schema language ###

A topic map schema in the Ontopia Schema Language consists of a set of topic and association class
definitions. These class definitions constrain the structure of the instances of the classes, and so
control the form information may take in a topic map that uses the schema.

This section shows you how the Ontopia Schema Language works by first giving a general introduction
to the concepts behind it, and then walking you through the creation of a simple schema for the
'Free XML Tools' topic map.

#### How it works ####

A schema in the Ontopia Schema Language consists of a set of topic and association class definitions
that state what the allowed structure of instances of the classes are. The class definitions contain
constraints on the characteristics of instances of the classes, which in turn may contain further
constraints.

A topic map is validated against a schema by iterating over the topics in the topic maps, validating
each, then repeating the process with the associations. For each topic or association the class it
belongs to is found, and the characteristics of the topic or association are then validated against
the constraints in the class. Each characteristic is compared with the constraints until a
constraint matching the characteristic is found; the characteristic is then validated against that
constraint.

Generally, the type of the characteristic is used to match the constraint, but for objects which
have no type scope is used. A special case is association roles seen from within topics, which are
matched by both role type and association type. This is because different rules may apply to how a
topic can play role A in association B from how it may play role A in association
C.

The table below shows what objects may be constrained in what contexts, what is used to match them
against their constraints, and by what they may be constrained beyond what the rows in the table
show.

| Container | Containee | Element | Match by | Constrainable | 
|---|---|---|
| Topic map | Topics | topic | Type | Other classes, superclasses | 
| Topic | Topic names | baseName | Scope | Cardinality | 
| Topic name | Variant names | variantName | Scope | Cardinality | 
| Topic | Occurrences | occurrence | Type | Cardinality, scope, internal/external | 
| Topic | Association roles | playing | Role and association type | Cardinality | 
| Topic map | Associations | association | Type | Scope | 
| Association | Association roles | role | Type | Cardinality, player type | 

Note that a topic map schema in the Ontopia Schema Language does not contain any topic definitions.
Instead, it refers to typing and scoping topics within the topic map to be validated by URI. This
can be done by referring directly to the `topic` element within the topic map to be validated (using
`topicRef`), by referring to the subject identifier of the topic (using `subjectIndicatorRef`), or
by giving the URI of the `topic` element relative to the document URI of the topic map (using
`internalTopicRef`).

What this means is that schemas in this schema language are not independent of the topic maps they
are used to validate. This is slightly awkward, but we have not been able to think of any better
solution.

#### Introductory example ####

In this section we'll make a schema for the 'Free XML Tools' topic map, which you can find in the
`${basedir}/samples` directory. This topic map contains information about free and open source XML
tools, their authors, the standards they support, and/or implement, and so on. We'll create the
schema step by step, using the validation errors we get to improve the schema.

We start of with the simplest schema imaginable, one that simply says that anything is allowed,
which is what the schema below does.

**The unconstraining schema**

````xml
<tm-schema match="loose">
</tm-schema>
````

This schema has no classes, and uses loose matching. Loose matching means that there are no
constraints on topics and associations that are not instances of one of the classes mentioned in the
schema. Since this schema has no classes it means that all topics and associations will be
accepted.

This, of course, is not very useful, so we'll add a class definition for the 'standard' class of
topics. Below is an empty class definition for this topic type.

**Empty standard class**

````xml
  <topic>
    <instanceOf>
      <internalTopicRef href="#TMTT_Standard"/>
    </instanceOf>

    <!-- characteristic constraints go here -->
  </topic>
````

Above is shown an empty topic class definition. It looks just like a topic in the XTM format, except
for the reference to the topic that defines the class. We use the `internalTopicRef` element here,
which contains a URI that is relative to the base address of the topic map, rather than of the
schema document. This is useful because it means you can refer to the typing topic without caring
where the topic map document is located relative to the schema document.

You can also use the `topicRef` element from XTM, but this will then depend on where the topic map
is located relative to the schema. The `subjectIndicatorRef` element can also be used, and is
independent of where the topic map is located, but requires the typing topic to have a subject
identifier.

If we try validating the topic map with this schema (see [Tools for working with
schemas](#tools-for-working-with-schemas)) we get 374 errors, so clearly we need to improve the
schema. One thing we do know is that all standards have at least one topic name in the unconstrained
scope. We add the following constraint to reflect that, causing the topic class definition to look
as shown below.

**Standard class with basename**

````xml
  <topic>
    <instanceOf>
      <internalTopicRef href="#TMTT_Standard"/>
    </instanceOf>

    <baseName min="1" max="Inf">
      <scope></scope>
    </baseName>
  </topic>
````

This example says that the topic must have one topic name or more in the unconstrained scope (the
`scope` element is empty). The `min` attribute says we must have at a minimum 1 topic name matching
this constraint, while the `max` says that there is no upper limit on the number of topic names.
Note that if we'd left out these two attributes there would still have been no upper limit, but
standards would then be allowed to have no name in the unconstrained scope.

Validating with this constraint we get only 290 errors. Many of these are related to occurrences of
the types 'specification', 'home page', 'link collection', and 'informational site'. We add four new
constraints to the topic class in order to get rid of those errors.

**Constraints for occurrences**

````xml
    <occurrence external="yes">
      <instanceOf>
          <internalTopicRef href="#TMOR_Specification"/>
      </instanceOf>
    </occurrence>

    <occurrence external="yes">
      <instanceOf>
          <internalTopicRef href="#TMOR_Homepage"/>
      </instanceOf>
    </occurrence>

    <occurrence external="yes">
      <instanceOf>
          <internalTopicRef href="#TMOR_Site"/>
      </instanceOf>
    </occurrence>

    <occurrence external="yes">
      <instanceOf>
          <internalTopicRef href="#TMOR_Links"/>
      </instanceOf>
    </occurrence>
````

These constraints allow topics of class 'standard' to have occurrences of these four types. The
`external` attribute is here used to say that the occurrences must be external (that is, they can't
use `resourceData`). It is possible to constrain the scope of occurrences as well, by using the
`scope` element inside the `occurrence` element, but we have no use for that here, so we don't do
it.

Validating with this improved schema gives us 247 errors, all of them related to standards playing
the role 'standard' in associations of the type 'product implements standard' or 'product uses
standard'. We solve this by adding a constraint allowing these two uses.

**Constraint for association roles**

````xml
    <playing>
      <instanceOf>
          <internalTopicRef href="#TMTT_Standard"/>
      </instanceOf>
      <in>
        <instanceOf>
              <internalTopicRef href="#TMAT_StandardImplemented"/>
        </instanceOf>
        <instanceOf>
              <internalTopicRef href="#TMAT_ProductUse"/>
        </instanceOf>
      </in>
    </playing>
````

This element says that topics of type 'standard' may play this role in associations of one of the
given types.

When we add this constraint to the schema it validates with no errors at all. Note, however, that so
far we only validate topics of one type, and we do not validate associations at all. We validate the
roles the topics of type 'standard' play, but not the associations themselves. In order to allow
associations to be constrained as well the schema language also supports association class
definitions

As an example, we'll create an association class definition for the 'standard implemented'
association. This is given below.

**Association class definition**

````xml
  <association>
    <instanceOf>
      <internalTopicRef href="#TMAT_Used"/>
    </instanceOf>

    <role min="1" max="1">
      <instanceOf>
          <internalTopicRef href="#TMAR_Used"/>
      </instanceOf>
      <player>
          <internalTopicRef href="#TMTT_Product"/>
      </player>
    </role>

    <role min="1" max="1">
      <instanceOf>
          <internalTopicRef href="#TMAR_Using"/>
      </instanceOf>
      <player>
          <internalTopicRef href="#TMTT_Standard"/>
      </player>
    </role>
  </association>
````

What this class definition does is to say that associations of this type must contain one role of
type 'used, the player of which role must be a product, and one role of type 'using, and that the
player of this role must also be of type 'product'. This means that the association is constrained
to always be a simple binary association between two products, where one is used and the other is
using.

At this stage, of course, we only validate one class of topics and one class of associations. If we
now change the schema to use strict matching we get 1154 errors when
validating.

### Working with schemas ###

#### Tools for working with schemas ####

Included in the distribution is a command-line tool for validating topic maps against schemas. This
tool can be run by giving the command `java net.ontopia.topicmaps.schema.impl.osl.cmdline.Validate`.
Doing so makes it show information on how to use it correctly. This tool lets you easily and quickly
try out schemas as you write them, and also to easily verify your topic maps after you have created
them.

Included in the distribution is also a validator plugin for the Omnigator (see *The Ontopia Schema
Tools: Installation guide* for information on installing it). This validates the current topic map
against a schema, which it looks for in the `WEB-INF/schemas` directory of the Omnigator web
application. It assumes that the schema document has the same file name as the ID of the topic map
being browsed, with the addition of the extension ".osl" (e.g.,
"xmltools-tm.xtm.osl").

### TMCL ###

ISO is currently working on a standard schema (or constraint) language for topic maps, to be called
ISO 19756 Topic Maps Constraint Language, or TMCL. At the time of writing it seems that this
language will be very similar to OSL, but more powerful, and that the syntax will be different. For
more details, please consult [the TMCL home
page](http://www.isotopicmaps.org/tmcl/).


