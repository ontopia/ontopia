<%@ include file="fragments/declarations.jsp"%>

<tolog:context topicmap="ontopia.xtm">
<%@ include file="fragments/tolog.jsp"%>

<template:insert template='fragments/template-2col.jsp'>
<template:put name='title'>Services</template:put>
<template:put name="breadcrumbs">SERVICES</template:put>

<template:put name="menu"><!-- EMPTY --></template:put>

<template:put name="main">

<p>This page lists a number of companies which provide services of
different kinds related to the Ontopia software.</p>

<tolog:foreach query="
  instance-of($COMPANY, op:Company),
  o:homepage($COMPANY, $URL),
  o:image($COMPANY, $IMAGE),
  o:page-text($COMPANY, $TEXT)
  order by $COMPANY?">

  <h3><tolog:out var="COMPANY"/></h3>

  <div class=logobox>
    <a href="<tolog:out var="URL"/>"
      ><img src="images/<tolog:out var="IMAGE"/>"></a>
  </div>

  <tolog:out var="TEXT" escape="false"/>

</tolog:foreach>

</template:put>

</template:insert>
</tolog:context>