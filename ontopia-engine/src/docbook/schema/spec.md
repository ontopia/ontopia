The Ontopia Schema Language
===========================

Reference Specification
-----------------------

<p class="introduction">
This document is the specification of the Ontopia Schema Language, providing an exhaustive reference
documenting all features of the language. For an introduction to the schema language, see the schema
tutorial.
</p>

<span class="version">5.1 ($Revision: 1.20 $) $Date: 2008/06/13 12:06:19 $</p>

### Introduction ###

The Ontopia Schema Language uses an XML syntax defined in a DTD to specify schemas. Schemas written
in the language consist of definitions of classes of topics and associations, defining the allowed
characteristics of each. Validating a topic map against a schema is done by finding the class
definition for each topic and association, and verifying that the instance matches the class
definition.

The Ontopia Schema Language has been designed to have a minimal number of features and a minimum of
expressive power. We expect to extend its feature set according to user demand, and eventually to
replace it with the Topic Map Constraint Language (TMCL) currently being developed within ISO. We
have decided not to implement the TMCL before it is finished in order to have a stable schema
language as the basis for our product suite.

The Ontopia Schema Language is intended to support many functions within topic map projects, the
most important of which are:

*  validation of the structure of topic map instances,
*  documentation of the structure of topic maps, and
*  guidance of topic map editing applications, in order to make topic map editing easier and more
   convenient.

### High-level elements ###

This section defines the semantics of the high-level elements of the Ontopia Schema Language,
element by element.

#### The tm-schema element ####

