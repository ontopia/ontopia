<%@ page language="java" contentType="text/html" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'   prefix='logic'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'  prefix='output' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value'   prefix='value'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue' prefix='tm'     %>
<html>
  <head>
    <title>Very Simple Topic Page</title>
    <meta name='CVS-Id' content='$Id: simple.jsp,v 1.2 2007/09/14 11:14:41 geir.gronmo Exp $' />
  </head>

  <body>
    <logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">
      Object Id of Topic: <output:objectid of="topic"/>.
    </logic:context>
  </body>
</html>
