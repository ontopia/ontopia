DB2TM
=====

User's Guide
------------

<p class="introduction">
This document introduces the DB2TM module and explains how to use it to convert relational data,
whether in CSV files or a relational database, into a topic map. It explains the XML configuration
file format, the command-line tool, and the public API of the converter.
</p>

<span class="version">Ontopia 5.2 2011-09-27</p>

### Introduction ###

The DB2TM module can be used to convert relational data into a topic map. It supports both CSV and
JDBC data sources, and uses an XML configuration file to define the mapping from the relational
source into the Topic Maps ontology you use. It can be accessed using either a Java API or a
command-line tool.

The basic approach of the DB2TM module is that it takes an existing topic map (possibly empty), a
configuration, and a set of relations (ie: tables), and converts the rows in the relations into data
in the topic map. The mapping from the relations to the topic map is described by the
configuration.

The configuration file contains a set of data sources (often just one), which can be either a
database or a directory containing CSV files, and a set of relation declarations. The relation
declarations define the mapping from each relation to the topic map ontology used. In addition, URI
prefixes similar to those of LTM and tolog can be defined at the top of the configuration file for
convenience.

### Tutorial ###

This section explains how to use DB2TM by walking through how to set up the conversion process for
an example conversion.

#### Basics ####

Let's say we have a file called `organizations.csv`, which contains the following:

````
ID;NAME;WEBSITE
1;Ontopia;http://www.ontopia.net
2;United Nations;http://www.un.org
3;Bouvet;http://www.bouvet.no
````

We can easily create a topic map of the data in this file with DB2TM, but to get a nice result in
Omnigator we need a seed topic map containing the ontology. Since the CSV file only contains topics
of a single type, with a name, and occurrences of a single type, the following LTM would work
fine:

````ltm
#PREFIX ex @"http://psi.example.org/"
[ex:organization = "Organization"]
[ex:homepage = "Homepage"]
````

The next thing we will need is an XML configuration file that describes the mapping from the CSV
file into our ontology. A configuration file that only defines our data source would look as
follows:

````xml
<db2tm name="demo">
  <sources>
    <csv path="./" ignoreFirstLines="1"/>
  </sources>
</db2tm>
````