|--|--|
| Summary: | The container element for the schema. | 
| Content model: | (ruleset &#124; topic &#124; association)*  | 
| Parents: | None | 

The `tm-schema` element must be the document element of all topic map schemas. It has no special
semantics, but merely acts as a container for the topic map schema.

The `tm-schema` element has a single attribute: `match`, which can have the values `loose` and
`strict`. When doing loose matching, topics and associations which match none of the specified
classes are accepted without complaint. When doing strict matching, such topics and associations are
rejected. The default is loose matching.

The allowed children of `tm-schema` elements are `ruleset`, `topic`, and `association` elements, in
any order.

| Name | Type | Required | Default | Definition | 
|--|--|--|--|--|
| match | loose, strict | #DEFAULT | loose | Controls whether the schema uses loose or strict matching. | 

#### The ruleset element ####

|--|--|
| Summary: | Contains a set of reusable named constraints. | 
| Content model: | (ruleref* , (baseName &#124; occurrence &#124; playing)* )  | 
| Parents: | tm-schema | 

The `ruleset` element is used to specify a set of constraints which can be reused by any topic class
definition. Its intent is to allow the specification of reused rules in a single place, improving
maintainability and readability of topic map schemas.

The `ruleset` element must have one, and only one attribute, named `id`, and containing a unique
identifier for the rule set, allowing it to be referenced from topic classes.

The `ruleset` element may contain any number of `ruleref` elements, followed by any number of
`baseName`, `occurrence`, and `playing` elements.

| Name | Type | Required | Default | Definition | 
|--|--|--|--|--|
| id | ID | #REQUIRED |   | The ID of the ruleset, by which it may be referred to. | 

#### The topic element ####

|--|--|
| Summary: | Defines a topic class. | 
| Content model: | (instanceOf , otherClass* , (ruleref &#124; superclass)* , (baseName &#124; occurrence &#124; playing)* )  | 
| Parents: | tm-schema | 

The `topic` element defines the constraints applying to topics belonging to a particular class. The
element may contain constraints on the characteristics on instances of the class, as well as other
kinds of restrictions.

Each `topic` element must have an `instanceOf` child, specifying what class of topic is constrained
by this rule. Only topics which match this `instanceOf` element must follow the constraints
specified by this `topic` element.

`topic` elements may have a `match` attribute, which may be set to `loose` or `strict`. If set to
`loose` topic names, occurrences, and roles not matching any of the constraints in the topic class
definition are accepted. If set to `strict` such characteristics will be rejected. The default is
`strict` matching.

The `otherClass` elements can be used to specify what other classes instances of this topic class
may be instances of. If the element is not present instances of this class may not be instances of
any other class. If present, instances of this class may only be instances of the classes specified
by the element, and not any other classes.

The `baseName`, `occurrence`, and `playing` elements specify what characteristics instances of this
class may have.

| Name | Type | Required | Default | Definition | 
|--|--|--|--|--|
| id | ID | #IMPLIED |   | The ID of the class by which it may be referred to. | 
| match | loose, strict | #DEFAULT | strict | Whether matching of constraints is loose or strict. | 

#### The baseName element ####

|--|--|
| Summary: | A constraint on the topic names of a topic. | 
| Content model: | (scope , variant*)  | 
| Parents: | topic, ruleset | 

`baseName` elements constrain the topic names a topic may have. If a topic name has scope matching
that specified in the `scope` child element of a `baseName` element it is validated against that
`baseName` rule.

`baseName` elements may have `min` and `max` attributes specifying the minimum and maximum number of
matching topic names each topic may have. The default is that any number of matches is
allowed.

The `variant` child element is used to specify what variants a topic name may have. Matching of
variant names is always strict, so variant names that do not match a rule in the schema are
rejected.

| Name | Type | Required | Default | Definition | 
|--|--|--|--|--|
| max | CDATA | #DEFAULT | Inf | The maximum number of matches allowed on this constraint for each instance of the class. | 
| min | CDATA | #DEFAULT | 0 | The minimum number of matches allowed on this constraint for each instance of the class. | 

#### The variant element ####

|--|--|
| Summary: | A constraint on the variant names of a base name. | 
| Content model: | (scope)  | 
| Parents: | baseName | 

The `variant` element is used to constrain the allowed variants of topic names. If a variant name
has scope matching that specified in the `scope` child element of a `variant` element it is
validated against that `variant` rule.

`variant` elements may have `min` and `max` attributes specifying the minimum and maximum number of
matching variant names each topic name may have. The default is that any number of matches is
allowed.

| Name | Type | Required | Default | Definition | 
|--|--|--|--|--|
| max | CDATA | #DEFAULT | Inf | The maximum number of matches allowed on this constraint for each instance of the class. | 
| min | CDATA | #DEFAULT | 0 | The minimum number of matches allowed on this constraint for each instance of the class. | 

#### The occurrence element ####

|--|--|
| Summary: | A constraint on the occurrences of a topic. | 
| Content model: | (instanceOf , scope?)  | 
| Parents: | topic, ruleset | 

The `occurrence` element is used to constrain the possible occurrences a topic may have. If an
occurrence has a type matching that specified by the `instanceOf` child it is validated against that
`occurrence` rule.

The `occurrence` element may also have `min` and `max` attributes. The `internal` attribute may be
set to `yes` (meaning that matching occurrences must be internal), `no` (meaning that matching
occurrences must be external), or `either`, meaning that they may be either.

If the `occurrence` element has a `scope` child element the scope of each occurrence is matched
against that specified in the `scope` child.

| Name | Type | Required | Default | Definition | 
|--|--|--|--|--|
| internal | yes, no, either | #DEFAULT | either | Whether the occurrence must be internal or external, or whether it can be both. | 
| max | CDATA | #DEFAULT | Inf | The maximum number of matches allowed on this constraint for each instance of the class. | 
| min | CDATA | #DEFAULT | 0 | The minimum number of matches allowed on this constraint for each instance of the class. | 

#### The playing element ####

|--|--|
| Summary: | Defines a constraint on the roles a topic may play in associations. | 
| Content model: | (instanceOf , in?)  | 
| Parents: | topic, ruleset | 

The `playing` element is used to constrain what roles a topic may play in associations. If an
association role has a type matching that specified in the `instanceOf` child it is validated
against the `playing` rule.

The `playing` element may have `min` and `max` attributes constraining the cardinality of matching
association roles.

The `playing` element may have an `in` child, which specifies what types of associations the
association roles may be part of. The `in` element may contain any number of `instanceOf` elements.
As long as the containing association matches one of those elements the association role is
accepted.

| Name | Type | Required | Default | Definition | 
|--|--|--|--|--|
| max | CDATA | #DEFAULT | Inf | The maximum number of matches allowed on this constraint for each instance of the class. | 
| min | CDATA | #DEFAULT | 0 | The minimum number of matches allowed on this constraint for each instance of the class. | 

#### The association element ####

|--|--|
| Summary: | Defines an association class. | 
| Content model: | (instanceOf , scope? , role+)  | 
| Parents: | tm-schema | 

The `association` element is used to define the structure of a class of associations. Any
association which matches the `instanceOf` child of an `association` element is validated against
the rule specified by that element.

The `scope` element can be used to define what scopes are allowed for associations of this class. If
the element is not present, all scopes are allowed. If present, the scope of all associations must
match that specified.

The association roles in the association will be validated against the `role` children of the
`association` element. Roles that do not match any `role` element will be
rejected.

The `association` element has no attributes.

#### The role element ####

|--|--|
| Summary: | Contains a constraint on the association roles in an association. | 
| Content model: | (instanceOf , player*)  | 
| Parents: | association | 

The `role` element is used to constrain the association roles that may appear inside an association.
The `instanceOf` child is used to specify the type matched by the `role` element, and all
association roles that match the `instanceOf` element will be matched against the `role`
element.

The `role` element may have `min` and `max` attributes, which specify the number of matches that are
allowed within each association.

The `player` child element is used to specify what classes of topics are allowed to play this role
in an association. Topics are matched against the `player` element in the same way they are matched
against the `instanceOf` element. (See the section on that element for details.) To specify that
instances of more than one class may play a role, use more than one `player`
element.

| Name | Type | Required | Default | Definition | 
|--|--|--|--|--|
| max | CDATA | #DEFAULT | Inf | The maximum number of matches allowed on this constraint for each instance of the class. | 
| min | CDATA | #DEFAULT | 0 | The minimum number of matches allowed on this constraint for each instance of the class. | 

### Low-level elements ###

This section defines the semantics of the low-level elements of the Ontopia Schema Language.

#### The ruleref element ####

|--|--|
| Summary: | A reference to a ruleset that is to be included in a topic class. | 
| Content model: | EMPTY | 
| Parents: | topic, ruleset | 

The `ruleref` element is used to refer to `ruleset` elements in the same schema. The element has a
`rule` attribute, which contains the ID of the `ruleset` element being referenced. All the
constraints contained in that `ruleset` element are then included in the set of constraints defined
by the `ruleset` or `topic` element that contains the `ruleref` element.

It is an error for the `ruleref` element to refer to an ID that does not exist in the schema.

| Name | Type | Required | Default | Definition | 
|--|--|--|--|--|
| rule | IDREF | #REQUIRED |   | The ID of the ruleset being referred to. | 

#### The superclass element ####

|--|--|
| Summary: | Refers to the superclass of the topic class being defined. | 
| Content model: | EMPTY | 
| Parents: | topic | 

The `superclass` element is used to declare that a class must be a subclass of another. The
validator will use this information to check that the topic representing the class actually is a
subclass of the referred-to superclass. The superclass is identified by referring to the topic class
definition that corresponds to the superclass topic.

| Name | Type | Required | Default | Definition | 
|--|--|--|--|--|
| ref | IDREF | #REQUIRED |   | The ID of the superclass topic class definition. | 

#### The player element ####

|--|--|
| Summary: | Controls the allowed types of the topic that may play a particular role in an association. | 
| Content model: | (topicRef &#124; subjectIndicatorRef &#124; internalTopicRef &#124; any)?  | 
| Parents: | role | 

Used inside `role` elements to specify the allowed classes of topics that may play a given role in
an association. Its semantics are otherwise the same as those for `instanceOf`.

| Name | Type | Required | Default | Definition | 
|--|--|--|--|--|
| subclasses | yes, no | #DEFAULT | yes | Whether subclasses of the specified types are accepted or not. | 

#### The instanceOf element ####

|--|--|
| Summary: | Specifies the type of a topic map object. | 
| Content model: | (topicRef &#124; subjectIndicatorRef &#124; internalTopicRef &#124; any)?  | 
| Parents: | topic, role, occurrence, in, scope, playing, association | 

The `instanceOf` element is used to match topic map objects by their type. The contents of the
element specify what topic is allowed as the type of the objects to be matched. If the element is
empty it means that the object must have no type in order to match.

The `subclasses` attribute is used to control whether instances of subclasses should match or not.
If it is set to `yes` they do match, and if set to `no` they do not. The default is that subclasses
do match.

The `topicRef`, `subjectIndicatorRef`, `internalTopicRef`, and `any` child elements are used to
specify the type of the topic map object.

| Name | Type | Required | Default | Definition | 
|--|--|--|--|--|
| subclasses | yes, no | #DEFAULT | yes | Whether instances of subclasses are accepted. | 

#### The scope element ####

|--|--|
| Summary: | Controls the allowed scope of a topic characteristic.  | 
| Content model: | (topicRef &#124; subjectIndicatorRef &#124; internalTopicRef &#124; any &#124; instanceOf)*  | 
| Parents: | baseName, variant, occurrence, association | 

The `scope` element is used to constrain (or match) the scopes of topic map objects. The contents of
the element specify what topics are allowed as themes in the scope. If the element is empty it means
that the object must be in the unconstrained scope to match.

The `match` attribute is used to specify how the scope of the topic map object is matched against
that specified in this element. If set to `exact` every topic mentioned in the `scope` element must
be present, and no topics that are not specified are allowed. If set to `superset` some or all of
the topics specified may be left out, but no extra topics are allowed. If set to `subset` no topics
may be left out, but the ones specified must at least be present.

The `topicRef`, `subjectIndicatorRef`, `internalTopicRef`, and `any` child elements are used to
specify the themes that may appear in the scope. The `instanceOf` element is also allowed as a child
of the `scope` element, and if present it states that all topics that match the `instanceOf` element
are allowed as themes in the scope.

| Name | Type | Required | Default | Definition | 
|--|--|--|--|--|
| match | subset, superset, exact | #DEFAULT | exact | Controls how the specified scope is matched against the scope of actual topic characteristics. | 

#### The topicRef element ####

|--|--|
| Summary: | Identifies a topic by its source locator. | 
| Content model: | EMPTY | 
| Parents: | instanceOf, player, otherClass, scope | 

The `topicRef` element is used to match topics by their source locators. The `href` attribute
contains a URI (resolved relative to that of the current entity), and topics which have that URI as
their source locator will match, while no other topics will.

| Name | Type | Required | Default | Definition | 
|--|--|--|--|--|
| href | CDATA | #REQUIRED |   | The URI that is the source locator of the topic. | 

#### The subjectIndicatorRef element ####

|--|--|
| Summary: | Identifies a topic by its subject identifier. | 
| Content model: | EMPTY | 
| Parents: | instanceOf, player, otherClass, scope | 

The `subjectIndicatorRef` element is used to match topics by their subject identifiers. The `href`
attribute contains a URI (resolved relative to that of the current entity), and topics which have
that URI as their subject identifier will match, while no other topics will.

| Name | Type | Required | Default | Definition | 
|--|--|--|--|--|
| href | CDATA | #REQUIRED |   | The URI that is the subject identifier. | 

#### The internalTopicRef element ####

|--|--|
| Summary: | Identifies a topic by its source locator. | 
| Content model: | EMPTY | 
| Parents: | instanceOf, player, otherClass, scope | 

The `internalTopicRef` element is used to match topics by their source locators. The `href`
attribute contains a URI (resolved relative to the base address of the topic map), and topics which
have that URI as their source locator will match, while no other topics will.

| Name | Type | Required | Default | Definition | 
|--|--|--|--|--|
| href | CDATA | #REQUIRED |   | The URI that is the source locator of the topic. | 

#### The any element ####

|--|--|
| Summary: | Any topic. | 
| Content model: | EMPTY | 
| Parents: | instanceOf, player, otherClass, scope | 

The `any` element matches any topic and is used as a wildcard in matching.

The `any` element has no attributes.

#### The player element ####

|--|--|
| Summary: | Controls the allowed types of the topic that may play a particular role in an association. | 
| Content model: | (topicRef &#124; subjectIndicatorRef &#124; internalTopicRef &#124; any)?  | 
| Parents: | role | 

Used inside `role` elements to specify the allowed classes of topics that may play a given role in
an association. Its semantics are otherwise the same as those for `instanceOf`.

| Name | Type | Required | Default | Definition | 
|--|--|--|--|--|
| subclasses | yes, no | #DEFAULT | yes | Whether subclasses of the specified types are accepted or not. | 


