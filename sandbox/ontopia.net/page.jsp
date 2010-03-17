<%@ include file="fragments/declarations.jsp"%>

<tolog:context topicmap="ontopia.xtm">
<%@ include file="fragments/tolog.jsp"%>
<tolog:set var="topic" reqparam="id"/>
<%
  // ugly, I know, but time is tight
  if (ContextUtils.getValue("topic", pageContext).isEmpty()) {
%>
  <tolog:query name="find-topic">
    o:id($TOPIC, "<%= request.getParameter("id") %>")?
  </tolog:query>
  <tolog:set var="topic" query="find-topic"/>
<%
  }
%>

<template:insert template='fragments/template-2col.jsp'>
<template:put name='title'><tolog:out var="topic"/></template:put>

<template:put name="breadcrumbs">
  <tolog:choose>
  <tolog:when query="o:contained-in($SECTION : o:container, %topic% : o:containee)?">
    <a href="learn.jsp">LEARN!</a> &gt;
    <a href="section.jsp?id=<tolog:id var="SECTION"/>"
      ><tolog:out var="SECTION"/></a> &gt;
  </tolog:when>

  <tolog:when query="instance-of(%topic%, o:project)?">
    <a href="success.jsp">SUCCESS STORIES</a> &gt;
  </tolog:when>
  </tolog:choose>

  <tolog:out var="topic"/>
</template:put>

<template:put name="menu">
<tolog:if query="o:contained-in($section : o:container, %topic% : o:containee)?">
  <%@ include file="fragments/menu.jsp"%>
</tolog:if>
</template:put>

<template:put name="main">

<tolog:if query="o:image(%topic%, $IMAGE)?">
  <img src="images/<tolog:out var="IMAGE"/>" 
       style="float: right; padding: 6pt">
</tolog:if>

<tolog:out query="o:page-text(%topic%, $PAGE)?" escape="false"/>

<tolog:if query="o:big-image(%topic%, $IMAGE)?">
  <img src="images/<tolog:out var="IMAGE"/>" style="padding: 6pt" width="600">
</tolog:if>

</template:put>

</template:insert>
</tolog:context>