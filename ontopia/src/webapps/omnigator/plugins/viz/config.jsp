<%@ page language="java"
    import="java.io.*,
            net.ontopia.utils.StreamUtils"
%><%
  response.setContentType("text/xml; charset=utf-8");

  String tmid = request.getParameter("tm");
  // FIXME: verify that it doesn't contain slashes or os.sep characters

  InputStream in = pageContext.getServletContext().getResourceAsStream("/WEB-INF/topicmaps/" + tmid + ".viz");
  if (in != null)
    StreamUtils.transfer(new InputStreamReader(in, "utf-8"), out);
  else
    out.write("<topicMap/>");
%>
