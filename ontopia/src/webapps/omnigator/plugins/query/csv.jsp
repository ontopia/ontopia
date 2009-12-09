<%@ page import="
  net.ontopia.utils.StringifierIF,
  net.ontopia.topicmaps.core.*,
  net.ontopia.topicmaps.nav2.core.*,
  net.ontopia.topicmaps.nav2.utils.NavigatorUtils,
  net.ontopia.topicmaps.nav2.utils.FrameworkUtils,
  net.ontopia.topicmaps.query.core.QueryProcessorIF,
  net.ontopia.topicmaps.query.core.QueryProcessorFactoryIF,
  net.ontopia.topicmaps.query.core.ParsedQueryIF,
  net.ontopia.topicmaps.query.core.QueryResultIF,
  net.ontopia.topicmaps.query.core.InvalidQueryException,
  net.ontopia.topicmaps.query.utils.QueryUtils,
  net.ontopia.topicmaps.query.parser.*,
  net.ontopia.topicmaps.utils.TopicStringifiers,
  java.lang.reflect.Constructor"
%><%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'  
%><logic:context tmparam="tm" settm="topicmap"><%
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

response.setContentType("text/plain");

String query = request.getParameter("query");
String tmid = request.getParameter("tm");
String processor = request.getParameter("processor");

// get the topic map
TopicMapIF topicmap = navApp.getTopicMapById(tmid);

try {
  QueryProcessorFactoryIF factory = QueryUtils.getQueryProcessorFactory(processor);
  QueryProcessorIF proc = factory.createQueryProcessor(topicmap, null, null);
    
  StringifierIF str = TopicStringifiers.getDefaultStringifier();
  QueryResultIF result = proc.execute(query);
  String[] variables = result.getColumnNames();
    
  if (variables != null) {
    for (int ix = 0; ix < variables.length; ix++) {
      out.write(variables[ix]);
      if (ix + 1 < variables.length)
        out.write(",");
    }
    out.write("\n");
  }

  while (result.next()) {
    Object[] row = result.getValues();
    for (int ix = 0; ix < variables.length; ix++) {
      String value;

      if (row[ix] == null)
        value = "null";
      else if (row[ix] instanceof TopicIF)
        value = str.toString(row[ix]);
      else if (row[ix] instanceof TopicNameIF)
        value = ((TopicNameIF) row[ix]).getValue();
      else if (row[ix] instanceof OccurrenceIF)
        value = ((OccurrenceIF) row[ix]).getValue();
      else if (row[ix] instanceof TMObjectIF)
        value = ((TMObjectIF) row[ix]).getObjectId();
      else
        value = "" + row[ix];
      
      out.write("\"" + value.replace("\"", "\"\"") + "\"");

      if (ix + 1 < variables.length)
        out.write(",");
    }
    out.write("\n");
  }

} finally {
  navApp.returnTopicMap(topicmap);
}
%></logic:context>
