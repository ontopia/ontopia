<%@ include file="fragments/declarations.jsp"%>

<tolog:context topicmap="ontopia.xtm">
<%@ include file="fragments/tolog.jsp"%>

<template:insert template='fragments/template-2col.jsp'>
<template:put name='title'>Learn</template:put>
<template:put name="breadcrumbs">LEARN!</template:put>

<template:put name="menu">
  <tolog:set var="topic" query="$A=1?"/>
  <tolog:set var="section" query="$A=1?"/>

  <%@ include file="fragments/menu.jsp"%>
</template:put>

<template:put name="main">
<table>

<%
  int ix = 0;
%>
<tolog:foreach query="
  instance-of($SECTION, o:section),
  o:order($SECTION, $ORDER),
  dc:description($SECTION, $DESC),
  { o:internal-url($SECTION, $URL) }
  order by $ORDER?
  ">
  <%
    if (ix % 2 == 0)
      out.write("<tr>");
    ix++;
  %>
  <td class=column>
  <tolog:choose>
    <tolog:when var="URL">
      <p><b><a href="<tolog:out var="URL"/>"
              ><tolog:out var="SECTION"/></a></b><br>
    </tolog:when>
    <tolog:otherwise>
      <p><b><a href="section.jsp?id=<tolog:id var="SECTION"/>"
              ><tolog:out var="SECTION"/></a></b><br>
    </tolog:otherwise>
  </tolog:choose>

  <tolog:out var="DESC"/></p>
</tolog:foreach>

</table>
</template:put>

</template:insert>
</tolog:context>