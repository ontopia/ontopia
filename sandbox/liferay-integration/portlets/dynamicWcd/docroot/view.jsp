<%
/**
 * This page renders webcontent, if the id's have been provided in the url. Otherwise the page will be empty.
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
        if(queryString != null){
            String articleid = queryString.substring(queryString.lastIndexOf("article=")+"article=".length()); // get to the article id number
            if(articleid.indexOf("&") != -1){ // there are more parameters
                articleid = articleid.substring(0, articleid.indexOf("&")); // the next ampersand is the delimiter for the article id
            } 

            String groupid = queryString.substring(queryString.lastIndexOf("group=")+"group=".length()); // get to the topic id number
            if(groupid.indexOf("&") != -1){ // there are more parameters
                groupid = groupid.substring(0, groupid.indexOf("&")); // the next ampersand is the delimiter for the topic id
            }

            long groupidNumber = Long.parseLong(groupid);

            JournalArticle article = JournalArticleLocalServiceUtil.getArticle(groupidNumber, articleid);
            long articleResourcePrimKey = article.getResourcePrimKey();

    %>
    <h3><%= article.getTitle() %></h3>
    <liferay-ui:journal-article articleResourcePrimKey="<%= articleResourcePrimKey %>" />

    <%
    }
    %>