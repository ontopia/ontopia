/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */
/**
This package provides topic map utility classes, which are a toolkit for
working with the topic map model provided by the core interfaces. Note
that these are supplementary to the core interfaces, and more likely to
change in the future.
<p>Note: In the doc comments, "iff" means "if and only if".
<p>The "experimental" utilities are not (yet) fully doc-commented.
<h3>
Package Specification</h3>
Utilities for interacting with topic maps and topic map objects. The design
strategy of this package is to provide similar methods for similar facilites
wherever possible; also, to provide similar ranges of facilities for similar
objects.
<br>These utilities use the core interfaces as their only interface with
topic maps.
<h3>
Class and Interface Summaries</h3>

<h4>
AssociationWalker</h4>
The AssociationWalker class allows an application to treat a proper part
of the topic map as a transitive binary relation between topics. This is
done by computing the transitive closure of a relation characterized by
two specific roles within a specific association type; this closure is
then available as an object, which represents the resulting transitive
binary relation between topics. The paths traversed within the topic map
in order to find these topics are also retained, if required.
<h4>
AssociationWalkerListenerIF</h4>
An interface implemented by objects which "listen" to the progess of an
AssociationWalker. The listener is "told" each time a <tt>topic, association,
associated-topic </tt>structure, with the appropriate association-type
and rolespecs, is found by the walker. Event processing controlling the
termination of the walk can be implemented using this interface.
<h4>
CharacteristicUtils</h4>
Utilities for getting characteristics of topics (names. occurrences and
association roles). Some are included only for completeness - the main
value of this class is in the methods to get characteristics from given
collections (rather than single objects), and also the handling of nested
variant names.
<h4>
ConflictingSubjectHandlerIF</h4>
An interface which is used by <tt>MergeUtils</tt> (see below); this interface
is implemented by objects which can resolve subject identity conflicts
on topics.
<h4>
DeletionUtils</h4>
Utilities supporting the deletion of topicmap objects from a topic map.
<br>The utilities in this class are of two kinds: for each kind of topicmap
object, there is a method for determining whether it is "removable"; and
a method for removing it.&nbsp; These are not interdependent; if you ask
for an object to be removed after finding out that it is not "removeable",
it will still be removed.
<br>Removing a topic removes all uses of that topic in the topic map. See
the Developers Guide for further discussion of topic deletion.
<h4>
Scope Utilities</h4>
The Scope utilites are in classes ScopeUtils, ScopedIFComparator, InIdenticalScopeDecider,
InNarrowScopeDecider, InBroadScopeDecider, InRelatedScopeDecider.
<br>The overall purpose of these utilites is to provide facilites for handling
scopes on topic charactersitics. Details of each utility function are provided
in the doc comments. Note that the empty scope is the most general scope
("unconstrained") - rather like "ace high" in a pack of playing cards.
<br>The decider classes all implement DeciderIF&nbsp; in&nbsp; {@link net.ontopia.utils};
they provide simple interfaces to scope comparison logic on individual
scoped objects, implemented in ScopeUtils. ScopeUtils makes use of methods
provided by ScopedIF in {@link net.ontopia.topicmaps.core}, and also provides
methods for ranking and filtering collections of scoped objects by scope.
<br>ScopedIFComparator is used by ScopeUtils; it makes use of methods in
ScopedIF, to implement a comparator which can be used to sort scoped objects
by their applicability in a given scope.
<h4>
KeyGenerator</h4>
Makes a key for either an occurrence or&nbsp; base name - these keys use
internal object IDs in their construction. These keys are used in MergeUtils
to detect duplicate characteristics on merged topics.
<h4>
MergeUtils</h4>
Utilites for merging topic maps. This area is still under development;
these utilities are prototypes.
<h4>
Grabber utilities</h4>
All these utilities clothe logic to access data, as GrabberIF objects,
see {@link net.ontopia.utils}.
<h5>
BaseNameGrabber</h5>
Selects and returns one basename for a given topic, selected according
to a comparator which is determined when an instance of this class is created.
This comparator may be given (as a parameter which is a Comparator), or
alternatively, a scope may be given (i.e. a collection of topics serving
as a scope in the topicmap) - in which case, an instanceof ScopedIFComparator
(see below) is created and used.
<h5>
DisplayNameGrabber</h5>
Selects &amp; returns a name to display for a topic. This is a display
name, if available, otherwise a base name. See doc comments for details.
<h5>
VariantNameGrabber</h5>
Selects and returns a variant name for a base name. The comparator used
is determined as in BaseNameGrabber above.
<h5>
ObjectIdGrabber</h5>
Returns the internal object id of an arbitrary topicmap object. Do not
use this to get round the core interface restrictions on how you access
topicmap objects.
<h5>
RoleGrabber</h5>
Returns all the association roles of a given association, as a collection.
<h5>
RolePlayerGrabber</h5>
Returns the topic which is playing the role in a given association role.
<h5>
TypedIFGrabber</h5>
Returns the topic which is the type of a given (singly) typed topic map
object.
<h4>
Stringifier utilities</h4>
These generate string representations of topic map objects, which are used
by various other classes.
<br>NameStringifier and ObjectIdStringifier define stringifier classes
directly; TopicStringifiers provides methods to get stringifier objects
which stringify topics according to a given scope. All these stringifiers
implement StringifierIF in {@link net.ontopia.utils}.
<h4>
PSI (Public Subject Identifiers)</h4>
This class provides a local reference for the PSIs defined in the XTM 1.0
spec. Each PSI is available as a string and as a locator.
<h4>
SameStoreFactory.java</h4>
This is a class providing a utility implementation of TopicMapStoreFactoryIF;
a sameStoreFactory always returns the same topicmap store, which is the
store given to its constructor.
<h4>
Serialization utilities</h4>
Experimental utilites using java serialization.
<h4>
Subject utilities</h4>
The subject utilities are in classes SubjectUtils and SubjectIdentityDecider.
SubjectUtils provides methods for determining whether given topic map objects
are related to given subjects.
<br>SubjectIdentityDecider provides a simple interface to logic in SubjectUtils
which determines whether an object has a given subject identity.
<h4>
Comparator utilities</h4>

<h5>
ObjectIdComparator</h5>
Compares the internally generated object ids of two topic map objects.
<h5>
Topic Comparators</h5>
Experimental utilites concerning topic comparison.
<h5>
TypedIFComparator</h5>
Compares the types of two typed topic map objects.
<h4>
TopicMapBuilder</h4>
This is the default implementation for TopicMapBuilderIF in {@link net.ontopia.topicmaps.core}.
<h4>
TypeHierarchyUtils</h4>
Utilites to collect supertypes and subtypes for topic map objects.
<h3>
Related Documentation</h3>
Ontopia Topic Map Engine Developers Guide.
*/

package net.ontopia.topicmaps.utils;
