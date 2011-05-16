<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'          prefix='tolog'     %>

<% response.setContentType("text/html; charset=utf-8"); %>

<tolog:context topicmap="i18n.ltm">
  <tolog:set var="language" reqparam="id"/>
<template:insert template='i18n-template.jsp'>

<template:put name="title" body="true">
<tolog:out var="language"/>
</template:put>

<template:put name="type" body="true">
( a language )
</template:put>


<template:put name="content" body="true">

<!-- ===== PLACES ============================== -->

<tolog:if query="spoken-in(%language% : language, $PLACE : region)?">
<p>
<tolog:out var="language"/> is spoken in:
</p>

<ul>
<tolog:foreach query="spoken-in(%language% : language, $PLACE : region),
                      object-id($PLACE, $PID)
                      order by $PLACE?">
  <tolog:choose>
    <tolog:when query="instance-of(%PLACE%, country)?">
      <li><a href="country.jsp?id=<tolog:out var="PID"/>"><tolog:out var="PLACE"/></a> (country)</li>
    </tolog:when>

    <tolog:otherwise>
      <li><a href="province.jsp?id=<tolog:out var="PID"/>"><tolog:out var="PLACE"/></a> (province)</li>
    </tolog:otherwise>
  </tolog:choose>
</tolog:foreach>
</ul>
</tolog:if>


<!-- ===== SCRIPTS ============================== -->

<tolog:if query="written-in(%language% : language, $SCRIPT : script)?">
<p>
<tolog:out var="language"/> is written in the following scripts:
</p>

<ul>
<tolog:foreach query="written-in(%language% : language, $SCRIPT : script),
                        object-id($SCRIPT, $SID)
                        order by $SCRIPT?">
  <li><a href="script.jsp?id=<tolog:out var="SID"/>"><tolog:out var="SCRIPT"/></a></li>
</tolog:foreach>
</ul>
</tolog:if>

<!-- ===== NAMES ============================== -->

<p>
<b>Names:</b>
<tolog:foreach query="topic-name(%language%, $NAME)?" separator=", "
  ><tolog:out var="NAME"/></tolog:foreach>.
</p>

<!-- ===== DESCRIPTION ============================== -->

<tolog:if query="description(%language%, $DESC)?">
<p><tolog:out var="DESC"/></p>
</tolog:if>

<!-- ===== TRANSXIONS =============================== -->
<tolog:set var="topic" reqparam="id"/>
<jsp:include page="fragments/transxions.jsp"/>

<!-- ===== OCCURRENCES ============================== -->
<jsp:include page="fragments/occurrences.jsp"/>
</template:put>

</template:insert>
</tolog:context>
