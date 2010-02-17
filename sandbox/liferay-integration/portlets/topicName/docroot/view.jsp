<%
/**
* View Jsp for TopicName Portlet. This page shows information on a given topic.
*/
%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog'%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@page import = "com.liferay.portal.util.PortalUtil" %>
<%@page import = "java.util.Enumeration" %>


<portlet:defineObjects />
<tolog:context topicmap="liferay.ltm">
<%
String topicid = (String) request.getAttribute("topic");
if(topicid == null){
    System.out.println("ToicName: topic parameter as not been passed!");
    out.println("Please provide a topic id to this url!");
} 
    String query = "object-id($topic, \"" + topicid + "\")?";
%>
    <tolog:set var="topic" query="<%=query%>"/>
    This is the page for the topic:
    <h3><tolog:out var="topic"/></h3>
    <tolog:set var="psi" query="subject-identifier(%topic%, $psi)?" />
    Read more on:
    <a href="<tolog:out var="psi"/>"><tolog:out var="psi"/></a>

</tolog:context>
