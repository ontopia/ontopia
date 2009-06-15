<%@ page
    errorPage="/models/error.jsp"
    import="java.util.Map,
            net.ontopia.topicmaps.entry.TopicMapRegistry,
            net.ontopia.topicmaps.nav.generic.*,
            net.ontopia.topicmaps.nav.conf.*"
%>
<%@ taglib uri='http://www.ontopia.net/library/jsp/taglibs/topicmap.tld' prefix='tm' %>
<%@ taglib uri='http://www.ontopia.net/library/jsp/taglibs/render.tld' prefix='rn' %>

<%
  ControlServlet.initRequest(request, application);

  String pageName = request.getServletPath();
  pageName = pageName.substring( pageName.lastIndexOf('/')+1);
%>
