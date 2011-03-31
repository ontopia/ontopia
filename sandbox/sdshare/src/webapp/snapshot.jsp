<%@ page 
  language="java" 
  contentType="application/x-tm+xml; charset=utf-8"
  import="net.ontopia.topicmaps.utils.sdshare.*,
          net.ontopia.topicmaps.entry.*,
	  net.ontopia.topicmaps.core.*"
%><%
 
  String tmid = request.getParameter("topicmap");
  String synid = request.getParameter("syntax");
  if (synid == null)
    synid = "xtm";
  SyntaxIF syntax = StartUpServlet.getSyntax(synid);

  TopicMapRepositoryIF rep = TopicMaps.getRepository();
  TopicMapReferenceIF ref = rep.getReferenceByKey(tmid);
  TopicMapIF tm = ref.createStore(true).getTopicMap();

  TopicMapWriterIF writer = syntax.getWriter(response.getOutputStream(), "utf-8");
  writer.write(tm);

%>