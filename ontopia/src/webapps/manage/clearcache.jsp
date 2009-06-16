<%@ page language="java" 
           import="net.ontopia.topicmaps.core.TopicMapIF,
                   net.ontopia.topicmaps.core.TopicMapStoreIF,
                   net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore,
                   net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag,
                   net.ontopia.topicmaps.nav2.utils.FrameworkUtils" %>

<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template' prefix='template'%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog'%>

<%-- JSP page that clears the shared cache for a given topic map --%>

<tolog:context topicmap='<%= request.getParameter("tm") %>'>
<%
String rp_clear = request.getParameter("clear");
if (rp_clear != null && rp_clear.equals("true")) {
  ContextTag ctag = FrameworkUtils.getContextTag(request);
  TopicMapStoreIF store = ctag.getTopicMap().getStore();
  if (store instanceof RDBMSTopicMapStore) {
    ((RDBMSTopicMapStore)store).clearCache();
    out.write("Cleared shared cache on topic map '" + request.getParameter("tm") + "'");
  } else {
    out.write("Topic map '" + request.getParameter("tm") + "' have no shared cache.");
  }
} else {
  out.write("Not instructed to clear topic map '" + request.getParameter("tm") + "'");
}
%>
</tolog:context>