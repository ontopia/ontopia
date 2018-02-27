The Linear Topic Map Notation
=============================

Definition and introduction, version 1.3
----------------------------------------

<p class="introduction">
This technical report defines version 1.3 of the Linear Topic Map Notation, also known as LTM. It
provides both an introduction and a formal definition, the latter in the form of a complete EBNF
specification given in <a href="#formal-syntax-definition">Formal syntax definition.</a>
</p>

<p class="introduction">
Please note that this document is not a formal specification from any recognized standards body, but
a Technical Report published by Ontopia, for the convenience of all interested parties. ISO is 
currently finishing standardization of an alternative standardized syntax called CTM. For more 
information, see <a href="http://www.isotopicmaps.org/ctm/">the CTM home page</a>.
</p>

<span class="version">Ontopia A/S Version 1.3 ($Revision: 1.23 $) $Date: 2006/06/17 17:52:01 $</p>

### Linear topic map notation? What's that? ###

The Linear Topic Map notation (LTM) is a simple textual format for topic maps. Just like XTM, the
XML interchange format, it represents the constructs in the topic map standard as text, but unlike
XTM it is compact and simple. The notation can be written in any text editor and processed by topic
map software that supports it, or converted into the XML format supported by such
software.

The XML-based topic map interchange format is defined in such a way as to make it easy to understand
for humans and to develop software for, and these purposes it fulfills very well. However, this has
the cost of making it awkward to read and write for humans. Humans were not really intended to do
this, of course, they were intended to use specialized topic map editors, which would insulate their
users from the syntactical details of the interchange format.

Despite the existence of editors there is still a need for a simple textual format that can be used
to concisely and clearly express topic map constructs in emails, discussions and similar contexts.
Such a format also makes it easy to quickly create and maintain small topic maps for demonstration
and personal purposes.

While you may find that this syntax provides you with a convenient and easy way to maintain your
topic maps, please note that the only standardized form for interchangeable topic maps remains the
XTM 1.0 syntax.

The notation has been developed by Ontopia. Steve Pepper came up with the original idea, based on
the linear notation for conceptual graphs. The notation has since been refined by Lars Marius
Garshol, with input from Geir Ove Grønmo, Steve Pepper, and Kal Ahmed. Useful contributions from
Murray Altheim, Akio Yamamoto, Robert Barta, Michael Chapman, Thomas Flemming, Are D. Gulbrandsen,
Kaj Hejer, Eirik Jensen Opland, Tom-Anders N. Røst, and Ian Meikle are also gratefully acknowledged.
Lars Heuer pointed out a number of mistakes in and suggested one improvement to version
1.3.

While the copyright to both this description and the format itself is held by Ontopia, Ontopia
reserves *only* the right to be recognized as the originator of the notation. Permission to use it
in any way for any purpose whatsoever is hereby granted in perpetuity to all potential
users.

#### Changes in version 1.3 ####

The following changes have been made in version 1.3 relative to version 1.2:

*  The `#INCLUDE` directive has been added.
*  The `#VERSION` directive has been added.
*  The `#PREFIX` directive (and support for qualified, or prefixed, names) has been added.
*  Support for reification has been added.
*  Support for variant names has been added.
*  The escape syntax for Unicode characters (and quotes) in strings has been added.

#### Completeness ####

The following features of XTM 1.0 are not supported:

*  Variant names that are external resources referred to by URI.
*  When merging in external topic maps with the `MERGEMAP` directive it is not possible to add scopes
   to the merged-in topic characteristic assignments. This is not properly an unsupported model
   construct, but is still a feature of the XTM 1.0 syntax that is missing from
   LTM.
*  Untyped occurrences, associations, and association roles.
*  Association roles with unspecified players.

