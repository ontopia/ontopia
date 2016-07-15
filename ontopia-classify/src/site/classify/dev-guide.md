The Ontopia Automatic Classifier
================================

A Developer's Guide
-------------------

<p class="introduction">
This Developer's Guide is an introduction to the automatic classification in Ontopia, and shows how
to use it to classify content into a topic map.
</p>

<span class="version">5.1 2010-06-09</p>

### Introduction ###

The automatic classification module can be used to process content to either classify it
automatically, or give suggestions for classification to a user who can then fine-tune the
suggestions into the final classification.

Given a file the module does the following:

*  detect the format of the file,
*  extract the textual content from the file,
*  detect the language of the text,
*  process the text to find out what it is about.

The module returns a set of terms, each one of which has a score between 0 and 1 expressing how
confident the module is that the content is about this term. The module does stemming of terms found
in the content, based on the detected language, so that different variants of a single term, like
"widget" and "widgets" are treated as a single term. The API provides mechanisms to access all
variants of a term.

It's also possible to provide a topic map to the classifier, and if so the classifier will use
information in this topic map to provide better classification results. For example, terms which
occur in the topic map will be ranked higher than terms which do not. The information is also used
to help the classifier detect compound terms (like "Topic Maps" or "New York").

As an example, below is the output of running the classification on this developer's guide:

````

1: classifier 1.0, 10
2: Ontopia 0.9398362722937127, 2
3: automatic classification 0.6733045538011962, 2
4: topic map 0.32963578857473336, 7
5: Developer Guide 0.31782661003640583, 2
6: widget 0.31465856836714945, 2
7: fine-tune 0.2391013657364179, 1
8: single term 0.2283465338494093, 2
9: API 0.20243323677418937, 1
10: OOXML 0.19983477366712343, 2
11: XML 0.15688292149123131, 1
12: HTML 0.15535359913968713, 1
13: PDF 0.1549748891676282, 1
14: Powerpoint 0.15236326757637844, 1
15: ppt 0.15162942789649314, 1
16: WordProcessingML 0.15053865122008855, 1
17: docx 0.14981801021049407, 1
18: PresentationML 0.14874667734265895, 1
19: pptx 0.14803876861331663, 1
20: module 0.13035138155842613, 5
21: optionally 0.12971709388058605, 1
22: Microsoft 0.12555221252942206, 2
23: java 0.11704152864641565, 1
24: process content 0.11542729078259414, 2
25: URI 0.1055602118063965, 1
26: command-line 0.10543579259361881, 1
27: classification 0.10537143393837285, 8
28: TermDatabase 0.104641226752405, 1
29: textual 0.10399562569001171, 2
30: suggestions 0.10272639505645716, 3
Total: 186 terms.

````

The module is still fairly immature, and so it's hard to predict how well the module will perform on
any given collection of content. In general, however, the longer the text and the more focused on
particular concepts it is, the better the module will perform. It also tends to be confused by
mixed-language content, and by embedded code and markup. These last two problems can be handled, but
not through the PUBLIC API. For help on this sort of issue, please contact the mailing
list.

The following formats are supported:

*  plain text,
*  plain XML (using any schema),
*  HTML,
*  PDF,
*  Microsoft Word format (.doc binary),
*  Microsoft Powerpoint format (.ppt binary),
*  OOXML WordProcessingML format (.docx), and
*  OOXML PresentationML format (.pptx).

At the moment, the languages supported are Norwegian and English only, but it is possible to plug in
support for more languages. If you are interested in doing this, please contact the mailing
list.

### Running the classifier ###

The simplest way to run the classifier is to use `Chew`, the command-line tool. This takes a content
file and, optionally, a topic map as input, and provides a textual dump of the classification
results (as shown above). The main benefit of this is that it makes it easy to try out the
classifier and see how it works on your content.

To try `Chew` run the command `java net.ontopia.topicmaps.classify.Chew` and follow the instructions
you get.

To really make use of the classifier you have to write your own code to process content and update
the topic map accordingly. To do that, use the `net.ontopia.topicmaps.classify.SimpleClassifier`
class. This will let you provide a reference to a file or URI, and will return a `TermDatabase` with
the classification results. From this you can update the topic map as desired, or even provide
suggestions to users.

For more details, please consult the javadoc.

We are well aware that this limited API probably provides less functionality than users may need,
but are awaiting further user feedback before we provide a more detailed API. Feedback to the
mailing list is very much welcome.


