<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'          prefix='tolog'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>

<% response.setContentType("text/html; charset=utf-8"); %>

<tolog:context topicmap="i18n.ltm">
  <tolog:set var="transxion" reqparam="id"/>
  <tolog:set var="type" query="instance-of(%transxion%, $TYPE)?"/>

<!-- ===== CONSTANTS ================================ -->
<logic:set name="type">
  <tm:classesOf of="transxion"/></logic:set>
<logic:set name="transforms-to">
  <tm:lookup source="#transforms-to"/></logic:set>
<logic:set name="transforms-from">
  <tm:lookup source="#transforms-from"/></logic:set>
<logic:set name="description">
  <tm:lookup source="#description"/></logic:set>
<logic:set name="other-names">
  <value:difference>
    <tm:names of="transxion"/>
    <tm:name of="transxion"/>
  </value:difference>
</logic:set>

<!-- ===== VARIABLES ================================ -->
<logic:set name="the-description">
  <tm:filter instanceOf="description">
    <tm:occurrences of="transxion"/>
  </tm:filter>
</logic:set>
<logic:set name="source">
  <tm:associated from="transxion" type="transforms-from"/>
</logic:set>
<logic:set name="target">
  <tm:associated from="transxion" type="transforms-to"/>
</logic:set>

<!-- ===== TEMPLATE ================================= -->
<template:insert template='i18n-template.jsp'>

<template:put name="title" body="true">
<tolog:out var="transxion"/>
</template:put>

<template:put name="type" body="true">
( a <tolog:out var="type"/> )
</template:put>


<template:put name="content" body="true">

<!-- ===== DESCRIPTION ============================== -->
<tolog:if query="description(%transxion%, $DESC)?">
<p><tolog:out var="DESC"/></p>
</tolog:if>

<!-- ===== BASICS =================================== -->
<tolog:set var="source" query="transforms-from(%transxion% : method, $S : source)?"/>
<tolog:set var="target" query="transforms-to(%transxion% : method, $T : target)?"/>

<p>
<tolog:out var="transxion"/> can be used to express
<a href="script.jsp?id=<tolog:id var="source"/>"><tolog:out var="source"/></a> in
<a href="script.jsp?id=<tolog:id var="target"/>"><tolog:out var="target"/></a>.

<tolog:if query="topic-name(%transxion%, $TN1), 
                 topic-name(%transxion%, $TN2), $TN1 /= $TN2?">
  It is also known as:
  <tolog:foreach separator=", " query="topic-name(%transxion%, $TN)?"
                ><tolog:out var="TN"/></tolog:foreach>.
</tolog:if>

</p>

<!-- ===== OCCURRENCES ============================== -->
<tolog:set var="topic" query="$TOPIC = %transxion%?"/>
<jsp:include page="fragments/occurrences.jsp"/>

</template:put>

</template:insert>
</tolog:context>
