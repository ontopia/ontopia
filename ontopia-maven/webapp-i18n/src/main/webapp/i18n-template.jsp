<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title><template:get name='title'/></title>
<link rel="stylesheet" type="text/css" href="tools.css">
</head>

<body>

<table width="100%">
<tr><td><a href="http://www.topicmap.com"><img src="tm-logo.gif"
alt="made better through topic maps" border=0></a>
<td valign=top><p align=right class=partof>
  <a href="index.jsp">top</a> | 
  <a href="scripts.jsp">scripts</a> | 
  <a href="languages.jsp">languages</a> |
  <a href="countries.jsp">countries</a> |
  <a href="script-types.jsp">types</a> |
  <a href="categories.jsp">categories</a> |
  <a href="fulltext.jsp">search</a>
</p>
</table>

<h1><template:get name='title'/></h1>
<p class=type><template:get name='type'/></p>

<template:get name="content"/>

<hr>

<address>
<a href="mailto:larsga@ontopia.net">Lars Marius Garshol</a>,
<a href="http://www.ontopia.net">Ontopian</a>.
</address>
</body>
</html>