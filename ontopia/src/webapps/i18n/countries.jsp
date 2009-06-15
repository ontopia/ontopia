<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'          prefix='tolog'     %>
<% response.setContentType("text/html; charset=utf-8"); %>

<tolog:context topicmap="i18n.ltm">
<template:insert template='i18n-template.jsp'>

<template:put name="title" body="true">
Countries by name
</template:put>


<template:put name="content" body="true">

<p>
These are all the countries this site has information about.
</p>

<ul>
<tolog:foreach query="instance-of($COUNTRY, country),
                      object-id($COUNTRY, $CID)
                      order by $COUNTRY?">
  <li><a href="country.jsp?id=<tolog:out var="CID"/>"
        ><tolog:out var="COUNTRY"/></a></li>
</tolog:foreach>
</ul>
</template:put>

</template:insert>
</tolog:context>