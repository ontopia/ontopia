The RTM RDF to Topic Maps mapping
=================================

Definition and introduction
---------------------------

<p class="introduction">
This technical report defines version 0.2 of the RTM RDF to Topic Maps mapping vocabulary.
</p>

<p class="introduction">
Please note that this document is not a formal specification from any recognized standards body, but
a Technical Report published by Ontopia, for the convenience of all interested parties. The
specification may be taken up by some standards body at some point, but no initiatives in this
direction have been taken as yet.
</p>

<span class="version">Ontopia Version 0.2 ($Revision: 1.3 $) $Date: 2004/12/08 16:52:27 $</p>

### Introduction ###

RTM is a vocabulary that can be used to describe the mapping of an RDF vocabulary to topic maps in
such a way that RDF data using that vocabulary can be converted automatically to topic maps. This
allows RDF vocabularies to be used both as RDF vocabularies and as topic map
vocabularies.

The vocabulary annotates RDF properties to indicate which topic map construct the property is to be
mapped to. The type and scope of the resulting construct can be controlled, and for associations the
role played by the subject and the object must be indicated. The RTM mapping declarations can be
stored as part of the data to be mapped, or it can be stored externally.

The background and rationale for the design of this vocabulary can be found in the paper [Living
with topic maps and
RDF](http://www.ontopia.net/topicmaps/materials/tmrdf.html).

### The vocabulary ###

Throughout this document the convention `rtm:foo` should be taken to mean the URI reference
`http://psi.ontopia.net/rdf2tm/#foo`. Each of these URI references are published subject identifiers
that together make up the RTM vocabulary.

RTM consists of the following RDF properties, as well as the individual resources defined in [The
mapping algorithm](#the-mapping-algorithm). The subjects of statements using these properties are
always RDF properties whose mapping to topic maps is to be described.

rtm:maps-to
:    This property defines which topic map construct an RDF property is converted to during RDF-to-topic
     map mapping. RDF properties for which no `rtm:maps-to` statement is found are ignored. The possible
     values of this predicate are enumerated in [The mapping
     algorithm](#the-mapping-algorithm).

rtm:type
:    This property is used to override the default type otherwise assigned to topic map constructs
     created by the mapping.

rtm:in-scope
:    This property is used to add a topic to the scope of topic map constructs created by the mapping.

rtm:subject-role
:    This property is used to indicate the association role type played by the subject of an RDF
     statement mapped to an association.

rtm:object-role
:    This property is used to indicate the association role type played by the object of an RDF statement
     mapped to an association.


### The mapping algorithm ###

The input to the mapping consists of:

*  An RDF model containing the data to be mapped to a topic map, known as the *input model*.
*  An RDF model containing the mapping information, known as the *mapping model*. This may be the same
   model as the input model, but need not be.
*  The topic map into which the data is to be mapped. It may be empty, but need not be.

For precision, the topic map is here described as an instance of the Topic Maps Data Model defined
in [&#91;TMDM&#93;](#tmdm), while the RDF models are described as RDF graphs, as defined by
[&#91;RDF&#93;](#rdf).

The mapping is done by traversing all the triples in the input model and for each triple:

 1.  Let `s1` be the subject of that triple, `p1` the predicate, and `o1` the object.
 2.  Look for a triple in the mapping model whose subject is `p1` and whose predicate is `rtm:maps-to`.
    If no such triple is found, move on to the next triple of the input model. If more than one such
    triple is found, select one arbitrarily and let `o2` be the object of that
    predicate.
 3.  If `s1` is a URI reference, get the topic produced by the procedure in [Getting a topic for a URI
    reference](#getting-a-topic-for-a-uri-reference) for that URI reference and let it be known as
    `t1`.
 4.  Now follow the instructions in the section below whose name corresponds to the value of `o2`. It is
    an error if there is no such section, or if `o2` is a literal.

#### rtm:basename ####

Create a topic name item and add it to the [topic names] property of `t1`. Set its [value] property
to the string value of `o1`. It is an error if `o1` is not a literal.

Finally, let the topic name item be known as `c` and follow the procedure in [Adding
scope](#adding-scope). Implementations which support typed names must also follow the procedure in
[Adding type](#adding-type).

#### rtm:occurrence ####

Create an occurrence item and add it to the [occurrences] property of `t1`. If `o1` is a literal,
set its [value] property to the string value of `o1`, otherwise create a locator item and set its
[notation] property to `"URI"` and its [reference] property to `o1`, and set the locator as the
value of the occurrence item's [locator] property. It is an error if `o1` is a blank
node.

Finally, let the occurrence item be known as `c` and follow the procedures in [Adding
scope](#adding-scope) and [Adding type](#adding-type).

#### rtm:association ####

Create an association item and add it to the [associations] property of the topic map item. Let `t2`
be the topic for `o1`; it is an error if `o1` is a literal.

Look for a triple in the mapping model whose subject is `p1` and whose predicate is
`rtm:subject-role`; let `rt1` be the object. It is an error if `rt1` is a blank node or a literal.
Create an association role item and add it to the [roles] property of the association, set its
[player] property to `t1`, and its [type] property to the topic for `rt1`.

Now look for a triple in the mapping model whose subject is `p1` and whose predicate is
`rtm:object-role`; let `rt2` be the object. It is an error if `rt2` is a blank node or a literal.
Create an association role item and add it to the [roles] property of the association, set its
[player] property to `t2`, and its [type] property to the topic for `rt2`.

Finally, let the association item be known as `c` and follow the procedures in [Adding
scope](#adding-scope) and [Adding type](#adding-type).

#### rtm:instance-of ####

Create an association item. Get the topic for `http://psi.topicmaps.org/sam/1.0/#type-instance` and
put it in the [type] property of the association item.

Create an association role item. Get the topic for `http://psi.topicmaps.org/sam/1.0/#type` and put
it in its [type] property. Get the topic for `o1` and put it in the [player] property. It is an
error if `o1` is a literal.

Create an association role item. Get the topic for `http://psi.topicmaps.org/sam/1.0/#instance` and
put it in its [type] property, and put `t1` in the [player] property.

Finally, let the association item be known as `c` and follow the procedure in [Adding
type](#adding-type).

> **Note**
> The URIs used here are the ones given in the current draft of TMDM, but they are unlikely to be
> the URIs in the final version. It is recommended that implementations represent RDF statements
> mapped to `instance-of` using their internal representation for the `type-instance` relationship.
> This document will be updated when TMDM is finalized.

#### rtm:subject-identifier ####

Create a locator item and set its [notation] property to `"URI"` and its [reference] property to
`o1`, then add it to the [subject identifiers] property of `t1`. It is an error if `o1` is a literal
or a blank node.

#### rtm:subject-locator ####

Create a locator item and set its [notation] property to `"URI"` and its [reference] property to
`o1`, then set it as the value of the [subject locator] property of `t1`. It is an error if `o1` is
a literal or a blank node, or if `t1` already has a different value in its [subject locator]
property.

#### rtm:source-locator ####

Create a locator item and set its [notation] property to `"URI"` and its [reference] property to
`o1`, then add it to the [source locators] property of `t1`. It is an error if `o1` is a literal or
a blank node.

#### Adding scope ####

For each triple whose subject is `p1` and whose predicate is `rtm:in-scope`, get the topic for the
object of the triple and add it to the [scope] property of `c`. It is an error if the object is a
literal.

#### Adding type ####

Look for a triple whose subject is `p1` and whose predicate is `rtm:type`. If such a triple is
found, get the topic for the object of the triple and set it as the value of the [type] property of
`c`. It is an error if the object is a literal.

If not such triple is found, get the topic for `p1` and set it as the value of the [type] property
of `c`.

#### Getting a topic for a URI reference ####

Look for a topic item whose [subject identifiers] property contains a locator item whose [notation]
property is `"URI"` and whose [reference] property contains the URI reference. If no such topic item
is found, create one.

### Appendix: Bibliography ###

<a name="rdf"></a>
RDF
:    _Resource Description Framework (RDF): Concepts and Abstract Syntax_ , Graham Klyne, Jeremy J.
     Carroll, World Wide Web Consortium

<a name="tmdm"></a>
TMDM
:    _ISO/IEC 13250-2: Topic Maps — Data Model_ , International Organization for Standardization, Geneva
     Switzerland

### Appendix 1: The RTM schema ###

This appendix provides an RDF Schema and OWL schema for the RTM vocabulary.

````xml
<rdf:RDF 
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" 
	xmlns:owl="http://www.w3.org/2002/07/owl#"
	xmlns:rtm="http://psi.ontopia.net/rdf2tm/#">

  <!-- ===== ONTOLOGY ===================================================== -->
  <owl:Ontology rdf:about="http://psi.ontopia.net/rdf2tm/">
    <rdfs:label>RTM: RDF to topic map mapping</rdfs:label>
    <rdfs:comment>A vocabulary for expressing mappings from RDF
    vocabularies to topic map forms of the same vocabularies.</rdfs:comment>
    <rdfs:seeAlso rdf:resource="http://www.ontopia.net/topicmaps/materials/tmrdf.html"/>
    <owl:imports rdf:resource="http://www.w3.org/2000/01/rdf-schema"/>
    <owl:imports rdf:resource="http://www.w3.org/2002/07/owl"/>
  </owl:Ontology>


  <!-- ~~~~~ CONSTRUCT -->
  <owl:Class rdf:about="http://psi.ontopia.net/rdf2tm/#Construct" 
	rdfs:label="Construct"
	rdfs:comment="A topic map construct to which an RDF property may be mapped.">
    <rdfs:isDefinedBy rdf:resource="http://psi.ontopia.net/rdf2tm/"/>
    <owl:oneOf rdf:parseType="Collection">
      <owl:Thing rdf:about="http://psi.ontopia.net/rdf2tm/#basename"/>
      <owl:Thing rdf:about="http://psi.ontopia.net/rdf2tm/#occurrence"/>
      <owl:Thing rdf:about="http://psi.ontopia.net/rdf2tm/#association"/>
      <owl:Thing rdf:about="http://psi.ontopia.net/rdf2tm/#source-locator"/>
      <owl:Thing rdf:about="http://psi.ontopia.net/rdf2tm/#subject-identifier"/>
      <owl:Thing rdf:about="http://psi.ontopia.net/rdf2tm/#subject-locator"/>
      <owl:Thing rdf:about="http://psi.ontopia.net/rdf2tm/#instance-of"/>
    </owl:oneOf>
    <!-- disjoint with pretty much everything else -->
  </owl:Class>


  <!-- ~~~~~ MAPS-TO -->
  <owl:FunctionalProperty rdf:about="http://psi.ontopia.net/rdf2tm/#maps-to" 
	rdfs:label="maps to"
	rdfs:comment="Specifies the topic map construct to which a particular RDF 
        property is to be mapped.">
    <rdfs:domain rdf:resource="http://www.w3.org/2000/01/rdf-schema#Property"/>
    <rdfs:range rdf:resource="http://psi.ontopia.net/rdf2tm/#Construct"/>
    <rdfs:isDefinedBy rdf:resource="http://psi.ontopia.net/rdf2tm/"/>
  </owl:FunctionalProperty>

  <!-- ~~~~~ TYPE -->
  <owl:FunctionalProperty rdf:about="http://psi.ontopia.net/rdf2tm/#type" 
	rdfs:label="of type"
	rdfs:comment="Specifies the type of the topic map construct created from 
        statements using this RDF property.">
    <rdfs:domain rdf:resource="http://www.w3.org/2000/01/rdf-schema#Property"/>
    <rdfs:range rdf:resource="http://www.w3.org/2000/01/rdf-schema#Resource"/>
    <rdfs:isDefinedBy rdf:resource="http://psi.ontopia.net/rdf2tm/"/>
  </owl:FunctionalProperty>

  <!-- ~~~~~ IN-SCOPE -->
  <rdf:Property rdf:about="http://psi.ontopia.net/rdf2tm/#in-scope" 
	rdfs:label="in scope"
	rdfs:comment="Specifies a topic to be added to the scope of topic map 
        constructs created from statements with this property.">
    <rdfs:domain rdf:resource="http://www.w3.org/2000/01/rdf-schema#Property"/>
    <rdfs:range rdf:resource="http://www.w3.org/2000/01/rdf-schema#Resource"/>
    <rdfs:isDefinedBy rdf:resource="http://psi.ontopia.net/rdf2tm/"/>
  </rdf:Property>

  <!-- ~~~~~ SUBJECT-ROLE -->
  <owl:FunctionalProperty rdf:about="http://psi.ontopia.net/rdf2tm/#subject-role" 
	rdfs:label="subject role"
	rdfs:comment="Specifies the role type played by the subject in associations 
        created from statements with this property.">
    <rdfs:domain rdf:resource="http://www.w3.org/2000/01/rdf-schema#Property"/>
    <rdfs:range rdf:resource="http://www.w3.org/2000/01/rdf-schema#Resource"/>
    <rdfs:isDefinedBy rdf:resource="http://psi.ontopia.net/rdf2tm/"/>
  </owl:FunctionalProperty>

  <!-- ~~~~~ OBJECT-ROLE -->
  <owl:FunctionalProperty rdf:about="http://psi.ontopia.net/rdf2tm/#object-role" 
	rdfs:label="object role"
	rdfs:comment="Specifies the role type played by the object in associations 
        created from statements with this property.">
    <rdfs:domain rdf:resource="http://www.w3.org/2000/01/rdf-schema#Property"/>
    <rdfs:range rdf:resource="http://www.w3.org/2000/01/rdf-schema#Resource"/>
    <rdfs:isDefinedBy rdf:resource="http://psi.ontopia.net/rdf2tm/"/>
  </owl:FunctionalProperty>

</rdf:RDF>
````


