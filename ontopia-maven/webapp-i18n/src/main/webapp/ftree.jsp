<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'   prefix='logic' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'  prefix='output'%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value'   prefix='value' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue' prefix='tm'    %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>

<% response.setContentType("text/html; charset=utf-8"); %>

<logic:context topicmap="i18n.ltm" objparam="id" set="script">
<template:insert template='i18n-template.jsp'>

<template:put name="title" body="true">
<output:name/>, family tree
</template:put>


<template:put name="content" body="true">

<!-- Gathering data -->
<logic:set name="derived-from">
 <tm:lookup indicator="http://psi.ontopia.net/i18n/#derived-from"/></logic:set>
<logic:set name="predecessor">
  <tm:lookup indicator="http://psi.ontopia.net/i18n/#predecessor"/></logic:set>
<logic:set name="successor">
  <tm:lookup indicator="http://psi.ontopia.net/i18n/#successor"/></logic:set>

<%-- output:treediagram
  src          = "pngtree.jsp"
  assoctype    = "derived-from"
  parentrole   = "predecessor"
  childrole    = "successor"
  current      = "script"
  description  = "Family tree"
  linktemplate = "script.jsp?id=%id%"/ --%> <%-- commented by PK because net.ontopia.topicmaps.nav2.taglibs.output.TreeDiagramTag does not exist --%>

</template:put>

</template:insert>
</logic:context>
