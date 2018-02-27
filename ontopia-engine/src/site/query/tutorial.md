tolog
=====

Language tutorial
-----------------

<p class="introduction">
This document is a tutorial introduction to the tolog topic map query language. It explains how to
use all the features of the language, as defined in version 1.2.
</p>

<span class="version">Ontopia 5.1 2010-10-18</p>

### Introduction ###

tolog is a language for querying and updating topic maps, inspired by Datalog (a subset of Prolog)
and SQL. Using tolog one can query a topic map in much the same way as a relational database can be
queried with SQL. It is possible to ask for all topics of a particular types, the names of all
topics of a particular type in a particular scope, for all topics used as association role types,
for all associations with more than two roles, and so on.

This tutorial will walk the reader through the use of tolog step by step, and by the end of it all
of tolog will have been covered.

#### Predicates and variables ####

tolog is a logic-based query language, which means that the basic operation is for the user to ask
tolog in which cases a certain assertion holds true, and tolog will then respond with all the sets
of values that make the assertion true.

Assertions in tolog consist of *predicates*, which are relationships between sets of values. A
predicate can be thought of as a table of all the sets of values that make it true, and querying is
done by matching the query against the table, returning all sets of values that
match.

One tolog predicate is the one known as `instance-of`, which is used to query the topic
instance-topic type relationship. One query using this predicate might be:

````tolog
instance-of($TOPIC, $TYPE)?
````

In this query there is only one predicate, and that has variables being passed as both of its
parameters. This means that we want *all* combinations of values for these variables that make the
assertion true. That is, we want all combinations of (topic, topic type) that are found in the topic
map. If we ran this query against `opera.ltm` we might get a result like the
following.

| TOPIC | TYPE | 
|---|---|
| RAI | organization | 
| RAI | TV company | 
| Teatro Nuovo | organization | 
| Casinò di San Remo | organization | 
| Imperial Opera | organization | 
| Teatro La Pariola | organization | 
| ... | ... | 

This shows all topics that have a type, and for each topic, all of its types (which is why RAI
appears twice; it is an instance of two types). The actual table of results is several hundred lines
long.

Now, let's say we want to find all instances of the type `theatre`. Recall that the previous query
was as shown below.

````tolog
instance-of($TOPIC, $TYPE)?
````

This gave us all (topic, type) combinations because we had variables as both parameters. In this
case we don't want all possible types, we just want the `theatre`, so we replace the variable by
that particular type. This gives us the query below.

````tolog
instance-of($TOPIC, theatre)?
````

Now we are asking for all values of `TOPIC` that would make the above true; that is, we are asking
for all instances of `theatre`.

| TOPIC | 
|---|
| Teatro Massimo | 
| Teatro Bellini | 
| Teatro San Carlo | 
| Teatro La Pariola | 
| Académie Royale de Musique | 
| Teatro Pagliano | 
| ... | 

Similarly, should we want to know all types of which a topic is an instance we can replace the
`TOPIC` variable with a topic reference, and put in a variable as the second parameter, as shown
below.

````tolog
instance-of(teatro-massimo, $TYPE)?
````

When run, this query gives the following results.

| TYPE | 
|---|
| Theatre | 
| Organization | 

We get 'Theatre', as expected, but also 'Organization', which may be somewhat surprising. In the
topic map the only type given for Teatro Massimo is `theatre`, so what is `organization` doing here?
The answer is that `organization` is defined as a supertype of `theatre` in the topic map. This
means that any instance of `theatre` is also an instance of `organization`. tolog knows this and
makes use of the information to make Teatro Massimo an instance of both types.

You should now be familiar with what a predicate is and what a variables. From this point on we'll
look at how to combine predicates in more complex ways, more types of predicates, and also some ways
of processing the results of query.

#### Dynamic association predicates ####

As we said above, a predicate represents a relationship, and because of this tolog allows topic map
associations to be treated as predicates. In this case the name of the predicate is the name of the
association type, and the values are the topics playing roles in associations of this type. Given
this we might expect to be able to write the following query to find all operas composed by
Puccini.

````tolog
composed-by(puccini, $OPERA)?
````

However, this results in an error, because the tolog engine is not able to work out which
association role is played by `puccini` and which by the `OPERA`. This must be given explicitly in
the query as shown below.

````tolog
composed-by(puccini : composer, $OPERA : opera)?
````

Here it is made explicit that we want to find out when `puccini` plays the role `composer` and when
the `OPERA` plays the role `opera`. This syntax is *only* used for association predicates, not for
any other type of predicate. The result is as shown below.

| OPERA | 
|---|
| Le Villi | 
| Madame Butterfly | 
| Tosca | 
| Turandot | 
| Manon Lescaut | 
| La Bohème | 
| ... | 

