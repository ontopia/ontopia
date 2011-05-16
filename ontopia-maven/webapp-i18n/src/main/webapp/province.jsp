<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'   prefix='tolog' %>

<% response.setContentType("text/html; charset=utf-8"); %>

<tolog:context topicmap="i18n.ltm">
  <tolog:set var="province" reqparam="id"/>
<template:insert template='i18n-template.jsp'>


<template:put name="title" body="true">
<tolog:out var="province"/>
</template:put>

<template:put name="type" body="true">
( a province )
</template:put>


<template:put name="content" body="true">

<tolog:set var="country" query="part-of(%province% : containee, $COUNTRY : container)?"/>

<p>
<tolog:out var="province"/> is a province of
<a href="country.jsp?id=<tolog:id var="country"/>"><tolog:out var="country"/></a>.
</p>

<!-- ===== LANGUAGES ============================== -->

<tolog:if query="spoken-in(%province% : region, $LANGUAGE : language)?">
<p>
The following languages are spoken in <tolog:out var="province"/>:
</p>

<ul>
<tolog:foreach query="spoken-in(%province% : region, $LANGUAGE : language)
                      order by $LANGUAGE?">
  <li><a href="language.jsp?id=<tolog:id var="LANGUAGE"/>"
        ><tolog:out var="LANGUAGE"/></a></li>
</tolog:foreach>
</ul>
</tolog:if>


<!-- ===== OCCURRENCES ============================== -->

<tolog:set var="topic" query="$TOPIC = %province%?"/>
<jsp:include page="fragments/occurrences.jsp"/>

</template:put>
</template:insert>
</tolog:context>