This defines a CSV data source named 'files' for CSV files contained in the current directory
(that's what './' means), and where the first line of every file will be ignored. Note that the
`path` attribute is always evaluated relative to the location of the XML configuration file. The
next step is to define the mapping from the `organizations.csv` file to the ontology shown above.
However, to do that we need to add a prefix declaration at the top of the file, as
follows:

````xml
  <using prefix="ex" subject-identifier="http://psi.example.org/"/>
````

This one element says that the prefix 'ex' is a subject identifier prefix referring to topics whose
PSIs begin with `http://psi.example.org/`. With this in hand we can move on to the mapping of our
one relation, which is done as follows:

````xml
  <relation name="organizations.csv" columns="id name url">
    <topic type="ex:organization">
      <item-identifier>#org${id}</item-identifier>
      <topic-name>${name}</topic-name>
      <occurrence type="ex:homepage">${url}</occurrence>
    </topic>
  </relation>
````

This is the key part, so let's walk through this slowly. The `name` attribute on the `relation`
element gives the name of the CSV file. The `columns` attribute contains the names we will use for
the columns in the CSV file in order. The next line has the `topic` element, which means that every
row in the relation will map to a topic. The `type` attribute gives the type of the topics (in this
case: `organization`).

The `item-identifier` element is used to create an item identifier for the topic, and in this case
it's created from the first column in the file. (We do this to make it possible to refer to these
topics in [Associations](#associations).) The `topic-name` element creates a topic name from the
second column. The `occurrence` element creates an occurrence of type `ex:homepage` from the third
column.

There is one problem with the mapping above, however. It doesn't say that `ex:homepage` is an
external occurrence, and so the URL is just treated as a string. To solve this we need to declare
the datatype to be a URI. The easiest way to do this is to define a subject identifier prefix for
the XML Schema data types, like this:

````xml
  <using prefix="xsd" subject-identifier="http://www.w3.org/2001/XMLSchema#"/>
````

Once this is in place we can add `datatype="xsd:anyURI"` on the `occurrence` element, and DB2TM will
create external occurrences instead.

Now that we have assembled all the pieces, the next step is to run the conversion, and this is done
by issuing the following command on the command-line:

````
java net.ontopia.topicmaps.db2tm.Execute --tm=ontology.ltm --out=tm.ltm add organizations.xml
````

The result is the following LTM:

````ltm
[org1 : id1 = "Ontopia"]
   {org1, id2, "http://www.ontopia.net"}
[org2 : id1 = "United Nations"]
   {org2, id2, "http://www.un.org"}
[org3 : id1 = "Bouvet"]
   {org3, id2, "http://www.bouvet.no"}
````

The `id1` and `id2` references are found in the LTM because the LTM exporter doesn't have any IDs
for these topics, just the PSIs, and it always refers to topics using ID.

#### Associations ####

So far we've mapped only one relation, and to get an interesting topic map we really need to handle
more relations. The next one up is `people.csv`, which looks as follows:

````
ID;GIVEN;FAMILY;EMPLOYER;PHONE
1;Lars Marius;Garshol;1;+47 90215550
2;Geir Ove;Grønmo;1;
3;Kofi;Annan;2;
````

The third column here is a foreign key reference to the `organizations.csv` file. To handle this
relation we need to extend the LTM seed ontology as follows:

````ltm
[ex:person = "Person"]
[ex:employed-by = "Employed by"
                = "Employs" / ex:employer]
  [ex:employer = "Employer"]
  [ex:employee = "Employee"]
[ex:phone = "Phone"]
````

Having done this we are ready to extend the configuration file with a new relation as follows:

````xml
  <relation name="people.csv" columns="id given family employer phone">
    <topic id="employer">
      <item-identifier>#org${employer}</item-identifier>
    </topic>
    <topic type="ex:person">
      <item-identifier>#person${id}</item-identifier>
      <topic-name>${given} ${family}</topic-name>
      <occurrence type="ex:phone">${phone}</occurrence>
      <player atype="ex:employed-by" rtype="ex:employee">
        <other rtype="ex:employer" player="#employer"/>
      </player>
    </topic>
  </relation>
````

The first new thing to notice here is that we have two `topic` elements inside the `relation`
element. The first is used to create the topic playing the employer role in the employed-by
association with the person topic. It has an `id` so we can refer to it, and an `item-identifier`
element so we know its identity. (Note that the content of the `item-identifier` element is designed
to match up with that in the mapping for organizations.)

The second `topic` element is mostly straightforward. Notice how we combined two columns into a
single topic name. Also note how we don't need to specify a datatype for the phone occurrence type
since it's just a string. The `player` element is used to create an association from this topic (of
type `employed-by`, where the person topic plays the role `employee`). The `other` element specifies
the other role in the association (of type `employer`), played by the topic we defined earlier with
the id `employer`.

And that's it. The result is that we get the following extra LTM:

````ltm
[person1 : person = "Lars Marius Garshol"]
   {person1, phone, [[+47 90215550]]}
[person2 : person = "Geir Ove Grønmo"]
[person3 : person = "Kofi Annan"]

employed-by( org1 : employer, person1 : employee )
employed-by( org1 : employer, person2 : employee )
employed-by( org2 : employer, person3 : employee )
````

### Advanced topics ###

This section covers more advanced topics in DB2TM.

#### Other mapping functionality ####

Some functionality of the DB2TM mapping was not covered in [Tutorial](#tutorial), and so it will be
briefly described here. For a full definition of the syntax of the mapping file, see the [DB2TM
RELAX-NG schema](db2tm.rnc).

##### Scope support #####

Scope is supported through the `scope` attribute which can be used on the `topic-name`,
`occurrence`, `player`, and `association` (see [Mapping relations to
associations](#mapping-relations-to-associations)) elements. An example of using the `scope`
attribute might be:

````xml
  <relation name="nicknames.csv" columns="id nick">
    <topic>
      <item-identifier>#person${id}</item-identifier>
      <topic-name scope="ex:nick">${nick}</topic-name>
    </topic>
  </relation>
````

Here we use the `item-identifier` to match up with the topics from the `people.csv` file, and the
`scope` attribute as explained above. Note that the `scope` attribute supports multiple topic
references separated by whitespace.

##### Mapping relations to associations #####

In some cases a relation does not map to topics of a particular type, but instead maps to
associations of a particular type. An example of such a relation might be the `offices-in.csv` file,
which lists which countries the various organizations have offices in. The contents of this file are
given below:

````
ORGANIZATION;COUNTRY
1;578
1;826
2;840
2;756
3;578
````

To map this file into the topic map we would use the following `relation` mapping:

````xml
  <relation name="offices-in.csv" columns="organization country">
    <topic id="organization">
      <item-identifier>#org${organization}</item-identifier>
    </topic>

    <topic id="country">
      <subject-identifier
       >http://psi.oasis-open.org/iso/3166/#${country}</subject-identifier>
    </topic>

    <association type="ex:has-offices-in">
      <role type="ex:organization" player="#organization"/>
      <role type="ex:country" player="#country"/>
    </association>
  </relation>
````

Note that it's possible to use the same child elements inside `association` as inside `topic`. If
these elements occur a topic reifying the association will be created, and the topic characteristics
assigned to the reifying topic.

#### Virtual columns ####

Sometimes the values in the database are not exactly as one would like them to be in the topic map,
and for these cases DB2TM offers what we call "virtual columns". These are essentially new columns
created by mapping the values from an existing column either using a mapping table or a
function.

##### Mapping tables #####

The simplest way to map values is to use a mapping table, which is useful for handling special codes
and IDs which you do not want in your database. Let's say that the `organizations.csv` file
contained an extra column, like this:

````
ID;NAME;WEBSITE;TYPE
1;Ontopia;http://www.ontopia.net;C
2;United Nations;http://www.un.org;O
3;Bouvet;http://www.bouvet.no;C
````

The `TYPE` column here tells us what type of organization we are dealing with, and the choices are
`O` (meaning organization) and `C` (meaning company). However, these codes do not occur in the topic
map, where these topic types are identified with the PSIs `http://example.org/organization` and
`http://example.org/company`. So how to do the mapping? The solution is to use a mapping table that
translates the codes into the correct typing topics, as shown below.

````xml
<relation name="organizations.csv" columns="id name url type">
  <mapping-column column='type' name='typepsi'>
    <map from='C' to='company'/>
    <map from='O' to='organization'/>
  </mapping-column>

  <topic id='type'>
    <subject-identifier>http://example.org/${typepsi}</subject-identifier>
  </topic>

  <topic type="#type">
    <item-identifier>#org${id}</item-identifier>
    <topic-name>${name}</topic-name>
    <occurrence type="ex:homepage">${url}</occurrence>
  </topic>
</relation>
````

This creates a new virtual column with the name `typepsi`, which will contain `company` if the value
in `type` is `C` and `organization` if it is `O`. The mapping of the `type` topic then converts this
into the correct topic type, and the reference from the next `topic` element makes a topic of the
correct type.

##### Functional columns #####

Sometimes the mapping from the values you have to the values you want is more complicated, and in
these cases you can use a Java method to do the mapping. Let's imagine, for example, that the people
maintaining the database of organizations don't always bother to put in correct URLs, so that some
of the rows are incorrect, as shown in the example below.

````
ID;NAME;WEBSITE
1;Ontopia;http://www.ontopia.net
2;United Nations;http://www.un.org
3;Bouvet;http://www.bouvet.no
4;OfficeNet;www.officenet.no
````

In this case, a mapping table is not going to help, but we can easily implement the correction we
need in Java. All that's needed is something like this:

````text/x-java
package net.ontopia.utils;

// WARNING: this class does not actually exist in Ontopia!
public class ExampleUtils {

  public static String correctURI(String baduri) {
    if (!baduri.startsWith("http://"))
      return "http://" + baduri;
    else
      return baduri; // not so bad, after all
  }
  
}
````

We can now use this method (note that it is static) from the DB2TM configuration as follows:

````xml
<relation name="organizations.csv" columns="id name url">
  <function-column name='goodurl' 
     method='net.ontopia.utils.ExampleUtils.correctURI'>
    <param>${url}</param>
  </function-column>

  <topic id='type'>
    <subject-identifier>http://example.org/${typepsi}</subject-identifier>
  </topic>

  <topic type="#type">
    <item-identifier>#org${id}</item-identifier>
    <topic-name>${name}</topic-name>
    <occurrence type="ex:homepage">${goodurl}</occurrence>
  </topic>
</relation>
````

This will produce a topic map where the topic for OfficeNet will have the URI
`http://www.officenet.no`. Note that it is possible to pass static parameters to the methods by
simply giving the parameter values in the `param` element instead of a column
reference.

#### Supported data sources ####

DB2TM currently supports two kinds of data sources: CSV files and JDBC connections. This section
describes both in more detail.

##### CSV data sources #####

CSV data sources are configured with the `csv` element, which represents a *directory* containing a
set of CSV files. The attributes accepted by this element are listed in the table
below.

| Attribute | Meaning | Required? | 
|---|---|
| path | The path to the directory containing the CSV files. The path is relative to the location of the XML file. | Yes | 
| encoding | The character encoding of the CSV files. If not specified it defaults to the platform default. | No | 
| separator | The character separating entries in a line in a CSV file. Default is semicolon (;). | No | 
| quoting | The character used to quote text strings in the CSV files. Default is doublequote ("). | No | 
| ignoreFirstLines | The number of lines to ignore at the beginning of the file. Defaults to 0. | No | 

Note that quoting characters in text strings are escaped by repeating them. In other words, if you
use (") to quote your strings, and you want to put a " in a string, write it as "". Below is an
example:

````
40;"Foo";"This is a ""real"" example"
````

##### JDBC data sources #####

JDBC data sources are configured with the `jdbc` element, which represents a database containing a
set of tables. The attributes accepted by this element are listed in the table
below.

| Attribute | Meaning | Required? | 
|---|---|
| propfile | The path to the RDBMS properties file that tells DB2TM how to connect to the database. This property file uses the same set of properties as the Ontopia RDBMS backend does (except that it doesn't make use of all the properties that tune the backend cache etc). The path is relative to the location of the XML file. (If no file is found, DB2TM tries to load it from the classpath.) | Yes | 

Note that the JDBC source requires a number of things in order to work. The JDBC drivers you wish to
use have to be on the classpath, the database server has to be running, the username/password has to
be correct, etc.

#### Using the API ####

In addition to the command-line interface DB2TM also has an API that can be used to run conversions
and synchronizations in other contexts than on the command-line. This API is very simple and
consists of the single class `net.ontopia.topicmaps.db2tm.DB2TM`, which has two methods: `add` and
`sync`. The `add` method can run conversions given a string reference to a configuration file and a
topic map. The `sync` method can perform synchronizations given a string reference to a
configuration file and a topic map to synchronize against.

A command-line Java program that runs the same conversion that we saw in [Basics](#basics) could be
written as follows:

````text/x-java
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.db2tm.DB2TM;

public class Convert {

  public static void main(String argv[]) throws java.io.IOException {
    TopicMapIF topicmap = ImportExportUtils.getReader("ontology.ltm").read();
    DB2TM.add("organizations.xml", topicmap);
    ImportExportUtils.getWriter("tm.ltm").write(topicmap);
  }
}
````

Here is a simple command-line Java program that performs synchronization:

````text/x-java
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.db2tm.DB2TM;

public class Synchronize {

  public static void main(String argv[]) throws java.io.IOException {
    TopicMapIF topicmap = ImportExportUtils.getReader("ontology.ltm").read();
    DB2TM.sync("organizations.xml", topicmap);
    ImportExportUtils.getWriter("tm.ltm").write(topicmap);
  }
}
````

#### Using the command-line tool with RDBMS topic maps ####

So far, the examples have shown only running DB2TM against topic maps stored in files. In practice,
most topic maps will be stored using the RDBMS backend, and so we will here show how to use it with
such topic maps. When using the RDBMS backend, there are two pieces of information that the
command-line tool needs:

*  The RDBMS properties file, which tells DB2TM how to connect to the database.
*  The ID of the within the database topic map to update.

There is a special URI syntax that can be used in place of a file reference to provide the topic map
ID, and a system property which can be used to reference the RDBMS properties file. Using both would
look like this:

````
java -Xmx512M -Dnet.ontopia.topicmaps.impl.rdbms.PropertyFile=rdbms.props net.ontopia.topicmaps.db2tm.Execute --tm=x-ontopia:tm-rdbms:id sync mapping-file.xml
````

### Synchronization ###

So far, we have only discussed how to use DB2TM for conversion, but if the data source you are
converting from is going to continue to be updated, then this is not very interesting. However,
after the initial conversion DB2TM can also update your topic map so that it remains in sync with
the data source.

#### Basics ####

Let's say that we change the LTM topic map we produced in [Basics](#basics) to the following:

````ltm
[org1 : id1 = "Ontopia"]
   {org1, id2, "http://www.ontopia.net"}
located-in(ontopia : located, oslo : location)   
[org2 : id1 = "United Nations"]
   {org2, id2, "http://www.un.org"}
[org3 : id1 = "Bouvet"]
   {org3, id2, "http://www.bouvet.no"}
````

The change here is that we added an association for Ontopia. At the same time, we've changed the CSV
file as follows:

````
ID;NAME;WEBSITE
1;Ontopia AS;http://www.ontopia.net
3;Bouvet;http://www.bouvet.no
4;OfficeNet;http://www.officenet.no
````

Here we have deleted the United Nations, added OfficeNet, and changed Ontopia's name to end in "AS".
We now run DB2TM with this command:

````
java net.ontopia.topicmaps.db2tm.Execute --tm=tm.ltm --out=sync.ltm sync organizations.xml
````

The result is the LTM fragment below:

````ltm
[org1 : id1 = "Ontopia AS"]
   {org1, id2, [[http://www.ontopia.net]]}
located-in(org1 : located, oslo : location )   
[org3 : id1 = "Bouvet"]
   {org3, id2, [[http://www.bouvet.no]]}
[org4 : id1 = "OfficeNet"]
   {org4, id2, [[http://www.officenet.no]]}
````

As you can see, we have gotten all the changes in the CSV file into the topic map without losing any
of the changes we made in the LTM. This is because DB2TM only synchronizes the topics and
characteristics it has mappings for, and it leaves everything else alone. There is one problem,
however, which we'll discuss in the next section.

#### Changelog tables ####

A problem with the approach taken in the previous section is that it requires reading the entire
table when synchronizing. Of course, when there are only 3 rows, this isn't much of a problem, but
when there are, say, 50,000 rows, things are rather different. Rescanning the entire table gets
costly in this case, and so DB2TM provides an alternative approach, which is to use a changelog
table.

A changelog table is basically a table that contains an entry for every change made to the master
table. For DB2TM to be able to make use of the table it has to contain a primary key reference to
the changed row in the master table, and a column by which the changes can be ordered. It can
contain other information as well, but DB2TM will ignore it.

Let's assume that we want to repeat what we did in the previous section, but this time using a
changelog table. To do that we need to make a new CSV file which describes the changes. This file
might look as follows:

````
ID;CHANGE;ORDER
2;D;1
1;C;2
4;A;3
````

The first row here refers to the row with ID 2 from [Basics](#basics) (that is, to the United
Nations row), and the second column says it was deleted. The last column is there to tell us in what
order the changes happen. We are using numbers here, but timestamps would work just as well, as long
as we can sort on the values. The second row refers to Ontopia, and says the row was changed
somehow. (The name changed.) Finally, the third row says that a new row with ID 4 has been added.
This gives DB2TM the information it needs to do the updates correctly without reading the entire
table. (Note that the `CHANGE` column is ignored, since version 5.2.0.)

So what would we need to do to make use of this? The only thing we need to change is that we have to
add the following inside the `relation` element, at the very end:

````xml
<changelog table="organization-changes.csv"
           primary-key="ID"
           order-column="ORDER"/>
````

This element tells DB2TM where the changelog is, which column contains the primary key reference,
and what column to order the changes by.

Running the sync again would give the same result as in the previous section, except that changelog
tables are not supported for the CSV data source. We have used CSV to explain it here for
simplicity, but currently you need to use a database for this to work. (The rationale is that the
speedup for a CSV file is very limited, since one has to read the entire CSV file even if only a few
rows have changed.)

#### Handling a single changelog for multiple tables ####

Some systems use a single changelog table for all tables, which causes difficulties, because it
means that not all rows are relevant for each table. Further, it often means that primary key values
are encoded in a single column and need to be parsed.

Let's assume that we have a changelog table with the following columns:

table
:    Which table the change occurred in.

keys
:    The primary key, encoded as a single string value. The scheme used is
     `colname1=value|colname2=value|...`

timestamp
:    The time the change was made.

changetype
:    Describes the kind of change: 'I' for insert of new row, 'U' for update, and 'D' for delete. This is
     not needed by DB2TM.


To be able to set up a changelog for the organization table we need to fix both issues. This can be
done as follows:

````xml
<changelog table="organization-changes.csv"
           primary-key="PARSED_ID"
           order-column="TIMESTAMP"
           condition="table='organization'">
  <expression-column name="PARSED_ID">
    <!-- turns 'columnname=value' into 'value' --> 
    substring(KEYS, position('=', KEYS) + 1)
  </expression-column>
</changelog>
````

The `condition` attribute filters out changes to tables other than the one we're interested in. The
`expression-column` takes care of the parsing of the primary key values so that DB2TM can join the
changelog table with the data table.

#### Secondary relations ####

Let's say we had another table with information about our organizations, for example references to
external web sites that have articles about them. Each row in this table would map to an
`article-about` occurrence, and the table might look like this:

````
ID;URL
1;http://www.ligent.net/company.jsp?id=107&bundle=162
1;http://www.techquila.com/topicmaps/tmworld/11770.html
1;http://www.knowledge-synergy.com/partner/partner.html#ontopia
2;http://en.wikipedia.org/wiki/United_Nations
2;http://topics.nytimes.com/top/reference/timestopics/organizations/u/united_nations/index.html?inline=nyt-org
````

Doing a conversion from this would be straightforward, but on doing a sync we would run into
problems. There are no articles about Bouvet in the table, and so DB2TM would delete Bouvet from the
topic map, since the row for Bouvet is gone. Of course, this is wrong, but DB2TM does not know that
the main organization table is where organizations are stored, and not here. So we need to tell it,
and this is what the `primary` attribute is for. Using that, the mapping would look as
follows:

````xml
  <relation name="articles.csv" columns="id url">
    <topic primary="false">
      <item-identifier>#org${id}</item-identifier>
      <occurrence type="ex:article-about">${url}</occurrence>
    </topic>
  </relation>
````

#### Incomplete mappings ####

In some cases the table you are mapping may not contain all topics of the type they map to, and in
this case the rescanning strategy can get it wrong. Let's say that, for whatever reason, in addition
to the organizations coming from the CSV file we need to add some manually. If we did this, the
first sync would delete all the manually added organizations, because they are not found in the CSV
file, and so the rescan assumes they have been deleted.

To solve this, you need to tell DB2TM which topics in the topic map were created from the CSV file.
One way to do this is to include a unary association in the mapping that says the organization comes
from the database. This would mean that every topic created from the CSV file would look like
this:

````ltm
[ontopia : organization = "Ontopia"]
stored-in-db(ontopia : stored)
````

This is enough that we could distinguish these topics from the manually added ones, if we write the
mapping like this:

````xml
    <topic type="ex:organization">
      <extent query="direct-instance-of($T, ex:organization),
                     ex:stored-in-db($T : ex:stored)?"/>
      <item-identifier>#org${id}</item-identifier>
      <topic-name>${name}</topic-name>
      <occurrence type="ex:homepage">${url}</occurrence>

      <!-- static unary marker association -->
      <player atype="ex:stored-in-db" rtype="ex:stored"/>      
    </topic>
````

The `extent` element defines a query that produces all topics in the topic map created from this
table mapping. This enables DB2TM to recognize manually created organization topics, and not delete
them.

#### Synchronization in web applications ####

In general it is not recommended to use the command-line tool to synchronize topic maps while a web
application is running, since Ontopia's object cache inside the web application will not notice
changes made by the command-line tool. (This will change once clustering support arrives.) Instead,
the synchronization should be run from inside the web server. There are three ways to do
this:

*  Set up the synchronization servlet to run the synchronization at regular intervals. The servlet is
   documented in the javadoc for the DB2TM package.
*  Use the Ontopoly plug-in to run the synchronization manually. This plug-in is not yet part of
   Ontopia, but is available on request.
*  Write your own code to run the synchronization using the DB2TM API. For more information, see [Using
   the API](#using-the-api).

### Logging ###

DB2TM can produce a lot of logging information if you tell it to do so. Set the
`net.ontopia.topicmaps.db2tm` logging category to the `DEBUG` logging level. Most of the logging
messages is produced by the `net.ontopia.topicmaps.db2tm.Processor` class. A lot of logging is made
at this level can be vast because of the huge number of modications typically made to the topic map
during processing.

The information logged is a little terse and can be a little hard to interpret. The table below is
an attempt at explaining what each of the messages mean.

#### Relation operations ####

| Log message | Description | 
|---|---|---|
| Adding tuples from data source &lt;datasource&gt; | Relations and their tuples are being read from the given data source. | 
| ignoring relation: &lt;relation&gt; | Ignored relation as there was no &lt;relation&gt; definition for it. | 
| adding relation: &lt;relation&gt; | Reading tuples from the given relation and adding them according to its &lt;relation&gt; definition. | 
| removing relation: &lt;relation&gt; | Reading tuples from the given relation and removing them according to its &lt;relation&gt; definition. | 
| synchronizing relation: &lt;relation&gt; type: &lt;synchronization-type&gt; | Reading tuples from the given relation and synchronizing them according to its &lt;relation&gt; definition. The synchronization type can be either `2` (rescan) or `4` (changelog). | 
| &lt;number-of-tuples&gt; tuples, &lt;elapsed-time&gt; ms. | Processed the given number of tuples in the given number of milliseconds. | 
| done: &lt;number-of-tuples&gt;, &lt;elapsed-time&gt; ms. | Processed the given number of tuples from all relations in the data source in the given number of milliseconds. | 

#### Tuple operations ####

| Log message | Description | 
|---|---|---|
| a(1&#124;2&#124;3) | Adds a table row containing three values into the topic map. The tuple will be processed according to the rules of the current &lt;relation&gt; definition. | 
| r(1&#124;2&#124;3) | Removes a table row containing three values into the topic map. The tuple will be processed according to the rules of the current &lt;relation&gt; definition. | 
| u(1&#124;2&#124;3) | Updating a table row containing three values against the topic map. The tuple will be processed according to the rules of the current &lt;relation&gt; definition. | 

#### Entity and characteristics operations ####

| Log message | Description | 
|---|---|---|
| -T &lt;topic&gt; | Removing a topic. | 
| &gt;T &lt;topic&gt; | Removing characteristics defined in this &lt;relation&gt; definition from a non-primary topic entity. This will leave any other characteristics alone. | 
| +A &lt;association&gt; &lt;association-type&gt; | Added the association of the given type. | 
| =A &lt;association&gt; | The association already existed, so instead of creating a new one this one was reused. | 
| -A &lt;association&gt; &lt;association-type&gt; | Removing an association. | 
| +P &lt;association&gt; &lt;association-type&gt; | Added the association of the given type (via &lt;player&gt; defintion). | 
| =P &lt;association&gt; | The association already existed, so instead of creating a new one this one was reused (via &lt;player&gt; defintion). | 
| -P &lt;association&gt; : &lt;association-type&gt; | Removing an association (via &lt;player&gt; defintion). | 
| &gt;A &lt;association&gt; | Association is not primary entity, so we can't remove it here. | 
| +R &lt;role-player&gt; : &lt;role-type&gt; | Added an association role with the given role player and role type. | 
| =R &lt;role-player&gt; : &lt;role-type&gt; | The association role already existed, so instead of creating a new one this one was reused. | 
| -R &lt;role-player&gt; : &lt;role-type&gt; | Removed an association role with the given role player and role type. The association role belongs to the association logged a little earlier. | 
| ?R &lt;role-player&gt; : &lt;role-type&gt; | Could not add role because role player retrieved from tuple was null. | 
| -A-reified &lt;topic&gt; -&gt; &lt;association&gt; &lt;association-type&gt; | Removed the given association and its reifier topic. | 
| +N &lt;topic&gt; &lt;topic-name&gt; | Added the topic name. | 
| =N &lt;topic&gt; : &lt;topic-name&gt; | The topic name already existed, so instead of creating a new one this one was reused. | 
| -N &lt;topic&gt; : &lt;topic-name&gt; | Removing a topic name. | 
| +O &lt;topic&gt; &lt;occurrence&gt; | Added the occurrence. | 
| =O &lt;topic&gt; : &lt;occurrence&gt; | The occurrence already existed, so instead of creating a new one this one was reused. | 
| -O &lt;topic&gt; : &lt;occurrence&gt; | Removing an occurrence. | 

### Frequently Asked Questions (FAQ) ###

#### How do I add a new column to an existing relation? ####

Lets say that we have a table called `organizations`, with the mapping below, and that we have
already added the contents of this relation into the topic map.

````xml
  <relation name="organizations" columns="id name url">
    <topic type="ex:organization">
      <subject-identifier>http://example.org/organization/${id}</subject-identifier>
      <topic-name>${name}</topic-name>
      <occurrence type="ex:homepage">${url}</occurrence>
    </topic>
  </relation>
````

This means that all the data in the relation have already been mapped to topics and characteristics
in the topic map. The problem is now that we cannot just add a new column to the mapping as DB2TM
would not be able to detect that a new column had been added. DB2TM will not detect the new column
and the data in the column will not be added to the topic map before the individual rows in gets
updated and the row contents is reread through synchronization.

Fortunately there is a way to get DB2TM to read the content of the new column. To do this we need to
create a new temporary mapping file with just this single relation. We'll use this new mapping file
just for the purpose of adding the characteristic(s) mapped to the new column.

Lets say that the `organizations` table has a new column called `phone` and that we want to map this
column to an internal occurrence of type `ex:phone`. The `organizations` table has one row per
organization, so there is a `topic` element mapping these organizations to topics in the topic map.
The temporary mapping file also needs this element, but this time with only two fields: one field to
identify the topic and one field to add the new characteristic. In our case the relation mapping
should look like this:

````xml
  <relation name="organizations" columns="id name url phone">
    <topic>
      <subject-identifier>http://example.org/organization/${id}</subject-identifier>
      <occurrence type="ex:phone">${phone}</occurrence>
    </topic>
  </relation>
````

Note that the new `phone` is now in the list of table columns. We now also have a
`subject-identifier` field so that we can look up the existing topic, and finally the `occurrence`
field to add the new internal occurrence.

The prefix declarations and the data sources can be the same as in the original mapping file, so
just copy and paste those from the original mapping file.

> **Warning**
> Before we proceed make sure that you shut down your application so that cache inconsistency issues
> do not arise.

We can now have DB2TM add the contents of the new column to the topic map by running the following
command.

````
  java net.ontopia.topicmaps.db2tm.Execute --relations=organizations add temporary.xml
````

The organizations in the topic map should now have their respective phone numbers added. The next
thing we need to do is update the original mapping file. Add the new `phone` column to the list of
table columns and the new `occurrence` field to the relation mapping.

````xml
  <relation name="organizations" columns="id name url phone">
    <topic type="ex:organization">
      <subject-identifier>http://example.org/organization/${id}</subject-identifier>
      <topic-name>${name}</topic-name>
      <occurrence type="ex:homepage">${url}</occurrence>
      <occurrence type="ex:phone">${phone}</occurrence>
    </topic>
  </relation>
````

We are now done with the temporary mapping file and it can be discarded. The application can now be
restarted.

#### How to delete objects that were added to the topic map by DB2TM? ####

The `net.ontopia.topicmaps.cmdlineutils.TologDelete` command-line utility is very useful for this
purpose. Just hand it a tolog query and it will delete the objects returned by the
query.

Alternatively, you can use tolog updates in the query plug-in in Omnigator, where you just run a
DELETE statement to remove whatever you want to remove.

#### How do I remove data in the topic map that originates from an old column? ####

Use the tolog delete utility described above.

#### Oops, I made a mistake in the DB2TM mapping. How do I repair the topic map? ####

Doing a `rescan` or `add` on the relation together with the tolog delete utility mentioned above
will allow you to repair most mistakes. Note that you can override the synchronization type without
modifying the mapping file by passing the `--force-rescan` option to the
`net.ontopia.topicmaps.db2tm.Execute` command line utility.


