Omnigator: The Topic Maps Browser
=================================

User’s Guide
------------

<p class="introduction">
This document is the user's guide to the Ontopia Omnigator, the omnivorous topic map navigator built
using Ontopia. It explains how to navigate topic maps using the Omnigator, and also provides some
guidance on how to create and load your own topic maps. It assumes some basic knowledge about topic
maps. We welcome any suggestions you might have on ways of improving this User Guide.
</p>

<span class="version">Ontopia 5.1 2010-06-09</p>

### Introduction ###

The Omnigator is an application that lets you load and browse any topic map, including your own,
using a standard web browser. The name is a contraction of "omnivorous navigator", and was chosen to
underline the application's principal design goal, which is to be able to make sense of *any*
conforming topic map. The Omnigator is intended as a teaching aid, to help you understand topic map
concepts, and as an aid in developing your own topic maps.

#### A word about the user interface ####

Before going any further it needs to be made clear that the Omnigator’s user interface is not to be
recommended for an end-user application. End-users shouldn’t need to know that the application they
are using is driven by a topic map. They don’t need to understand concepts like “topic”, “topic
type”, “association”, and “scope”, and they shouldn’t even be exposed to such terms. The user should
simply experience a user interface that for once makes it possible to really find the information
they are looking for, quickly, easily, and intuitively.