We can play around with the variables here in the same way as we did for the `instance-of`
predicate, and get all combinations of opera and composer, and all composers for a specific opera
(say `tosca`). This is done in exactly the same way, so we won't explain how here, but leave it as
an exercise for the reader. See [The tolog plug-in](#the-tolog-plug-in) to learn how to run tolog
queries in the Omnigator.

#### AND ####

If we want to find all cities located in Italy we know that this is easy: we can just query the
`located-in` association type to find the city topics that have this association with the Italy
topic. Similarly, if we want to find everyone born in a particular just query the `born-in`
association type.

But what if we want to find everyone born in a city located in Italy? What we want in this case is
something like what is shown below.

````tolog
born-in($PERSON : person, $PLACE : place) 
AND 
located-in($CITY : containee, italy : container)?
````

This query is quite close to the correct one, but there are two problems with it. First of all, the
way to chain predicates together with an AND condition is simply to put a comma between them. (You
can say that AND is the default boolean operator in tolog.) The second problem is that we want to
make sure that the `CITY` located in Italy is the same as the `PLACE` where the person is born. If
we use the same variable in both places tolog will take this to mean that the same values have to be
used both places. So what we want is the query below.

````tolog
born-in($PERSON : person, $CITY : place),
located-in($CITY : containee, italy : container)?
````

When this query is run the first predicate will produce a table of all (person, city) combinations
where the person is born in the city. This is then passed to the second predicate, which takes out
all rows where the city is not located in Italy <span class="footnote">In practice the tolog query
optimizer will start with the second predicate and find all cities in Italy. It will then find all
the people born in those cities. It will do this because the other way around will find lots of
(person, city) combinations outside Italy, which will be wasted because they have to be thrown away
afterwards. The result will of course be exactly the same. It may be useful to be aware, however,
that tolog will not necessarily run the query exactly as given, but may transform it to a faster,
but equivalent, query before running it.</span>.

We can chain together as many predicates as we want in order to express our query; there's nothing
unusual in having 4-5 predicates chained together to form a query.

#### Projection ####

In the above example we have the problem that the query returns both the people and the city, while
we are only interested in the people. This can be solved by using projection, as
follows:

````tolog
select $PERSON from
  born-in($PERSON : person, $CITY : place),
  located-in($CITY : containee, italy : container)?
````

This tells the query engine that we're only interested in the `PERSON` variable, and so it will
produce a query result that only consists of bindings of the `PERSON` variable. In this case, this
is not so important, as we could just as well have just ignored the bindings to the `CITY` variable.
In some cases, however, ignoring one variable means reducing the size of the query result (and
avoiding duplicates), and sometimes reducing the query result size quite dramatically. One example
is given below.

````tolog
select $B from
  instance-of($A, $B)?
````

This query returns all the topic types used in the topic map, instead of all class-instance pairs.
The difference in the query result size (not to mention performance) can be enormous, which is what
makes this useful.

#### Counting ####

Quite often, one uses queries to produce statistics about the data contained in a topic map. If one
wants to find out which Italian opera composer was the most prolific, for example, one will want to
count the number of query results. tolog supports this, as the query below
shows.

````tolog
select $A, count($B) from
  composed-by($A : composer, $B : opera)?
````

This query produces the full `A, B` table, then replaces all rows that have the same value in the
`A` column by the number of such rows. The result is to count the number of `B` matches per `A`
match.

#### Sorting ####

While the query above does return the number of operas composed by each composer, it does so in an
order that is effectively random. What we really want is to see the composers ordered by decreasing
number of composed operas. This can be achieved by writing the query as shown
below.

````tolog
  select $A, count($B) from
    composed-by($A : composer, $B : opera)
  order by $B desc?
````

If the `desc` keyword is removed, the query results will be ordered by ascending opera count instead
of descending count. The 'order by' clause can contain any number of variables, separated by
commas.

The `asc` keyword specifies that columns be ordered in ascending order. This is also the default
order.

### Advanced features ###

What has been presented so far are only the simplest parts of tolog; what one could call "basic
tolog". tolog is much more powerful than this, however, and that additional power is explored in
this section.

#### Dynamic occurrence predicates ####

So far we have only looked at how to query on associations, but occurrences also contain useful
information, and in many cases we want to retrieve or query on this information. This can be done in
a manner very similar to how associations are queried: the occurrence type is used as the predicate.
However, occurrences have a much simpler structure, so for these we only need two positional
parameters: first the topic, then the occurrence value.

Finding the date of birth of every composer can be done as follows.

````tolog
instance-of($COMPOSER, composer), 
date-of-birth($COMPOSER, $DATE)?
````

This query gives the following result.

| COMPOSER | DATE | 
|---|
| Verdi, Giuseppe | 1813 (10 Oct) | 
| Ponchielli, Amilcar | 1834 (31 Aug) | 
| Cilea, Francisco | 1866 (23 Jul) | 
| Boïto, Arrigo | 1842 (24 Feb) | 
| Catalani, Alfredo | 1854 (19 Jun) | 
| Puccini, Giacomo | 1858 (22 Dec) | 
| ... | ... | 

As with association predicates it is also possible to find the date of birth of a specific composer,
or to find all composers born on a particular date. To do the latter we write:

````tolog
date-of-birth($PERSON, "1867 (24 Mar)")?
````

This will find all topics which have this particular value for this particular occurrence type
(though of course only people have dates of birth in this topic map), regardless of whether they are
composers or not. As it turns out, only Guido Menasci has this value, and he's a
`librettist`.

Note that locators are treated the same way as ordinary strings, which means that to find all topics
whose home pages are `http://www.puccini.it`, you can use the following query.

````tolog
homepage($TOPIC, "http://www.puccini.it")?
````

This finds all topics with a `homepage` occurrence whose locator is the URI `http://www.puccini.it`.
This turns out to produce the results shown below.

| TOPIC | 
|---|
| Centro studi Giacomo Puccini | 

#### OR ####

Quite often one may want to include information that matches either one condition OR another
condition. If, for example, we want to find all operas which had their premiere in a particular city
there are two ways to do that. Usually we know which theatre the opera had its premiere in, and in
these cases the premiere is connected to that theatre, and the theatre is connected with the city.
In some cases, however, all we know is the city, and in these cases the premiere is directly
connected with the city. Therefore, if we want to find all operas which premiered in Milano<span
class="footnote">The traditional English perversion of this is 'Milan'.</span>, we have to do as
shown below.

````tolog
select $OPERA from
  { premiere($OPERA : opera, milano : place) | 
    premiere($OPERA : opera, $THEATRE : place), 
    located-in($THEATRE : containee, milano : container) }?
````

Here matches will be accepted so long as they satisfy *one* of the branches inside the curly braces,
where the branches are separated by `|` characters. Other predicates can be used in front of and
after the curly braces, and the braces can be nested arbitrarily deep. The result is shown
below.

| OPERA | 
|---|
| Il sindaco babbeo | 
| Melenis | 
| La Falce | 
| I Cavalieri di Ekebù | 
| Turandot | 
| Marcella | 
| ... | 

This capability is essentially the same as the boolean operator `OR` in other languages, as it
allows tolog to return results where the criteria are true if `{ A | B }`.

##### Non-failing clauses #####

A closely related function is what is known as *non-failing clauses*; that is, a clause that will
produce a value for a variable if it can, but if it cannot still won't cause the query to fail. This
is useful when selecting optional values which are intended to be used for display
purposes.

One example might be if we want to all operas and for each the theatre in which it had its premiere.
If we don't know where it had its premiere we still want to show the opera, but we'll leave out the
theatre. This is easily done as follows, by including the optional part of the query in curly
braces, without any alternative branches.

````tolog
instance-of($OPERA, opera),
{ premiere($OPERA : opera, $THEATRE : place) }?
````

As you'll see if you try it, this query also returns matches where `THEATRE` is bound to a city,
since nothing in the query says that it has to be bound to a theatre (tolog completely ignores the
meaning of variable names), and operas also have this association with cities. So we have to add a
condition that the `THEATRE` actually be an instance of the type `theatre`, as shown
below.

````tolog
instance-of($OPERA, opera),
{ premiere($OPERA : opera, $THEATRE : place), 
  instance-of($THEATRE, theatre) }?
````

This query gives the desired result, which is shown below.

| THEATRE | OPERA | 
|---|
| Teatro Argentina | I due foscari | 
| Teatro Sociale di Lecco | Il parlatore eterno | 
| Teatro La Fenice | Attila | 
| Teatro alla Scala | Madame Butterfly | 
| null | Marina | 
| null | Il sindaco babbeo | 
| ... | ... | 

#### Using negation ####

tolog supports negation, even though negation in logical querying is not at all straightforward. To
take an example imagine running the query below.

````tolog
not(born-in(milano : place, $A : person))?
````

What is this query going to return? All topics that have no born-in association with Milano? All
person topics? An infinite number of topics, representing everything that wasn't born in Milano?
What about the persons which have no born-in association yet, but which were actually born in
Milano? And so on.

The approach taken in tolog is that the `not` operator is used as a filter. You must specify a start
set, and the query processor will then eliminate the members of the start set which match the
condition specified in the `not` clause. The example below shows the people in the topic map that
were not born in Italy.

````tolog
instance-of($PERSON, person),
not(born-in($PLACE : place, $PERSON : person),
    located-in($PLACE : containee, italy : container))?
````

This gives the results shown below.

| PERSON | 
|---|
| Guglielmo Ratcliff | 
| Manzoni, Alessandro | 
| Comtessa di Coigny | 
| Sardou, Victorien | 
| ... | 

One thing that stands out here is that Alessandro Manzoni actually *is* Italian. So why was he
included? Well, this query is subtly different from asking for the composers born somewhere not in
Italy. This query will also find composers which have no `born-in` association, or who are born
somewhere that has no `located-in` association. It turns out that Alessandro Manzoni has no
`born-in` association, and so he is "not born in Italy". To eliminate this type of match we would
have had to pick out the country where the person is born and use `/=` to make sure that it was not
Italy. On the other hand, that would have left out Victorien Sardou, who is a person (with no
`born-in` association) and not Italian. (So why do we say all this? Simply to emphasise that `not`
is subtly different from true negation. It might not mean what you think it
means.)

Another thing to note is that although this query uses the `PLACE` variable that variable is not
actually bound in the query. That is because every `COMPOSER` that creates a match for `PLACE` is
eliminated, leaving only those which don't. So while the query result will contain a `PLACE` column,
that column will be empty.

#### Comparison predicates ####

tolog also supports the comparison operators familiar from many other languages. We have already
seen `/=`, but there are many more. For example, there is also `=`, which means the opposite of what
`/=` does. That is, it is true when the values on both sides are equal. This means that we can do a
query to find operas which premiered on a particular day, as in:

````tolog
premiere-date($OPERA, $DATE),
$DATE = "1870 (22 Feb)"?
````

This will give us the (single) opera premiered on this date, according to the Opera topic map. It
should be noted that in most cases the `=` comparator is not necessary, since if a variable is equal
to another variable or a literal we can just replace all occurrences of the variable with what it is
supposed to be equal to. This gives us simpler queries, like the one below.

````tolog
premiere-date($OPERA, "1870 (22 Feb)")?
````

However, this comparator *does* have its uses, such as when defining an inference rule (see
following section) that will connect a topic to all topics matching some condition as well as the
topic itself. To put it another way: it's useful to know that this predicate exists, but be aware
that you rarely need it.

There are also four other comparison predicates (`<`, `>`, `<=`, `>=`) which only compare strings,
and, at the moment, compare strings as strings. This makes it possible to do a query to find all
operas premiered in the 19th century, as follows:

````tolog
premiere-date($OPERA, $DATE),
$DATE < "1900"?
````

This will do a string comparison on the dates, and filter out everything premiered in the 20th
century. Note that these four predicates do not compare anything other than strings (such as
topics), and that they require both values they are comparing to be bound.

#### Inference rules ####

In many cases there are implicit relationships in the topic map not stated as associations, but
which can be deduced from more basic relationships that are given explicitly as associations.
Inference rules provide a way to capture these implicit relationships through the declaration of a
simple rule. The rule can then be used throughout an application to simplify
queries.

We can say that if an opera composer was a pupil of another composer, or if the composer wrote
operas based on the work of another person, that other person or composer influenced the composer.
We can of course query for this, but if we want to use the influenced-by relationship in several
larger queries this quickly gets awkward. Instead, we can capture the relationship in an inference
rule, as shown below.

````tolog
influenced-by($A, $B) :- {
  pupil-of($A : pupil, $B : teacher) |
  composed-by($OPERA : opera, $A : composer),
  based-on($OPERA : result, $WORK : source),
  written-by($WORK : work, $B : writer)
}.
````

This rule has now created a new predicate called `influenced-by`, which can be used in queries.
Let's say that we want to find every case where an Italian opera composer was influenced by someone
who was not himself Italian. We can now do this as shown below.

````tolog
instance-of($COMPOSER, composer),
influenced-by($COMPOSER, $INFLUENCE),
born-in($INFLUENCE : person, $PLACE : place),
not(located-in($PLACE : containee, italy : container))?
````

We skip verifying that the composer was born in Italy, since all composers in this topic map are
born in Italy. This gives the result shown below.

| COMPOSER | INFLUENCE | 
|---|
| Verdi, Giuseppe | Méry, Joseph | 
| Ponchielli, Amilcar | Scribe, Eugène | 
| Cilea, Francesco | Daudet, Alphonse | 
| Cilea, Francesco | Scribe, Eugène | 
| Cilea, Francesco | Mélésville | 

Inference rules can also use other influence rules, which means that one can build large reasoning
structures by layering the inference rules on top of each other. Inference rules can also use
themselves, which makes it possible to define recursive inference rules, for example for traversing
hierarchical association structures.

Note that tolog has the same comment syntax as LTM (that is, comments begin with `/*` and end with
`*/`). This can be used for example to document inference rules, as shown
below.

````tolog

/* connects opera composers (A) with the people who influenced them
   (B), either by being their teachers or by writing works on which
   the composer based operas */

influenced-by($A, $B) :- {
  pupil-of($A : pupil, $B : teacher) |
  composed-by($OPERA : opera, $A : composer),
  based-on($OPERA : result, $WORK : source),
  written-by($WORK : work, $B : writer)
}.
````

In the example above it may look as though the bodies of inference rules should be wrapped in curly
braces, like blocks in Java or C, but the curlies are there because the entire rule is one big OR
clause. If we drop the `pupil-of` part the rule would look as shown below. If we had wrapped this in
curly braces we would have turned the body of the rule into a non-failing clause ([Non-failing
clauses](#non-failing-clauses)), allowing it to match topics which don't actually satisfy the rule.
So beware of this.

````tolog
influenced-by($A, $B) :-
  composed-by($OPERA : opera, $A : composer),
  based-on($OPERA : result, $WORK : source),
  written-by($WORK : work, $B : writer).      
````

As said above, tolog supports recursive inference rules, which are typically used to traverse
hierarchies, or to query transitive association types. An example of such an inference rule, for a
generic parent-child relationship, might look as follows:

````tolog
descendant-of($ANC, $DES) :- {
  parent-child($ANC : parent, $DES : child) |
  parent-child($ANC : parent, $MID : child), descendant-of($MID, $DES)
}.
````

Notice how the rule is essentially a formalization of what it means to be a descendant. That is,
either the ancestor is the parent of the descendant, or the ancestor is the parent of some middle
topic of which the descendant is a descendant. This explanation may sound a little strange, and
that's because of the recursion in the last step.

#### The built-in predicates ####

The parts of tolog shown thus far only support querying on type-instance relationships, associations
with a specific structure, and occurrences of specific types. It is impossible to do queries like
"find all association types", "find all associations between topics X and Y", "find all occurrences
of topic Z", and so on with the parts of tolog that have been explained thus
far.

tolog has a number of built-in predicates like `instance-of` that make it possible to do this kind
of query. These predicates are listed below, and more fully documented in [The Built-in tolog
Predicates — Reference Documentation](predicate-reference.html).

association($ASSOC)
:    This predicate takes only one parameter and finds all associations. Can be used to test whether a
     value is an association, and to find all associations.

association-role($ASSOC, $ROLE)
:    This predicate connects associations with their association roles.

base-locator($LOC)
:    Finds the base locator of the topic map.

direct-instance-of($INSTANCE, $TYPE)
:    A variant of `instance-of` which connects a topic with the topics it is explicitly said to be an
     instance of. In other words, just like `instance-of`, except that it ignores the superclass-subclass
     association.

instance-of($INSTANCE, $TYPE)
:    This predicate connects a topic (INSTANCE) with every topic it is an instance of (TYPE). A topic is
     considered to be an instance of another if the other is explicitly set as the type, or if it is a
     superclass of a topic that is set as the type.

occurrence($TOPIC, $OCCURRENCE)
:    Connects occurrences with the topics they belong to.

reifies($REIFIER, $REIFIED)
:    Connects topics (`REIFIER`) with the topic map constructs (`REIFIED`) they reify.

resource($OBJECT, $LOCATOR)
:    For any variant name or occurrence that has a locator (`OBJECT`), finds that locator.

role-player($ROLE, $PLAYER)
:    Connects association roles with the topic that plays the role.

scope($SCOPED, $THEME)
:    Connects topic names, occurrences, and associations with the topics that make up the scope.

item-identifier($OBJECT, $LOCATOR)
:    Finds the item identifiers of a topic map construct.

subject-identifier($TOPIC, $LOCATOR)
:    Connects topics with their subject identifiers.

subject-locator($TOPIC, $LOCATOR)
:    Connects a topic with its subject locator, if it has one.

topic($TOPIC)
:    Finds all topics in the topic map.

topic-name($TOPIC, $NAME)
:    Connects topics with their topic names.

topicmap($TOPICMAP)
:    Finds the topic map itself.

type($TYPED, $TYPE)
:    Connects occurrences, associations, and association roles (`TYPED`) with their type, if they have
     one.

value($OBJECT, $VALUE)
:    Finds the string value of topic names, variant names, and occurrences, if they have one.

value-like($OBJECT, $SEARCHSTRING)
:    This predicate *must* have a bound value for the `SEARCHSTRING` argument, and will do a full-text
     search for topic map constructs that match the `SEARCHSTRING`. This makes it possible to do
     full-text searches in tolog queries.

variant($BASENAME, $VARIANT)
:    Connects variants with the topic names they are contained in.

/=
:    Compares two values (both of which must be bound) to see if they are the different; fails if the two
     values are equal.


We can't give examples of the use of all of these predicates, since there are so many of them, but
at least we can show some. We can start with "find all association types", which is given below.,
"find all associations between topics X and Y", "find all occurrences of topic
Z"

````tolog
select $TYPE from
  association($ASSOC), type($ASSOC, $TYPE)?
````

Finding all associations between topic 'x' and topic 'y' is quite easy, though a bit more involved.

````tolog
select $ASSOC from
  role-player($ROLE1, x),
  association-role($ASSOC, $ROLE1),
  association-role($ASSOC, $ROLE2),
  role-player($ROLE2, y)?
````

We can also find all occurrences of topic z quite easily, as shown below.

````tolog
occurrence(z, $OCC)?
````

The `/=` predicate allows an interesting class of queries. We can control which values must be the
same in a query, simply by using the same variable, but we cannot force different variables to have
different values. This means that if we try to do a query like "find all people born on the same
day" we run into a quite subtle problem. Below is the naive way to approach it.

````tolog
date-of-birth($PERSON1, $DATE),
date-of-birth($PERSON2, $DATE)?
````

This runs, but does not produce the result we want, as can be seen from the table below.

| PERSON1 | DATE | PERSON2 | 
|---|
| Benelli, Sem | 1877 (10 Aug) | Benelli, Sem | 
| Ghislanzoni, Antonio | 1824 (25 Nov) | Ghislanzoni, Antonio | 
| Coppée, François | 1842 (26 Jan) | Coppée, François | 

Here tolog has discovered that if `PERSON1` is 'Benelli, Sam', `DATE` is '1877 (10 Aug)', and
`PERSON2` is 'Benelli, Sam', then our query criteria are satisfied. In other words, tolog has found,
as we asked it to, that every person has the same birthdate as himself. We knew this all along, of
course, and did not want it in our query results at all, so what we do now is to specify that we are
not interested in matches where there is only one person. This is done as
below.

````tolog
date-of-birth($PERSON1, $DATE),
date-of-birth($PERSON2, $DATE),
$PERSON1 /= $PERSON2?
````

This gives the result we wanted, which is shown below.

| PERSON1 | DATE | PERSON2 | 
|---|
| Bognasco, G. di | (unknown) | Lombardo, Carlo | 
| Bognasco, G. di | (unknown) | Franci, Arturo | 
| Bognasco, G. di | (unknown) | Vaucaire, Maurice | 
| Duveyrier, Charles | 1803 | Royer, Alphonse | 
| Royer, Alphonse | 1803 | Duveyrier, Charles | 
| ... | ... | ... | 

Again the results are somewhat disappointing. We see that there are indeed two people who share the
specified birth information, but in their case we only know the year. We also see that a number of
people all have their birth date given as `"(unknown)"` and since all these values are the same they
are also returned as query matches, though they are of course not guaranteed to have the same birth
date.

#### Paging ####

Sometimes we don't want all the results from a query, but only a limited set of results. For
example, we may only want to know the answer to "who is the most prolific opera composer?" In this
case, we only want the first row of the results, and not the rest, however many they may be. tolog
supports this, through the `LIMIT` keyword, as shown below.

````tolog
select $A, count($B) from
  composed-by($A : composer, $B : opera)
order by $B desc limit 1?
````

This gives the result ("Verdi, Guiseppe", 28), but does not return any of the other rows. Note that
the `order by` is very important. Without this we would have produced the rows in random order, then
only returned the first, which need not have been the row for the most prolific
composer.

The `LIMIT` keyword is mainly used as a performance optimization in cases where a large number of
results can be returned. `LIMIT` allows the application to tell the query processor the limit the
number of results to a manageable number, and this can have significant performance benefits in some
cases.

Another use for `LIMIT` is when one wants to show a paged list. That is, a query is run to show
first results 1-10, then, if the user wants to go further, 11-20, then, 21-30, and so on. tolog can
do this with `LIMIT` and `OFFSET` together. Let's say we want to show operas, and we only want to
show the first page. We can then do as below.

````tolog
instance-of($OPERA, opera)
order by $OPERA limit 10?
````

This gives us the first ten operas (note the `order by` which guarantees a consistent ordering), and
we can continue with the below to get operas 11-20.

````tolog
instance-of($OPERA, opera)
order by $OPERA limit 10 offset 10?
````

This query starts on row 10 (the first one did rows 0-9) according to `OFFSET` and stops after 10
rows, according to `LIMIT`. By increasing `OFFSET` to 20, 30, and so on we can continue stepping
through the pages.

### Identifiers ###

One thing that has been consistently glossed over so far is how topic identifiers like `composer`
actually refer to topics, and how to refer to topics by means such as their subject identifiers.
Another issue we've ignored is how to actually make inference rule definitions available to the
query processor. This section deals with both of those issues in more detail.

#### Referring to topics ####

There are several different ways of referring to topics (and other topic map constructs, such as
topic names, occurrences etc) in tolog, although so far we have only seen topic IDs. Below is a
complete list of the different syntaxes.

id
:   This syntax is used to look up topics by their source locators. The given ID is expanded to a full
    source locator by prepending the base URI of the topic map being queried, followed by `#`. If the
    topic being looked up did not originate in the root document of the topic map (but instead came from
    a file that was merged into it), this lookup will fail.

i"uri"
:   Used to look up topics by their subject identifiers. The URI of the identifier is given between the
    quotes, and is resolved relative to the base URI of the topicmap.

a"uri"
:   Used to look up topics by their subject locator. The URI of the subject locator is given between the
    quotes, and is resolved relative to the base URI of the topicmap.

s"uri"
:   Used to look up topics by their source locators. The URI of the locator is given between the quotes,
    and is resolved relative to the base URI of the topicmap.

@objid
:   Used to look up topics by their object ids. This is the fastest method, and the one that is
    recommended when generating tolog statements automatically. It does not make for readable queries,
    however, and so should not be used for other purposes.

This allows us to for example write a query that in any topic map will find the topics at the top of
the superclass-subclass hierarchy, as shown below.

````tolog
select $TOP from
  i"http://www.topicmaps.org/xtm/1.0/core.xtm#superclass-subclass"(
    $TOP : i"http://www.topicmaps.org/xtm/1.0/core.xtm#superclass",
    $SUB : i"http://www.topicmaps.org/xtm/1.0/core.xtm#subclass"),
  not(i"http://www.topicmaps.org/xtm/1.0/core.xtm#superclass-subclass"(
    $OTHER : i"http://www.topicmaps.org/xtm/1.0/core.xtm#superclass",
    $TOP : i"http://www.topicmaps.org/xtm/1.0/core.xtm#subclass"))?
````

Since this query uses the subject identifiers for the association type and role types defined by XTM
1.0 this will work regardless of what the XML IDs given to the topics are in any particular topic
map. This makes the query quite portable and robust. (Unfortunately, it also makes the query almost
entirely unreadable, but more about that in the next section.)

#### URI prefixes ####

As noted above, giving URIs in full throughout a query quickly makes the query so verbose as to be
virtually unreadable. In order to solve this, tolog has a feature called *URI prefixes*, which
allows the definition of an identifier prefix that can be combined with a local name to create a
full URI. Using this we can rewrite the query from the previous section as shown
below.

````tolog
using xtm for i"http://www.topicmaps.org/xtm/1.0/core.xtm#"
select $TOP from
  xtm:superclass-subclass($TOP : xtm:superclass, $SUB : xtm:subclass),
  not(xtm:superclass-subclass($OTHER : xtm:superclass,
                              $TOP : xtm:subclass))?
````

This query defines the prefix `xtm` which is bound to the URI
`"http://www.topicmaps.org/xtm/1.0/core.xtm#"`, and which is defined to be interpreted as subject
identifier reference (because of the `i` before the URI). Thus, `xtm:subclass` is interpreted as
`i"http://www.topicmaps.org/xtm/1.0/core.xtm#subclass"`.

It is possible to define more than one prefix in a single query, and it is also possible to bind
prefixes to `a"..."` and `s"..."` URIs.

#### Modules ####

Another thing we have glossed over so far is how to actually make an inference rule available for
use in queries. As it turns out, there are several ways to do this, and we'll walk through them one
by one. The easiest is simply to put the rule into the query that uses it, like shown
below.

````tolog
influenced-by($A, $B) :- {
  pupil-of($A : pupil, $B : teacher) |
  composed-by($OPERA : opera, $A : composer),
  based-on($OPERA : result, $WORK : source),
  written-by($WORK : work, $B : writer)
}.

instance-of($COMPOSER, composer),
influenced-by($COMPOSER, $INFLUENCE),
born-in($INFLUENCE : person, $PLACE : place),
not(located-in($PLACE : containee, italy : container))?
````

However, having to repeat the rule for every query that uses it is not exactly a good code reuse
strategy, so tolog also supports importing rule sets from files. If the `influenced-by` rule is
stored in the file `opera.tl` in the same directory as the `opera.ltm` topic map file the above
query can also be written as shown below.

````tolog
import "opera.tl" as opera

instance-of($COMPOSER, composer),
opera:influenced-by($COMPOSER, $INFLUENCE),
born-in($INFLUENCE : person, $PLACE : place),
not(located-in($PLACE : containee, italy : container))?
````

What happens here is that the `import` statement imports the rule file, which is stored as a module
bound to the `opera` prefix. The inference rules in the file then turn into predicates in the
module, and can be accessed through the `opera` prefix.

References to module files are first attempted loaded from the CLASSPATH. If it was not found on the
CLASSPATH then it is resolved relative to the URI of the topic map, except inside module files where
it is resolved relative to the URI of the module file. (Module files can import other module
files.)

#### Scoping rules ####

One thing we haven't looked at yet is what are known as *scoping rules*; that is, the rules for
which declarations are visible where. Declarations in tolog are of two types: inference rule
declarations, and prefix declarations (whether URI prefixes or module imports). The rules for all
declarations are the same, and they are as listed below.

*  Declarations included in a query only apply to that query.
*  Declarations included in a module are only visible in that module, although rules defined in the
   module will be available through the module prefix.
*  Declarations loaded into a query processor will be available throughout the life of that processor.
   (See [Using the query processor API](#using-the-query-processor-api) for more
   information.)

### Running tolog queries ###

This section explains how you can run tolog queries with Ontopia.

#### The tolog plug-in ####

The easiest way to get started with actually running tolog queries and seeing the query results is
to use the tolog plug-in in the Omnigator. This plug-in appears as a "Query" link in the plug-in row
of the Omnigator's pages. Clicking on this link takes you to a page where you can write tolog
queries in a form and see them evaluated. The results display as a table where each topic is linked
back into the Omnigator.

#### The 'tolog' tag library ####

The Navigator Framework contains a tag library named 'tolog', which consists of JSP tags specially
designed for creating HTML output from topic maps using tolog. The tag library allows queries to be
run against the topic map, and makes the resulting variable bindings available as parameters for new
queries or to be output, etc. For more information on this, please consult The Navigator Framework —
Developer's Guide.

#### Using the query processor API ####

The tolog query processor also has a Java API, which can be used to evaluate queries and make use of
the query results. The heart of this API is the `QueryProcessorIF` interface, which represents the
query processor. Query processors can parse queries and return objects representing them, and also
take queries and return query results (represented by `QueryResultIF` objects). These interfaces are
all in the `net.ontopia.topicmaps.query.core` package.

There are two tolog implementations in Ontopia. One of these works directly against the API, and
thus works for all the topic map engine backends. However, it does not perform optimally for very
large topic maps stored in RDBMSs, and so there is a special implementation for this backend. The
`QueryUtils` class in the `net.ontopia.topicmaps.query.utils` package can be used to create query
processors. It will automatically select the right processor for your topic map, and lets you write
code that is independent of which backend you use.

The example below shows the implementation of the tolog plug-in in the Omnigator, which is written
using the API. The example has been simplified somewhat, in order to make it more
clear.

**The tolog plug-in**

````application/x-jsp
<p>Query:<br> <%= query %></p>

<%
 String query = request.getParameter("query");
 QueryProcessor proc = QueryUtils.getQueryProcessor(topicmap);
 QueryResultIF result = proc.execute(query);
 StringifierIF str = TopicStringifiers.getDefaultStringifier();
%>

<table class="text">
<tr><%
  String[] variables = result.getColumnNames();
  for (int ix = 0; ix < variables.length; ix++)
    out.write("<th>" + variables[ix]);
  }
%></tr>

<%
  Object[] row = new Object[result.getWidth()];
  while (result.next()) {
    out.write("<tr>");
    result.getValues(row);
    for (int ix = 0; ix < variables.length; ix++) {
      if (row[ix] == null)
        out.write("<td>null");
      else if (row[ix] instanceof TopicIF)
        out.write("<td><a href=\"../../models/topic_" + model + ".jsp?tm=" + tmid  + "&id=" + ((TopicIF) row[ix]).getObjectId() + "\">" +
                  str.toString(row[ix]) + "</a>");
      else
        out.write("<td>" + row[ix]);
      out.write("&nbsp;&nbsp;&nbsp;\n");
    }
    out.write("</tr>");
  }
  result.close();
%>
</table>
````

Note that in order to avoid resource leaks it is necessary to close all result sets when using the
RDBMS backend.

An easier interface for running queries in Ontopia is provided by the `QueryWrapper` class in the
`net.ontopia.topicmaps.query.utils` package. This provides methods for running a query and getting a
single topic or string, or a list of objects, and also has methods for converting a query result
into a list of custom objects and so on. This class automatically takes care of closing result
sets.

#### Parameters ####

The query processor API supports parsing a query once and then running the parsed query many times.
This is supported through the `parse(String query)` method of the `QueryProcessorIF`. This returns
an object implementing `ParsedQueryIF`, which can be executed many times.

Of course, executing the same query many times is not very interesting; one might as well just cache
the results and achieve the same thing much more efficiently. However, tolog also supports
parameters to parsed queries. These are written with the `%foo%` syntax that we saw used with the
`tm:tolog` tag above.

The `ParsedQueryIF` interface has two methods named `execute`. One takes no arguments and
effectively reruns the same query. The other takes a `Map` argument which maps parameter names to
their values. Using this, the same query can be run over and over again with different parameters
without having to pay the cost of parsing and optimizing the query more than
once.

### tolog updates ###

In addition to querying the topic map to retrieve data tolog also supports making modifications to
the topic map. This section explains how to use the `DELETE`, `UPDATE`, `INSERT`, and `MERGE`
statements, in addition to the `SELECT` statement covered so far.

Note that in all of these statement types, topic references and prefix declarations have exactly the
same syntax as in `SELECT` statements.

#### The DELETE statement ####

This statement is used to delete objects from the topic map. The simplest form of delete statements
is as follows:

**Deleting a topic**

````tolog
delete foo
````

This would delete the topic map object with the ID `foo`. If it is a topic map everything in the
topic map will be deleted. If it is an association, the association and all its roles will be
deleted. If it's a topic the topic and all its names, occurrences, and all the associations it plays
roles in will be deleted. It will also be removed as a reifier, a topic type, and from scopes, and
statements typed with the topic will also be deleted.

It's possible to run a query to delete things, as follows:

**Deleting all person topics**

````tolog
delete $PERSON from
  instance-of($PERSON, person)
````

The `from` part of the query is the same as for `SELECT` statements, and so the above would find all
topics of type `PERSON` and delete them. Any number of variables and topic references are allowed in
the `delete` clause.

Finally, there is a third form of `DELETE` statements, which calls a function to remove a value from
a property. Here is an example, removing all Wikipedia PSIs from all topics in the topic
map:

**Removing Wikipedia PSIs**

````tolog
import "http://psi.ontopia.net/tolog/string/" as str
  delete subject-identifier($TOPIC, $PSI) from
  subject-identifier($TOPIC, $PSI),
  str:starts-with($PSI, "http://en.wikipedia.org/wiki/")
````

The deletion functions available in tolog are:

| Function | Parameters | Meaning | 
|---|---|---|
| subject-identifier | topic, locator | Removes the locator as a subject identifier for the topic. | 
| subject-locator | topic, locator | Removes the locator as a subject locator for the topic. | 
| item-identifier | object, locator | Removes the locator as an item identifier for the object. | 
| direct-instance-of | topic, topic | Removes the second topic as an a topic type of the first topic. | 
| scope | statement, topic | Removes the topic from the scope of the statement. | 
| reifies | statement, topic | Removes the topic as a reifier of the statement. | 

#### The INSERT statement ####

The `INSERT` statement is used to add new information to the topic map. In its simplest form it
looks like this:

**Adding a new topic**

````tolog
INSERT
  tolog-updates isa update-language;
    - "tolog updates".
````

Basically, after the `INSERT` keyword follows topic map data in
[CTM](http://www.isotopicmaps.org/ctm/) syntax. Note that it is possible to use the CTM wildcard
syntax to create new topics without giving them explicit IDs in the CTM fragment. Prefixes declared
for the tolog query can also be used in the CTM fragment.

There is another more powerful form of the `INSERT` statement which runs a query and instantiates
the CTM part once for each row in the query result. An example of this might be as
follows:

**Adding Wikipedia PSIs**

````tolog
import "http://psi.ontopia.net/tolog/string/" as str
  insert $topic $psi . from
  article-about($topic, $psi),
  str:starts-with($psi, "http://en.wikipedia.org/wiki/")
````

For every `article-about` occurrence which references a Wikipedia article this statement adds the
URI of the article as a subject identifier of the topic.

#### The UPDATE statement ####

The `UPDATE` statement makes it possible to change the values of some properties in the topic map.
The statement has two forms, of which the simplest is this:

**Simple update**

````tolog
update value(@2312, "Ontopia")
````

The above statement sets the string value of the object with ID `2312` to `"Ontopia"`. It is also
possible to run a query to find the objects to change (and their values), as
follows:

**Query update**

````tolog
update value($TN, "Ontopia") from
  topic-name(oks, $TN)
````

The available update functions are:

| Function | Parameters | Meaning | 
|---|---|---|
| value | object, string | Sets the string value of the object, which must be a topic name, variant name, or occurrence. | 
| resource | object, locator | Sets the value of the object to the given locator (and also changes the datatype to URI), where the object must be a variant name or an occurrence. | 

#### The MERGE statement ####

The `MERGE` statement can be used to merge topics either by running a query or directly, as in the
example below.

**Merging two topics**

````tolog
merge topic1, topic2
````

It's also possible to find the topics to merge by running a query, as in the following:

**Merging by email**

````tolog
merge $T1, $T2 from
  email($T1, $EMAIL),
  email($T2, $EMAIL)
````

Rows where `T1` and `T2` refer to the same topic are ignored.

### Further reading ###

The most detailed description of tolog can be found in *[tolog
specification](http://www.ontopia.net/topicmaps/materials/tolog-spec.html)* and *[the TMRA 2005
paper](http://www.springerlink.com/content/d7w040544002x220/)*.

The *[Extending tolog](http://www.ontopia.net/topicmaps/materials/extending-tolog.html)* conference
paper from Extreme Markup 2003 has more background on why tolog was extended from version 0.1 to
1.0, and the design issues faced in that process.

The original paper on tolog is *[tolog - A topic map query
language](http://www.ontopia.net/topicmaps/materials/tolog.html)*, from XML Europe
2001.

More about tolog updates can be found in [the TMRA 2009
paper](http://www.tmra.de/2009/talks/tolog_updates).


