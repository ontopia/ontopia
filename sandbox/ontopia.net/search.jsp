<%@ include file="fragments/declarations.jsp"%>

<tolog:context topicmap="ontopia.xtm">
<%@ include file="fragments/tolog.jsp"%>
<tolog:set var="query"><%= request.getParameter("query") %></tolog:set>

<template:insert template='fragments/template-2col.jsp'>
<template:put name='title'>Search results</template:put>
<template:put name="breadcrumbs">SEARCH</template:put>

<template:put name="menu"><!-- EMPTY --></template:put>

<template:put name="main">

<p>Search results for <b>"<tolog:out var="query"/>"</b>:</p>

<%
  // yes, I know this is ugly. but it's more important to get this
  // site online than it is to write pretty code.
  Set seen = new HashSet();
%>
<table class=ordinary>
<tolog:foreach query="
  select $TOPIC, $TYPE, $REL from
    value-like($OBJ, %query%, $REL),
    { topic-name($TOPIC, $OBJ) | occurrence($TOPIC, $OBJ) },
    direct-instance-of($TOPIC, $TYPE),
    not(instance-of($TOPIC, onto:system-topic))
  order by $REL desc?">

  <%
    Object topic = ContextUtils.getSingleValue("TOPIC", pageContext);
    if (!seen.contains(topic)) {
  %>

  <tr><td>

    <tolog:choose>
    <tolog:when query="subject-locator(%TOPIC%, $SL)?">
      <a href="<tolog:out var="SL"/>"
        ><tolog:out var="TOPIC"/></a>
    </tolog:when>
    <tolog:when query="o:id(%TOPIC%, $ID),
                       o:url-pattern(%TYPE%, $URL)?">
      <a href="<tolog:out var="URL"/><tolog:out var="ID"/>"
        ><tolog:out var="TOPIC"/></a>
    </tolog:when>
    <tolog:otherwise>
      <tolog:out var="TOPIC"/>
    </tolog:otherwise>
    </tolog:choose>

      <td style="width: 20px">
      <td><tolog:out var="TYPE"/></tr>

  <%
      seen.add(topic);
    }
  %>

</tolog:foreach>

</template:put>

</template:insert>
</tolog:context>