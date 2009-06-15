<%@ page language="java" import="
  java.util.Collection,
  net.ontopia.topicmaps.core.*,
  net.ontopia.topicmaps.nav2.utils.ContextUtils,
  net.ontopia.topicmaps.nav2.utils.TreeWidget"%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template' prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'          prefix='tolog'     %>
<% response.setContentType("text/html; charset=utf-8"); %>

<tolog:context topicmap="i18n.ltm">
<template:insert template='i18n-template.jsp'>

<template:put name="title" body="true">
Script categories
</template:put>


<template:put name="content" body="true">
<p>
On this page you find the scripts classified according to
how they were developed and what other scripts they are related
to. The classification here has less to do with how the scripts
work than with who developed it when, why, and what other scripts
they are based on.
</p>

<tolog:set var="topicmap" query="topicmap($TM)?"/>
<% // ===== CATEGORY TREE ========================================
  String query = "belongs-to(%parent% : container, $CHILD : containee) " +
                 "order by $CHILD?";
  String topquery = "instance-of($CAT, script-category) order by $CAT?";

  String nodepage = "category.jsp?";
  String ownuri = "categories.jsp?";

  TopicMapIF topicmap = (TopicMapIF) ContextUtils.getSingleValue("topicmap", pageContext);
  TreeWidget widget = new TreeWidget(topicmap, query, topquery, ownuri, nodepage);
  widget.setImageUrl("images/");
  widget.setAddAnchor(false);
  widget.setWidgetName("i18n-categories");
  widget.run(pageContext, out);
%>

</template:put>

</template:insert>
</tolog:context>
