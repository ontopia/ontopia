<%
/**
 * A portlet which given
 *
 *   a tolog query,
 *   a template
 *
 * displays a list of articles produced by the query using the given
 * template. The template is optional.
 */
%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog'%>

<%@ page import="com.liferay.portlet.journal.model.JournalArticle" %>
<%@ page import="com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil"%>
<%@ page import="com.liferay.portlet.journal.model.JournalArticleDisplay" %>
<%@ page import="com.liferay.portlet.journal.model.JournalArticleResource" %>
<%@ page import="com.liferay.portlet.journal.service.JournalArticleResourceLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.journalcontent.util.JournalContentUtil" %>
<%@ page import="net.ontopia.topicmaps.nav2.utils.ContextUtils" %>

<%
 String tmid = tm.OntopiaAdapter.getInstance(true).getTopicMapId();
%>

<portlet:defineObjects />

<c:choose>
<c:when test="${topic != null}">

<tolog:context topicmap="<%= tmid %>">

<%
  String query = 
    "using lr for i\"http://psi.ontopia.net/liferay/\" " +
    "lr:is-about(%topic% : lr:concept, $ART : lr:work), " +
    "lr:article_id($ART, $ARTID), " +
    "lr:contains($ART : lr:containee, $GRP : lr:container), " +
    "lr:groupid($GRP, $GRPID)?";

  String templateid = (String) request.getAttribute("templateid");
%>
<tolog:query name="thequery"><c:out value="${query}" escapeXml="false"/></tolog:query>
<tolog:foreach query="thequery">
  <%
    String artid = (String) ContextUtils.getSingleValue("ARTID", pageContext);
    long grpid = Long.parseLong((String) ContextUtils.getSingleValue("GRPID", pageContext));
  %>

  <liferay-ui:journal-article 
    articleId="<%= artid %>" 
    groupId="<%= grpid %>" 
    templateId="<%= templateid %>"/>

</tolog:foreach>

</tolog:context>

</c:when>
<c:otherwise>

<p>No topic found!</p>

</c:otherwise>
</c:choose>