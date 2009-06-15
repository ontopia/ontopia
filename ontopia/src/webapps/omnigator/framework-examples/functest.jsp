<%@ page language="java" contentType="text/html" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'   prefix='logic'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'  prefix='output' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value'   prefix='value'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue' prefix='tm'     %>

<html>
  <head>
    <title>Test Function Mechanism</title>
  </head>

  <body>
    <h1>Test Function Mechanism</h1>
  
    <logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">

    Function loading...
    <logic:include file="/functions/names.jsf" />
     ...done.<br><p>

    Function executing:
    <logic:call name="names">
      <logic:set name="names" comparator="nameComparator">
        <tm:names of="topic" />
      </logic:set>
    </logic:call>
    ...done.
    
    </logic:context>
  </body>
</html>
