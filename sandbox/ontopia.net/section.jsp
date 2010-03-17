<%@ include file="fragments/declarations.jsp"%>

<tolog:context topicmap="ontopia.xtm">
<%@ include file="fragments/tolog.jsp"%>
<tolog:set var="section" reqparam="id"/>
<%
  // ugly, I know, but time is tight
  if (ContextUtils.getValue("section", pageContext).isEmpty()) {
%>
  <tolog:query name="find-topic">
    o:id($TOPIC, "<%= request.getParameter("id") %>")?
  </tolog:query>
  <tolog:set var="section" query="find-topic"/>
<%
  }
%>

<%@ include file="fragments/tolog.jsp"%>

<template:insert template='fragments/template-2col.jsp'>
<template:put name='title'><tolog:out var="section"/></template:put>
<template:put name='breadcrumbs'>
  <a href="learn.jsp">LEARN!</a> &gt; <tolog:out var="section"/>
</template:put>

<template:put name="menu">
  <tolog:set var="topic" query="$A=1?"/>
  <%@ include file="fragments/menu.jsp"%>
</template:put>

<template:put name="main">
<%-- articles directly in section --%>
<tolog:foreach query="
  o:contained-in(%section% : o:container, $ARTICLE : o:containee),
  { subject-locator($ARTICLE, $URL) },
  instance-of($ARTICLE, o:article),
  o:order($ARTICLE, $ORDER),
  dc:description($ARTICLE, $DESC),
  { o:file-size($ARTICLE, $SIZE),
    o:format-of-file($ARTICLE : o:work, $FORMAT : o:format) }
  order by $ORDER?">
  <%@ include file="fragments/article.jsp"%>
</tolog:foreach>

<%-- articles divided by section --%>
<tolog:foreach query="
  o:contained-in(%section% : o:container, $SUB : o:containee),
  instance-of($SUB, o:section),
  o:order($SUB, $ORDER)
  order by $ORDER?">

  <h3><tolog:out var="SUB"/></h3>

<tolog:foreach query="
  o:contained-in(%SUB% : o:container, $ARTICLE : o:containee),
  { subject-locator($ARTICLE, $URL) },
  o:order($ARTICLE, $ORDER),
  dc:description($ARTICLE, $DESC),
  { o:file-size($ARTICLE, $SIZE),
    o:format-of-file($ARTICLE : o:work, $FORMAT : o:format) }
  order by $ORDER?">
  <%@ include file="fragments/article.jsp"%>
</tolog:foreach>
</tolog:foreach>

<%-- page that has content for the section --%>
<tolog:if query="
  o:contained-in(%section% : o:container, $PAGE : o:containee),
  instance-of($PAGE, o:page),
  o:page-text($PAGE, $TEXT),
  not(o:order($PAGE, $ORDER))?">

  <tolog:out var="TEXT" escape="false"/>

  <tolog:if query="o:big-image(%PAGE%, $IMAGE)?">
    <img src="images/<tolog:out var="IMAGE"/>" style="padding: 6pt" width="600">
  </tolog:if>
</tolog:if>
</table>
</template:put>

</template:insert>
</tolog:context>