<%@ page language="java"
    import="
    java.io.*,
    java.util.*,
    java.net.URLEncoder,
    net.ontopia.utils.*,
    net.ontopia.topicmaps.core.*,
    net.ontopia.topicmaps.entry.*,
    net.ontopia.topicmaps.utils.*,
    net.ontopia.topicmaps.nav2.core.*,
    net.ontopia.topicmaps.nav2.impl.basic.*,
    net.ontopia.topicmaps.nav2.utils.FrameworkUtils,
    net.ontopia.infoset.fulltext.core.*,
    net.ontopia.topicmaps.nav2.plugins.PluginIF,
    net.ontopia.infoset.fulltext.impl.lucene.*"
%><% response.setContentType("text/html; charset=utf-8"); %>

<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'          prefix='tolog'     %>

<tolog:context topicmap="i18n.ltm">
<template:insert template='i18n-template.jsp'>

<template:put name="title" body="true">
Search result
</template:put>

<template:put name="content" body="true">
<% if(request.getParameter("query") != null) { %>

<p> Your search for '<%= request.getParameter("query") %>' found
the following: </p>

<table style="border: solid thin black">
<tolog:set var="search"><%= request.getParameter("query") %></tolog:set>

<tolog:foreach query="
  select $TOPIC, $TYPE from
    { value-like($TNAME, %search%), topic-name($TOPIC, $TNAME) |
      value-like($OCC, %search%), occurrence($TOPIC, $OCC) },
    direct-instance-of($TOPIC, $TYPE),
    { instance-of($TOPIC, script) |
      $TYPE = language | $TYPE = country | $TYPE = province | 
      $TYPE = transcription | $TYPE = transliteration }
  order by $TOPIC?">

  <tolog:choose>
    <tolog:when query="%TYPE% = language?">
      <tr><td><a href="language.jsp?id=<tolog:id var="TOPIC"/>"
                ><tolog:out var="TOPIC"/></a>&nbsp;&nbsp;&nbsp;
          <td><tolog:out var="TYPE"/>
    </tolog:when>
    <tolog:when query="%TYPE% = country?">
      <tr><td><a href="country.jsp?id=<tolog:id var="TOPIC"/>"
                ><tolog:out var="TOPIC"/></a>&nbsp;&nbsp;&nbsp;
          <td><tolog:out var="TYPE"/>
    </tolog:when>
    <tolog:when query="%TYPE% = province?">
      <tr><td><a href="province.jsp?id=<tolog:id var="TOPIC"/>"
                ><tolog:out var="TOPIC"/></a>&nbsp;&nbsp;&nbsp;
          <td><tolog:out var="TYPE"/>
    </tolog:when>
    <tolog:when query="{ %TYPE% = transcription |
                         %TYPE% = transliteration }?">
      <tr><td><a href="transxion.jsp?id=<tolog:id var="TOPIC"/>"
                ><tolog:out var="TOPIC"/></a>&nbsp;&nbsp;&nbsp;
          <td><tolog:out var="TYPE"/>
    </tolog:when>
    <tolog:otherwise>
      <tr><td><a href="script.jsp?id=<tolog:id var="TOPIC"/>"
                ><tolog:out var="TOPIC"/></a>&nbsp;&nbsp;&nbsp;
          <td><tolog:out var="TYPE"/>
    </tolog:otherwise>
  </tolog:choose>
</tolog:foreach>
</table>

<br><br><br>

<p>
Search again:
</p>
<% } %>

<form action="fulltext.jsp" method="get">
<input type=text name=query>
<input type=submit value="Search">
</form>

</template:put>

</template:insert>
</tolog:context>
