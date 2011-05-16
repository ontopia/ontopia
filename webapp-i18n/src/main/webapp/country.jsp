<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'          prefix='tolog'     %>

<% response.setContentType("text/html; charset=utf-8"); %>

<tolog:context topicmap="i18n.ltm">
<tolog:set var="country" reqparam="id"/>
<template:insert template='i18n-template.jsp'>

<template:put name="title" body="true">
<tolog:out var="country"/>
</template:put>

<template:put name="type" body="true">
( a country )
</template:put>


<template:put name="content" body="true">

<!-- ===== LANGUAGES ============================== -->
<tolog:if query="spoken-in(%country% : region, $LANGUAGE : language)?">
<p>
The following languages are spoken in <tolog:out var="country"/>:
</p>

<ul>
<tolog:foreach query="spoken-in(%country% : region, $LANGUAGE : language),
                      object-id($LANGUAGE, $LID)
                      order by $LANGUAGE?">
  <li><a href="language.jsp?id=<tolog:out var="LID"/>"
        ><tolog:out var="LANGUAGE"/></a></li>
</tolog:foreach>
</ul>
</tolog:if>

<!-- ===== PROVINCES ============================== -->
<tolog:if query="part-of(%country% : container, $PROVINCE : containee)?">
<p>
<tolog:out var="country"/> consists of the following provinces:
</p>

<ul>
<tolog:foreach query="part-of(%country% : container, $PROVINCE : containee),
                      object-id($PROVINCE, $PID)
                      order by $PID?">
  <li><a href="province.jsp?id=<tolog:out var="PID"/>"
        ><tolog:out var="PROVINCE"/></a></li>
</tolog:foreach>
</ul>
</tolog:if>


<!-- ===== OCCURRENCES ============================== -->
<tolog:set var="topic" reqparam="id"/>
<jsp:include page="fragments/occurrences.jsp"/>
</template:put>

</template:insert>
</tolog:context>
