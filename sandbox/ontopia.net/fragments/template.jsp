<%@ include file="declarations.jsp"%>
<%
  boolean mainpage = true;
%>
<html>
<head>
  <title><template:get name="title"/></title> 
  <link rel="stylesheet" type="text/css" href="resources/stylesheet.css"></link>
  <template:get name="headertags"/>
</head>

<body>

<%@ include file="fragments/topbox.jsp"%>

<template:get name="body"/>

</body>
</html>

