<%
// View Jsp for TopicName Portlet. This page shows information on a given topic.
String tmid = tm.OntopiaAdapter.getInstance(false).getTopicMapId();
%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog'%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<portlet:defineObjects />

<%
  String topicid = (String) request.getAttribute("topic");
  String query = "object-id($topic, \"" + topicid + "\")?";

  if (topicid != null) {
%>

<tolog:context topicmap="<%= tmid %>">
  <tolog:set var="topic" query="<%=query%>"/>

  <tolog:choose>
    <tolog:when var="topic">
      <h2><tolog:out var="topic"/></h2>
    </tolog:when>

    <tolog:otherwise>
      TopicName: topic parameter as not been passed!
      Please provide a topic id to this url!
    </tolog:otherwise>
  </tolog:choose>
</tolog:context>

<%
 } else {
%><p class=error>No topic ID found.</p>

<% } %>