<%
/**
 * A portlet for rendering web content as identified by page parameters.
 * Either "topic" for the ID of the article topic, or "article" and
 * "group" for direct access.
 */
%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="java.util.Map" %>
<%@ page import="net.ontopia.topicmaps.core.TopicMapIF" %>
<%@ page import="net.ontopia.topicmaps.query.utils.QueryWrapper" %>
<%@ page import="com.liferay.portlet.journal.model.JournalArticle" %>
<%@ page import="com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil"%>
<%@ page import="tm.OntopiaAdapter"%>

<portlet:defineObjects />

<%
 // get the url as typed in by the user
 String queryString = (String)renderRequest.getAttribute("javax.servlet.forward.query_string");

 Map<String, String> params = util.PortletUtils.parseQueryString(queryString);
 String articleid = params.get("article");
 String groupid = params.get("group");
 String topicid = params.get("topic");

 if (articleid == null && groupid == null && topicid == null) {
%>
 <p>No parameters provided. Need either article and group, or topic.
<%
   return;
 }

 // if a topic id is provided we use that to look up the article and group
 // IDs
 if (topicid != null) {
   TopicMapIF tm = OntopiaAdapter.getInstance(true).getTopicMap();
   QueryWrapper w = new QueryWrapper(tm);
   w.setDeclarations("using lr for i\"http://psi.ontopia.net/liferay/\" ");

   Map p = w.makeParams("id", topicid);

   articleid = w.queryForString("select $AID from " +
                                "object-id($topic, %id%), " +
                                "lr:article_id($topic, $AID)?", p);

   groupid = w.queryForString(
        "select $GID from " +                                
        "object-id($TOPIC, %id%), " +    
        "lr:contains($TOPIC : lr:containee, $GROUP : lr:container), " +
        "lr:groupid($GROUP, $GID)?", p);
 }

 long groupidNumber = Long.parseLong(groupid);

 JournalArticle article = JournalArticleLocalServiceUtil.getArticle(groupidNumber, articleid);
 long articleResourcePrimKey = article.getResourcePrimKey();

%>
<h3><%= article.getTitle() %></h3>
<liferay-ui:journal-article articleResourcePrimKey="<%= articleResourcePrimKey %>" />
