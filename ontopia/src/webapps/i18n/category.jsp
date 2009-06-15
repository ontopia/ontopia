<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'          prefix='tolog'     %>

<% response.setContentType("text/html; charset=utf-8"); %>

<tolog:context topicmap="i18n.ltm">
<tolog:set var="category" reqparam="id"/>
<template:insert template='i18n-template.jsp'>

<template:put name="title" body="true">
<tolog:out var="category"/>
</template:put>

<template:put name="type" body="true">
( a script category )
</template:put>


<template:put name="content" body="true">

<tolog:if query="definition(%category%, $DESC)?">
<p><tolog:out var="DESC"/></p>
</tolog:if>

<p>
<tolog:if query="belongs-to(%category% : containee, $PARENT : container),
                 object-id($PARENT, $PID)?">
This is a part of the category
<a href="category.jsp?id=<tolog:out var="PID"/>"><tolog:out var="PARENT"/></a>.
</tolog:if>

You can also see the <a href="categories.jsp">full category tree</a>.
</p>

  <logic:set name="subcats">
    <tm:filter instanceOf="script" invert="true">
   <tm:associated startrole="container" type="belongs-to" endrole="containee"/>
    </tm:filter>
  </logic:set>

<!-- SCRIPTS -->
<tolog:if query="belongs-to(%category% : container, $CHILD : containee),
                 instance-of($CHILD, script)?">
<p>
Below are shown the scripts belonging to this category.
</p>

<ul>
  <tolog:foreach query="belongs-to(%category% : container, $CHILD : containee),
                        instance-of($CHILD, script)
                        order by $CHILD?">
    <li><a href="script.jsp?id=<tolog:id var="CHILD"/>"><tolog:out var="CHILD"/></a>
  </tolog:foreach>
</ul>

</tolog:if>

<!-- SUBCATEGORIES -->
<tolog:if query="belongs-to(%category% : container, $CHILD : containee),
                 not(instance-of($CHILD, script))?">
<p>
Subcategories of this category are:
</p>

<ul>
  <tolog:foreach query="belongs-to(%category% : container, $CHILD : containee)
                        order by $CHILD?">
    <li><a href="category.jsp?id=<tolog:id var="CHILD"/>"><tolog:out var="CHILD"/></a>
  </tolog:foreach>
</ul>
</tolog:if>

<!-- ===== OCCURRENCES ============================== -->

<tolog:set var="topic" query="$TOPIC = %category%?"/>
<jsp:include page="fragments/occurrences.jsp"/>

</template:put>

</template:insert>
</tolog:context>
