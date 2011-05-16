<%@ page import="
  java.io.StringWriter,
  net.ontopia.utils.StringifierIF,
  net.ontopia.topicmaps.core.*,
  net.ontopia.topicmaps.nav2.core.*,
  net.ontopia.topicmaps.nav2.utils.NavigatorUtils,
  net.ontopia.topicmaps.nav2.utils.FrameworkUtils,
  net.ontopia.topicmaps.query.core.*,
  net.ontopia.topicmaps.query.utils.QueryUtils,
  net.ontopia.topicmaps.query.impl.basic.QueryTracer,
  net.ontopia.topicmaps.query.impl.utils.SimpleQueryTracer,
  net.ontopia.topicmaps.query.parser.*,
  net.ontopia.topicmaps.utils.TopicStringifiers,
  java.lang.reflect.Constructor"
%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>

<framework:response/>

<logic:context tmparam="tm" settm="topicmap">
  <template:insert template='/views/template_%view%.jsp'>
    <template:put name='title' body='true'>[Omnigator] Query results</template:put>

    <template:put name='heading' body='true'>
      <h1 class="boxed">Query results</h1>
    </template:put>

    <template:put name='plugins' body='true'>
      <framework:pluginList separator=" | " group="topicmap"/>
    </template:put>

<%
// ---------------------------------------------------------------
// retrieve configuration
NavigatorApplicationIF navApp = NavigatorUtils.getNavigatorApplication(pageContext);

// Tomcat doesn't handle character encoding in requests properly,
// so we need to do it. See bug #1420.
String charenc = navApp.getConfiguration().getProperty("defaultCharacterEncoding");
if (charenc != null && charenc.trim().equals(""))
  charenc = null;
if (charenc != null)
  request.setCharacterEncoding(charenc); // weird that we need to do this

String executeQuery = request.getParameter("executeQuery");
String processor = request.getParameter("processor");
boolean search = request.getParameter("search") != null;

StringWriter trace = null;
SimpleQueryTracer tracer = null;
boolean analyzeQuery = false;
boolean update = request.getParameter("update") != null;

if (executeQuery != null) {
  if (executeQuery.equals("trace")) {
    trace = new StringWriter();
    tracer = new SimpleQueryTracer(trace);
  } else if (executeQuery.equals("analyze")) {
    analyzeQuery = true;
  } 
}

String tmid = request.getParameter("tm");
String query = request.getParameter("query");

String[] variables = null;
long millis = 0;
int rows = 0;
ParsedStatementIF stmt = null;
String error = null;
StringWriter tmp = new StringWriter();

// get the topic map
TopicMapIF topicmap = navApp.getTopicMapById(tmid, !update);

try {
  UserIF user = FrameworkUtils.getUser(pageContext);
  String model = "complete";
  if (user != null)
    model = user.getModel();

  QueryProcessorFactoryIF factory = QueryUtils.getQueryProcessorFactory(processor);
  QueryProcessorIF proc = factory.createQueryProcessor(topicmap, null, null);

  if (update) {
    stmt = proc.parseUpdate(query);
    if (tracer != null)
      QueryTracer.addListener(tracer);
    if (!analyzeQuery) {
      try {
        millis = System.currentTimeMillis();
        rows = ((ParsedModificationStatementIF) stmt).update();
        millis = System.currentTimeMillis() - millis;
      } finally {
      if (tracer != null)
        QueryTracer.removeListener(tracer);
      }
      tmp.write("<p>Rows updated: " + rows + "</p>");
    }
  } else {  
    stmt = proc.parse(query);
    QueryResultIF result = null;
  
    if (!analyzeQuery && stmt != null) {
      StringifierIF str = TopicStringifiers.getDefaultStringifier();
    
      if (tracer != null)
        QueryTracer.addListener(tracer);
      try {
        millis = System.currentTimeMillis();
        result = ((ParsedQueryIF) stmt).execute();
        millis = System.currentTimeMillis() - millis;
      } finally {
        if (tracer != null)
          QueryTracer.removeListener(tracer);
      }
      variables = result.getColumnNames();
      
      if (variables.length == 0) {
        tmp.write("<tr valign='top'>");
        if (result.next()) {
          // result: true
          rows++;
          tmp.write("<td><i>TRUE</i></td>");
        } else {
          // result: false
          tmp.write("<td><i>FALSE</i></td>");
        }
        tmp.write("</tr>");
      } else {
        tmp.write("<table class='text' border='1' cellpadding='2'>\n");
        tmp.write("<tr>");
	for (int ix = 0; ix < variables.length; ix++)
	  tmp.write("<th>" + variables[ix] + "</th>");
        tmp.write("</tr>");

        while (result.next()) {
          tmp.write("<tr valign='top'>");
          rows++;
          Object[] row = result.getValues();
          for (int ix = 0; ix < variables.length; ix++) {
            if (row[ix] == null)
              tmp.write("<td><i>null</i></td>");
            else if (row[ix] instanceof TopicIF)
              tmp.write("<td><a href=\"../../models/topic_" + model + ".jsp?tm=" + tmid  + "&id=" + ((TopicIF) row[ix]).getObjectId() + "\">" + str.toString(row[ix]) + "</a></td>");
            else if (row[ix] instanceof AssociationIF) {
              String objid = ((AssociationIF) row[ix]).getObjectId();
              tmp.write("<td><a href=\"../../models/association_" + model + ".jsp?tm=" + tmid  + "&id=" + objid + "\">Association " + objid + "</a></td>");
            } else if (row[ix] instanceof TopicNameIF) {
              String name = ((TopicNameIF) row[ix]).getValue();
              tmp.write("<td>" + name + "</td>");
            } else
              tmp.write("<td>" + row[ix] + "</td>");
          }
          tmp.write("</tr>");
        }
	tmp.write("</table>");
      }
    }
  }

} catch (InvalidQueryException e) {
  error = "<p>Error: " + e.getMessage() + "</p>";
  millis = 0;
} finally {
  // transactions
  if (update) {
    if (error == null) 
      topicmap.getStore().commit();
    else
      topicmap.getStore().abort();
  }
  navApp.returnTopicMap(topicmap);
}
%>

<template:put name="navigation" body="true">

<p><b>Query:</b><br>
<pre><%= query %></pre></p>

<% if (!analyzeQuery) { %>
<table>
<tr><th align=left>Execution time</th> <td><%= millis %> millisecs</td></tr>
<tr><th align=left>Result rows</th>    <td><%= rows %></td></tr>
</table>
<% } %>

<% if (analyzeQuery) { %>
  <p><b>Parsed query:</b></p>

  <pre><%= stmt %></pre>
<% } else if (!update) { %>
  <p>
  <form method=get action="csv.jsp">
    <input type=submit value="Export to CSV">
    <input type=hidden name=tm value="<%= tmid %>">
    <input type=hidden name=processor value="<%= processor %>">
    <input type=hidden name=query value="<%= query.replace("\"", "&#34;") %>">
  </form>
<% } %>

</template:put>


<template:put name="content" body="true">

<% if (stmt != null) { %>

  <%= tmp %>

<%
  } 
  if (error != null) {
%><p><%= error %></p><%
  }
%>

<% if (tracer != null) { %>
  <p>&nbsp;</p>
  <h2>Query trace</h2>
  <pre>
  <%= trace %>
  </pre>
<% } %>

</template:put>

  <template:put name='outro' body='true'></template:put>

  <%-- ============== Outsourced application wide standards ============== --%>
  <template:put name='application' content='/fragments/application.jsp'/>
  <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
  <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>
</template:insert>
</logic:context>
