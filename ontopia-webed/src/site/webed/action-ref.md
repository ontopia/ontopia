The Web Editor Action Library
=============================

Reference Documentation
-----------------------

<span class="version">Ontopia 5.1 2010-06-09</p>

### Introduction ###

The actions are Java classes used by the Web Editor Framework to receive information from the web
forms produced using the framework and update the topic map. Each action operates on an object, and
may take a number of positional parameters. This document provides a reference to all actions
included with the framework.

The Web Editor Framework consists of some more actions than those documented here, and we are aware
that this library of actions is unlikely to satisfy all needs. If you cannot find the action you
need or cannot make one of the actions do what you want them to do, please send an email to [the
mailing list](http://groups.google.com/group/ontopia) and let us know.

### The general actions ###

These are actions that require no particular object, and which do not fall into any of the other
categories. The package name is `net.ontopia.topicmaps.webed.impl.actions`.

#### Index ####

* [DummyAction](#p-DummyAction)
* [SetForwardAction](#p-SetForwardAction)

#### <a name="p-DummyAction">`DummyAction`</a> ####

This action does nothing, and is used as a dummy action in situations where the framework requires
an action, but none is really wanted.


#### <a name="p-SetForwardAction">`SetForwardAction`</a> ####

Used to dynamically set the page the user is to be forwarded to when form processing is complete.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| uri | no | string | The URI the user will be forwarded to. Defaults to the value of the HTTP request if not given. If neither is given nothing happens. | 
| parameter | no, repeatable | string | The name of a parameter whose value in the form is to be passed on to the forward page. This parameter can be repeated any number of times. | 

**HTTP request parameter**

Used as the fallback value for the `uri` parameter if given. Not required.

##### Execution #####

Sets the page the user will be forwarded to, overriding the configuration in `actions.xml`, and
allows parameters from the form being submitted to be passed on to the next
page.


### The topic map actions ###

These are actions at the topic map level. The package name is
`net.ontopia.topicmaps.webed.impl.actions.topicmap`.

#### Index ####

* [CreateAssoc](#p-CreateAssoc)
* [CreateTopic](#p-CreateTopic)

#### <a name="p-CreateAssoc">`CreateAssoc`</a> ####

Used to create an association, and, optionally, set its type.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| topicmap | yes | topic map | The topic map in which the association will be created. | 
| assoctype | no | topic | The association type of the new association. | 
| player | no | topic | A topic participating in the association. | 

##### Execution #####

Will always create a new association within the current topic map. If the `assoctype` parameter is
set the association will get that topic as its association type. If the `player` parameter is set an
association role with that topic as its player will be created.


#### <a name="p-CreateTopic">`CreateTopic`</a> ####

Used to create a topic, and, optionally, set its type.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| topicmap | yes | topic map | The topic map within which the topic will be created. | 
| type | no | topic | The topic type of the new topic. | 

##### Execution #####

Will always create a new topic within the current topic map. If the `type` parameter is set the
topic will get that topic as its topic type. If the `type` parameter is not provided the tag will
see if the action value can be used to find a topic to use as the type.


### The topic map object actions ###

These are actions that apply equally to all kinds of topic map objects. The package name is
`net.ontopia.topicmaps.webed.impl.actions.tmobject`.

#### Index ####

* [AddSourceLocator](#p-AddSourceLocator)
* [AddTheme](#p-AddTheme)
* [Delete](#p-Delete)
* [EvaluateLTM](#p-EvaluateLTM)
* [OSLValidate](#p-OSLValidate)
* [RemoveSourceLocator](#p-RemoveSourceLocator)
* [RemoveTheme](#p-RemoveTheme)
* [RemoveType](#p-RemoveType)
* [SetScope](#p-SetScope)
* [SetSourceLocator](#p-SetSourceLocator)
* [SetType](#p-SetType)
* [TologDelete](#p-TologDelete)

#### <a name="p-AddSourceLocator">`AddSourceLocator`</a> ####

Adds a item identifier to a topic map object.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| object | yes | topic map object | The topic map object to which the item identifier will be added. | 

**HTTP request parameter**

A URI. Added as the new item identifier. Required.

##### Execution #####

A URI locator is created from the given string value and added as the item identifier of the object
in the `object` parameter. An error will be thrown if the string value is not a syntactically valid
URI.


#### <a name="p-AddTheme">`AddTheme`</a> ####

Adds a topic to the object's scope.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| object | yes | a topic name, variant name, occurrence, or association | The object whose scope will be increased. | 

**HTTP request parameter**

A topic ID. The topic is added as the new theme. Required.

##### Execution #####

The topic selected is added to the scope of the topic map object. If the topic is already part of
the scope nothing happens.


#### <a name="p-Delete">`Delete`</a> ####

Deletes the topic map object from the topic map.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| objects | yes | topic map object collection | The object(s) to be deleted. | 
| next | no | topic map object | The object that is to be considered the current object on the forward page. Effectively sets the id request parameter. | 

**HTTP request parameter**

Topic map object ID(s). Used as fallback value for the `objects` parameter.

##### Execution #####

The topic map objects being acted on will be deleted from the topic map using the `DeletionUtils`.
If the `objects` parameter is not given the action will try to use the request parameter
value.


#### <a name="p-EvaluateLTM">`EvaluateLTM`</a> ####

Evaluates an LTM fragment to add content to the topic map.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| topicmap | yes | a topic map object | The topic map to read the LTM fragment into. | 
| fragment | yes | string | The LTM fragment. | 

**HTTP request parameter**

A string, a URI, or a topic map object ID. Used to produce the value for the `%value%` and `%topic%`
tokens.

##### Execution #####

The LTM fragment is imported into the topic map, in such a way that the IDs in the fragment can
refer to topics already in the topic map. The token `%new%` will, if present in the fragment, be
replaced with a unique topic map ID, which will create a new topic. If present, `%value%` will be
replaced by the string value of the request parameter that triggered the
action.

The action can also be used with one or more topic map objects as the values of the triggering form
control, in which case the fragment will be evaluated once for each topic map object with the
`%topic%` token will replaced by the symbolic ID of the current topic map
object.


#### <a name="p-OSLValidate">`OSLValidate`</a> ####

Validates modified objects against an OSL schema to ensure that the data has not been incorrectly
modified.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| object | yes | a set of topic maps, associations, and topics | The objects to validate. | 

**HTTP request parameter**

The HTTP request parameter, if any, will be ignored.

##### Execution #####

The objects in the `object` parameter will be validated against the OSL schema registered for the
current topic map. If errors are found, or if there is no schema, a critical error is thrown. If no
errors are found nothing happens.


#### <a name="p-RemoveSourceLocator">`RemoveSourceLocator`</a> ####

Removes an item identifier from the topic map object.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| object | yes | topic map object | The topic map object which is to lose an item identifier. | 
| locator &#124; URI string | yes | locator | The locator that is to be removed. | 

##### Execution #####

Removes the locator in the `locator` parameter from the topic map object in the `object` parameter.


#### <a name="p-RemoveTheme">`RemoveTheme`</a> ####

Removes a topic from the topic map object's scope.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| object | yes | a topic name, variant name, occurrence, or association | The object whose scope should be shrunk. | 

**HTTP request parameter**

A topic ID. Removed from the scope. Required.

##### Execution #####

The topic selected is removed from the scope of the topic map object. If the topic is not already
part of the scope nothing happens.


#### <a name="p-RemoveType">`RemoveType`</a> ####

Removes the type of the topic map object (occurrence, association, or association role).

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| object | yes | topic map object | The topic map object which will lose its type. | 

##### Execution #####

The type of the object in the `object` parameter is set to nothing, whether or not it already had a
type.


#### <a name="p-SetScope">`SetScope`</a> ####

Replaces the object's scope.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| object | yes | a topic name, variant name, occurrence, or association | The object whose scope will be set. | 
| scope | yes | a collection of topics | The scoping topics. | 

##### Execution #####

The topics given in the second parameter will be used to replace the scope object in the first
parameter.


#### <a name="p-SetSourceLocator">`SetSourceLocator`</a> ####

Sets one of the item identifier of a topic map object to a new value.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| object | yes | topic map object | The topic map object whose item identifier is to be changed. | 
| locator &#124; URI string | yes | locator | The item identifier to modify. | 

**HTTP request parameter**

A URI. Added as the new item identifier of the object. Required.

##### Execution #####

Removes the `locator` from the item identifiers of the topic map object in the `object` parameter. A
new item identifier created from the request parameter's string value is then added to the topic map
object.


#### <a name="p-SetType">`SetType`</a> ####

Sets the type of the topic map object (occurrence, association, or association role) to the chosen
topic.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| object | yes | topic map object | The object whose type is to be set. | 

**HTTP request parameter**

A topic object ID. The new type of the object. Required.

##### Execution #####

The type of the topic map object in the `object` parameter is set to the chosen topic, regardless of
whether the object already had a type.


#### <a name="p-TologDelete">`TologDelete`</a> ####

Runs a tolog query to select the objects that are going to be deleted from the topic map.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| query | yes | string(s) | The tolog query or queries. | 

**HTTP request parameter**

Topic map object ID(s). The query will be run once for each, with the `%topic%` parameter set to the
topic map object. Note that any kind of topic map object can be passed in. The parameter is called
`%topic%` only for reasons of backwards compatibility. Required.

##### Execution #####

For every topic map object in the triggering request parameter the query will be run once for each
value, and the value can then be accessed in the query using the `%topic%` parameter. If there are
no topic map object values the query will not be run at all. The objects are deleted using the
`DeletionUtils`.

This action is generally used when deleting one topic map object requires many other objects to be
deleted with it. An example of this might be deleting a composer in the Italian Opera topic map;
when doing this one might want the composer's operas to be deleted, as well as the characters and
arias in those operas. This tag would make it possible to do that by using a query selecting all the
topics to be deleted.


### The association actions ###

These are actions that apply to associations. The package name is
`net.ontopia.topicmaps.webed.impl.actions.association`.

#### Index ####

* [AssignRolePlayer](#p-AssignRolePlayer)

#### <a name="p-AssignRolePlayer">`AssignRolePlayer`</a> ####

Used to create, delete, or change an association from one topic to another.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| association | yes | association | The association itself. This parameter may be empty. | 
| assoctype | yes | topic | The type of the association. Used when a new association is created. | 
| fixedplayer | yes | topic | Topic on near side of the association. Used when a new association is created. | 
| fixedrole | yes | topic | Type of role played by the topic on near side of the association. Used when a new association is created. | 
| otherrole | yes | topic | Type of role played by topic being assigned as player on remote side of association. | 
| othertopic | no | topic | If specified, changes the behaviour of the action to use this topic as the topic at the other side of the association, in order to make the action work with checkboxes. | 

**HTTP request parameter**

A topic object ID. The topic set as the other player in the association. Required if the
`othertopic` parameter is not given.

##### Execution #####

If the `association` parameter is empty a new association will be created using the information in
the other parameters, and the chosen topic will be assigned as the player of the remote
role.

If the association already exists the chosen topic will be assigned as the player of the remote role
(this assumes that there is only one role of the remote type). If no topic is chosen the association
will be deleted.

Specifying the `othertopic` parameter changes the behaviour of the action. If given, this topic is
the topic on the other side of the association, and if the request parameter has a value (any value)
the association will be created (unless it already exists, in which case nothing happens), whereas
if it does not have a value the association will be deleted. The purpose of this is to make it
possible to use the action with a checkbox where the user can create/delete the association by
checking/unchecking the box.


### The association role actions ###

These are actions that apply to association roles. The package name is
`net.ontopia.topicmaps.webed.impl.actions.assocrole`.

#### Index ####

* [SetPlayer](#p-SetPlayer)

#### <a name="p-SetPlayer">`SetPlayer`</a> ####

Sets the player of an association role.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| role | yes | association role | The association role whose player is to be set. | 
| player | yes | topic | The topic to set as the player. | 

**HTTP request parameter**

A topic object ID. The topic to set as the player, if the `player` parameter is not given. Required
if the `player` parameter is not given.

##### Execution #####

The action will set the player of the association role in the `role` parameter to the topic in the
`player` parameter if that parameter is given. If the `player` parameter is not given the action
uses the object value of the request parameter that triggered the action.

The `role` must have a value, otherwise an error will occur.


### The topic actions ###

These are actions that apply to topics. The package name is
`net.ontopia.topicmaps.webed.impl.actions.topic`.

#### Index ####

* [AddSubjectIndicator](#p-AddSubjectIndicator)
* [AddType](#p-AddType)
* [RemoveSubjectIndicator](#p-RemoveSubjectIndicator)
* [RemoveType](#p-RemoveType)
* [SetSubjectIndicator](#p-SetSubjectIndicator)
* [SetSubjectIndicator2](#p-SetSubjectIndicator2)
* [SetSubjectLocator](#p-SetSubjectLocator)
* [SetType](#p-SetType)

#### <a name="p-AddSubjectIndicator">`AddSubjectIndicator`</a> ####

Adds a new subject identifier to a topic.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| topic | yes | topic | The topic that is to get a new subject identifier. | 

##### Execution #####

A new URI locator will be created from the string value passed to the action and added as a subject
identifier of the topic in the `topic` parameter. If some topic already has that subject identifier
or item identifier an error will be thrown.


#### <a name="p-AddType">`AddType`</a> ####

Add a new type to a topic, in addition to any types it may already have.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| topic | yes | topic | The topic to add the new type to. | 

##### Execution #####

The selected topic will be added as a new type of the topic in the `topic` parameter, regardless of
what types it may already have.


#### <a name="p-RemoveSubjectIndicator">`RemoveSubjectIndicator`</a> ####

Removes a subject identifier from a topic.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| topic | yes | topic | The topic to remove the subject identifier from. | 
| locator | yes | locator &#124; URI string | The locator that is to be removed. | 

##### Execution #####

Removes the locator in the `locator` parameter from the subject identifiers of the topic in the
`topic` parameter. If the topic does not already have the subject identifier nothing
happens.


#### <a name="p-RemoveType">`RemoveType`</a> ####

Removes a type from a topic.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| topic | yes | topic | The topic to remove the type from. | 
| type | yes | topic | The type to be removed. | 

##### Execution #####

Removes the topic in the `type` parameter from the types of the topic in the `topic` parameter. If
the topic does not already have the given type nothing happens.


#### <a name="p-SetSubjectIndicator">`SetSubjectIndicator`</a> ####

Sets a subject identifier of a topic to a specific value.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| topic | yes | topic | The topic whose subject identifier is to be modified. | 
| locator | yes | locator &#124; URI string | The subject identifier to modify. | 

##### Execution #####

The locator in the `locator` parameter is removed from the topic in the `topic` parameter. A new
locator is then constructed from the string value passed to the action and added as a subject
identifier of the `topic`, thus effectively replacing the original `locator`.

If the string value is not a syntactically valid URI the action will fail with an error. If the
string is empty this will be considered an invalid URI. The `SetSubjectIndicator2` action
([SetSubjectIndicator2](#sect-setsubjectindicator2)) will in this case delete the subject
identifier.


#### <a name="p-SetSubjectIndicator2">`SetSubjectIndicator2`</a> ####

Sets a subject identifier of a topic to a specific value.

##### Execution #####

This action is identical to the `SetSubjectIndicator` action
([SetSubjectIndicator](#sect-setsubjectindicator)), except that when given an empty string it will
delete the subject identifier.


#### <a name="p-SetSubjectLocator">`SetSubjectLocator`</a> ####

Sets the subject locator of a topic to a specific value.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| topic | yes | topic | The topic whose subject locator is to be modified. | 
| locator | yes | locator &#124; URI string | The new subject locator for the given topic. | 

##### Execution #####

The subject locator of the topic is set to the value given by the user, regardless of whether the
topic had any subject locator from before. Any previous subject locator will be overwritten. It is
an error if the string value is not a valid URI; if it is empty any already existing subject locator
will be deleted.


#### <a name="p-SetType">`SetType`</a> ####

Sets the type of a topic to a given topic.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| topic | yes | topic | The topic whose type will be set. | 

##### Execution #####

The topic in the `topic` parameter will get the selected topic as its type, replacing all previous
types.


### The topic name actions ###

These are actions that apply to topic names. The package name is
`net.ontopia.topicmaps.webed.impl.actions.basename`.

#### Index ####

* [AddBasename](#p-AddBasename)
* [SetValue](#p-SetValue)

#### <a name="p-AddBasename">`AddBasename`</a> ####

Creates a new topic name and sets its string value.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| topic | yes | topic | The topic to which the new topic name will be attached. | 
| scope | no | topic(s) | The scope of the new topic name. | 
| type | no | topic | The type of the new topic name. | 

##### Execution #####

Creates a new topic name, attaches it to the topic in the `topic` parameter, and sets its string
value to the given value. If the `type` parameter is set the topic in it will be set as the type of
the new topic name. If the `scope` parameter is set all the topics in it are added to the scope of
the new basename.


#### <a name="p-SetValue">`SetValue`</a> ####

Sets the string value of a topic name from a text field.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| topicname | yes | topic name | The topic name whose string value will be set. May be empty.  | 
| topic | no | topic | The topic containing the topic name; used if a new topic name must be created. | 
| scope | no | topic(s) | Scoping topics added to the topic name if one has to be created. | 
| type | no | topic | The type given to the new topic name, if one has to be created. | 

**HTTP request parameter**

The string value of the topic name is set to this.

##### Execution #####

If the `basename` parameter is empty a new base name attached to the topic in the `topic` parameter
will be created. If the `type` parameter is provided the topic there given is set as the topic name
type. If the `scope` parameter is given all the topics in it will be added to the scope of the topic
name.

If the `basename` parameter contains a topic name its string value will be set, unless the HTTP
request parameter is empty or null, in which case the topic name is removed.


### The occurrence actions ###

These are actions that apply to occurrences. The package name is
`net.ontopia.topicmaps.webed.impl.actions.occurrence`.

#### Index ####

* [AddExtOccurrence](#p-AddExtOccurrence)
* [AddIntOccurrence](#p-AddIntOccurrence)
* [LastModifiedAt](#p-LastModifiedAt)
* [SetLocator](#p-SetLocator)
* [SetLocator2](#p-SetLocator2)
* [SetValue](#p-SetValue)
* [SetValue2](#p-SetValue2)

#### <a name="p-AddExtOccurrence">`AddExtOccurrence`</a> ####

Adds an external occurrence to the topic with the string value provided by the user as the URI.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| topic | yes | topic | The topic the occurrence is to be attached to. | 
| type | yes | topic | The type of the new occurrence. | 
| scope | no | topic(s) | The scope of the new occurrence. | 

##### Execution #####

Adds a new external occurrence to the topic being acted on. The string value provided by the user is
set as the URI of the occurrence, and if the `type` parameter is provided the topic there given is
set as the occurrence type. If the string given by the user is not a valid URI the action will fail
with an error message. If the `scope` parameter is set, every topic in it is added to the scope of
the new occurrence.


#### <a name="p-AddIntOccurrence">`AddIntOccurrence`</a> ####

Adds an internal occurrence to the topic with the string value provided by the user.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| topic | yes | topic | The topic the occurrence is to be attached to. | 
| type | yes | topic | The type of the new occurrence. | 
| scope | no | topic(s) | The scope of the new occurrence. | 

##### Execution #####

Adds a new internal occurrence to the topic in the `topic` parameter. The string value provided by
the user is set as the occurrence value, and if the `type` parameter is provided the topic there
given is set as the occurrence type. If the `scope` parameter is set, all the topics there are added
to the scope of the new occurrence.


#### <a name="p-LastModifiedAt">`LastModifiedAt`</a> ####

Timestamps the topic passed to it with an occurrence containing the time and date of the topic's
last modification.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| topic | yes | topic | The topic to be timestamped. | 

##### Execution #####

After execution the topic will have an occurrence of type `last-modified-at` that contains a time
stamp in the format "2003-11-05 1800" set to the time when the action was executed. If the topic
didn't have this occurrence already it will be created, and if the `last-modified-at` topic did not
exist it will also be created. The `last-modified-at` topic is identified by the subject identifier
[http://psi.ontopia.net/xtm/occurrence-type/last-modified-at](http://psi.ontopia.net/xtm/occurrence-type/last-modified-at).


#### <a name="p-SetLocator">`SetLocator`</a> ####

Sets the locator of an external occurrence to the given string value.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| occurrence | yes | occurrence | The occurrence being modified; may be empty. | 
| topic | no | topic | The topic the occurrence is to be attached to. | 
| type | no | topic | The type given to the new occurrence, if one has to be created.  | 

##### Execution #####

If the `occurrence` parameter is not empty the locator of that occurrence is set to the string
provided by the user.

If the `occurrence` parameter is empty a new occurrence attached to the topic in the `topic`
parameter is created and its locator set to the value provided by the user. If the `type` parameter
is set the topic in it will be set as the type of the new occurrence.

In either case, if the string provided by the user is not a syntactically valid URI the action will
fail with an error message. This includes the case where the string is empty. The `SetLocator2`
action ([SetLocator2](#sect-setlocator2)) behaves identically to this action, except that when the
string is empty it will delete the occurrence from the parent topic.


#### <a name="p-SetLocator2">`SetLocator2`</a> ####

Sets the locator of an external occurrence to the given string value.

##### Execution #####

This action is identical to `SetLocator` action ([SetLocator](#sect-setlocator)), except that when
given an empty string it will delete the occurrence.


#### <a name="p-SetValue">`SetValue`</a> ####

Sets the string value of an internal occurrence to the given string value.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| occurrence | yes | occurrence | The occurrence being modified; may be empty. | 
| topic | no | topic | The topic the occurrence is to be attached to. | 
| type | no | topic | The type given to the new occurrence, if one has to be created.  | 

##### Execution #####

If the `occurrence` parameter is not empty the string value of that occurrence is set to the string
provided by the user.

If the `occurrence` parameter is empty a new occurrence attached to the topic in the `topic`
parameter is created and its string value set to the value provided by the user. If the `type`
parameter is set the topic in it will be set as the type of the new occurrence.

Note that if the string value given by the user is empty the string value of the occurrence will be
set to the empty string. The SetValue2 action ([SetValue2](#sect-setvalue2)) would in this case
delete the occurrence.


#### <a name="p-SetValue2">`SetValue2`</a> ####

Sets the string value of an internal occurrence to the given string value.

##### Execution #####

This action is identical to `SetValue` action ([SetValue](#sect-setvalue)), except that when given
an empty string it will delete the occurrence.


### The variant actions ###

These are actions that apply to variant names. The package name is
`net.ontopia.topicmaps.webed.impl.actions.variant`.

#### Index ####

* [AddExtVariant](#p-AddExtVariant)
* [AddIntVariant](#p-AddIntVariant)
* [SetLocator](#p-SetLocator)
* [SetValue](#p-SetValue)

#### <a name="p-AddExtVariant">`AddExtVariant`</a> ####

Adds an external variant to the topic name with the string value provided by the user as the URI.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| basename | yes | topic name | The topic name the variant is to be attached to. | 
| scope | no | topic(s) | The scope of the new variant. | 

##### Execution #####

Adds a new external variant to the topic name in the `base name` parameter. The string value
provided by the user is set as the URI of the variant, and if the `scope` parameter is set, every
topic in it is added to the scope of the new variant. If the string given by the user is not a valid
URI the action will fail with an error message.


#### <a name="p-AddIntVariant">`AddIntVariant`</a> ####

Adds an internal variant to the topic name with the string value provided by the user.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| basename | yes | topic name | The topic name the variant is to be attached to. | 
| scope | no | topic(s) | The scope of the new variant. | 

##### Execution #####

Adds a new internal variant to the topic name in the `topic name` parameter. The string value
provided by the user is set as the variant value, and if the `scope` parameter is set, all the
topics there are added to the scope of the new variant.


#### <a name="p-SetLocator">`SetLocator`</a> ####

Sets the locator of an external variant to the given string value.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| variant | yes | variant | The variant being modified; may be empty. | 
| topicname | no | topic name | The topic name the variant is to be attached to. | 

##### Execution #####

If the `variant` parameter is not empty the locator of that variant is set to the string provided by
the user.

If the `variant` parameter is empty a new variant attached to the topic name in the `basename`
parameter is created and its locator set to the value provided by the user.

In either case, if the string provided by the user is not a syntactically valid URI the action will
fail with an error message.


#### <a name="p-SetValue">`SetValue`</a> ####

Sets the string value of an internal variant to the given string value.

##### Parameters #####

| Name | Required | Value | Description | 
|--|--|--|
| variant | yes | variant | The variant being modified; may be empty. | 
| basename | no | topic name | The topic name the variant is to be attached to if one has to be created. | 

##### Execution #####

If the `variant` parameter is not empty the string value of that variant is set to the string
provided by the user.

If the `variant` parameter is empty a new variant attached to the topic name in the `basename`
parameter is created and its string value set to the value provided by the
user.



