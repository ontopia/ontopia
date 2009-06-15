<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog' %>
<html>
<head>
<tolog:context topicmap='<%= request.getParameter("tm") %>'>
<tolog:set var="TOPIC" reqparam="id"/>
<tolog:choose>
  <tolog:when var="TOPIC">
    <meta http-equiv="Refresh" content="0;URL=/ontopoly/?wicket:bookmarkablePage=%3Aontopoly.pages.EnterTopicPage&topicId=<tolog:oid var="TOPIC"/>&topicMapId=<%=request.getParameter("tm")%>"/>
  </tolog:when>
  <tolog:otherwise>
    <meta http-equiv="Refresh" content="0;URL=/ontopoly/?wicket:bookmarkablePage=%3Aontopoly.pages.InstanceTypesPage&topicMapId=<%=request.getParameter("tm")%>"/>
  </tolog:otherwise>
</tolog:choose>
</tolog:context>
</head>
<body></body>
</html>
