The Built-in tolog Predicates
=============================

Reference Documentation
-----------------------

<span class="version">Ontopia 5.1 2010-06-09</p>

### Introduction ###

The tolog query language has a number of predefined predicates which are part of the language
itself. Some of these exist in order to allow access to various parts of the Topic Maps model, while
others exist because they provide generally useful query functionality. This document provides a
reference to all predicates that are part of the language.

This document is quite difficult to read without a thorough understanding of the Topic Maps model.
It is recommended to study the [Topic Maps Data
Model](http://www.isotopicmaps.org/sam/sam-model/).

### The general predicates ###

These are predicates that are built directly into the language without being part of any specific
module.

#### Index ####

* [/=](#p-not-equal)
* [<](#p-less-than)
* [<=](#p-less-than-equal)
* [=](#p-equals)
* [>](#p-greater-than)
* [>=](#p-greater-than-equal)
* [coalesce](#p-coalesce)

#### <a name="p-not-equal">`/=`</a> ####

Compares two values to ensure they are different.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|----|----|----|
| v1 | object | yes | yes | The first value to compare. | 
| v2 | object | yes | yes | The second value to compare. | 

##### Description #####

The predicate is true when the two values are different. For strings this means the values must be
different when compared character by character case-sensitively, including all whitespace and
special characters. For topic map objects this means they must be different objects. Null is
different from everything except itself.

Note that this predicate *cannot* produce new rows in the query or new values in any columns; it can
only remove rows.

**Find all association types with more than two roles**

````tolog
select $TYPE from
  association-role($ASSOC, $ROLE1),
  association-role($ASSOC, $ROLE2), $ROLE1 /= $ROLE2,
  association-role($ASSOC, $ROLE3), $ROLE3 /= $ROLE1, $ROLE3 /= $ROLE2
  type($ASSOC, $TYPE)?
````

This query will first find all association roles and their associations, then repeat each row once
for each role in the same association, and finaly remove the rowws where the two roles are the same.
The rest of the query is similar, except that the final predicate finds the type of the
association.


#### <a name="p-less-than">`<`</a> ####

Compares two values to ensure the first is less than the second.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| v1 | string | yes | yes | The first value to compare. | 
| v2 | string | yes | yes | The second value to compare. | 

##### Description #####

The predicate is true when the first value is less than the second. Note that this predicate will
never bind any values, nor produce any new result rows. Like [/=](#p-not-equal) it can only filter
out matches.

**Find all operas premiered before 1900**

````tolog
premiere-date($OPERA, $DATE),
$DATE < "1900"?
````


#### <a name="p-less-than-equal">`<=`</a> ####

Compares two values to ensure the first is less than or equal to the second.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| v1 | string | yes | yes | The first value to compare. | 
| v2 | string | yes | yes | The second value to compare. | 

##### Description #####

The predicate is true when the first value is less than or equal to the second. Note that this
predicate will never bind any values, nor produce any new result rows. Like [/=](#p-not-equal) it
can only filter out matches.

**Find all operas premiered before 1900**

````tolog
premiere-date($OPERA, $DATE),
$DATE <= "1900"?
````


#### <a name="p-equals">`=`</a> ####

Compares two values to ensure they are equal.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| v1 | object | no | yes | The first value to compare. | 
| v2 | object | no | yes | The second value to compare. | 

##### Description #####

The predicate is true when the two values are equal using the same comparison rule as
[/=](#p-not-equal). Note that at least one of the values must be present. (That is, it cannot
compare two unbound variables.)

Note that unlike [/=](#p-not-equal) this predicate *can* produce new values in columns.

**Find all operas premiered on a given date**

````tolog
premiere-date($OPERA, $DATE),
$DATE = "1870 (22 Feb)"?
````

This query will first set `$DATE` to the given date, then find all operas premiered on that date.
(Thanks to the query optimizer; if that is turned off the query will be rather less efficient, if
not really slow.)


#### <a name="p-greater-than">`>`</a> ####

Compares two values to ensure the first is greater than the second.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| v1 | string | yes | yes | The first value to compare. | 
| v2 | string | yes | yes | The second value to compare. | 

##### Description #####

The predicate is true when the first value is greater than the second. Note that this predicate will
never bind any values, nor produce any new result rows. Like [/=](#p-not-equal) it can only filter
out matches.

**Find all operas premiered after 1900**

````tolog
premiere-date($OPERA, $DATE),
$DATE > "1900"?
````


#### <a name="p-greater-than-equal">`>=`</a> ####

Compares two values to ensure the first is greater than or equal to the second.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| v1 | string | yes | yes | The first value to compare. | 
| v2 | string | yes | yes | The second value to compare. | 

##### Description #####

The predicate is true when the first value is greater than or equal to the second. Note that this
predicate will never bind any values, nor produce any new result rows. Like [/=](#p-not-equal) it
can only filter out matches.

**Find all operas premiered after 1900**

````tolog
premiere-date($OPERA, $DATE),
$DATE >= "1900"?
````


#### <a name="p-coalesce">`coalesce`</a> ####

This predicate is often used to assign fallback or default values to variables. If the first
argument is unbound then it is bound to the first non-null argument following it. If the first
argument is bound then it will be compared to the first non-null argument following
it.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| result | object | no | yes | The result value. | 
| v1..vN | object+ | yes | yes | The value to check to see if it is not null. If not null then it will be bound or compared to the result argument. In that case any following arguments will be ignored. | 

##### Description #####

The coalesce predicate is often used to assign default values, and is quite often used with optional
clauses as can be seen in the example below:

**Find all operas and their illustrations. Assign a default illustration to the operas that do not have one already.**

````tolog
select $OPERA, $ILLUSTRATION from 
instance-of($OPERA, opera), { illustration($OPERA, $I) }, 
coalesce($ILLUSTRATION, $I, "http://example.org/nicephoto.jpg")?
````


### The Topic Maps predicates ###

#### Index ####

* [association](#p-association)
* [association-role](#p-association-role)
* [base-locator](#p-base-locator)
* [datatype](#p-datatype)
* [direct-instance-of](#p-direct-instance-of)
* [instance-of](#p-instance-of)
* [item-identifier](#p-item-identifier)
* [object-id](#p-object-id)
* [occurrence](#p-occurrence)
* [reifies](#p-reifies)
* [resource](#p-resource)
* [role-player](#p-role-player)
* [scope](#p-scope)
* [subject-identifier](#p-subject-identifier)
* [subject-locator](#p-subject-locator)
* [topic](#p-topic)
* [topic-name](#p-topic-name)
* [topicmap](#p-topicmap)
* [type](#p-type)
* [value](#p-value)
* [value-like](#p-value-like)
* [variant](#p-variant)

#### <a name="p-association">`association`</a> ####

Used to verify that a value is actually an association.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| assoc | association | no | yes | The value being tested. | 

##### Description #####

This predicate is true if the parameter is an association, and false if it is not. If used with an
unbound variable it will produce all associations in the topic map.

**Counting the associations in the topic map**

````tolog
select count($ASSOC) from
  association($ASSOC)?
````

The above query counts the associations in the topic map by first producing all of them, then
counting them.

**Finding all association types**

````tolog
select $TYPE from
  association($ASSOC), type($ASSOC, $TYPE)?
````

The above query first finds all associations in the topic map, then finds the type of each, and
finally reduces the result to only the types.


#### <a name="p-association-role">`association-role`</a> ####

Used to query the relationship between associations and the roles they contain.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| assoc | association | no | yes | The association containing the roles. | 
| role | association role | no | yes | The association role contained in the association. | 

##### Description #####

This predicate is true for a given association and association role if the association role is
contained in the role. Can be used to find the association a role belongs to, all roles in an
association, or all roles in all associations.

**Finding all unary association types**

````tolog
select $TYPE from
  association-role($ASSOC, $ROLE1),
  not(association-role($ASSOC, $ROLE2), $ROLE2 /= $ROLE1),
  type($ASSOC, $TYPE)?
````

The above query first finds all roles in all associations, then removes the associations where there
exists another role in the same association, finds the type of the association, and projects us down
to the list of types. (So for any given type in the result there *could* be associations that are
instances of it which are not unary. This can be solved, but is more involved.)

**Finding all associations between two topics**

````tolog
select $ASSOC from
  role-player($ROLE1, topic1),
  association-role($ASSOC, $ROLE1),
  association-role($ASSOC, $ROLE2),
  role-player($ROLE2, topic2)?
````

The above query first finds all roles played by `topic1`, then finds the association of the role,
then finds all other roles in the same association, then removes all rows where the second role
isn't played by `topic2`, and finally projects us down to just the
associations.


#### <a name="p-base-locator">`base-locator`</a> ####

Used to find the base locator of the topic map.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| locator | locator | no | yes | The base locator of the topic map. | 

##### Description #####

This predicate is true for a single value: the base locator of the topic map, if it has one.

**Finding the base locator of the topic map**

````tolog
base-locator($LOC)?
````

This will return the base locator of the topic map, if it has one. For a topic map loaded from a
file this will be the URI of the file, while for an RDBMS topic map this will a JDBC URI pointing to
the database.


#### <a name="p-datatype">`datatype`</a> ####

Finds the datatype of an occurrence or variant name.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| object | variant, occurrence | no | yes | The object having the datatype. | 
| datatype | string | no | yes | The URI of the object's datatype, as a string. | 

##### Description #####

The predicate is true when the `object` has the `datatype` as its datatype URI. The predicate can be
used to find the datatype of an object, all objects with a specific datatype, or all object/datatype
combinations.

**Find all occurrences with integer values**

````tolog
select $OCC from
  occurrence($TOPIC, $OCC),
  datatype($OCC, "http://www.w3.org/2001/XMLSchema#integer")?
````

This query will find all occurrences in the topic map, then remove the ones whose datatype is not
the XML Schema integer datatype.


#### <a name="p-direct-instance-of">`direct-instance-of`</a> ####

Used to query the types topic are instances of; usually to find the most specific types of the
topics.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| instance | topic | no | yes | The instance topic. | 
| type | topic | no | yes | The type topic. | 

##### Description #####

This predicate is true when the `instance` topic is explicitly defined by the topic map as being an
instance of the `type` topic. (Contrast with [instance-of](#p-instance-of), where this need not be
said explicitly.)

**Finding all topic types in the topic map**

````tolog
select $TYPE from
  direct-instance-of($INSTANCE, $TYPE)?
````

This will find all instance-type pairs in the topic map, then cut away the instances, leaving only
the types.

**Finding persons and their types**

````tolog
  instance-of($PERSON, person),
  direct-instance-of($PERSON, $TYPE)?
````

In the `opera.ltm` topic map this query would first find all instances of the type `person` (that
is, the instances of all subtypes of person, such as composers, librettists, writers, characters,
and so on), and then find the types actually given to the topics in the topic map. The result would
be something like the table shown below.

| PERSON | TYPE | 
|---|---|
| Ulrica | Character | 
| La Rocca | Character | 
| Maddalena | Character | 
| ... | ... | 
| Cammarano, Salvatore | Librettist | 
| Civinini, Guelfo | Librettist | 
| Daudet, Alphonse | Writer | 
| ... | ... | 


#### <a name="p-instance-of">`instance-of`</a> ####

Used to query the types topic are instances of; takes the superclass-subclass association into
account.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| instance | topic | no | yes | The instance topic. | 
| type | topic | no | yes | The type topic. | 

##### Description #####

This predicate is true when the `instance` topic is an instance of the `type` topic. This can be
either directly, or `instance` can be an instance of a type that is a subtype of `type`. Note that
for this to work the superclass-subclass association type must use the XTM 1.0 PSIs. (Contrast with
[direct-instance-of](#p-direct-instance-of), where this needs to be said explicitly in the topic
map, and the superclass-subclass associations are ignored.)

**Finding all abstract topic types in the topic map**

````tolog
select $TYPE from
  instance-of($INST, $TYPE),
  not(direct-instance-of($INST, $TYPE))?
````

This will find all type-instance pairs, but then remove pairs where it is explicitly said in the
topic map that `$INST` is an instance of `$TYPE`. This will leave us with only the types which have
no direct instances, that is, where all instances are instances of one of the subtypes. Such types
are often called abstract types. One example of this in the Italian Opera topic map is "place".
There are lots of places, such as cities, regions, and countries, but they are always defined as
instances of one of the more specific types, never as just "place".


#### <a name="p-item-identifier">`item-identifier`</a> ####

Used to query the item identifiers of a topic map construct.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| object | topic map, topic, topic name, variant, occurrence, association, association role | no | yes | The object having the item identifier. | 
| locator | string | no | yes | The item identifier of the object. | 

##### Description #####

The predicate is true when the `locator` is a item identifier for the `object`. This is useful for
finding the item identifiers of a particular object, for looking up the object that has a particular
item identifier, or for listing all item identifiers in the topic map.

**Find all non-topics which have item identifiers**

````tolog
select $OBJECT from
  item-identifier($OBJECT, $LOC),
  not(topic($OBJECT))?
````

This query will, if run against a topic map that was loaded from an XTM file, find all objects in
the topic map other than topics which had `id` attributes. It works by first finding all objects
which have item identifiers (and the item identifiers), then remove the rows where the object is a
topic.


#### <a name="p-object-id">`object-id`</a> ####

Used to query object IDs of topic map objects.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| object | topic map, topic, topic name, variant, occurrence, association, association role | no | yes | The topic map object that has the ID. | 
| id | string | no | yes | The object ID of the topic map object. | 

##### Description #####

This predicate is true when the `id` is the object ID of the `object`. It can be used to find the
object ID of a specific object, to find the object that has a specific ID, or to find all object IDs
in the topic map.

Note that the object ID is *not* the same as the symbolic ID used in XTM or LTM files; for this, see
the [item-identifier](#p-item-identifier) predicate.

**Finding the object ID of Puccini**

````tolog
object-id(puccini, $ID)?
````

This query will produce the object ID after having looked up the Puccini topic. Note that the ID
will *not* be `"puccini"`, but rather something like `"2532"`.

**Finding the object with ID 241**

````tolog
object-id($OBJECT, "241")?
````

What this will return for any given topic map can't be predicted (try it in the Omnigator!), but in
any non-trivial topic map it will find *something*.


#### <a name="p-occurrence">`occurrence`</a> ####

Used to query the topic-occurrence relationship.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| topic | topic | no | yes | The topic that has the occurrence. | 
| occurrence | occurrence | no | yes | The occurrence of the topic. | 

##### Description #####

This predicate is true when the `occurrence` is an occurrence of the `topic`. This is useful for
finding the topic an occurrence belongs to, or all occurrences of a topic, or just all occurrences
in the topic map.

**Finding all occurrences of a topic**

````tolog
select $TYPE, $VALUE from
  occurrence(topic, $OCC),
  type($OCC, $TYPE),
  { resource($OCC, $VALUE) | value($OCC, $VALUE) }?
````

This query will first find all occurrences of the topic, then the type of the occurrence, then
either the URI of the occurrence or its string value, and finally make a (type, value) table of the
results.

**Finding all persons born on a specific date**

````tolog
select $PERSON from
  occurrence($PERSON, $OCC),
  type($OCC, date-of-birth),
  value($OCC, "1973-12-25")?
````

This query will first find all occurrences of all topics, then remove the occurrences that are not
`date-of-birth` occurrences, and then remove all occurrences that don't have `"1973-12-25"` as their
value, and finally we project down to only the topics that have these
occurrences.

Note that the easiest way to do this is to use a dynamic occurrence predicate:

**Finding all persons born on a specific date**

````tolog
date-of-birth($PERSON, "1973-12-25")?
````


#### <a name="p-reifies">`reifies`</a> ####

Used to query the relationship between a reifying topic and the thing it reifies.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| reifier | topic | no | yes | The reifying topic. | 
| reified | topic map, association, association role, topic name, variant, or occurrence | no | yes | The reified topic map construct. | 

##### Description #####

This predicate is true when the `reifier` is an topic that reifies the `reified`. This is useful
when you use reification and want to navigate from the name, occurrence, or association to the
reifying topic (or vice versa).

**Finding the topic reifying the topic map**

````tolog
select $TOPIC from
  topicmap($TM), reifies($TOPIC, $TM)?
````

This query finds the topic map first, then the topic reifying it (if there is one).


#### <a name="p-resource">`resource`</a> ####

Used to find the URI of an occurrence or variant name, or to find the occurrences and variant names
that have a particular URI.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| object | occurrence, variant | no | yes | The occurrence or variant which has the URI. | 
| locator | string | no | yes | The URI of the occurrence or variant. | 

##### Description #####

The predicate is true when the occurrence or variant has the `locator` as its URI value. This is
useful for looking up all occurrences/variants with a specific URI, or finding the URI of a
variant/occurrence, or all occurrences/variants which have URI values.

**Find Ontopia's home page**

````tolog
select $URI from
  occurrence(ontopia, $OCC),
  type($OCC, homepage),
  resource($OCC, $URI)?
````

This query will find all occurrences of the topic `ontopia`, then remove the ones that are not
homepage occurrences, and finally find the URIs of the ones remaining.


#### <a name="p-role-player">`role-player`</a> ####

Used to find the topic playing a specific role, or all the roles played by a topic.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| role | association role | no | yes | The role played by the topic. | 
| topic | topic | no | yes | The topic playing the role. | 

##### Description #####

The predicate is true when the `topic` plays the given `role`. This is useful for finding all roles
played by a topic, or the topic playing a particular role.

**Finding all associations between two topics**

````tolog
select $ASSOC from
  role-player($ROLE1, topic1),
  association-role($ASSOC, $ROLE1),
  association-role($ASSOC, $ROLE2),
  role-player($ROLE2, topic2)?
````

The above query first finds all roles played by `topic1`, then finds the association of the role,
then finds all other roles in the same association, then removes all rows where the second role
isn't played by `topic2`, and finally projects us down to just the
associations.


#### <a name="p-scope">`scope`</a> ####

Used to query the scopes of topic characteristics.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| scoped | topic name, variant, occurrence, association | no | yes | The thing having the scope. | 
| theme | topic | no | yes | The scoping topic. | 

##### Description #####

The predicate is true when the `theme` is a topic in the scope of the `scoped` thing. This is useful
for finding topic characteristics in a particular scope, finding the scope of a topic
characteristic, and so on.

**Finding the English names of all operas**

````tolog
select $NAME from
  instance-of($OPERA, opera),
  topic-name($OPERA, $TNAME),
  scope($TNAME, english),
  value($TNAME, $NAME)?
````

The above query first finds all operas, then all topic names (or base names) of the operas, then
removes the names that are not in the English scope, and finally finds the string value of the
remaining names.


#### <a name="p-subject-identifier">`subject-identifier`</a> ####

Used to query the subject identifiers of a topic. (A subject identifier is the URI of a subject
identifier.)

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| topic | topic | no | yes | The topic having the subject identifier. | 
| locator | string | no | yes | The subject identifier of the topic. | 

##### Description #####

The predicate is true when the `locator` is a subject identifier for the `topic`. This is useful for
finding the subject identifiers of a particular topic, for looking up the topic that has a
particular subject identifier, or for listing all subject identifiers in the topic
map.

**Find all topics which have more than one subject identifier**

````tolog
select $TOPIC from
  subject-identifier($TOPIC, $LOC1),
  subject-identifier($TOPIC, $LOC2),
  $LOC1 /= $LOC2?
````

The query will first find all subject identifiers in the topic map and the topics that have them,
then for each topic find other subject identifiers belonging to it (again), and finally remove the
rows where the two subject identifiers are the same.


#### <a name="p-subject-locator">`subject-locator`</a> ####

Used to query the subject locator of a topic. (A subject locator is the URI of the information
resource that the topic represents.)

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| topic | topic | no | yes | The topic having the subject locator. | 
| locator | string | no | yes | The subject locator of the topic. | 

##### Description #####

The predicate is true when the `locator` is a subject locator for the `topic`. This is useful for
finding the subject locator of a particular topic, for looking up the topic that has a particular
subject locator, or for listing all subject locators in the topic map.

**Find all topics which represent an occurrence**

````tolog
select $TOPIC from
  occurrence($OTHERTOPIC, $OCC),
  resource($OCC, $LOC),
  subject-locator($TOPIC, $LOC)?
````

This query will find all topics which represent information resources that are occurrences of some
topic in the topic map. It starts by finding all occurrences, then removes all occurrences which
don't have a URI and at the same time notes the URI, and finally finds all topics which have this
URI as their subject locator.


#### <a name="p-topic">`topic`</a> ####

Used to verify that an object is a topic or to find all topics in the topic map.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| topic | topic | no | yes | The object that is a topic. | 

##### Description #####

The predicate is true when the `topic` is a topic. Can be used to find all topics or to verify that
some object is a topic.

**Count the topics in the topic map**

````tolog
select count($TOPIC) from
  topic($TOPIC)?
````

This query will find all topics in the topic map, then count them.

**Find all topics which have no name**

````tolog
select $TOPIC from
  topic($TOPIC),
  not(topic-name($TOPIC, $NAME))?
````

This query will find all topics in the topic map, then remove the ones that have at least one name.


#### <a name="p-topic-name">`topic-name`</a> ####

Queries the topic-name relationship.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| topic | topic | no | yes | The topic that has the name. | 
| name | topic name | no | yes | The name of the topic. | 

##### Description #####

The predicate is true when the `topic` has the `name` as a topic name. Note that `name` is *not* a
string, but an object. To find the string value use the [value](#p-value) predicate on the name
object. (Example below.)

The predicate can be used to find all names of a topic, the topic that has a particular name, or to
find all names in the topic map.

**Find all topics named "Tosca"**

````tolog
select $TOPIC from
  topic-name($TOPIC, $NAME), value($NAME, "Tosca")?
````

This query will find all topic names in the topic map, then remove the ones whose string value is
not `"Tosca"`. Note that this will only find names that match exactly. For inexact matching, use
[value-like](#p-value-like).

**Find all topics which have no name**

````tolog
select $TOPIC from
  topic($TOPIC),
  not(topic-name($TOPIC, $NAME))?
````

This query will find all topics in the topic map, then remove the ones that have at least one name.


#### <a name="p-topicmap">`topicmap`</a> ####

Finds the topic map.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| topicmap | topic map | no | yes | The topic map. | 

##### Description #####

The predicate is true when the `topicmap` is the topic map itself. This is useful for finding the
topic map.

**Find the name of the topic map**

````tolog
select $VALUE from
  topicmap($TM),
  reifies($TMTOPIC, $TM),
  topic-name($TMTOPIC, $NAME),
  value($NAME, $VALUE)?
````

This query will first find the topic map, then the topic that reifies it (if any), then all names of
that topic, then their string values.


#### <a name="p-type">`type`</a> ####

Queries the type of topic map objects (but not topics).

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| object | association, topic name, occurrence, association role | no | yes | The object having the type. | 
| type | topic | no | yes | The type of the object. | 

##### Description #####

The predicate is true when the `object` has the `type` as its type. This can be used to find the
type of an object, all objects of a specific type, or all types. This predicate does not take the
superclass-subclass associations into account. Also note that for topics the
[instance-of](#p-instance-of) and [direct-instance-of](#p-direct-instance-of) predicates must be
used.

**Find all association types**

````tolog
select $TYPE from
  association($ASSOC),
  type($ASSOC, $TYPE)?
````

This query will first find all associations, then the type of each association, and finally produce
just a list of the types.


#### <a name="p-value">`value`</a> ####

Finds the string value of an object.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| object | topic name, variant, occurrence | no | yes | The object having the string value. | 
| value | string | no | yes | The string value of the object. | 

##### Description #####

The predicate is true when the `object` has the `value` as its string value. Variant names and
occurrences which have URIs have no string value, and so will not be matched by this predicate. The
predicate can be used to find the string value of an object, all objects with a specific value, or
all string values.

**Find all topics named "Tosca"**

````tolog
select $TOPIC from
  topic-name($TOPIC, $NAME), value($NAME, "Tosca")?
````

This query will find all topic names in the topic map, then remove the ones whose string value is
not `"Tosca"`. Note that this will only find names that match exactly. For inexact matching, use
[value-like](#p-value-like).


#### <a name="p-value-like">`value-like`</a> ####

Performs a full-text search.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| object | topic name, variant, occurrence | no | yes | The object having the string value matched by the full-text search. | 
| query | string | yes | yes | The full-text query string. tolog does not define the syntax to be used here; the interpretation depends on the full-text search engine used by the backend, and different backends may well use different search engines. | 
| score | float | no | no | The score/relevancy of the matched object. The value is a float greater than 0.0 and less than 1.0. | 

##### Description #####

The predicate is true when the `object` has a string value which matches the `query`. This can be
used to find topic names, variants, and occurrences matching a particular string. Note that the
object found is *not* a topic, but a topic name, variant, or occurrence. The other predicates must
be used to connect this to a topic.

**Find all topics matching "tosca"**

````tolog
select $TOPIC from
  { topic-name($TOPIC, $NAME), value-like($NAME, "tosca") |
    occurrence($TOPIC, $OCC),  value-like($OCC,  "tosca") |
    topic-name($TOPIC, $TN), variant($TN, $V), value-like($V, "tosca")
    }?
````

This query will find all topics in the topic map matching the full-text query, whether the full-text
query matches a topic name, variant, or occurrence.

**Find default names matching "tosca"**

````tolog
select $NAME, $SCORE from
  topic-name($TOPIC, $NAME), not(scope($NAME, $SCOPE)),
  value-like($NAME, "tosca", $SCORE), $SCORE >= 0.50 ?
````

This query makes use of the optional third argument to the value-like predicate. The query finds all
unconstrained topic names containing the pattern "tosca", and where the score value is greater than
or equal to 0.50. The score value is also included in the projection.


#### <a name="p-variant">`variant`</a> ####

Queries the topic name-variant relationship.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| topicname | topic name | no | yes | The topic name of which the variant is a variant. | 
| variant | variant | no | yes | The variant of the topic name. | 

##### Description #####

The predicate is true when the `variant` is a variant of the `topicname`. Can be used to find all
variants of a topic name, the topic name of a variant, or all variants.

Note that although XTM allows variants to nest within each other
[TMDM](http://www.isotopicmaps.org/sam/sam-model/) collapses this structure, and so that structure
is not available to be queried in tolog.

**Find all topics which have a sort name**

````tolog
select $TOPIC from
  topic-name($TOPIC, $NAME),
  variant($NAME, $VARIANT),
  scope($VARIANT, i"http://www.topicmaps.org/xtm/1.0/core.xtm#sort")?
````

This query will find all topic names in the topic map together with their topics, then all variants
of the topic names, and finally removes the rows where the variant is not in the scope of the sort
PSI defined by XTM 1.0.


### External fulltext predicates ###

It is possible to extend tolog with external fulltext predicates by implementing the `SearcherIF`
and `SearchResultIF` interfaces from the `net.ontopia.topicmaps.query.spi` package (see the API
documentation for more information). If the class `com.foo.MySearcher` implements the `SearcherIF`
interface, that class can be used in tolog as follows:

````tolog
import "urn:x-java:com.foo.MySearcher" as fulltext
select $A, $RELEVANCE from
  fulltext:search($A, "CMS", $RELEVANCE)
order by $RELEVANCE?
````

The class name in the import URI is enough for Ontopia to instantiate the class and use it. The
actual predicate name (`fulltext:search` in the example above) does not need to be significant. The
name is passed into the `SearcherIF` class, but it is up to the `SearcherIF` class to attach
significance to the predicate name. (One alternative is to always use a specific name, such as
"search". Another is to use the name to select which full-text index to
search.)

The parameters to external fulltext predicates are:

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| result | unspecified | no | yes | This is the value found by the search. The type depends on the searcher implementation. More information about this below. | 
| query | string | yes | yes | This is the query passed to the external full-text implementation. | 
| relevance | float | no | no | This is a floating-point number between 0 and 1 indicating the relevance of this item in the search result to the search criteria. 1 indicates the highest relevance, 0 the lowest. | 

The `SearcherIF` implementation can produce values of different types, as indicated by the
`getValueType` method. The table below explains the different possible values.

| Value type | Result type | Meaning | 
|---|---|---|
| `SUBJECT_LOCATOR` | topic | The searcher must return a string, and the predicate will return the topic with that subject locator. | 
| `SUBJECT_IDENTIFIER` | topic | The searcher must return a string, and the predicate will return the topic with that subject identifier. | 
| `ITEM_IDENTIFIER` | topic map object | The searcher must return a string, and the predicate will return the topic map object with that item identifier (a.k.a. source locator). | 
| `OBJECT_ID` | topic map object | The searcher must return a string, and the predicate will return the topic map object with that object ID. | 
| `STRING_VALUE` | string | The predicate will return the result of calling the `toString` on the object returned by the searcher. There is no defined Topic Maps interpretation of the string. | 
| `OBJECT_VALUE` | any | The predicate will return the the object returned by the searcher. There is no defined Topic Maps interpretation of the object. | 

### The string module ###

The URI of this module is `http://psi.ontopia.net/tolog/string/`. For usage, see the examples.

#### Index ####

* [concat](#p-concat)
* [contains](#p-contains)
* [ends-with](#p-ends-with)
* [index-of](#p-index-of)
* [last-index-of](#p-last-index-of)
* [length](#p-length)
* [starts-with](#p-starts-with)
* [substring](#p-substring)
* [substring-after](#p-substring-after)
* [substring-before](#p-substring-before)
* [translate](#p-translate)

#### <a name="p-concat">`concat`</a> ####

Concatenates two strings.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| OUT | string | no | yes | The concatenated string. | 
| IN1 | string | yes | yes | The first of the two strings to concatenate. | 
| IN2 | string | yes | yes | The second of the two strings to concatenate. | 

##### Description #####

The predicate is true when `OUT` is equal to `IN1` immediately followed by `IN2`.

**Concatenate two strings**

````tolog
import "http://psi.ontopia.net/tolog/string/" as str
  str:concat($NAME, "Ontopia", " AS")? 
````

This results in a single row with a single column containing the string `"Ontopia AS"`.


#### <a name="p-contains">`contains`</a> ####

Tests whether a substring appears in another string.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| STR | string | yes | yes | The string to test. | 
| SUBSTRING | string | yes | yes | The substring that must appear inside `STR`. | 

##### Description #####

The predicate is true when `SUBSTRING` appears at some position inside `STR`.

**Find all PSIs defined by Ontopia**

````tolog
import "http://psi.ontopia.net/tolog/string/" as str
select $SI from
  subject-identifier($TOPIC, $SI),
  str:contains($SI, ".ontopia.net/")?
````

In the following topic map:

````ltm
[subtype-of : hierarchical-relation-type = "Subtype of"
  @"http://www.topicmaps.org/xtm/1.0/core.xtm#superclass-subclass"]
[supertype = "Supertype"
  @"http://www.topicmaps.org/xtm/1.0/core.xtm#superclass"]
[subtype = "Subtype"
  @"http://www.topicmaps.org/xtm/1.0/core.xtm#subclass"]
[descr = "Description"
    @"http://psi.ontopia.net/occurrence/description"]
````

The result of this query would be one row, containing
`"http://psi.ontopia.net/occurrence/description"`.


#### <a name="p-ends-with">`ends-with`</a> ####

Tests whether a substring appears at the end of another string.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| STR | string | yes | yes | The string to test. | 
| SUFFIX | string | yes | yes | The substring that must appear at the end of `STR`. | 

##### Description #####

The predicate is true when `SUFFIX` appears as the last part of `STR`. In other words, if `STR` is
the concatenation of some other string and `SUFFIX` the predicate is true.

**Find all class PSIs**

````tolog
import "http://psi.ontopia.net/tolog/string/" as str
select $SI from
  subject-identifier($TOPIC, $SI),
  str:ends-with($SI, "class")?
````

In the following topic map:

````ltm
[subtype-of : hierarchical-relation-type = "Subtype of"
  @"http://www.topicmaps.org/xtm/1.0/core.xtm#superclass-subclass"]
[supertype = "Supertype"
  @"http://www.topicmaps.org/xtm/1.0/core.xtm#superclass"]
[subtype = "Subtype"
  @"http://www.topicmaps.org/xtm/1.0/core.xtm#subclass"]
[descr = "Description"
    @"http://psi.ontopia.net/occurrence/description"]
````

The result of this query would be three rows, containing
`"http://www.topicmaps.org/xtm/1.0/core.xtm#superclass-subclass"`,
`"http://www.topicmaps.org/xtm/1.0/core.xtm#subclass"`, and
`"http://www.topicmaps.org/xtm/1.0/core.xtm#superclass"`.


#### <a name="p-index-of">`index-of`</a> ####

Finds the first occurrence of a substring within another string.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| OUT | number | no | yes | The position of the substring. | 
| IN | string | yes | yes | The string to search in. | 
| SEARCHFOR | string | yes | yes | The substring to search for. | 

##### Description #####

The predicate is true when `SEARCHFOR` can be found inside `IN` starting at position `OUT`, and
there are no occurrences of `SEARCHFOR` at a lower position inside `IN`. Position counts start at
zero. This means that if the substring cannot be found at all the predicate does not match. Multiple
occurrences of the substring later in the string are ignored.

**Find the first occurrence of a word in a string.**

````tolog
import "http://psi.ontopia.net/tolog/string/" as str
  str:index-of($POS, "The first occurrence of 'the' in the sentence.", "the")? 
````

This results is a single row with a single column containing the position of the left-most
occurrence of 'the' (all lowercase) in the sentence, i.e. `25`.


#### <a name="p-last-index-of">`last-index-of`</a> ####

Finds the last occurrence of a substring within another string.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| OUT | number | no | yes | The position of the substring. | 
| IN | string | yes | yes | The string to search in. | 
| SEARCHFOR | string | yes | yes | The substring to search for. | 

##### Description #####

The predicate is true when `SEARCHFOR` can be found inside `IN` starting at position `OUT`, and
there are no occurrences of `SEARCHFOR` at a higher position inside `IN`. Position counts start at
zero. This means that if the substring cannot be found at all the predicate does not match. Multiple
occurrences of the substring earlier in the string are ignored.

**Find all PSI namespaces in a topic map**

````tolog
import "http://psi.ontopia.net/tolog/string/" as str
select $PREFIX from
  subject-identifier($TOPIC, $SI),
  str:last-index-of($IX, $SI, "/"),
  str:substring($PREFIX, $SI, 0, $IX)
order by $PREFIX?
````

In the following topic map:

````ltm
[subtype-of : hierarchical-relation-type = "Subtype of"
  @"http://www.topicmaps.org/xtm/1.0/core.xtm#superclass-subclass"]
[supertype = "Supertype"
  @"http://www.topicmaps.org/xtm/1.0/core.xtm#superclass"]
[subtype = "Subtype"
  @"http://www.topicmaps.org/xtm/1.0/core.xtm#subclass"]
[descr = "Description"
    @"http://psi.ontopia.net/occurrence/description"]
````

The result of this query would be two rows, containing `"http://www.topicmaps.org/xtm/1.0/"` and
`"http://psi.ontopia.net/occurrence/"`.


#### <a name="p-length">`length`</a> ####

Finds the length of a string.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| STR | string | yes | yes | The string to test. | 
| LENGTH | number | no | yes | The number of characters in `STR`. | 

##### Description #####

The predicate is true when `STRING` contains exactly `LEN` characters.

**Find the topics with the longest names**

````tolog
import "http://psi.ontopia.net/tolog/string/" as str
select $TOPIC from
  topic-name($TOPIC, $TN),
  value($TN, $NAME),
  str:length($NAME, $LEN),
  not(topic-name($TOPIC2, $TN2),
      value($TN2, $NAME2),
      str:length($NAME2, $LEN2),
      $LEN < $LEN2)?
````

This query will find the topic with the longest name, or almost. What it in fact will do is to find
all topics for which there is no topic with a longer name. This means that if, say, three topics all
have names 26 characters long, and no topics have longer names, then all those three topics will be
found.


#### <a name="p-starts-with">`starts-with`</a> ####

Tests whether a substring appears at the beginning of another string.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| STR | string | yes | yes | The string to test. | 
| PREFIX | string | yes | yes | The substring that must appear at the beginning of `STR`. | 

##### Description #####

The predicate is true when `PREFIX` appears inside `STR` starting at position 0.

**Find all PSIs defined by Ontopia**

````tolog
import "http://psi.ontopia.net/tolog/string/" as str
select $SI from
  subject-identifier($TOPIC, $SI),
  str:starts-with($SI, "http://psi.ontopia.net/")?
````

In the following topic map:

````ltm
[subtype-of : hierarchical-relation-type = "Subtype of"
  @"http://www.topicmaps.org/xtm/1.0/core.xtm#superclass-subclass"]
[supertype = "Supertype"
  @"http://www.topicmaps.org/xtm/1.0/core.xtm#superclass"]
[subtype = "Subtype"
  @"http://www.topicmaps.org/xtm/1.0/core.xtm#subclass"]
[descr = "Description"
    @"http://psi.ontopia.net/occurrence/description"]
````

The result of this query would be one row, containing
`"http://psi.ontopia.net/occurrence/description"`.


#### <a name="p-substring">`substring`</a> ####

Extract part of a string.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| OUT | string | no | yes | The resulting string. | 
| STR | string | yes | yes | The string to operate on. | 
| FROM | number | yes | yes | The position of the start of the substring. | 
| TO | number | yes | no | The position of the end of the substring. | 

##### Description #####

The predicate is true when `OUT` is equal to the substring of `STR` starting at position `FROM` and
ending at position `TO` (non-inclusive). If `TO` is not given, the substring extends to the end of
the string. All position counts are zero-based.

**Find all PSI namespaces in a topic map**

````tolog
import "http://psi.ontopia.net/tolog/string/" as str
select $PREFIX from
  subject-identifier($TOPIC, $SI),
  str:last-index-of($IX, $SI, "/"),
  str:substring($PREFIX, $SI, 0, $IX)
order by $PREFIX?
````

This query would produce all PSI namespaces used in a given topic map.


#### <a name="p-substring-after">`substring-after`</a> ####

Extract part of a string after the first occurrence of a substring.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| OUT | string | no | yes | The resulting string. | 
| STR | string | yes | yes | The string to operate on. | 
| SUB | string | yes | yes | The substring to search for. | 

##### Description #####

The predicate is true when `OUT` is equal to the substring of `STR` after the first occurrence of
`SUB` within `STR`. If there are no occurrences of `SUB` the predicate is
false.

**Get month and day**

````tolog
import "http://psi.ontopia.net/tolog/string/" as str
select $DATE from
  str:substring-after($DATE, "2007-03-12", "-")?
````

This query would produce `03-12`.


#### <a name="p-substring-before">`substring-before`</a> ####

Extract part of a string before the first occurrence of a substring.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| OUT | string | no | yes | The resulting string. | 
| STR | string | yes | yes | The string to operate on. | 
| SUB | string | yes | yes | The substring to search for. | 

##### Description #####

The predicate is true when `OUT` is equal to the substring of `STR` before the first occurrence of
`SUB` within `STR`. If there are no occurrences of `SUB` the predicate is
false.

**Get month and day**

````tolog
import "http://psi.ontopia.net/tolog/string/" as str
select $DATE from
  str:substring-before($DATE, "2007-03-12", "-")?
````

This query would produce `2007`.


#### <a name="p-translate">`translate`</a> ####

Replace and/or delete characters in one string. Often used to remove whitespace or do case
normalization.

##### Parameters #####

| Name | Type | Must be bound | Required | Description | 
|---|---|---|
| OUT | string | no | yes | The resulting string. | 
| STR | string | yes | yes | The string to operate on. | 
| FROM | string | yes | yes | The list of characters to translate from. | 
| TO | string | yes | yes | The list of characters to translate to. | 
| DELETE | string | no | yes | The list of characters to remove. | 

##### Description #####

The predicate builds a translation table where the first character in `FROM` maps to the first
character in `TO` (and so on). This is the basic function performed by the
predicate.

The predicate is really a mix of the XPath `translate` function and the Python `translate` function,
turned into a predicate. If the `DELETE` parameter is provided it behaves like the Python function,
and if not it behaves like the XPath function.

That is, if the `DELETE` is not present, then the predicate translates characters as specified by
the translation table, but any characters for which no mapping has been defined will be deleted.
Characters in the `FROM` string for which there are no corresponding characters in the `TO` string
will map to themselves (that is, if `FROM` is longer than `TO`). Conversely, if `TO` is longer than
`FROM` any excess characters in `TO` will be ignored.

**Lowercasing**

````tolog
import "http://psi.ontopia.net/tolog/string/" as str
select $OUT from
  str:translate($OUT,
    "Addis Abeba (12)",
    "ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvxyz ",
    "abcdefghijklmnopqrstuvxyzabcdefghijklmnopqrstuvxyz ")?
````

The example above produces "addis abeba " as the output. If we remove the space at the end of the
`FROM` string the result is "addisabeba".

If the `DELETE` *is* present, then the predicate translates characters as specified by the
translation table and any characters for which no mapping has been defined will be mapped to
themselves. Any characters in the `DELETE` string will be deleted, of course. If `FROM` and `TO` are
of different lengths the effect is the same as specified above when `DELETE` is not
present.

**Lowercasing**

````tolog
import "http://psi.ontopia.net/tolog/string/" as str
select $OUT from
  str:translate($OUT,
    "Addis Abeba (12)",
    "ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvxyz",
    "abcdefghijklmnopqrstuvxyzabcdefghijklmnopqrstuvxyz",
    "()")?
````

The example above produces "addis abeba 12" as the output.



