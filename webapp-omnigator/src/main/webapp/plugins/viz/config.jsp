<%@page import="org.apache.commons.io.IOUtils"%>
<%@ page import="java.io.*" %>
<%
  response.setContentType("text/xml; charset=utf-8");

  String tmid = request.getParameter("tm");
  // FIXME: verify that it doesn't contain slashes or os.sep characters

  InputStream in = pageContext.getServletContext().getResourceAsStream("/WEB-INF/topicmaps/" + tmid + ".viz");
  if (in != null)
    IOUtils.copy(new InputStreamReader(in, "utf-8"), out);
  else
    out.write("<topicMap/>");
%>
