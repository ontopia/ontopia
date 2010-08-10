<%
/**
 * This page renders webcontent, if the id's have been provided in the url. 
 * Otherwise the page will be empty.
 */
%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portlet.journal.model.JournalArticle" %>
<%@ page import="com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil"%>

<portlet:defineObjects />

<%
 // get the url as typed in by the user
 String queryString = (String)renderRequest.getAttribute("javax.servlet.forward.query_string");
 Map<String, String> params = util.PortletUtils.parseQueryString(queryString);
 String articleid = params.get("article");
 String groupid = params.get("group");

 long groupidNumber = Long.parseLong(groupid);

 JournalArticle article = JournalArticleLocalServiceUtil.getArticle(groupidNumber, articleid);
 long articleResourcePrimKey = article.getResourcePrimKey();

%>
<h3><%= article.getTitle() %></h3>
<liferay-ui:journal-article articleResourcePrimKey="<%= articleResourcePrimKey %>" />

<%
  }
%>