So why have we chosen not to hide the underlying machinery in the user interface of the Omnigator?
Precisely because it is intended as a teaching aid targeted at authors, knowledge workers,
documentalists, and developers, who want to understand more about Topic Maps and perhaps create (and
debug) their own topic maps. So don’t be surprised by the “jargon” terms you will come across: They
are there for a purpose. Think instead about how the real-world applications you will build can hide
the technicalities while at the same time harnessing the power of Topic Maps to provide an even more
exhilarating experience for your end users! The [Ontopia Navigator
Framework](http://www.ontopia.net/solutions/navigator.html) (part of Ontopia) is the toolkit you
need for building that kind of application. The Omnigator is just a generic browser implemented
using the Navigator Framework. (For examples of other, jargon-free interfaces see the
[OperaMap](http://www.ontopia.net/operamap/),[Scripts and Languages](http://www.ontopia.net/i18n/),
and [Free XML Tools](http://www.garshol.priv.no/download/xmltools/)
applica­tions.)

#### About this user guide ####

This guide starts with a brief tour of one of the topic maps included in the Omnigator distribution,
explaining the various aspects of the user interface and the information it provides. It that
assumes you have some basic knowledge about topic maps. If you don’t, try reading one of the simple
introductions, such as [The TAO of Topic Maps](http://www.ontopia.net/topicmaps/materials/tao.html),
available from the Ontopia web site, or the chapter "Topic Maps: Knowledge navigation aids" in the
[XML Handbook](http://www.xmlbooks.com/xhb.htm). If you are technically minded you might also want
to look at the [XTM Specification](http://www.topicmaps.org/xtm/1.0), the original [ISO 13250
standard](http://www.y12.doe.gov/sgml/sc34/document/0129.pdf), or the new version of ISO 13250,
especially the [Data Model](http://www.isotopicmaps.org/sam/sam-model/) and the [XTM 2.0
syntax](http://www.isotopicmaps.org/sam/sam-xtm/).

Following the Guided Tour we discuss some of the more sophisticated aspects of the Omnigator.
Finally, for those that have never created their own topic map, we offer a short tutorial to prove
just how easy it is to get up and running with your very first topic map in the
Omnigator!

If you want to know how the Omnigator works, a brief description follows. If you’re not interested,
go straight to the next section.

#### How the Omnigator works ####

The Omnigator uses a simple client-server architecture based on a standard http protocol.

![The Omnigator Architecture](graphics/omnigator.png "The Omnigator Architecture")

On the server side there is a J2EE web application built using Ontopia, that runs in the Tomcat web
server. This application reads (and writes) topic maps and generates HTML pages on the fly. On the
client, a standard web browser receives these HTML pages and displays a view of some portion of the
topic map. This view is rich in links, built from the data structures that constitute the topic map.
Each time the user clicks on a link, a request is sent to the server application, resulting in a new
set of information extracted from the topic map.

If you have a standard installation of the free Omnigator download, the Tomcat server will be
running on the same machine as the client. Alternatively, your local technical support team may have
installed the server on another machine. If you are using our online demo, the server is on the
Ontopia web site. But whatever the configuration, the view from the client side will be pretty much
the same.

### The Guided Tour ###

This section assumes that you have a correctly configured installation of the Omnigator with the
server up and running, or that you are using the online demo. (For installation and start-up
information, see the installation guide that accompanies your distribution.)

#### The Welcome Page ####

To begin this demonstration start your web browser and point it at one of the following URLs:

Local installation
:   [http://localhost:8080/omnigator/](http://localhost:8080/omnigator/)

Online demo
:   [http://www.ontopia.net/omnigator/](http://www.ontopia.net/omnigator/)

Custom installation
:   *Contact your local technical support team for details.*

You should now see the *Welcome Page*, saying “Welcome to the Omnigator”. If you have difficulty
getting to this starting point, then please refer to the install documentation, contact your local
technical support team, or contact the Ontopia support team at
[support@ontopia.net](mailto:support@ontopia.net).

![The Welcome Page](graphics/WelcomePage.png "The Welcome Page")

On the Welcome Page there is a list of topic maps that are available to the application. (You will
find out how to add one of your own in [The Manage Page](#the-manage-page) of this User Guide). The
Welcome Page also contains a number of useful links to provide you with short cuts to topic map
information and examples.

This demonstration uses Steve Pepper’s *Italian Opera Topic Map*, a fairly small but semantically
rich topic map that has been used in many talks and tutorials in the last few
years.

You can return to the Welcome Page from anywhere in the application by clicking on the **Open...**
button in the top right-hand corner.

#### The Index Page ####

To go to the `opera.ltm` topic map, click on it in the list of topic maps, which is on the left hand
side of the Welcome Page. A new page opens, shown in the figure below. We call this page the *Index
Page*. At the top you will see a row of buttons. The **Welcome** button takes you back to the
Welcome Page. The other buttons are generated by *plug-ins*, which are discussed in [The
Plug-ins](#the-plug-ins).

![The Index Page for the Italian Opera Topic Map](graphics/TopicMapPage.png "The Index Page for the Italian Opera Topic Map")

The purpose of the Index Page is to provide you with various overviews of the topic map as a whole.
The views can be chosen from the list of links on the left, as follows:

Ontology
:   The ontology view shows the "types", or “kinds of things” that exist in the topic map. It does so by
    providing alphabetical lists of topic types, association types, association role types, and
    occurrence types. All of these types are also topics; clicking on any one of them causes it to
    become the current topic, but more about that in a moment. Note that the Omnigator’s notion of what
    constitutes a typing topic is entirely empirical: For example, if (and only if) a topic has
    instances that are themselves topics, then it is regarded as a topic type, regardless of what the
    author’s intention might have been.

Master Index
:   The master index view counts and lists all the topics in the topic map, in alphabetical order, under
    the heading **Index of Topics**.

Index of Individuals
:   This view lists all topics *except* typing topics.

Index of Themes
:   The themes view lists all topics that are used as themes in the topic map. (Themes are topics that
    are used to defined scope and are therefore sometimes called scoping topics.) The themes are grouped
    according to the kinds of topic characteristics (names, variant names, occurrences, and
    associations) they are used to scope.

Unnamed Topics
:   This link only appears if the topic map contains topics that have no base name. Clicking on it
    results in a list of such topics labelled either by their XML ID (if they have one; topics from
    merged topic maps do not) or by their internal object ID, and followed by their type (if any). This
    list is useful for debugging since the absence of a base name often indicates an error in the topic
    map.

Hierarchies
:   Under this heading a list of hierarchical association types is displayed (if any are present in the
    topic map). These are association types that conform to Techquila’s [hierarchical relationship
    design pattern](http://www.techquila.com/psi/hierarchy/#hierarchical-relation-type). Clicking on one
    of these links results in a tree view of that particular hierarchy. (One reason for introducing this
    feature was because people were abusing the supertype-subtype relationship in order to get the
    Omnigator to display other kinds of hierarchical relationships using indentation. With the new
    generalized tree widget, this should no longer be necessary.)

In the screen shot above, the name of the topic map is displayed at the top of the page. If the
topic map does not have a name, the Omnigator simply displays the words “Index Page”. (In order to
give a topic map a name it must be *reified*, as described in [Step 8:
Reification](#step-8:-reification).)

If metadata has been assigned to the topic map (using the mechanism described in [Step 9: Adding
metadata to your topic map](#step-9:-adding-metadata-to-your-topic-map)), it will be displayed in a
box headed **Topic Map Metadata** on the left-hand side of the screen.

To open another topic map, use the **Open...** button in the top right-hand corner of the screen.

If you use an application other than Ontopia’s new ontology-driven editor, Ontopoly, to modify a
topic map that is already loaded, you will need to reload it in order to see the effects of your
changes. This is easily done via the **Reload** button in the top right-hand corner. After reloading
the topic map, the Omnigator attempts to return you to the topic page you were on; if this is not
possible (because the topic map no longer contains a topic with the same XML ID), you will be
returned to the topic map’s Index Page. (NOTE: If the topic map is full-text indexed (see [The
Full-text Search Plug-in](#the-full-text-search-plug-in)), it will be automatically reindexed when
you reload it. If the topic map is large this might take some time.)

#### The Topic Page (of a topic type) ####

Having investigated the index views and hopefully gained some idea of what the topic map itself is
all about, go to the ontology view and click on the topic 'Composer'. A new page opens, shown in the
diagram below. We call this page the *Topic Page* and 'Composer' is the *current
topic*.

Note that a new button appears on the button bar, to the right of the **Welcome** button, containing
either the name of the topic map (in its short form, if it has one, which it does in our example),
or the text **Index Page**. Clicking on this button will always bring you back to the topic map’s
Index Page. (To learn how to specify a short name for a topic map, see [Step 8:
Reification](#step-8:-reification).)

![The Topic Page for topic type 'Composer'](graphics/TopicPage-composer.png "The Topic Page for topic type 'Composer'")

The Topic Page presents the information held in the topic map about the current topic, in this case
the information about the topic 'Composer'.

The *kind* of information presented will depend on the nature of the current topic. The main title
will always be the most appropriate name of the topic, based on the type(s) of the topic and the
*current context* (described in [The Filter plug-in](#the-filter-plug-in)). In addition, all the
base names (and any variant names) of this topic will be shown, along with the themes that define
their scopes (unless context-based filtering is in effect). In the illustration we see that the
topic 'Composer' has seven names: one in the unconstrained scope, and six others in language scopes
corresponding to French, Italian, Czech, Finnish, Hungarian, Dutch, German, and
Norwegian.

Names that have no type are grouped together under the heading **Untyped Names**, as in the example
above. Typed names, on the other hand, are grouped separately and the name type is used as a
heading. In the example in the next section, the composer Puccini has two untyped names (one of them
scoped), and also a name of type **Normal form**, “Giacomo Puccini”. (NOTE: Typed names were
introduced with the revised 2006 version of the Topic Maps standard. Topic maps written using XTM
1.0 will only have untyped names.)

Under **Associations** we find the topics that 'Composer' is associated with. The topic 'Composer'
doesn’t participate in any associations, apart from subclassing associations. The association
section is therefore empty, except for the association to its supertype ('Musician'). For other
kinds of topics we would typically see more associations (as the screenshots to follow will
show).

Because 'Composer' is a *topic type* (i.e., a topic that defines a class of topics), we also get a
list of topics of this type, in other words, an Index of Composers. If the current topic is an
*association role type* (i.e., a topic that defines a class of roles played in associations), the
Omnigator displays a similar list with the heading **Players of this Role** (see the topic 'Person'
for an example of this).

Sometimes the same topic is used for both purposes, i.e., to type other topics and to type
association roles. The topic 'Composer' is a case in point: It also defines one of the role types in
'Composed by' associations. In cases like this, the Omnigator will display *two* lists of topics
(one entitled **Topics of this Type**, the other entitled **Players of this Role**) *unless* the two
lists are identical (as they are in the case of 'Composer'), in which case only the first list is
displayed. (**TIP:** When the Omnigator displays two such lists and their contents are very similar,
it often indicates an inconsistency in your topic map.)

Finally, under **Subject Identifiers** are listed URIs for each *subject identifier* that this topic
has been given. In this case there is just one. Sometimes there are none and there may also be more
than one. Subject identifiers are a mechanism for establishing which subject a topic represents;
they are the basis for performing topic map merging without the problems normally caused by homonyms
and synonyms. If the author of the topic map has followed [OASIS'
recommendations](http://www.ontopia.net/tmp/pubsubj-gentle-intro.htm) for Published Subjects,
clicking on a subject identifier should lead to a resource called a *subject indicator* that
provides a human-interpretable “indication” of the identity of the subject represented by the
current topic.

If the current topic represents an information resource and has a *subject locator*, this will be
shown in addition to any subject identifiers.

#### The Topic Page (of an individual topic) ####

All the themes, types, topic instances, and role players on a Topic Page are also topics. You can
click on any one of them, and it then becomes the current topic. Try this by clicking on the topic
'Puccini'. The result is a new Topic Page, shown below, with Puccini as the current
topic.

![The Topic Page for topic 'Puccini'](graphics/TopicPage-Puccini.png "The Topic Page for topic 'Puccini'")

'Puccini' is a different kind of topic than 'Composer'. Whereas 'Composer' is a topic type (and
association role type), 'Puccini' is an individual, and therefore the kind of information that is
presented is slightly different. We still have the principal name and a complete list of names. We
also have a section for **Associations**, but this time the contents are rather more extensive. We
also see several new headings, explained below.

*  **Types** simply lists the types (or classes) of which 'Puccini' is an instance, in this case,
   Composer.
*  **Internal Occurrences** displays occurrences of the topic 'Puccini' that are internal to the topic
   map, in this case, the composer’s dates of birth and death.
*  **External Occurrences** displays occurrences that are resources external to the topic map.

Occurrences are organized by type, the name of the type being displayed in bold as a heading. Note
that occurrences are displayed in different ways, depending on whether the occurrences are internal
or external to the topic map. With internal resources (encoded using `resourceData` elements in XTM
syntax), the contents of the resource are shown inline. With external resources (encoded using
`resourceRef` elements in XTM syntax), only the locator of the resource is shown, unless there is a
topic that represents the resource, in which case the resource’s name is displayed instead (along
with a small icon). (To learn how to name a resource in this way, see [Step 6: Identification of
subjects](#step-6:-identification-of-subjects).) Clicking on the locator (or the icon) makes your
browser go to the resource. Clicking on the name takes you to the topic page for that resource (from
which you can access the resource itself via its subject locator).

The section headed **Associations**, lists topics that are related with the current topic through
associations, grouped according to the type of the association. It is the equivalent of “see also’s”
in a back-of-book index (except that here there is an association type that tells us something about
the *nature* of the “see also” relationship). For 'Puccini' we have associations labelled 'Born in',
'Composed', 'Died in', 'Exponent of', and 'Pupil of'. Under each of these labels are the associated
topics. We see that Puccini was born in Lucca, that he composed a number of operas, including *La
Bohème*, *Tosca*, and *Turandot*, that he died in Brussels, was an exponent of the musical style
known as verismo, and that he was a pupil of Ponchielli.

When displaying names of associated topics, the Omnigator assumes a current context defined by the
current topic. Thus the name of La Bohème is given as “La Bohème” even though its name in the
unconstrained scope (in order to distinguish it from Leoncavallo’s opera of the same name) is
actually “La Bohème (Puccini)”. The section headed **Scoped Names** at the bottom of the page lists
topics whose names are scoped by the current topic. (If any occurrences were scoped by this topic,
they would be listed in a separate section labelled **Scoped Occurrences**; similarly, if any
associations were scoped by the current topic their types would be listed in a separate section
labelled **Scoped Association Types**. See the topics 'Italian' and 'Biography' for more
examples.)

If you move the cursor over the associated topics (for example 'Lucca'), you’ll see the text “Role
type: Place” appear as a pop-up (or however your browser displays element titles). This tells you
that 'Lucca' plays the role of 'Place' in the 'Born in' association. If the association is scoped,
the names of the topics comprising the scope will also be displayed.

All of the associations in which Puccini plays a role are binary, i.e., they involve exactly two
topics (Puccini and one other). However, associations can have any arity: They may be n-ary (i.e.,
involve three or more topics) or even unary (involving just one topic). You will see how the
Omnigator displays n-ary associations shortly. Unary associations are less usual, but some topic map
authors use them to represent boolean properties, such as 'Unfinished'. Unary associations are
displayed separately from other associations, as you will see if you click on
'Turandot'.

#### Cruising the Knowledge Web ####

Click now on 'Lucca'. A new topic page opens, and Lucca is the current topic. It is a topic of type
'City', located in Italy, and the birthplace of both Puccini and Catalani. The association with
Puccini is the same as the one we just used to get to this page, it is simply labelled differently.
This illustrates an important point about topic maps: There is no directionality in associations,
just different roles played by the participants, thus giving a structure which can be navigated in
any direction required by an application. (To find out how to label your association types
differently in different contexts, see [Step 7: Adding associations](#step-7:-adding-associations)
in the tutorial.)

Now click on 'Catalani'. He becomes the current topic. We see that he too was a composer of operas,
born in Lucca (well, we already knew that: that’s how we found him), and that he died in
Milan.

Click on 'Milan'. Milan is an interesting city: Not many people were born there, but lots of people
died there! Why? Well, if you know anything about Italian opera, you know that Milan is the home of
the opera house La Scala and thus the “Mecca” of Italian opera. A composer may not have happened to
be born in Milan, but if he was any good he would tend to gravitate there during his career, and
that would increase the chance of him dying there! (This, by the way, is an interesting example of
the kind of new insights revealed by the juxtaposition of information found in a topic map. The
author of this topic map did not set out to illustrate Milan’s position as the Mecca of Italian
opera; that fact was “revealed” by the characteristics that collectively constitute the topic
'Milan'.)

Click now on 'Teatro alla Scala', and you will see a list of all the operas that were first
performed there. (See, it must have been an important place!) Choose 'Madame Butterfly'. You see the
première date, the name of its most famous aria, the play on which it was based, its principal
characters, who the composer was (our old friend, Puccini), etc.

Click now on 'Cio-cio-san' for an example of how the Omnigator displays n-ary associations. Two
ternary associations are shown under the headings 'Killed by' and 'Kills (by)'. These are actually
the same association type, but since Cio-cio-san plays different roles in them they are presented
separately and labelled differently. In fact they are actually the same association (the one
representing Cio-cio-san’s suicide, in which she plays the role of both victim and perpetrator!).
Note how the fact that one topic plays multiple roles in a single association is emphasized through
the use of the label **[self]**.

Now retrace your tracks to 'Madame Butterfly'. What you have essentially been doing is wandering
around a semantic network, or knowledge web, about Italian opera. Hopefully you are learning a lot
about that domain in the process, even without visiting any of the information resources themselves.
To delve deeper into a particular topic of interest, simply follow a link to one of the resources.
For Madame Butterfly they include two synopses, the libretto, and more.

#### The Topic Page (of an occurrence type) ####

So far we have only looked at topics that type other topics and association roles ('Composer'), and
topics that are individuals ('Puccini', 'Lucca', 'Catalani', 'Milan', 'La Scala', 'Madame
Butterfly', 'Cio-cio-san'). Now let’s look at a topic that is an occurrence type. Under **External
Occurrences** find one of the occurrences that is of type 'Synopsis' and click on the link in bold
text; 'Synopsis' becomes the current topic. The information shown for a topic that types occurrences
is of a different kind: What you see now is (on the left) a list of topics that have occurrences of
this type, and (on the right) the addresses of all those occurrences.

![The Topic Page for occurrence type 'Synopsis'](graphics/TopicPage-synopsis.png "The Topic Page for occurrence type 'Synopsis'")

To see another example of an occurrence type topic, click on 'Aida' (at the top of the list of
occurrences), and then on 'Première date' (under **Internal Occurrences**). The result is an
alphabetically ordered list of every topic that has a première date, and a chronological list of all
the corresponding dates. This time the *contents* of the occurrences are shown rather than the
addresses, because all the occurrences are internal rather than external.

![The Topic Page for occurrence type 'Première date'](graphics/TopicPage-premiere.png "The Topic Page for occurrence type 'Première date'")

#### The Topic Page (of an association type) ####

Now go back to Tosca (first performed in 1900) and from there to the character Floria Tosca (under
'Dramatis personae'). Note the associations of type 'Killed by' and 'Kills (by)'. Floria Tosca is
also involved in two such associations, one of them in two different roles (because she too kills
herself). Click on the heading 'Killed by'. The topic 'Killed by', which types these associations,
becomes the current topic. The information shown for a topic that types associations is different
yet again: This time we see all topics that play roles in associations of this type, grouped
according to the role they play. (The association type 'Killed by', as we have seen, is not binary;
it can (and usually does) have more than two roles. In this case they are 'Cause of death',
'Perpetrator', and 'Victim'. In the case of suicides, both the latter roles are played by the same
character.)

![The Topic Page for association type 'Killed by'](graphics/TopicPage-death.png "The Topic Page for association type 'Killed by'")

On this gory note, we used to end our guided tour of the basic Omnigator interface. But there is
another way to view your topic maps: Using the Vizigator.

#### The Gentle Art of Vizigation ####

Hit the Back button a couple of times and you should be back at the topic 'Tosca' (the opera, not
the character). Click on the **Vizigate** button, wait a few seconds while the applet loads, and you
should get a graphic display like the one shown below:

![Vizigating 'Tosca' (locality = 1)](graphics/Viz-tosca-1.png "Vizigating 'Tosca' (locality = 1)")

What you see is the focus topic ('Tosca') and all directly related topics, i.e., characters (e.g.
Mario, Scarpia, Floria Tosca, etc.), arias (Vissi d’arte, etc.), the composer Puccini, the setting
(Rome), the librettists (Illica and Giacosa), and the play on which the opera was based (La Tosca).
Topics are shown as labelled nodes and associations as arcs. Shapes and colours denote different
topic types and association types. Mousing over an arc displays the name of the association type
(e.g. “composed by” for the blue arc connecting Tosca and Puccini).

You can zoom in and out by using the *Zoom bar* at the top of the screen and you can move the whole
graph around, either by using the scroll bars on the right and at the bottom, or by dragging the
background. Topic nodes can also be dragged to different positions. (The Vizigator does have a mind
of its own and will want to have a say in how nodes are distributed. If you insist on deciding
exactly where a topic node should be positioned, right click on it and select
**Sticky**.)

The Vizigator chooses a name in a special scope (whose PSI is
`http://​psi.ontopia.net/​basename/​#short-name`), if there is one. Otherwise it chooses a name in
the unconstrained scope. (There should always be one of these if your topic map follows good
modelling practice.) Failing that a scoped name is chosen at random. If the name is too long, it is
abbreviated; in that case, mousing over the topic will show the full name.

Other names, types, identifiers, and occurrences can be viewed by right-clicking on the topic and
selecting **Properties**. You can navigate directly to any external occurrences displayed in this
box by first marking them (single click) and then right-clicking and selecting **Go
to...**.

Topic nodes can be added and removed from the display in a number of ways:

 1.  By increasing or decreasing the *Locality factor* using the increment/decrement number control to
    the left of the Zoom bar. This can take a couple of seconds, so be patient. You will quickly
    discover that topic maps of any size rapidly become very “busy” and you will have to zoom out to get
    a full overview, or zoom in to see details.
 2.  By clicking on a topic node that has a little red box in the top right-hand corner. This tells you
    that the topic has additional associations that are not displayed. Clicking on the topic brings
    those new associations into view.
 3.  By right clicking on a topic node and selecting either **Expand node**, **Collapse node**, or **Hide
    node**. Expanding has the same effect as clicking on the node; collapsing has the effect of hiding
    all associated topics that are not associated with some other topic; hiding speaks for
    itself.

![Zooming in on 'Tosca' at locality = 2](graphics/Viz-tosca-2.png "Zooming in on 'Tosca' at locality = 2")

Other options on the Context Menu reached by right-clicking on a topic node are: **Set as Start
Topic**, which refocuses the graph on that topic, and **Go to Topic Page**, which takes you to the
Topic Page for that topic in the normal Omnigator interface.

The Vizigate plug-in will work with any topic map, including your own. However, most of the topic
maps that are bundled with the Omnigator use predefined configurations created using the VizDesktop,
which is part of Ontopia. The VizDesktop provides an interface for defining shapes, colours and
fonts, and for designating which topic types and association types should be filtered out of the
display in order to make the visualization more manageable.

The VizDesktop also allows you to configure whether or not association role types are displayed. For
the Italian Opera Topic Map it was decided not to display the role types since these can mostly be
inferred quite easily. The default setting however, and the one you will see in your own topic map,
is for role types to be displayed along with association types.

Our introductory tour of the Omnigator is now finished. The following section describes some of the
more sophisticated aspects of the application, including the Manage Page, the Customize Page, the
Plug-ins, and the support for RDF. Thereafter follows a tutorial on creating your first topic
map.

### Advanced Omnigator Topics ###

#### RDF support in the Omnigator ####

The Omnigator allows you to load an RDF model and browse it as though it were a topic map, and to
export it in XTM syntax. You can also export a topic map as an RDF model and even merge topic maps
and RDF models. This new functionality constitutes an important step towards full interoperability
between RDF and Topic Maps and signals the start of the Omnigator’s evolution into a general
Semantic Web agent.

The theoretical foundation for this implementation of Topic Maps/RDF interoperability is to be found
in Lars Marius Garshol’s paper [Living with Topic Maps and
RDF](http://www.ontopia.net/topicmaps/materials/tmrdf.html). The basic tenet of this approach is
that generic mappings make no sense because Topic Maps and RDF operate at different levels of
semantics. In order to know how best to represent an RDF model conforming to a particular schema or
ontology as a topic map, it is necessary to specify what kind of topic map construct each individual
predicate should be mapped to. To illustrate this, consider the following example, taken from the
[RDF Primer](http://www.w3.org/TR/rdf-primer/):

**RDF example**

````xml
<?xml version="1.0"?>
<rdf:RDF
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  xmlns:contact="http://www.w3.org/2000/10/swap/pim/contact#">
  <contact:Person
    rdf:about="http://www.w3.org/People/EM/contact#me">
    <contact:fullName>Eric Miller</contact:fullName>
    <contact:mailbox rdf:resource="mailto:em@w3.org"/>
    <contact:personalTitle>Dr.</contact:personalTitle>
  </contact:Person>
</rdf:RDF>
````

This example contains three predicates (in the “contact” namespace): fullName, mailbox, and
personalTitle. (Note that Person is a type, not a predicate.) The first of these should be mapped to
a base name, the second and third to occurrences. Ontopia has defined an [RDF
vocabulary](http://psi.ontopia.net/rdf2tm/) for expressing such mappings. For the example above, the
mappings would look as follows:

**Example RDF-to-TM mapping**

````xml
<rdf:Description
  rdf:about=
    ="http://www.w3.org/2000/10/swap/pim/contact#fullName">
  <rtm:maps-to
    rdf:resource="http://psi.ontopia.net/rdf2tm/#basename"/>
</rdf:Description>

<rdf:Description
  rdf:about=
    ="http://www.w3.org/2000/10/swap/pim/contact#mailbox">
  <rtm:maps-to
    rdf:resource="http://psi.ontopia.net/rdf2tm/#occurrence"/>
</rdf:Description>

<rdf:Description
  rdf:about=
    ="http://www.w3.org/2000/10/swap/pim/contact#personalTitle">
  <rtm:maps-to
    rdf:resource="http://psi.ontopia.net/rdf2tm/#occurrence"/>
</rdf:Description>
````

Including these three statements (and an additional namespace declaration) with the original RDF
provides the Omnigator with enough information to be able to interpret the RDF as a topic map.
Adding a little more schema information and a couple of additional mappings yields an even better
result:

**Complete RDF-as-TM example**

````xml
<?xml version="1.0" encoding="iso-8859-1"?>
<rdf:RDF
   xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
   xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
   xmlns:contact="http://www.w3.org/2000/10/swap/pim/contact#"
   xmlns:rtm="http://psi.ontopia.net/rdf2tm/#"
>

<contact:Person
  rdf:about="http://www.w3.org/People/EM/contact#me">
  <contact:fullName>Eric Miller</contact:fullName>
  <contact:mailbox rdf:resource="mailto:em@w3.org"/>
  <contact:personalTitle>Dr.</contact:personalTitle>
</contact:Person>

<!--RDF schema definitions: Person, mailbox, personalTitle
    (to get labels) -->

<rdf:Description
  rdf:about=
    ="http://www.w3.org/2000/10/swap/pim/contact#Person">
  <rdfs:label>Person</rdfs:label>
</rdf:Description>

<rdf:Description
  rdf:about=
    ="http://www.w3.org/2000/10/swap/pim/contact#mailbox">
  <rdfs:label>Mailbox</rdfs:label>
</rdf:Description>

<rdf:Description
  rdf:about=
    ="http://www.w3.org/2000/10/swap/pim/contact#personalTitle">
  <rdfs:label>Personal Title</rdfs:label>
</rdf:Description>

<!-- RDF to Topic Maps mapping: type, label, fullName,
     mailbox, personalTitle -->

<rdf:Description
  rdf:about=
    ="http://www.w3.org/1999/02/22-rdf-syntax-ns#type">
  <rtm:maps-to
    rdf:resource="http://psi.ontopia.net/rdf2tm/#instance-of"/>
</rdf:Description>

<rdf:Description
  rdf:about=
    ="http://www.w3.org/2000/01/rdf-schema#label">
  <rtm:maps-to
    rdf:resource="http://psi.ontopia.net/rdf2tm/#basename"/>
</rdf:Description>

<rdf:Description
  rdf:about=
    ="http://www.w3.org/2000/10/swap/pim/contact#fullName">
  <rtm:maps-to
    rdf:resource="http://psi.ontopia.net/rdf2tm/#basename"/>
</rdf:Description>

<rdf:Description
  rdf:about=
    ="http://www.w3.org/2000/10/swap/pim/contact#mailbox">
  <rtm:maps-to
    rdf:resource="http://psi.ontopia.net/rdf2tm/#occurrence"/>
</rdf:Description>

<rdf:Description
  rdf:about=
    ="http://www.w3.org/2000/10/swap/pim/contact#personalTitle">
  <rtm:maps-to
    rdf:resource="http://psi.ontopia.net/rdf2tm/#occurrence"/>
</rdf:Description>

</rdf:RDF>
````

An alternative to including the mapping information in the RDF document is to put it in a separate
mapping file. The RDF2TM plug-in uses this approach and provides an easy interface for configuring
mappings, as described in [The RDF2TM plug-in](#the-rdf2tm-plug-in).

To learn more about the Omnigator’s RDF support, please refer to the papers and documentation
mentioned above. For a more extensive mapping example, see the file `concert.rdf`, based on Masahide
Kanzaki’s [Music Vocabulary](http://www.kanzaki.com/ns/music) and included in the Omnigator
distribution by kind permission of the author.

#### The Manage Page ####

The *Manage Page* is used to control various aspects of the Omnigator’s configuration, and to load,
reload, and drop topic maps. (Note: The Manage Page is not accessible in the online demo, and may
also have been disabled by your system administrator.) The functionality available via this page is
described in detail in the Ontopia Navigator Framework Developer Guide. Users of the Omnigator will
primarily be interested in two Manage Page functions: controlling which topic maps are loaded and
controlling which plug-ins are activated. (The configuration files mentioned in this section are
found in the `$TOMCAT_HOME/​webapps/​omnigator/​WEB-INF/​config/` directory.)

![The Manage Page](graphics/ManagePage.png "The Manage Page")

Under the heading **Registry Items** the page presents a complete list of all the topic maps known
to the system, based on the paths, extensions, and other information provided in the
`tm-sources.xml` file. (This file, and all other configuration files for the Omnigator, is described
in the Navigator Framework Configuration guide.) The default is to recognise all files with the
extensions `.xtm`, `.hytm`, `.ltm`, or `.rdf` in the `{Ontopia}/topicmaps/` directory. (LTM is the
Linear Topic Map Notation, a compact clear-text syntax for topic maps, defined by Ontopia and useful
for rapid prototyping. An example LTM file is included in the distribution of the Omnigator.
Documentation is included in the distribution and can also be found at
[http://www.ontopia.net/download/ltm.html](http://www.ontopia.net/download/ltm.html). The
Omnigator’s support for RDF is described in [RDF support in the
Omnigator](#rdf-support-in-the-omnigator).)

Some of these topic maps (those listed in the `application.xml` configuration file) are loaded
automatically. Others can be loaded manually by clicking on the **Load** button. Once loaded, the
name of the topic map document is shown in a larger font, and the **Load** button changes to
**Drop** and **Reload** buttons. Clicking on the name of a loaded topic map takes you straight to
its Index Page.

If the topic map document is not well-formed, or contains errors, it will not load. In such a case
you will need to fix the errors and then try again. Some typical errors are:

Well-formedness error
:   The error message will tell you which line the error occurred on. You may have misspelt (or omitted)
    a start-tag or end-tag.

Validity error
:   If validation of XTM files has been turned on in your `tm-sources.xml` file you will get errors if
    your XTM file is not valid according to the XTM 1.0 DTD. These will be displayed in the same way as
    the well-formedness errors.

Character encoding error
:   You have used a character that is not legal in the character encoding the XML parser thinks you are
    using. This tends to happen when using one of the ISO 8859-x character encodings without declaring
    them in the XML declaration, since the parser will then assume that you are using the Unicode
    encoding UTF-8. The error message will suggest that an XML encoding declaration may be
    missing.

Namespace error
:   If you get the error message “No topic maps in document” you have either made a mistake in the
    namespace declaration (see [Step 2: Getting the framework in
    place](#step-2:-getting-the-framework-in-place) for what it should look like), or you do not have a
    `topicMap` element in your document.

The **Refresh Sources** button is used to refresh the list of topic maps available to the system
without restarting the application. This is useful if you have just added a new topic map (perhaps
your own!) to the `{Ontopia}/topicmaps/` directory.

#### The Plug-in Page ####

The other part of the Manage Page that you might want to experiment with is the Plug-ins section.
Plug-ins are modules that can be added to Navigator applications to provide additional
functionality. (Some of those that are shipped with the Omnigator are described in [The
Plug-ins](#the-plug-ins).) Clicking the **Plug-ins** link at the top brings up the *Plug-ins Page*,
which lists the names and descriptions of all the currently installed plug-ins and allows you to
switch them on or off according to your needs. You can also control which plug-ins are to appear on
which pages in the Omnigator.

Please note that any changes you make here will not be saved, and so the Omnigator will revert to
the original settings when you restart it. To make your changes persistent you should change the
configuration files of the plug-ins. (See [The Ontopia Navigator Plug-ins: Developer’s
Guide](plugins.html) for more information.)

#### The Full-text Indexing Plug-in ####

Next to the link to the Plug-ins section at the top of the Manage page is a link to the *Full-text
indexing page*. This page allows you to create full-text indexes for your topic maps so that you can
use the full-text search plug-in (see [The Full-text Search Plug-in](#the-full-text-search-plug-in))
to search them.

![The Full-text Indexing Plug-in](graphics/fulltext-admin.png "The Full-text Indexing Plug-in")

In the screenshot above, most of the topic maps have full-text indexes. To create an index for one
that hasn’t, simply press the **Create index** button and the Omnigator will create a full-text
index for that topic map. The Omnigator will *not* index external occurrences referenced from the
topic map, although the full-text system can do that when run from the
command-line.

If the topic maps are changed the indexes will be out of date and you will get strange results when
you try searching. This is because the full-text index stores the object IDs of the objects indexed,
and when topic maps are loaded from a file the object IDs are assigned sequentially. If the topic
map file changes, so do the object IDs, with the result that the full-text index will be out of sync
with the topic map. Using the **Reindex** button you can update the index for any of the topic maps
whenever they change.

For information on how to search a topic map, see [The Full-text Search
Plug-in](#the-full-text-search-plug-in).

#### The Customize Page ####

An Ontopia Navigator application such as the Omnigator is built in three layers, called Model, View,
and Skin, respectively.

**Models** control the set of information that is extracted from the topic map and placed in each
page. The Omnigator is currently shipped with two models, called “Complete” (the default) and
“Nontopoly”. The Complete model, as its name implies, includes everything that the topic map
contains; this includes, for example, system information from Ontopoly, Ontopia’s ontology-driven
editor. The Nontopoly model hides most of this system information..

**Views** control the visual structure or layout of the HTML pages appearing in the client browser.
Two views are currently shipped: “No frames” (a two-column layout), and “Single column” (for use
with limited screen real estate, e.g. in portlets and on PDAs, in conjunction with the “Compact”
skin).

**Skins** are CSS stylesheets that control the styling of a page, i.e., improving its detailed
appearance in browsers that support CSS. The Omnigator ships with two style sheets, some tasteful,
others less so. (Feel free to try creating your own and send us the results!)

Clicking on the **Customize** button from anywhere in the Omnigator brings up the *Customize Page*:

![The Customize Page](graphics/CustomisePage.png "The Customize Page")

Experiment with changing the settings on this page to get a feel for the kind of flexibility offered
to application designers. (Note: Depending on the caching used by your browser, you may have to use
its reload command (normally `F5`) to see the effect of the changes.)

#### The Plug-ins ####

Plug-ins are a general concept that allows extra functionality to be dropped into any Navigator
application by simply adding files to a directory. One example of how this could be used is to add a
topic map visualisation application into the Omnigator. This section describes some of the plug-ins
that are shipped with the product.

How to develop your own Omnigator plug-ins is described in this document: [The Ontopia Navigator,
Plug-ins Developer's Guide](plugins.html).

##### The Edit plug-in #####

The Edit plug-in allows you to edit your topic map using Ontopia’s new ontology-driven editor,
Ontopoly. What happens when you click on the **Edit** button depends on whether the topic map
contains Ontopoly system information or not, i.e., whether or not it is an *Ontopoly topic map*. Any
topic map created in (or imported to) Ontopoly will have such information; all others will
not.

If you choose to edit an Ontopoly topic map you will either be taken to Ontopoly’s Instance Indexes
Page (if you start from an Omnigator Index Page), to the appropriate Instance Page (if you start
from the Topic Page of an instance topic), or to the appropriate Configuration Page (if you start
from the Topic Page of a typing topic).

![Editing an instance with Ontopoly](graphics/Ontopoly.png "Editing an instance with Ontopoly")

If you attempt to edit one of Ontopoly’s system topics (e.g., 'Field order'), you will be taken to
the Instance Indexes Page.

If you choose to edit an non-Ontopoly topic map you will be informed about the need to convert the
topic map and given the choice of either updating the existing topic map or creating a new one. Once
you have made your choice, Ontopoly will infer an ontology and schema from the topic map and take
you to its Ontology Index Page for topic types so that you can inspect the ontology before
proceeding to edit the topic map.

(While we are on the subject of Ontopoly, you may be interested to know that it is possible to hide
the system information when browsing an Ontopoly topic map. To find out how, see [The Customize
Page](#the-customize-page). For a complete introduction to Ontopoly, see the [Ontopoly User’s
Guide](../../../ontopoly/doc/user-guide.html).

##### The Filter plug-in #####

This plug-in allows you to customize your view of the topic map by establishing a *context* within
which the scope of topic characteristics is evaluated. You do this by simply specifying the themes
(i.e., scoping topics) you are interested in via the *Set Context Page*.

![The Set Context Page](graphics/SetContextPage.png "The Set Context Page")

Clicking on the **Filter** button brings up the Set Context Page. Here themes are grouped in two
levels: first according to whether they are used to scope names, associations, or occurrences; and
then by axis. Axes are determined automatically based on the classes to which themes belong. (Themes
that don’t belong to any class are grouped along an axis labelled
**[unspecified]**.)

Context themes are currently used for two purposes: *selecting* (i.e., choosing the most relevant
name), and *filtering* (i.e., removing unwanted associations and occurrences).

Since name context is used for selection, it usually makes sense to only specify one name theme (at
most) from each axis. Once you have set your preferences, the application will endeavour to use the
most appropriate name based on whatever themes you have specified. To test this using the *Italian
Opera Topic Map*, go to Puccini’s opera *La fanciulla del West*, click on the **Filter** button, set
your language preference (under Name Context) to English, and press **Activate**. You will be
returned to the same Topic Page, but now the name of the topic has changed to "The Girl of the
Golden West".

Association context and occurrence context is used differently. In this case, the more themes you
specify, the more associations and occurrences will be shown (unless you don’t specify any themes,
in which case all the associations and occurrences will be shown). To test this functionality,
return to 'Puccini' and take careful note of the number of **Associations** and **Occurrences**. Now
click on the **Filter** button and set your preferences to 'Biography' (for association context) and
'Italian' (for occurrence context). Click **Activate** and compare the results. You should see only
three association types ('Born in', 'Died in', and 'Pupil of', all biographical) and a shorter list
of occurrences.

Scope is a powerful mechanism but the ISO standard and the XTM specification neither specify nor
constrain the ways in which it might be used. Since the Omnigator is designed to work across a wide
range of topic maps, the implementation of selection and filtering by scope is based on certain
general principles:

 1.  A distinction is made between *principal themes* and *incidental themes*. Only principal themes are
    shown for selection on the Filter Page, otherwise the task of choosing among them would be
    difficult, to say the least. (The *Italian Opera Topic Map* uses over 70 topics as themes, but only
    half of them appear on the Set Context Page.)

    A principal theme is one that the author has deliberately employed in order to provide different 
    kinds of topic names (e.g., in different languages) for use in different contexts, or to provide 
    ways of reducing the number of associations or occurrences. An incidental theme, on the other 
    hand, is one whose sole purpose is to disambiguate topic names (i.e., to avoid the topic naming 
    constraint), or some other specific purpose that is under the control of the application rather 
    than the user.
	
 2.  The Omnigator only uses context to govern the *selection* of names (in situations in which just one
    name is required), and the *filtering* of associations and occurrences.

    When a name context is specified, the name whose scope has the most themes in common with the 
    context is selected. (If there are no names whose scope contains a context theme, a name in the 
    unconstrained scope is used, if it exists. If not, a name is chosen at random.)

    When an association or occurrence context is specified, only associations and occurrences that 
    have at least one theme in common with the context are allowed through the filter (along with 
    any that are in the unconstrained scope).	

 3.  Themes are grouped on the Set Context Page along *axes of scope*, according to the classes to which
    they belong. (The concept of axes was introduced in the paper [Towards a General Theory of
    Scope](http://www.ontopia.net/topicmaps/materials/scope.htm), available at the Ontopia web
    site.)

    In the current version of the Omnigator the only purpose of axes is to provide a less cluttered 
    user interface. However, later versions may exploit this concept to provide more powerful 
    filtering capabilities. Topic map authors are therefore advised to follow what in any case is a 
    sound design principle: Ensure that any topics used as themes have explicit types.


##### The Merge plug-in #####

This plug-in (which is not available in the online demo) allows you to merge a second topic map with
the current one, or suppress duplicates in the current topic map. Clicking on the **Merge** button
takes you to a page with a scroll box from which you can choose any of the topic maps registered
with the system (except the current topic map). After merging you can browse the resulting topic
map.

The **Also do name-based merging** checkbox allows you to control whether or not topics with the
same base names in the same scope will be merged in accordance with the topic naming constraint. If
you don’t want the system to enforce the topic naming constraint, leave this box
unchecked.

Whatever your setting for the name based merging option, topics will be merged if they have the same
subject identifier or subject locator. In addition, duplicate names, associations, and occurrences
*that arise as a result of the merge* will be removed.

![The Merge Page](graphics/MergePage.png "The Merge Page")

Duplicates in the source topic maps will be treated in accordance with the settings for performing
duplicate suppression when loading a topic map. The default is for no duplicate suppression to be
performed. (This allows the Omnigator to be used as a debugger and also speeds up loading.) The
default can be overridden by modifying the duplicateSuppression properties in the `tm-sources.xml`
file. If you have loaded a topic map with duplicate suppression switched off and want to remove any
duplicates that it might contain, simply click on the 'Suppress' button on the Merging and Duplicate
Suppression page.

**TIP:** A useful trick for testing the consistency of your topic map is to merge it with itself and
compare the statistics of the result with those of the original. Put two copies of your topic map in
the `{Ontopia}/topicmaps/` directory under different names, and **Refresh Sources** from the Manage
Page. Load one of them and run the Statistics plug-in. Open the same topic map in a new browser
window and merge it with the copy using the Merge plug-in. (Note the way that the URL of the
resulting merged topic map contains the names of both the source topic maps.) Run the Statistics
plug-in on the merged topic map and compare the results with the statistics of the first topic map.
Any discrepancies between the two could indicate inconsistencies or redundancies in your topic
map.

##### The Export plug-in #####

This plug-in creates a serialisation of the current topic map in XTM, LTM, TM/XML, or RDF syntax and
allows you to either save it directly to a disk file or load it as XML into your browser. Some of
the situations in which you might want to use it are:

*  you have loaded a topic map in one format and want to write it out in another;
*  you have merged two or more topic maps and want to persist the result; or
*  you have modified the topic map in some way (for example, via a plug-in) and want to save the
   result.

When exporting back to the same format as the source format, the exporter does not preserve white
space, comments, or the ordering of elements. However, it *does* preserve XML IDs, except when
merging has been performed (either by `mergeMap` or the Merge plug-in), or when the IDs in question
have the form `id#`, where `#` is a number.

When topic maps have been merged, the uniqueness of IDs can no longer be guaranteed. The exporter
will therefore only preserve IDs that originated in the base topic map; IDs that originated in topic
maps merged into the base topic map will be replaced by automatically generated IDs of the form
`id#`, where `#` is a decimal number. In order to prevent clashes, IDs that have the form `id#`; in
the source topic map are never preserved.

##### The RDF2TM plug-in #####

This plug-in provides an easy point-and-click interface for configuring RDF2TM mappings as described
in [RDF support in the Omnigator](#rdf-support-in-the-omnigator). Specify the desired mapping for
each property using the selection box and press Confirm to accept proposed and/or changed
mappings.

Each RDF property can be mapped to a characteristic (a basename, occurrence, or association) or to
an identifier (subject identifier, subject locator, or source locator); or else it can be
ignored.

A table of existing mappings (usually `mapping.rdff` in the Omnigator’s `topicmaps` directory) is
used to record mappings for later use.

![The RDF2TM Mapping Page](graphics/RDF2TMPage.png "The RDF2TM Mapping Page")

When the Omnigator encounters a property for which no mapping exists it makes a “best guess”
proposal based on the kinds of values exhibited by the property: Properties whose values are
literals are mapped to occurrences; those whose values are URIrefs or blank nodes are mapped to
associations. These proposed mappings can sometimes be improved as follows:

*  Some properties whose values are literals (e.g. dc:title) are more appropriately mapped to basenames
   (or scoped basenames).
*  Some properties whose values are URIs (e.g. foaf:homepage) are more appropriately mapped to
   (external) occurrences.
*  Other properties whose values are URIs (e.g. skos:subjectIndicator) might be more appropriately
   mapped to an identifier.

Properties are used to type the resulting occurrences and associations, or to scope basenames. Thus,
for example, dc:description properties mapped to occurrences will result in occurrences of type
dc:description; foaf:knows properties mapped to associations will result in associations of type
foaf:knows; and foaf:nick properties mapped to scoped basenames will result in basenames scoped by
foaf:nick.

The role types of associations are set to the predefined subjects rdf2tm:subject and rdf2tm:object
(where rdf2tm is the namespace <http://psi.ontopia.net/rdf2tm/>).

The technical details of this mapping are described in an [Ontopia Technical
Report](http://www.ontopia.net/topicmaps/materials/rdf2tm.html). Ontopia is currently leading a
[Task Force](http://www.w3.org/2001/sw/BestPractices/RDFTM/) within the Semantic Web Best Practices
and Deployment Working Group of the W3C to create guidelines for RDF/Topic Maps
Interoperability.

##### The Query plug-in #####

The Query plug-in lets you perform queries on your topic map using the *tolog* query language.
Clicking on the **Query** button brings up a page with a text entry box in which you write your
query using tolog syntax. (For most of the topic maps delivered with the Omnigator you can also
choose one or more example queries from a drop-down list.) For example, using the `opera.ltm` topic
map you might enter the following query:

**Sample tolog query**

````tolog
select $COMPOSER, count($OPERA) from
composed-by($OPERA : opera, $COMPOSER : composer)
order by $OPERA desc?
````

This query finds all operas and composers and then returns a list of composers and the number of
operas they composed, sorted in descending order (i.e., from highest to
lowest):

![Results of the sample tolog query](graphics/tologresults.png "Results of the sample tolog query")

Tolog is a structured query language for topic maps developed by Ontopia. Its role with respect to
topic maps is the same as SQL’s role with respect to relational databases: It both simplifies
application development and offers much more efficient data retrieval. Tolog has been proposed as a
candidate for Topic Maps Query Language, the standard query language being developed by the ISO
Topic Maps working group. For a more detailed description, refer to the [Query Language
Tutorial](../query/tutorial.html).

##### The Full-text Search Plug-in #####

The Full-text Search Plug-in allows you to do simple full-text searches in your topic maps. Topic
names and occurrences are searched using a pre-built full-text index. This index can be created
using the Full-text indexing Plug-in (described in [The Full-text Indexing
Plug-in](#the-full-text-indexing-plug-in).) When a full-text index has been created for a topic map,
a search box will show up in the plug-ins line (seen to the right of the **Search** link in the
screenshot in [The Index Page](#the-index-page)). If no index exists, the button will read **Not
indexed** and will link to the Fulltext Index Administration page.

Entering a search text here and pressing enter will get you to the page that displays the full-text
search results. These are a list of links to the topics you found, as shown in the screenshot
below.

![Full-text search result](graphics/fulltext-result.png "Full-text search result")

In this screenshot we have searched for “Tosca” in the *Italian Opera Topic Map*, and the plug-in is
showing us all the matches it has found. In this case the matches were all in the base names of
topics, and we have found the opera “Tosca”, the character “Floria Tosca”, the play “La Tosca”, the
aria “Va, Tosca!”, and the topic “The setting of Tosca in Rome” (a reification of the association
between Tosca and Rome). Clicking on any of these will take us to that particular topic. Note how
the “intelligence” of the topic map shows through here: Even a simple thing like a full-text search
becomes a dialog between the user and the system, where the user says “Tosca” and the system
responds “I know these Toscas, which one did you mean?”

Full-text search is very effective when you want to jump directly to a specific topic, or see if
some concept is mentioned anywhere in the topic map. Combined with the browsing interface it makes
the task of locating information much simpler.

##### The Statistics plug-in #####

This plug-in is a report generator for topic maps. It provides an overview of the map’s “vital
statistics” and a detailed breakdown of some of its structures. Surprisingly often this information
can be used to reveal inconsistencies or other problems with the topic map.

![The Statistics Page](graphics/StatisticsPage.png "The Statistics Page")

Most of the *Statistics Page* speaks for itself but it is worth pointing out some of the details.
Under the heading **Association structure summary** is a list of association types broken down
according to the roles that are played in those associations. Some association types only appear
once (e.g., 'Born in', with the roles 'Person' and 'Place'). Others may appear more than once (e.g.,
'Killed by'); when this happens it is due to different combinations of roles, which may either be
perfectly legitimate (as in this case), or the result of an error in the topic
map.

Note also that there are links from the typing topics (e.g. 'Aria') straight to the Topic Page for
that topic, and also links from the numbers in the second column to more detailed statistics on the
particular typing topic in question.

### Building Your Own Topic Map ###

This section provides a simple step by step tutorial for creating a (small) topicmap in XTM (XML
Topic Maps) syntax and loading it into your local copy of the Omnigator. You may be surprised just
how easy it is. Since you will be typing in XML data, we assume some minimal familiarity with
start-tags, end-tags, and attributes, as used in SGML, XML, and HTML, but that's about as hard as it
gets.

In following the tutorial, you can of course cheat by copying and pasting the markup examples
instead of typing everything in by hand. Our advice, though, is to do it the hard way. Nothing beats
laboriously typing in angle brackets if you really want to understand the syntax (and, by extension,
the concepts themselves). You will make mistakes – but you will also learn from
them!

There are two alternatives to typing in angle brackets. One is to develop a custom editing
environment based on the Ontopia Web Editor Framework. That way you can build a forms-based
interface which completely hides the fact that you are creating a topic map. But since the whole
point of this exercise is to gain a deeper understanding of topic maps, we suggest you save that
option for later. The other alternative, if you need to create and maintain large topic maps
manually, is to investigate the *Linear Topic Map notation (LTM)*, which is another of the input
formats supported by the Ontopia Topic Map Engine. It is vastly more compact than XTM!
(Documentation of LTM is included in all Ontopia distributions and can also be found at
[http://www.ontopia.net/download/ltm.html](http://www.ontopia.net/download/ltm.html)).

#### Step 1: Determining scope and basic ontology ####

Before you start, you need to have a rough idea of what it is that you want to represent in your
topic map. There are two parts to this: delimiting the scope of the topic map – that is, deciding
the extent of the domain it should cover; and designing the basic ontology. In Topic Maps
terminology, an ontology is a precise description of the kinds of things which are found in the
domain covered by the topic map: in other words, the set of topics that are used to define classes
of topics, associations, roles, and occurrences. (The term "ontology" is also used in other fields,
such as philosophy, where it has other meanings. You may also encounter it used to include
constraints as well as a specification of classes.) In Topic Maps it is easy to extend the ontology,
so you don't have to decide everything up front.

To make things easy we have chosen a subject domain that ought to appeal to you. The subject is
*you* and the company you work for (or may one day work for). The scope can easily be extended to
cover your colleagues, the projects in which you are involved, the products your company builds or
services it provides, etc. In the examples that follow, we will assume that your name is Jill
Hacker, and that you are a developer working for Ontopia. The basic ontology therefore consists of
the topic types 'Developer' and 'Company', the association type 'Employed by', and the roles
'Employee' and 'Employer'.

#### Step 2: Getting the framework in place ####

To start with, here is the simplest possible valid topic map in XTM syntax. It is empty, but it does
at least declare itself to be a topic map in XTM syntax with a specific character encoding. (If you
leave out the encoding attribute, UTF-8 will be assumed.)

**The simplest possible topic map**

````xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<topicMap xmlns="http://www.topicmaps.org/xtm/1.0/"
          xmlns:xlink="http://www.w3.org/1999/xlink">
</topicMap>
````

To see this topic map in the Omnigator, type it in (no cheating!) using your favourite text editor
(e.g., emacs, TextPad, Notepad, etc.), and save it under a name with the extension `.xtm` in the
appropriate directory. (By default this is `{Ontopia}/topicmaps/`.

Once the topic map file is in the correct place, start the Omnigator, go to the Manage Page, and
there you will see your XTM document among the Registry Items. (If the Omnigator was already running
you will need to click on "Refresh Sources" in order to get your topic map to appear in the list.)
Now click on "Load". Assuming no parsing errors occur, the document will be loaded and its name will
become a link.

If you get parsing errors, you will need to go back and fix the document. Look for missing quotes,
misspelt tag names, wrong use of case, etc. If you get the error message "No topic maps in
document", it means you have made a mistake in the namespace declaration, or have a misspelling in
the `topicMap` start-tag.

Once your topic map has been successfully loaded, click on its name and you will be taken to the
Index Page. Not surprisingly, you won't see very much, either in the "Ontology", the "Master Index",
or the "Themes" view, but at least you have the topic map in the Omnigator! Now is the time to start
adding topics.

#### Step 3: Creating the first topic ####

If you were going about the creation of this topic map in a methodical way, you would first create
all the typing topics and then create instances of them. However, doing things in the wrong order
can sometimes be quite instructive, so that is what we will do! Add a single topic – yourself – to
the topic map:

**A one-topic topic map**

````xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<topicMap xmlns="http://www.topicmaps.org/xtm/1.0/"
          xmlns:xlink="http://www.w3.org/1999/xlink">

<topic id="jill">
  <baseName>
    <baseNameString>Jill Hacker</baseNameString>
  </baseName>
</topic>

</topicMap>
````

Pay careful attention to the case of your tag names (it is "baseName", not "basename"). Also note
that the ID ("jill") is not significant; it can be any valid XML ID. Save the file, go to the
Omnigator, and click on the **Reload** button. Assuming no errors, the topic map will be reloaded
and you will be returned to the Index Page.

Once again there is nothing in the ontology view. Don't be alarmed: An ontology consists of topics
that represent classes or types and you don't have any yet. All you have is an individual
(yourself). However, if you now choose "Master Index" from the pull-down list you will notice a
difference. (You may have to use the browser's Reload command, normally `Ctrl-R` or `F5`, if your
browser is doing caching. In fact, you might want to switch caching off while doing these exercises:
Every time you modify your topic map you run the risk that the internal IDs will have changed, and
this can cause confusion.)

What you should see in the master index view is a single topic, Jill Hacker. If you click on it, you
are taken to the Topic Page where you see that this topic has just one characteristic: its
name.

#### Step 4: Adding a topic type ####

Now let's give this topic a type by adding an `instanceOf` subelement:

**A topic map with an (erroneously) typed topic**

````xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<topicMap xmlns="http://www.topicmaps.org/xtm/1.0/"
          xmlns:xlink="http://www.w3.org/1999/xlink">

<topic id="jill">
  <instanceOf>
    <topicRef xlink:href="#developer"/>
  </instanceOf>
  <baseName>
    <baseNameString>Jill Hacker</baseNameString>
  </baseName>
</topic>

</topicMap>
````

This example is incomplete, since it references a topic ('Developer') that does not yet exist. But
the Omnigator is quite forgiving in situations like this and simply instantiates an unnamed topic,
as you will see if you reload your topic map. In the Ontology view you will now see that topic (with
the name "[No name]" under the under the heading "Index of Topic Types". If you click on it, you are
taken to a Topic Page for this topic type and you will see yourself in the list of "Topics of this
Type". On the Index Page you will also see a link for "Unnamed Topics".

Now let's create a real 'Developer' topic, and while we're at it, topics for the company 'Ontopia'
and the topic type 'Company':

**A topic map with two correctly typed topics**

````xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<topicMap xmlns="http://www.topicmaps.org/xtm/1.0/"
          xmlns:xlink="http://www.w3.org/1999/xlink">

<topic id="jill">
  <instanceOf>
    <topicRef xlink:href="#developer"/>
  </instanceOf>
  <baseName>
    <baseNameString>Jill Hacker</baseNameString>
  </baseName>
</topic>

<topic id="developer">
  <baseName>
    <baseNameString>Developer</baseNameString>
  </baseName>
</topic>

<topic id="ontopia">
  <instanceOf>
    <topicRef xlink:href="#company"/>
  </instanceOf>
  <baseName>
    <baseNameString>Ontopia</baseNameString>
  </baseName>
</topic>

<topic id="company">
  <baseName>
    <baseNameString>Company</baseNameString>
  </baseName>
</topic>

</topicMap>
````

When you reload your topic map you will see that the ontology now consists of two topics, and that
there are four topics in the master index. There should now be no "Unnamed Topics". (If there are,
you screwed up!) So far, so good. Now for the next step.

#### Step 5: Adding occurrences ####

Adding occurrences is a simple matter, as the following examples show (look for the `occurrence`
elements):

**Topics with occurrences**

````xml
<topic id="ontopia">
  <instanceOf>
    <topicRef xlink:href="#company"/>
  </instanceOf>
  <baseName>
    <baseNameString>Ontopia</baseNameString>
  </baseName>
  <occurrence>
    <instanceOf>
      <topicRef xlink:href="#website"/>
    </instanceOf>
    <resourceRef xlink:href="http://www.ontopia.net/"/>
  </occurrence>
</topic>

<topic id="jill">
  <instanceOf>
    <topicRef xlink:href="#developer"/>
  </instanceOf>
  <baseName>
    <baseNameString>Jill Hacker</baseNameString>
  </baseName>
  <occurrence>
    <instanceOf>
      <topicRef xlink:href="#kudo"/>
    </instanceOf>
    <resourceData>Jill's a cool girl and a great hacker</resourceData>
  </occurrence>
</topic>

<topic id="website">
  <baseName>
    <baseNameString>Web site</baseNameString>
  </baseName>
</topic>

<topic id="kudo">
  <baseName>
    <baseNameString>Kudo</baseNameString>
  </baseName>
</topic>
````

The occurrence for Ontopia *points to* an external resource (using a `resourceRef` element with a
URL as the address), while the occurrence for you, 'Jill', *contains* an internal resource (in the
form of a `resourceData` element). Note also that we need to add topics for the occurrence types
'Website' and 'Kudo' in order to avoid more "[No name]" topics.

#### Step 6: Identification of subjects ####

One of the unique features of Topic Maps is its identity model, which allows arbitrary topic maps to
be merged without the problems usually occasioned by homonyms and synonyms. To take full advantage
of this feature, you are encouraged to define identifiers for your topics. This can be done in one
of two ways, both of which involve the use of URIs.

The preferred approach is to define a URI (called a "subject identifier") that resolves to a
resource (called a "subject indicator") that provides a human with a compelling *indication* of the
subject's identity. For the topic 'Ontopia', that might look like this:

**A topic with a subject identifier**

````xml
<topic id="ontopia">
  <instanceOf>
    <topicRef xlink:href="#company"/>
  </instanceOf>
  <subjectIdentity>
    <subjectIndicatorRef xlink:href="http://psi.ontopia.net/ontopia/#ontopia"/>
  </subjectIdentity>
  <baseName>
    <baseNameString>Ontopia</baseNameString>
  </baseName>
</topic>
````

Subject identifiers can be used for absolutely any kind of subject. However, if the subject that the
topic represents is an *information resource* (such as a web page), the address of the resource can
also be used as an identifier, in which case it is called a *subject locator*:

**A topic with a subject locator**

````xml
<topic id="ontopias-website">
  <instanceOf>
    <topicRef xlink:href="#website"/>
  </instanceOf>
  <subjectIdentity>
    <resourceRef xlink:href="http://www.ontopia.net/"/>
  </subjectIdentity>
  <baseName>
    <baseNameString>Ontopia's Web Site</baseNameString>
  </baseName>
</topic>
````

Note, by the way, how adding this topic changes the way in which the web site occurrence is
displayed on the page for the topic 'Ontopia': Instead of the URL, the name "Ontopia's Web Site" is
shown. This is because the Omnigator now has a name for the resource whose URL is
"http://www.ontopia.net/".

#### Step 7: Adding associations ####

Having by now become an expert on topics, occurrences, and identifiers, it is time to add an
association asserting that you work for Ontopia. (You never know your luck!) The association will
look like this:

**An association element**

````xml
<association>
  <instanceOf>
    <topicRef xlink:href="#employment"/>
  </instanceOf>
  <member>
    <roleSpec><topicRef xlink:href="#employee"/></roleSpec>
    <topicRef xlink:href="#jill"/>
  </member>
  <member>
    <roleSpec><topicRef xlink:href="#employer"/></roleSpec>
    <topicRef xlink:href="#ontopia"/>
  </member>
</association>
````

This association element contains references to three topics that we don't yet have in our topic
map: 'Employer' and 'Employee' (the association role types) and 'Employment' (the association type).
If you insert it as is, without adding these three topics, you will get more "[no name]" topics in
the Omnigator. Try it if you like and see.

The three new topics you need to add could look like this:

**Association type and association role type topics**

````xml
<topic id="employment">
  <baseName>
    <baseNameString>Employment</baseNameString>
  </baseName>
  <baseName>
    <scope><topicRef xlink:href="#employer"/></scope>
    <baseNameString>Employs</baseNameString>
  </baseName>
  <baseName>
    <scope><topicRef xlink:href="#employee"/></scope>
    <baseNameString>Employed by</baseNameString>
  </baseName>
</topic>

<topic id="employer">
  <baseName>
    <baseNameString>Employer</baseNameString>
  </baseName>
</topic>

<topic id="employee">
  <baseName>
    <baseNameString>Employee</baseNameString>
  </baseName>
</topic>
````

Look carefully at the 'Employment' topic that defines the association type. We have given it three
base names: one (a noun, "employment") in the unconstrained scope, which serves as the default name
used to characterise the relationship type, and two others (the verb forms "Employs" and "Employed
by") in the scope 'Employer' and 'Employee' respectively.

Is it necessary to do this? No. But the advantage is that it enables an application like the
Omnigator to choose the most appropriate label for the association type depending on the
context:

*  When the association type itself is the current topic, the name in the unconstrained scope
   ("employment") will be chosen.
*  When associations of this type are being viewed from the vantage point of a topic playing the role
   'Employer' (e.g., when Ontopia is the current topic), the name in the scope 'Employer' ("Employs")
   will be chosen.
*  When associations of this type are being viewed from the vantage point of a topic playing the role
   'Employee' (e.g., when the current topic is you), the name in the scope 'Employee' ("Employed by")
   will be chosen.

This is a simple but effective use of scope that helps overcome the conflict between the fact that
relationships (as expressed by associations in topic maps) have no direction, and the fact that the
serialisation syntax used to *talk about* relationships (natural language) often *does* have a
direction (for example, subject-verb-object in most Western European languages). To test the
intuitiveness of this technique, load the complete topic map into the Omnigator and go back and
forth between the topics you ('Jill') and 'Ontopia', noting how the label for the relationship
changes depending on the role played by the current topic.

#### Step 8: Reification ####

Before letting you loose on your own, there is one final, slightly tricky concept that you should
try your hand at. It's called *reification*. Don't be put off by the name; the basic concept is
really quite simple. Reification in general is about turning something into a "thing" ("re" in
Latin); it could also be called "thingification".

In Topic Maps, reification means taking a topic map construct, e.g., an association, occurrence, or
even the topic map itself, and turning it into a topic, in order to be able to make assertions about
it. How is it done in XTM? It's quite simple. There are two steps:

 1.  Give the element representing the topic map object you want to reify an ID.
 2.  Create a topic whose subject identity has a subject indicator reference to that element.

Piece of cake, right? So let's try it. Start by reifying the topic map. Modify your `topicMap`
start-tag by adding an ID, and create a topic that references the topic map element as its subject
indicator:

**Reifying the topic map**

````xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<topicMap id="jillstm"
          xmlns="http://www.topicmaps.org/xtm/1.0/"
          xmlns:xlink="http://www.w3.org/1999/xlink">

<!-- other topics and associations omitted for brevity -->

<topic id="jillstm-topic">
  <subjectIdentity>
    <subjectIndicatorRef xlink:href="#jillstm"/>
  </subjectIdentity>
  <baseName>
    <baseNameString>Jill's First Topic Map</baseNameString>
  </baseName>
</topic>

</topicMap>
````

When you load this topic map into the Omnigator, you will see that the name of the topic that
reifies the topic map ("Jill's First Topic Map") is used as the title of the Index Page, and also
replaces the **Index Page** button. If you feel this text is a bit long to use as a button, you can
give the same topic a second name, this time in a specific *scope* that will be preferred by the
Omnigator:

**Giving the topic map a short name**

````xml
<topic id="jillstm-topic">
  <subjectIdentity>
    <subjectIndicatorRef xlink:href="#jillstm"/>
  </subjectIdentity>
  <baseName>
    <baseNameString>Jill's First Topic Map</baseNameString>
  </baseName>
  <baseName>
    <scope>
      <subjectIndicatorRef xlink:href="http://psi.ontopia.net/basename/#short-name"/>
    </scope>
    <baseNameString>Jill's 1st TM</baseNameString>
  </baseName>
</topic>
````

Now let's reify an association.

Why would you want to do that? Well, you might want to say something more about the relationship
asserted by an association, over and above its type and roles – and the only way to "say something"
about *anything* in Topic Maps Land is by making that thing into a topic. For example, you might
have information resources that pertain to your relationship with Ontopia, such as your contract,
terms of employment, assessments, or whatever. By reifying the association, you obtain a topic on
which to hang these resources as occurrences, like this:

**Reifying an association**

````xml
<topic id="jill-ontopia-topic">
  <instanceOf>
    <topicRef xlink:href="#employment"/>
  </instanceOf>
  <subjectIdentity>
    <subjectIndicatorRef xlink:href="#jill-ontopia-association"/>
  </subjectIdentity>
  <baseName>
    <baseNameString>Jill's position with Ontopia</baseNameString>
  </baseName>
  <occurrence>
    <instanceOf>
      <topicRef xlink:href="#contract"/>
    </instanceOf>
    <resourceRef
      xlink:href="http://www.ontopia.net/internal/employees/contracts/jill.htm"/>
  </occurrence>
</topic>
````

We haven't shown how to add the ID (`jill-ontopia-association`) to the association that we created
in Step 7; we assume you can add that yourself. (If you can't, you are allowed to cheat by looking
at the complete example at the end of this document) We also assume that by now you can create a
topic for 'Contract'.

You'll see the result when you reload the topic map: The association between you and Ontopia now has
a *more...* link which will take you to the topic that represents the association. That association
has its own occurrence (your contract with Ontopia), and there is a section labelled "Reification
Topics", under which you will see the topics (you and 'Ontopia') that participate in the reified
association.

Since a reified association is also a topic, it can have its own characteristics. Yours already has
a name and an occurrence. It could also play roles in associations with other topics, and those
associations could themselves be reified. This leads to a multilayered topic map that can be browsed
at varying levels of detail. A user can start at the topic level, and then "zoom in" on an
association or other characteristic that is of particular interest and get a new level of
detail.

The Omnigator also supports reification of occurrences, base names, and variant names, but we'll
leave it to you to experiment with these.

#### Step 9: Adding metadata to your topic map ####

Now that you know how to reify the topic map it is easy to add metadata describing properties such
as creator, date, description, publisher, etc. These properties will then be displayed on the Index
Page in a box labelled "Topic Map Metadata". Most of the topic maps that are distributed with the
Omnigator have examples of such metadata. We recommend using the Dublin Core vocabulary in the
manner shown in the version of `jill.xtm` included in the distribution.

#### Step 10: Over to you ####

From this point there are many directions in which you could take your topic map. For example, you
could extend the ontology to include departments and projects, and the kinds of relationships that
exist between them and your existing categories. Or you can add your colleagues, create new
associations and occurrences, or provide more names in different scopes to those topics that already
exist. Now you know the syntax you could also autogenerate topic maps from existing structured
information (e.g., an LDAP database) and merge it with topic maps you have created by
hand.

If you are going to continue maintaining the topic map by hand for the time being, you should
seriously consider using LTM syntax rather than XTM. It is vastly more compact and much easier to
work with. (Don't worry about it not being a standard syntax because you can always convert it to
XTM using the Export plug-in.)

The possibilities are almost unlimited. We hope that this tutorial has been useful in getting you
started with your own topic maps. If you decide that this paradigm is the way to go for you and your
organisation, feel free to contact Ontopia for more advice, to book our training courses, license
our software, or get in touch with our partners. Full details of our offerings are available via the
web site at [http://www.ontopia.net/](http://www.ontopia.net/). Good luck!

#### Jill's First TM ####

Here is the complete version of the topic map used for the tutorial, including comments. It is
available as `jill.xtm` in the `{Ontopia}/topicmaps/` directory of the Omnigator distribution, and
you can also view it as XML [in your browser](jill.xtm).

**Jill's First TM**

````xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<topicMap xmlns="http://www.topicmaps.org/xtm/1.0/"
          xmlns:xlink="http://www.w3.org/1999/xlink"
          id="jillstm">

<!-- ...................... ONTOLOGY TOPICS ...................... -->

<!-- .................... THE TOPIC MAP TOPIC .................... -->

<topic id="jillstm-topic">
  <!-- Reifies the topic map and gives it a name -->
  <subjectIdentity>
    <subjectIndicatorRef xlink:href="#jillstm"/>
  </subjectIdentity>
  <baseName>
    <baseNameString>Jill's First Topic Map</baseNameString>
  </baseName>
  <baseName>
    <scope>
      <subjectIndicatorRef
        xlink:href="http://psi.ontopia.net/basename/#short-name"/>
    </scope>
    <baseNameString>Jill's 1st TM</baseNameString>
  </baseName>
</topic>

<!-- ........................ THEMES (SCOPING TOPICS) ............ -->

<topic id="short-name">
  <subjectIdentity>
      <subjectIndicatorRef
        xlink:href="http://psi.ontopia.net/basename/#short-name"/>
  </subjectIdentity>
  <baseName>
    <baseNameString>Short name</baseNameString>
  </baseName>
</topic>

<!-- ........................ TOPIC TYPES ........................ -->

<topic id="developer">
  <baseName>
    <baseNameString>Developer</baseNameString>
  </baseName>
</topic>

<topic id="company">
  <baseName>
    <baseNameString>Company</baseNameString>
  </baseName>
</topic>

<!-- ..................... OCCURRENCE TYPES ...................... -->

<topic id="description">
  <!-- Uses Ontopia's "description occurrence" PSI -->
  <subjectIdentity>
    <subjectIndicatorRef
      xlink:href="http://psi.ontopia.net/xtm/occurrence-type/description"/>
  </subjectIdentity>
  <baseName>
    <baseNameString>Description</baseNameString>
  </baseName>
</topic>

<topic id="kudo">
  <!-- An instance of the "description occurrence" class -->
  <instanceOf>
    <topicRef xlink:href="#description"/>
  </instanceOf>
  <baseName>
    <baseNameString>Kudo</baseNameString>
  </baseName>
</topic>

<topic id="website">
  <baseName>
    <baseNameString>Web site</baseNameString>
  </baseName>
</topic>

<topic id="contract">
  <baseName>
    <baseNameString>Contract</baseNameString>
  </baseName>
</topic>

<!-- ..................... ASSOCIATION TYPES ..................... -->

<topic id="employment">
  <!-- Illustrates the use of names scoped by roles -->
  <baseName>
    <baseNameString>Employment</baseNameString>
  </baseName>
  <baseName>
    <scope><topicRef xlink:href="#employer"/></scope>
    <baseNameString>Employs</baseNameString>
  </baseName>
  <baseName>
    <scope><topicRef xlink:href="#employee"/></scope>
    <baseNameString>Employed by</baseNameString>
  </baseName>
</topic>

<!-- .................. ASSOCIATION ROLE TYPES.................... -->

<topic id="employer">
  <baseName>
    <baseNameString>Employer</baseNameString>
  </baseName>
</topic>

<topic id="employee">
  <baseName>
    <baseNameString>Employee</baseNameString>
  </baseName>
</topic>


<!-- ..................... INDIVIDUAL TOPICS ..................... -->


<topic id="ontopia">
  <instanceOf>
    <topicRef xlink:href="#company"/>
  </instanceOf>
  <subjectIdentity>
    <subjectIndicatorRef xlink:href="http://psi.ontopia.net/ontopia/#ontopia"/>
  </subjectIdentity>
  <baseName>
    <baseNameString>Ontopia</baseNameString>
  </baseName>
  <occurrence>
    <instanceOf>
      <topicRef xlink:href="#website"/>
    </instanceOf>
    <resourceRef xlink:href="http://www.ontopia.net/"/>
  </occurrence>
</topic>

<topic id="jill">
  <instanceOf>
    <topicRef xlink:href="#developer"/>
  </instanceOf>
  <baseName>
    <baseNameString>Jill Hacker</baseNameString>
  </baseName>
  <occurrence>
    <instanceOf>
      <topicRef xlink:href="#kudo"/>
    </instanceOf>
    <resourceData>Jill's a cool girl and a great hacker</resourceData>
  </occurrence>
</topic>

<!-- .................... ADDRESSABLE SUBJECT .................... -->

<topic id="ontopias-website">
  <instanceOf>
    <topicRef xlink:href="#website"/>
  </instanceOf>
  <subjectIdentity>
    <resourceRef xlink:href="http://www.ontopia.net/"/>
  </subjectIdentity>
  <baseName>
    <baseNameString>Ontopia's Web Site</baseNameString>
  </baseName>
</topic>


<!-- ....................... ASSOCIATIONS ........................ -->


<association id="jill-ontopia-association">
  <instanceOf>
    <topicRef xlink:href="#employment"/>
  </instanceOf>
  <member>
    <roleSpec><topicRef xlink:href="#employee"/></roleSpec>
    <topicRef xlink:href="#jill"/>
  </member>
  <member>
    <roleSpec><topicRef xlink:href="#employer"/></roleSpec>
    <topicRef xlink:href="#ontopia"/>
  </member>
</association>


<!-- ................... REIFICATION EXAMPLES .................... -->


<!-- ................... REIFIED ASSOCIATIONS .................... -->

<topic id="jill-ontopia-topic">
  <!-- reifies the Jill/Ontopia association -->
  <instanceOf>
    <topicRef xlink:href="#employment"/>
  </instanceOf>
  <subjectIdentity>
    <subjectIndicatorRef xlink:href="#jill-ontopia-association"/>
  </subjectIdentity>
  <baseName>
    <baseNameString>Jill's position with Ontopia</baseNameString>
  </baseName>
  <occurrence id="jills-contract-occurrence">
    <instanceOf>
      <topicRef xlink:href="#contract"/>
    </instanceOf>
    <resourceRef
      xlink:href="http://www.ontopia.net/internal/employees/contracts/jill.htm"/>
  </occurrence>
</topic>

<!-- .................... REIFIED OCCURRENCES .................... -->

<topic id="jills-contract-topic">
  <!-- Reifies the occurrence of the reified Jill/Ontopia association -->
  <instanceOf>
    <topicRef xlink:href="#contract"/>
  </instanceOf>
  <subjectIdentity>
    <subjectIndicatorRef xlink:href="#jills-contract-occurrence"/>
  </subjectIdentity>
  <baseName>
    <baseNameString>Jill's contract with Ontopia</baseNameString>
  </baseName>
  <occurrence>
    <instanceOf>
      <topicRef xlink:href="#contract"/>
    </instanceOf>
    <resourceRef
      xlink:href="http://www.ontopia.net/internal/employees/contracts/jill.htm"/>
  </occurrence>
</topic>

<!-- .................... DUBLIN CORE METADATA ................... -->

<!-- Merge DC ontology and related controlled vocabulary -->

<mergeMap xlink:href="dc.xtmm"/>

<!-- Another topic that reifies the topic map. This will be merged with the topic
     whose ID is "jillstm-topic" because they have the same subject indicator. -->

<topic id="tm-topic">
  <subjectIdentity>
    <subjectIndicatorRef xlink:href="#jillstm"/>
  </subjectIdentity>

  <!-- dc:Rights modelled as occurrence -->
  <occurrence>
    <instanceOf>
      <subjectIndicatorRef xlink:href="http://purl.org/dc/elements/1.1/rights"/>
    </instanceOf>
    <resourceData>(C) Copyright 2003 Ontopia</resourceData>
  </occurrence>

  <!-- dc:Date modelled as occurrence -->
  <occurrence>
    <instanceOf>
      <subjectIndicatorRef xlink:href="http://purl.org/dc/elements/1.1/date"/>
    </instanceOf>
    <resourceData>$Date: 2008/05/29 07:01:12 $</resourceData>
  </occurrence>

  <!-- dc:Description modelled as occurrence -->
  <occurrence>
    <instanceOf>
      <subjectIndicatorRef xlink:href="http://purl.org/dc/elements/1.1/description"/>
    </instanceOf>
    <resourceData>Example topic map from Omnigator User Guide.</resourceData>
  </occurrence>
</topic>

<!-- dc:Creator modelled as an association -->

<association>
  <instanceOf>
    <subjectIndicatorRef xlink:href="http://purl.org/dc/elements/1.1/creator"/>
  </instanceOf>
  <member>
    <roleSpec>
      <subjectIndicatorRef xlink:href="http://psi.ontopia.net/metadata/#resource"/>
    </roleSpec>
    <topicRef xlink:href="#tm-topic"/>
  </member>
  <member>
    <roleSpec>
      <subjectIndicatorRef xlink:href="http://psi.ontopia.net/metadata/#value"/>
    </roleSpec>
    <subjectIndicatorRef xlink:href="http://psi.ontopia.net/ontopia/#pepper"/>
  </member>
</association>

<!-- add topic for value of dc:Creator -->

<topic id="pepper">
  <subjectIdentity>
    <subjectIndicatorRef xlink:href="http://psi.ontopia.net/ontopia/#pepper"/>
  </subjectIdentity>
  <baseName>
    <baseNameString>Steve Pepper</baseNameString>
  </baseName>
</topic>

<!-- dc:Publisher modelled as an association -->

<association>
  <instanceOf>
    <subjectIndicatorRef xlink:href="http://purl.org/dc/elements/1.1/publisher"/>
  </instanceOf>
  <member>
    <roleSpec>
      <subjectIndicatorRef xlink:href="http://psi.ontopia.net/metadata/#resource"/>
    </roleSpec>
    <topicRef xlink:href="#tm-topic"/>
  </member>
  <member>
    <roleSpec>
      <subjectIndicatorRef xlink:href="http://psi.ontopia.net/metadata/#value"/>
    </roleSpec>
    <subjectIndicatorRef xlink:href="http://psi.ontopia.net/ontopia/#ontopia"/>
  </member>
</association>

<!-- Ontopia is already a topic in this topic map, so no need to add it again -->

<!-- dc:Subject modelled as an association -->

<association>
  <instanceOf>
    <subjectIndicatorRef xlink:href="http://purl.org/dc/elements/1.1/subject"/>
  </instanceOf>
  <member>
    <roleSpec>
      <subjectIndicatorRef xlink:href="http://psi.ontopia.net/metadata/#resource"/>
    </roleSpec>
    <topicRef xlink:href="#tm-topic"/>
  </member>
  <member>
    <roleSpec>
      <subjectIndicatorRef xlink:href="http://psi.ontopia.net/metadata/#value"/>
    </roleSpec>
    <subjectIndicatorRef xlink:href="http://psi.ontopia.net/ontopia/#ontopia"/>
  </member>
</association>

<!-- Ontopia is already a topic in this topic map, so no need to add it again -->

<!-- dc:Type modelled as an association -->

<association>
  <instanceOf>
    <subjectIndicatorRef xlink:href="http://purl.org/dc/elements/1.1/type"/>
  </instanceOf>
  <member>
    <roleSpec>
      <subjectIndicatorRef xlink:href="http://psi.ontopia.net/metadata/#resource"/>
    </roleSpec>
    <topicRef xlink:href="#tm-topic"/>
  </member>
  <member>
    <roleSpec>
      <subjectIndicatorRef xlink:href="http://psi.ontopia.net/metadata/#value"/>
    </roleSpec>
    <subjectIndicatorRef xlink:href="http://psi.ontopia.net/metadata/#topicmap"/>
  </member>
</association>

<!-- dc:Format modelled as an association -->

<association>
  <instanceOf>
    <subjectIndicatorRef xlink:href="http://purl.org/dc/elements/1.1/format"/>
  </instanceOf>
  <member>
    <roleSpec>
      <subjectIndicatorRef xlink:href="http://psi.ontopia.net/metadata/#resource"/>
    </roleSpec>
    <topicRef xlink:href="#tm-topic"/>
  </member>
  <member>
    <roleSpec>
      <subjectIndicatorRef xlink:href="http://psi.ontopia.net/metadata/#value"/>
    </roleSpec>
    <subjectIndicatorRef xlink:href="http://psi.ontopia.net/metadata/#XTM"/>
  </member>
</association>

<!-- dc:Language modelled as an association -->

<association>
  <instanceOf>
    <subjectIndicatorRef xlink:href="http://purl.org/dc/elements/1.1/language"/>
  </instanceOf>
  <member>
    <roleSpec>
      <subjectIndicatorRef xlink:href="http://psi.ontopia.net/metadata/#resource"/>
    </roleSpec>
    <topicRef xlink:href="#tm-topic"/>
  </member>
  <member>
    <roleSpec>
      <subjectIndicatorRef xlink:href="http://psi.ontopia.net/metadata/#value"/>
    </roleSpec>
    <subjectIndicatorRef xlink:href="http://www.topicmaps.org/xtm/1.0/language.xtm#en"/>
  </member>
</association>

<!-- add topic for value of dc:Language -->

<topic id="english">
  <subjectIdentity>
    <subjectIndicatorRef xlink:href="http://www.topicmaps.org/xtm/1.0/language.xtm#en"/>
  </subjectIdentity>
  <baseName>
    <baseNameString>English</baseNameString>
  </baseName>
</topic>

</topicMap>
````

#### Jill's First TM in LTM syntax ####

Here is slightly abbreviated version of the topic map used for the tutorial, this time in LTM
syntax. It is given here in order to convince you that LTM is worth considering if you intend to
maintain your topic maps by hand. This topic map is exactly equivalent to the one given above in XTM
syntax.

**Jill's First TM in LTM syntax**

````ltm
#TOPICMAP ~jills-tm
#INCLUDE "dc.ltmm"

/* ONTOLOGY TOPICS */

/* THE TOPIC MAP TOPIC */
[jillstm = "Jill's First Topic Map"
         = "Jill's 1st TM" /short]

/* THEMES (SCOPING TOPICS) */
[short = "short name" @"http://psi.ontopia.net/basename/#short-name"]

/* TOPIC TYPES */
[developer = "Developer"]
[company = "Company"]

/* OCCURRENCE TYPES */
[description = "Description" @"http://psi.ontopia.net/xtm/occurrence-type/description"]
[kudo : description = "Kudo"]
[website = "Website"]
[contract = "Contract"]

/* ASSOCIATION TYPES */
[employment = "Employment"
            = "Employs" /employer
            = "Employed by" /employee]

/* ASSOCIATION ROLE TYPES */
[employer = "Employer"]
[employee = "Employee"]

/* INDIVIDUAL TOPICS */
[ontopia : company = "Ontopia" @"http://psi.ontopia.net/ontopia/#ontopia"]
{ontopia, website, "http://psi.ontopia.net/"}

[jill : developer = "Jill Hacker"]
{jill, kudo, [[Jill's a cool girl and a great hacker]]}

/* ADDRESSABLE SUBJECT */
[ontopias-website : website = "Ontopia's Web Site" %"http://www.ontopia.net/"]

/* ASSOCIATIONS */
employment(jill : employee, ontopia : employer) ~ jill-ontopia

/* REIFICATION EXAMPLES */

/* REIFIED ASSOCIATIONS */
[jill-ontopia : employment = "Jill's position with Ontopia"]
{jill-ontopia, contract,
 "http://www.ontopia.net/internal/employees/contracts/jill.htm"}
 ~ jills-contract

/* REIFIED OCCURRENCES */
[jills-contract : contract = "Jill's contract with Ontopia"]
{jills-contract, contract,
 "http://www.ontopia.net/internal/employees/contracts/jill.htm"}

/* DUBLIN CORE METADATA */
{jillstm, Rights, [[(C) Copyright 2003 Ontopia]]}
{jillstm, Date, [[$Date: 2008/05/29 07:01:12 $]]}
{jillstm, Description, [[Example topic map from Omnigator User Guide.]]}
Creator(jillstm : resource, pepper : value)
  [pepper = "Steve Pepper" @"http://psi.ontopia.net/ontopia/#pepper"]
Publisher(jillstm : resource, ontopia : value)
Subject(jillstm : resource, ontopia : value)
Type(jillstm : resource, topicmap-type : value)
Format(jillstm : resource, LTM-format : value)
Language(jillstm : resource, en : value)
  [en = "English" @"http://www.topicmaps.org/xtm/1.0/language.xtm#en"]
````

