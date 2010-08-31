<%--

 This Page will display the tags for an article by using the "related
 topics" taglibs from ontopia
 
--%><%
 String tmid = tm.OntopiaAdapter.getInstance(true).getTopicMapId();
%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/portlets" prefix="portal" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog'%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ page import = "net.ontopia.topicmaps.core.TopicIF" %>
<%@ page import = "net.ontopia.topicmaps.nav2.utils.ContextUtils" %>

<portlet:defineObjects />

<c:choose>
<c:when test="${topic != null}">

<tolog:context topicmap="<%= tmid %>">
  <!-- TODO: Take parameter and check whether white or blacklisting should be done (use appropriate tags then)-->
  <!-- topic is to contain a topic object, that has been passed from the portlets java code, assocTypes contains a set of association type -->
  <portal:related topic="topic" var="headings" excludeAssociations="assocTypes">
    <ul>
      <c:forEach items="${headings}" var="heading">
        <!-- Display association type name -->
        <li><b><c:out value="${heading.title}"/></b></li>
        <ul>
          <c:forEach items="${heading.children}" var="assoc">
            <c:set value="${assoc.player}" var="player"/>
            <%
            // get the topic we want to display next as a TopicIF object
            TopicIF t =(TopicIF) pageContext.getAttribute("player");
	    String url = util.PortletUtils.makeLinkTo(t);
            %>
            <!-- Display player's name plus a clickable link to another page -->
            <li><a href="<%= url %>"><tolog:out var="assoc.player"/></a></li>
          </c:forEach>
        </ul>
      </c:forEach>
    </ul>
  </portal:related>
</tolog:context>

</c:when>
<c:otherwise>

<p>No topic found!</p>

</c:otherwise>
</c:choose>