/**
Provides interfaces for the engine's index system; the API for Ontopia
topic map indexes. This package enables quick retrieval of selected topic
map content; using these interfaces, these facilities can be used without
any dependencies on the repository-specific backend(s) used for managing
the topic map data.
<br>NB that these indexing facilities are likely to be changed, and certain
to be extended, when the Topic Map Query Language standard is available,
so you are advised to design your application code so that the required
changes are easy.
<h3>
Package Specification</h3>
The interfaces in this package provide an API for topic map indexes. Topic
map indexes provide straightforward and efficient ways to look up information
in a topic map; these facilities are frequently used, and would otherwise
require a step by step traversal of the entire topic map.
<br>A typical example is looking up all instances of a given topic type,
which with the index system can be achieved with a single method call.
<p>Each topic map has an <a href="IndexManagerIF.html">IndexManagerIF</a>
instance associated with it. This index manager object can be used to retrieve
specific indexes and also to check what indexes are available, which ones
have been loaded and so on.
<p>The indexes currently available are:
<ul>
<li>
<a href="ClassInstanceIndexIF.html">ClassInstanceIndex</a>, which is used
to find information about which objects are instances of specific classes.</li>

<li>
<a href="LocatorIndexIF.html">LocatorIndex</a>, which is used to find out
which locators are used where.</li>

<li>
<a href="NameIndexIF.html">NameIndex</a>, which is used to find out which
names are used where.</li>

<li>
<a href="ScopeIndexIF.html">ScopeIndex</a>, which is used to find out where
a topic has been used in a scope.</li>
</ul>

<h3>
Related Documentation</h3>
The purpose and scope of this package is explained further in the Ontopia
Engine Developers Guide.
<h3>
Interface Summary</h3>
IndexManagerIF is implemented by objects which manage indexes on a topic
map (NB there are currently no facilities for indexing across more than
one topic map.)
<p>IndexIF is the common interface for all topicmap indexes. IndexIF is
extended by ClassInstanceIF, LocatorIndexIF, NameIndexIF, and ScopeIndexIF. These interfaces
have a similar structure; each contains methods that return collections
of topic map objects (eg associations, variant names), selected according
to some criterion.
*/

package net.ontopia.topicmaps.core.index;
