<%@ include file="fragments/declarations.jsp"%>

<tolog:context topicmap="ontopia.xtm">
<%@ include file="fragments/tolog.jsp"%>

<template:insert template='fragments/template-2col.jsp'>
<template:put name='title'>Success stories</template:put>
<template:put name="breadcrumbs">SUCCESS STORIES</template:put>

<template:put name="menu">
  <tolog:set var="topic" query="$A=1?"/>
  <tolog:set var="section" query='o:id($SECTION, "success-stories")?'/>

  <%@ include file="fragments/menu.jsp"%>
</template:put>

<template:put name="main">

<tolog:if query='o:id($SECTION, "success-stories"),
                 dc:description($SECTION, $DESC)?'>
  <p><tolog:out var="DESC"/></p>
</tolog:if>

<tolog:foreach query="
  instance-of($PROJECT, o:project),
  o:image($PROJECT, $IMAGE),
  o:abstract($PROJECT, $ABSTRACT)
  order by $PROJECT?">

  <h3><tolog:out var="PROJECT"/></h3>

  <div class=logobox>
    <a href="page.jsp?id=<tolog:id var="PROJECT"/>"
      ><img src="images/<tolog:out var="IMAGE"/>"></a>
  </div>

  <p><tolog:out var="ABSTRACT"/></p>

</tolog:foreach>

</template:put>

</template:insert>
</tolog:context>