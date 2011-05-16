<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ page import="
  net.ontopia.topicmaps.query.impl.basic.*,
  net.ontopia.topicmaps.query.core.QueryResultIF,
  net.ontopia.topicmaps.core.*,
  net.ontopia.infoset.core.LocatorIF,
  net.ontopia.utils.StringifierIF,
  net.ontopia.topicmaps.entry.*,
  net.ontopia.topicmaps.nav2.core.*,
  net.ontopia.topicmaps.utils.TopicStringifiers
  "  %>

<%!
  private TopicIF getTopicById(TopicMapIF topicmap, String id) {
    TopicMapStoreIF store = topicmap.getStore();
    LocatorIF loc = store.getBaseAddress().resolveAbsolute("#" + id);
    return (TopicIF) topicmap.getObjectByItemIdentifier(loc);
  }
%>

<% response.setContentType("text/html; charset=utf-8"); %>
<template:insert template='i18n-template.jsp'>

<template:put name="title" body="true">
Search results
</template:put>

<template:put name="content" body="true">

<%
  // retrieve configuration
  NavigatorApplicationIF navApp = (NavigatorApplicationIF) application
                           .getAttribute(NavigatorApplicationIF.NAV_APP_KEY);
  TopicMapIF topicmap = navApp.getTopicMapById("i18n.ltm");
  
  // get some important topics
  TopicIF cat = getTopicById(topicmap, "script-category");
  TopicIF grp = getTopicById(topicmap, "script-group");

  // build query
  String tolog = "select $SCRIPT from\n";  
  String type = request.getParameter("type");
  if (!type.equals("any")) 
    tolog += "direct-instance-of($SCRIPT, @" + type + ")";
  else
    tolog += "instance-of($SCRIPT, script)";

  String category = request.getParameter("category");
  if (!category.equals("any")) {
    tolog += ",\n";

    TopicIF thecat = (TopicIF) topicmap.getObjectById(category);
    if (thecat.getTypes().contains(cat)) {
      tolog += "belongs-to($CAT : containee, @" + category +" : container)";
      tolog += ",\nbelongs-to($GROUP : containee, $CAT : container)";
      tolog += ",\nbelongs-to($SCRIPT : containee, $GROUP : container)";
    } else if (thecat.getTypes().contains(grp)) {
      tolog += "belongs-to($GROUP : containee, @" + category +" : container)";
      tolog += ",\nbelongs-to($SCRIPT : containee, $GROUP : container)";
    } else 
      tolog += "belongs-to($SCRIPT : containee, @" + category +" : container)";
  }

  String country = request.getParameter("country");
  if (!country.equals("any")) {
    tolog += ",\n";
    tolog += "spoken-in($LANGUAGE : language, @" + country + " : country), \n";
    tolog += "written-in($LANGUAGE : language, $SCRIPT : script)";
  }

  String direction = request.getParameter("direction");
  if (!direction.equals("any")) {
    tolog += ",\n";
    tolog += "writing-direction($SCRIPT : script, @" + direction + 
             " : direction)";
  }

  tolog += "\norder by $SCRIPT?";
%>

<p>
Your query matched the following scripts:
</p>

<ul>
<%
  QueryProcessor proc = new QueryProcessor(topicmap);
  QueryResultIF result = proc.execute(tolog);
  StringifierIF str = TopicStringifiers.getDefaultStringifier();

  while (result.next()) {
    TopicIF script = (TopicIF) result.getValue("SCRIPT");
%>
  <li><a href="script.jsp?id=<%= script.getObjectId() %>"><%= str.toString(script) %></a></li>
<%
  }
%>
</ul>

<p>
The query results were produced from the following tolog query:
</p>

<pre><%= tolog %></pre>

</template:put>
</template:insert>