In addition, none of the new features of the upcoming [Topic Maps Data
Model](http://www.isotopicmaps.org/sam/sam-model/) and [XTM
1.1](http://www.isotopicmaps.org/sam/sam-xtm/) specifications are supported.

### An introduction to the notation ###

#### Defining topics ####

The basis of the notation is the ability to define topics, which is done by writing the ID of the
topic in square brackets. An example is shown below.

````ltm
[ltm]
````

This represents a topic map consisting of a single topic that has the ID 'ltm', but no other
characteristics. If you want, you can provide it with a base name and a sort name as well, as in the
example below. The sort name is optional.

````ltm
[ltm = "The linear topic map notation";
       "linear topic map notation, the"]
````

You can even add a display name. If you have a display name the sort name is optional, but you need
two semicolons to tell the parser that the second name is a display name and not a sort name. The
example below shows a topic with all three name types.

````ltm
[foo = "basename"; "sortname"; "dispname"]
````

The topic can also be typed. The example below adds the type 'format' to the `ltm` topic. Multiple
type IDs can be listed after the colon, separated by whitespace, if the topic has more than just one
type.

````ltm
[ltm : format = "The linear topic map notation";
                "linear topic map notation, the"]
````

Note that even if no topic with the ID 'format' is defined anywhere in the LTM file this reference
will cause the topic to be created by the LTM processor. The 'format' topic will have an ID, but no
other characteristics. Note also that LTM is oblivious to whitespace. You can add as much whitespace
as you want anywhere (except inside strings) without having any effect on the resulting topic
map.

LTM also supports providing subject indicators for topics, as shown below. The URL of the subject
indicator is quoted and preceded by an '@' character. Any number of subject indicators can be
given.

````ltm
[ltm : format = "The linear topic map notation";
                "linear topic map notation, the"
     @"http://www.ontopia.net/download/ltm.html"]
````

For topics which represent information resources it is not necessary to use a proxy resource (a
subject indicator) to indicate the identity of the subject; it can instead be referred to directly.
LTM supports this, by using a '%' character followed by the quoted URL of the resource. An example
is shown below.

````ltm
[xmlspec : specification = "The XML 1.0 specification"
     %"http://www.w3.org/TR/REC-xml"]
````

The final construct supported by LTM for topics is scoping of names. This can be done for the base
name, sortname, dispname-trinity as a whole, by appending a topic ID preceded by a slash after the
name, as shown below. Multiple topic IDs are allowed, separated by whitespace.

````ltm
[ltm : format = "Den lineære emnekart-notasjonen";
                "lineære emnekart-notasjonen, den"
                / norwegian 
     @"http://www.ontopia.net/download/ltm.html"]
````

Note that if this example and the previous `[ltm]` example were to appear in the same LTM file it
would cause a single topic to be created with the union of the characteristics of these two
definitions. That means that the topic would have the 'ltm' ID, the format type, the two different
name sets and the given subject indicator.

Note also that there are no requirements on the order in which constructs appear in LTM files. A
topic type can be used before it is defined, for example.

#### Defining associations ####

The LTM notation also supports defining associations. In the example below the LTM topic defined
above is associated with a topic with the ID 'topic maps' by an association that has the
`format-for` type. ('format-for' is of course the ID of the topic that types that
association.)

````ltm
format-for(ltm, topic-maps)
````

The meaning of this example is that LTM is a serialization format for topic maps. This should
perhaps be made clearer by adding association role types. The example below does
this.

````ltm
format-for(ltm : format, topic-maps : standard)
````

Note that if the association role type is omitted the role type will default to the type of the
topic (provided it has one). If the topic has more than one type, one of these will be selected at
random. The rationale for this was that it is a useful shorthand for a commonly occurring
construction; we urge caution in the use of this construct, however, as it can cause difficulties
when topics have multiple types or when their types change.

As a shorthand it is allowed to specify a topic in the role player position, instead of just
referencing it. All the constructs used when defining topics can be used here, which means that it
is possible to define topics with their characteristics in the associations they participate in
without defining them anywhere else. The example could therefore also have been written as
follows.

````ltm
format-for(ltm, [topic-maps : standard = "Topic maps"])
````

Associations can also be scoped, as with base names, by appending a slash followed by the IDs of the
scoping topics, separated by whitespace. The example below illustrates this.

````ltm
[lmg : person = "Lars Marius Garshol"]

format-for(ltm : format, topic-maps : standard) / lmg
````

#### Defining occurrences ####

LTM also supports defining occurrences. This is done using the notation shown below, where the
occurrence information is given in curly braces. Three pieces of information, all of which are
required, appear inside the braces, separated by commas. The first is the ID of the topic which has
the occurrence, the second is the ID of the occurrence role type and the third is the locator of the
occurrence in double quotes.

````ltm
{ltm, specification, "http://www.ontopia.net/download/ltm.html"}
````

You can also specify the resource data of an occurrence inline in the LTM file, as shown below.

````ltm
{ltm, description, [[A simple text-based format for topic maps.]]}
````

Occurrences are scoped in the same way as associations:

````ltm
{ltm, specification, "http://www.ontopia.net/download/ltm.html"} / english
````

#### A complete example ####

Below is given a more complete example of an LTM topic map. Note that text appearing between '/*'
and '*/' is comments.

````ltm
/* topic types */

[format       = "Format"]
[standard     = "Standard"]
[organization = "Organization"]

/* association types */

[format-for = "Format for"]
[defined-by = "Defined by"]

/* occurrence types */

[specification = "Specification"]
[homepage      = "Home page"]

/* topics, associations and occurrences */

[topic-maps : standard  = "Topic maps"
                        = "ISO/IEC 13250 Topic Maps" / fullname]
{topic-maps, specification,
   "http://www.y12.doe.gov/sgml/sc34/document/0129.pdf"}

[xtm : format = "XTM Syntax"]

[ltm : format = "The linear topic map notation";
                "linear topic map notation, the"
     @"http://www.ontopia.net/topicmaps/ltm-tech-report.html"]
{ltm, specification, "http://www.ontopia.net/topicmaps/ltm-tech-report.html"}

format-for(ltm, topic-maps)
format-for(xtm, topic-maps)

defined-by(ltm, ontopia)
defined-by(xtm, topicmaps.org)

[ontopia : organization = "Ontopia AS"]
{ontopia, homepage, "http://www.ontopia.net"}

[topicmaps.org  : organization = "TopicMaps.Org"]
{topicmaps.org, homepage, "http://www.topicmaps.org"}
````

#### Variant names ####

Variant names are used to represent names that are alternative forms of a base name. An example of
this might be:

````ltm
[xml = "Extensible Markup Language"
         ("XML" / acronym)
         ("Extended Markup Language" / erroneous)]
````

The canonical name for XML is "Extensible Markup Language", but there are two related forms of it:
"XML", which is the acronym, and "Extended Markup Language", which is an often found (but wrong)
form of it. The example above represents these as variants of the base name. Note that all variants
must have a scope.

Note that sort name, display name, and base name scope must come before the variants, as shown
below.

````ltm
[xml = "Extensible Markup Language"; "extensible markup language" / english
         ("XML" / acronym)
         ("Extended Markup Language" / erroneous)]
````

#### Reification ####

Reification may sound scary, but it's actually quite simple. If you have a name, or association, or
occurrence, or association role that you want to say more about, how do you do it? For example,
imagine that you have an association like the one below, which states that I work for
Ontopia.

````ltm
employed-by(lmg : employee, ontopia : employer)
````

What if you want to say when I started working for Ontopia? It's possible to do this by turning the
employment into a topic and using two associations (one from me to the employment and another from
the employment to Ontopia), but if you don't want to do that you are stuck, because topic maps don't
let you put an occurrence on an association.

This is where reification comes in: you create a topic that represents the association (that is, the
topic reifies the association). Then you can assign an occurrence to the reifying topic, and all is
well.

The way to do this in LTM is to add `~ topicid` after the construct you want to reify. The ID is the
ID of the reifying topic, which you can use directly. So for the example above it would
be:

````ltm
employed-by(lmg : employee, ontopia : employer) ~ lmg-employment
{lmg-employment, start-month, [[2000-04]]}
````

Similarly, to reify an occurrence, you could do this:

````ltm
{topicmaps.org, homepage, "http://www.topicmaps.org"} ~ tm.org-hp
last-modified-by(tm.org-hp : modified, lmg : modifier)
````

This would say that I'm the last person to modify this occurrence. For names, the syntax would be as
shown below.

````ltm
[ltm : syntax = "LTM" / acronym ~ltm-name]
[ltm-name : name = "The name 'LTM'"]

invented-by(ltm-name : invention, steve-pepper : inventor)
````

Special syntax is needed to reify the topic map; for that, see [The TOPICMAP
directive](#the-topicmap-directive).

#### Directives ####

LTM has a concept of so-called "syntax directives", which are used not to represent topic map
constructs, but to provide information related to processing. There are six different directives,
each covered in a separate section below.

##### The TOPICMAP directive #####

The `TOPICMAP` directive is used to make it possible to reify the topic map itself. (How to reify
other parts of the topic map is shown in [Reification](#reification).) This is useful, since it
makes it possible to attach metadata to the topic map using topic map constructs. What the directive
does is to create a topic that reifies the topic map, and give it an ID.

Below is an example.

````ltm
#TOPICMAP ~topicmap

[topicmap = "An example topic map"]
````

This creates a topic `topicmap`, which reifies the topic map. This could then be used to add
metadata about the topic map, such as:

````ltm
#TOPICMAP ~topicmap

[topicmap = "An example topic map"]
{topicmap, publication-date, [[2005-01-12]]}
````

##### The MERGEMAP directive #####

The `MERGEMAP` directive is used to merge external topic maps into the LTM topic map. The external
topic maps can be in any syntax, but if this syntax is not LTM it must be declared what syntax it
is. An example is shown below.

````ltm
#MERGEMAP "geography.xtm" "xtm"
````

This directive causes the topic map at the given URI to be loaded according to the rules of the
syntax it is written in and merged with the current topic map once the loading is complete. This
means that topics defined in the loaded topic map will be merged with topics in this topic map when
they have the same subject indicator or subject locator, but *not* because they have the same ID.
(For this behaviour, see [The INCLUDE directive](#the-include-directive).) Note that the URI is
allowed to use any URI scheme, although there is no guarantee that an LTM processor will understand
any URI schemes beyond 'file'.

LTM processors are required to recognize the syntaxes listed below, but not necessarily to support
them. XTM and LTM must be supported, while the other syntaxes are optional. It is an error if the
LTM processor is asked to merge in a topic map in a syntax it does not understand. Note that the
syntax names are case-insensitive. If no syntax is specified, the default is
LTM.

xtm
:   The XTM 1.0 XML topic maps syntax.

hytm
:   The HyTime-based architectural form syntax defined in the original ISO 13250 standard.

ltm
:   The Linear Topic Map Notation.

astma
:   The textual syntax for topic maps known as
    [AsTMa=](http://topicmaps.bond.edu.au/astma/astma=.html).

Directives declared in the merged-in file have no effect in the parent file, except, of course, for
`MERGEMAP`s and `INCLUDE`s.

##### The INCLUDE directive #####

The `INCLUDE` is very similar to `MERGEMAP`, the difference being that with `INCLUDE` the file
merged in will use the same namespace for IDs. This means that topics with the same IDs in the two
files will be merged (as will topics with the same subject indicators or
locators).

To use the `INCLUDE` directive, simply write:

````ltm
#INCLUDE "other-file.ltm"
````

Note that unlike `MERGEMAP` the `INCLUDE` directive only supports LTM files, and the same
limitations on URI schemes apply. Directives declared in the included file have no effect in the
parent file, except, of course, for `MERGEMAP`s and `INCLUDE`s.

##### The BASEURI directive #####

This directive is used to change the base URI against which relative URIs in the document are
resolved. It works exactly like the `xml:base` attribute in XML Base, or the `BASE` element in HTML.
Below is shown an example.

````ltm
#BASEURI "http://www.ontopia.net/"
````

All URIs occurring *after* the directive will resolve against the given URI, which must be absolute,
rather than against the URI of the LTM document itself. This applies to URIs in `MERGEMAP`,
`PREFIX`, and `INCLUDE` directives, subject locators, subject indicators, and the URIs of
occurrences. (More formally, it applies to all instances of the grammar symbol `uri`.) Note that the
`BASEURI` directive does not apply inside any files included with `MERGEMAP` or
`INCLUDE`.

Note that having more than one `BASEURI` directive in the same file is an error. Note also that
same-document references, that is, URI references that consist only of a fragment identifier (of the
form `#foo`) are relative to the file URI, not to the base URI set with this
directive.

##### The PREFIX directive #####

In some cases one wants to refer to a topic using its subject indicator (or, less often, subject
locator) for example because it's defined in a different file that is loaded with `MERGEMAP`. To do
that using a topic ID you have to create a new topic with an ID and give it the subject indicator,
and then refer to it with the ID. The `PREFIX` directive makes it possible to refer to the topic
directly using its subject indicator.

Below is a simple example topic map that uses this capability.

````ltm
#MERGEMAP "core.xtm" "xtm" /* load superclass-subclass and other topics */
#PREFIX xtm @"http://www.topicmaps.org/xtm/1.0/core.xtm#"

[sentient-being = "Sentient being"]
[human = "Human"]
[giraffe = "Giraffe"]

xtm:superclass-subclass(sentient-being : xtm:superclass, human : xtm:subclass)
xtm:superclass-subclass(sentient-being : xtm:superclass, giraffe : xtm:subclass)
````

The `PREFIX` directive here declares that the `xtm` prefix is used for topics whose subject
indicators (that's what the `@` means) begin with `http://www.topicmaps.org/xtm/1.0/core.xtm#`. The
part after the `xtm:` is added to get the full subject indicator.

This can also be used for topic maps that define a lot of subject indicators, so that you could
write an LTM version of core.xtm like this:

````ltm
#PREFIX xtm @"http://www.topicmaps.org/xtm/1.0/core.xtm#"

[xtm:superclass-subclass = "Superclass/subclass"]
[xtm:superclass = "Superclass"]
[xtm:subclass = "Subclass"]
````

These three topics would only have subject indicators and no IDs, since when declaring the topics
the subject indicator is used where the topic ID would normally be.

##### The VERSION directive #####

This directive is used to indicate what version of LTM the file is written in. It has no particular
effect in LTM 1.3, but if later LTM versions are incompatible with version 1.3 it is possible that
parsers will use this information to process LTM 1.3 files with a different
parser.

To make it clear that your LTM file is using LTM 1.3, just write:

````ltm
#VERSION "1.3"
````

Note that the `VERSION` directive has to appear first of all the directives, if it is present. This
restriction is there in case new directives are introduced in later versions.

#### Escape syntax in strings ####

Strings in LTM (that is, anything enclosed in double quotes) support two different escape syntaxes.
Firstly, if you need to include a double quote in a string in LTM you can do it by writing it twice,
as in the example below.

````ltm
[doublequote : character = "The '""' character"]
````

The name of this topic will be `The '"' character`.

There is also a general escape syntax for all Unicode characters: `\u0000`. Four, five, or six
hexadecimal digits can be used to give the number of the Unicode character you want to write (digits
can be written in both upper- and lowercase). Thus the example above could also have been written as
shown below.

````ltm
[doublequote : character = "The '\u0022' character"]
````

The Unicode escape syntax is mostly useful for writing characters not available on your keyboard.
For example, if I wanted to write 'Katakana' in Japanese, I would do it as shown
below.

````ltm
[katakana : syllabary = "Katakana"
                      = "\u30AB\u30BF\u30AB\u30CA" / native]
````

Below is a table containing the escape sequences for some useful characters that may be needed in
LTM files.

| Escape sequence | Character | 
|---|---|
| \u0022 | " | 
| \u0027 | ' | 
| \u005B | &#91; | 
| \u005D | &#93; | 
| \u007B | { | 
| \u007D | } | 

### Formal syntax definition ###

This section defines the LTM syntax using a formal extended BNF grammar. Lexical tokens are given
either as single-quoted strings directly in the grammar, or as upper-case names of token types. The
token types are defined separately further below.

````text/x-ebnf
  topic-map  ::= encoding? version? directive* (topic | assoc | occur) *

  encoding   ::= '@' STRING

  directive  ::= topicmapid | mergemap | baseuri | include | prefix

  topicmapid ::= '#' 'TOPICMAP' WS (NAME | reify-id)

  mergemap   ::= '#' 'MERGEMAP' WS uri (WS STRING)?

  baseuri    ::= '#' 'BASEURI' WS uri

  include    ::= '#' 'INCLUDE' WS uri

  version    ::= '#' 'VERSION' WS STRING

  prefix     ::= '#' 'PREFIX' WS NAME WS ('@' | '%') STRING
     
  topic      ::= '[' qname (WS ':' qname+)? (topname)* subject? indicator* ']'

  subject    ::= '%' uri

  indicator  ::= '@' uri

  topname    ::= '=' basename ((';' sortname) |
                               (';' sortname? ';' dispname))?
                     scope? reify-id? variant*

  scope      ::= '/' qname+

  basename   ::= STRING

  sortname   ::= STRING

  dispname   ::= STRING

  variant    ::= '(' STRING scope reify-id? ')'

  assoc      ::= qname '(' assoc-role (',' assoc-role)*  ')' scope? reify-id?
     
  assoc-role ::= (topic | qname) WS (':' qname )? reify-id?
     
  occur      ::= '{' occ-topic ',' occ-type ',' resource '}' scope? reify-id?

  resource   ::= uri | DATA

  occ-topic  ::= qname

  occ-type   ::= qname

  uri        ::= STRING

  qname      ::= NAME ':' NAME | NAME

  reify-id   ::= '~' WS? NAME
````

The lexical token types defined below use Perl-style regular expressions for their definitions. Note
that while whitespace (represented by the `WS` token type) is implicitly allowed between any two
tokens, it is explicitly required in the 'topic' and 'assoc-role' productions in the above grammar.
This is to avoid problems caused by the fact that a colon is allowed in topic
IDs.

````text/x-ebnf
  NAME       = [A-Za-z_][-A-Za-z_0-9.]*
      
  COMMENT    = /\*([^*]|\*[^/])*\*/
     
  STRING     = "[^"]*"

  DATA       = \[\[(([^\]]+\])*|\])\]

  WS         = [\r\n\t ]+
````

The `NAME` token type is slightly modified compared to the definition in the XML recommendation. The
colon is no longer allowed as a name start character, since otherwise a single colon could be both a
name and a separator.

All tokens are case-sensitive.

Comments can occur anywhere where whitespace is allowed.

#### Character encoding handling ####

All LTM files are to be processed *as if* they were composed of Unicode characters. Files may be in
any encoding, but if that encoding is not ISO 8859-1 it should be declared using the `encoding`
production. If the encoding declaration appears in the file it must appear at the very beginning.
Support for this construct is optional, but all processors must allow it to be present and at least
ignore it.

The encoding names used are those defined by IANA, which are the same as those used by XML. The IANA
character encoding identifier registry can be found at
[http://www.isi.edu/in-notes/iana/assignments/character-sets](http://www.isi.edu/in-notes/iana/assignments/character-sets).

Below is shown a simple example of an LTM file that uses the UTF-8 character encoding.

````ltm
@"utf-8"

[grove : person = "Geir Ove GrÃ¸nmo"]
````

(The name is of course Geir Ove Grønmo, encoded in UTF-8, but viewed as if it were ISO 8859-1.)

#### Processing and references ####

This section provides some clarifications for implementors in lieu of a proper specification based
on TMDM.

##### Topic IDs and merging #####

Any topic referred to by its ID in an LTM file, but never defined anywhere by an explicit occurrence
of the `topic` production with that topic, is automatically generated by the LTM processor. All
occurrences of the same topic ID are considered to be references to the same
topic.

When an instance of the `topic` production is found, and a topic with the same ID has already been
found, the two topic definitions are merged as follows:

*  The types of the resulting topic are considered to be the union of the types found in each
   definition.
*  The names of the resulting topic are considered to be the union of the names found in each
   definition.
*  The occurrences of the resulting topic are considered to be the union of the occurrences found in
   each definition.
*  The associations of the resulting topic are considered to be the union of the associations found in
   each definition.
*  The subject indicators of the resulting topic are considered to be the union of the subject
   indicators found in each definition.
*  If more than one subject locator for the topic is found, the one occurring last in the file is
   used.

If two topic definitions are found which have different topic IDs, but in which the same name occurs
in the same scope, no specific behaviour is guaranteed. Possible results are that the topics may be
merged, that they may remain distinct and that an error may be signalled. Topics with equal subject
locators or subject indicators but different IDs are merged.

##### Sort and display names #####

Note that although display names and sort names have a syntax that is different from the general
syntax for variant names they are still considered variant names with the 'sort' and 'display'
scopes from XTM 1.0.

##### The 'INCLUDE' directive #####

The `INCLUDE` directive requires special handling in the parser. What happens (during all LTM
processing, whether `INCLUDE` is used or not) in terms of the Topic Map Data Model (TMDM) is that
topic IDs turn into source locators of the form `uri-of-ltm-file#topic-id`. When a file is included
with `INCLUDE` all topics in the included file get *two* source locators, one with the URI of the
included file, and one with the URI of the file that includes. (This is what causes the merge by ID
across the files.)

##### Reification #####

The `reify-id` production works as follows in TMDM terms (where `id` is the id given in the
`reify-id` production):

*  If no topic with the `id` exists, one is created. If a topic exists, that is the topic used below.
*  A source locator of the form `uri-of-file#--reified--id` (replace 'id' with the reification `id`) is
   added to the topic map construct being reified.
*  A subject identifier of the form `uri-of-file#--reified--id` (replace 'id' with the reification
   `id`) is added to the reifying topic.

````ltm
#TOPICMAP ~tm
````

In the example above, a topic with the id `tm` would be created, and have the source locator
`file.ltm#tm` assigned to it. The topic map would have the source locator `file.ltm#--reified--tm`
assigned to it, and the same URI would be assigned to the created topic as a subject
identifier.

##### The 'PREFIX' directive #####

The `PREFIX` directive causes qualified names like `foo:bar` to be interpreted as references to the
topic with the expanded URI (the prefix URI + the part after the colon) as its subject indicator (if
the prefix declaration used '@') or locator (if '%' was used). Note that qualified names can also be
used in a topic definition as below:

````ltm
[xtm:superclass-subclass = "Superclass/subclass"]
````

This topic has no source locator (since it has no topic ID); instead the qualified name gives it a
subject indicator.


