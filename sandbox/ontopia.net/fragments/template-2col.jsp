<%@ include file="declarations.jsp"%>
<%
  boolean mainpage = false;
%>
<html>
<head>
  <title>Ontopia - <template:get name="title"/></title> 
  <link rel="stylesheet" type="text/css" href="resources/stylesheet.css"></link>
  <template:get name="headertags"/>
</head>

<body>

<%@ include file="fragments/topbox.jsp"%>

<div class=home>Home/</div>
<div class=breadcrumbs><template:get name="breadcrumbs"/></div>

<table class=contentbox>
<tr><td class=menu>
  <template:get name="menu"/>

<td class=spacer>

<td class="main">

<div class=breadcrumbs><template:get name="title"/></div>

<template:get name="main"/>

<tr><td colspan="3" id="bottom-spacer">
</table>

</body>
</html>

