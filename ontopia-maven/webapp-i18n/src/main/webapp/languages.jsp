<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template' prefix='template'%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'    prefix='tolog'   %>
<% response.setContentType("text/html; charset=utf-8"); %>

<tolog:context topicmap="i18n.ltm">
<template:insert template='i18n-template.jsp'>

<template:put name="title" body="true">
Languages by name
</template:put>


<template:put name="content" body="true">

<p>
These are all the languages this site has information about.
</p>

<ul>
<tolog:foreach query="instance-of($LANGUAGE, language),
                      object-id($LANGUAGE, $LANGID)
                      order by $LANGUAGE?">
  <li><a href="language.jsp?id=<tolog:out var="LANGID"/>"
       ><tolog:out var="LANGUAGE"/></a></li>
</tolog:foreach>
</ul>
</template:put>

</template:insert>
</tolog:context>