<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'          prefix='tolog'     %>
<% response.setContentType("text/html; charset=utf-8"); %>

<tolog:context topicmap="i18n.ltm">
<template:insert template='i18n-template.jsp'>

<template:put name="title" body="true">
Types of scripts
</template:put>


<template:put name="content" body="true">

<p>Scripts work in many different ways: some use one symbol per
letter, others use one symbol per syllable, and some use one symbol
per word. Below you can find descriptions of the different types of
scripts, and see what scripts belong to each type.</p>

<tolog:foreach query="subclass-of(script : superclass, $TYPE : subclass),
                      definition($TYPE, $DEF),
                      object-id($TYPE, $TID)
                      order by $TYPE?">
  <h2><a name="id<tolog:out var="TID"/>"><tolog:out var="TYPE"/></a></h2>

  <p><tolog:out var="DEF"/></p>

  <p>There are <tolog:out query="select count($S) from
                                 instance-of($S, %TYPE%)?"/> instances:

    <tolog:foreach query="instance-of($SCRIPT, %TYPE%),
                          object-id($SCRIPT, $SID) order by $SCRIPT?"
                   separator=", ">
      <a href="script.jsp?id=<tolog:out var="SID"/>"
        ><tolog:out var="SCRIPT"/></a></tolog:foreach>.
  </p>
</tolog:foreach>

<h2><a name="idscript">Unclassified</a></h2>

<p> I haven't been able to classify all the scripts on this site yet,
and below are listed the ones that have no classification yet. If you
can suggest classifications for some of these I would be happy to hear
it.  Note that some of these, like Rongorongo, are unclassified
because nobody knows how they work yet, and so nobody has enough
information to classify them properly.  </p>

<p>There are <tolog:out query="select count($S) from
                               direct-instance-of($S, script)?"/>
unclassifieds:

<tolog:foreach query="direct-instance-of($S, script),
                      object-id($S, $SID)
                      order by $S?" separator=", ">
  <a href="script.jsp?id=<tolog:out var="SID"/>"><tolog:out var="S"/></a></tolog:foreach>
</p>
</template:put>

</template:insert>
</tolog:context>
