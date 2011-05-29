<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" import="net.ontopia.topicmaps.nav2.core.*,
                                 net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag,
                                 net.ontopia.topicmaps.nav2.plugins.PluginIF,
                                 net.ontopia.topicmaps.nav2.utils.NavigatorUtils,
                                 net.ontopia.topicmaps.nav2.utils.FrameworkUtils,
                                 net.ontopia.topicmaps.core.TopicMapIF,
                                 net.ontopia.topicmaps.entry.*,
                                 java.io.File,
                                 javax.servlet.ServletContext" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template' prefix='template'   %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%-- Template Page - View: 'no_frames' --%>

<html>
  <head>
    <title><template:get name='title'/></title>
    <%
      String skin = UserIF.DEFAULT_SKIN;
      // try to retrieve skin from user object in session scope
      UserIF user = (UserIF) session.getAttribute(NavigatorApplicationIF.USER_KEY);
      if (user != null && user.getSkin() != null)
        skin = user.getSkin();
      String skinPath = "skins/" + skin + ".css";
      ContextTag context = FrameworkUtils.getContextTag(pageContext);
    %>
    <link rel='stylesheet' href='/omnigator/<%= skinPath %>' >
    <template:get name='head'/>
  </head>
  <body>
    <!-- header table -->
    <div id="header">
      <table width="100%" cellspacing="0" cellpadding="0" border="0">
      <tbody>
        <tr>
          <td class="topLinks">

            <!-- Left -->
            <img src="/omnigator/images/omnigator.gif" alt="Omnigator logo"/><br/>

            <template:get name='toplinks'/>
          </td>
          <td align="right" valign="top" width="300">
            <!-- Right -->
            <div id="oksmenu">
              <a href="/" title="Go to application home page.">Home</a>&nbsp;|&nbsp;<a href="/manage/manage.jsp" title="Refresh sources, reload settings, manage plug-ins, create indexes, etc.">Manage</a>&nbsp;|&nbsp;<a href="http://www.ontopia.net/" title="Go to the Ontopia website.">Website</a>&nbsp;|&nbsp;<a href="http://groups.google.com/group/ontopia" title="Mailing list.">Support</a>&nbsp;|&nbsp;<a href="/about.jsp">About</a>
            <%
              String tm = request.getParameter("tm");
              if (tm != null && tm.equals("null"))
                tm = null;
            %>
            <% if (tm != null) { %>
            <table id="activetm" width="100%">
              <tr>
                <td id="activetmtitle">
                  <a href="/omnigator/models/topicmap_complete.jsp?tm=<%=tm%>">Browsing <%= tm %></a>
                </td>
              </tr>
              <tr>
                <td id="activetmfunc">
                  <a href="/omnigator/models/index.jsp" id="tmfuncopen">Open...</a>
                  <%
                     String id = request.getParameter("id");
                     String currentURI = request.getRequestURI();
                     String query = request.getQueryString();

                     String link = "/manage/manage.jsp"
                         + "?action=reload";
                     link += "&id=" + tm;
                     if (currentURI != null)
                       link += "&redirect=" + currentURI + "?tm%3D" + tm;
                     if (id != null)
                       link += "%26id%3D" + id + "&objectid=" + id;
                   %>
                     <a href="<%= link %>" id="tmfuncreload">Reload</a>
                   <%
                     boolean exists = false;
                     ServletContext ctxt = pageContext.getServletContext();
                     String path = ctxt.getRealPath("/") + "WEB-INF/indexes/" + tm;

TopicMapRepositoryIF rep = NavigatorUtils.getTopicMapRepository(pageContext);
TopicMapReferenceIF ref = rep.getReferenceByKey(tm);
if (ref instanceof AbstractOntopolyURLReference) {
  AbstractOntopolyURLReference oref = (AbstractOntopolyURLReference) ref;
  if (oref.getIndexDirectory() != null)
    path = oref.getIndexDirectory() + File.separator + tm;
}
                      
                     if (context != null) {
                       TopicMapIF topicmap = context.getTopicMap();
                       if (topicmap != null && topicmap.getStore().getProperty("net.ontopia.infoset.fulltext.impl.rdbms.RDBMSSearcher.type") != null) {
                         exists = true;
                       } else {
                         exists = new File(path).exists();
                       }
                     }
                     if (exists) {
                  %>
                  <form action="/omnigator/plugins/fulltext/search.jsp" method="get" style="display:inline; vertical-align:middle; padding-right:2px;" title="Full-text search of the topic map resources.">
                    <input value="<%= tm %>" name="tm" type="hidden"><input name="query" size="10" value="" type="text" style="font-size:75%;"/><input value="Find" type="submit" style="font-size:75%; font-weight:bold;">
                  </form>
                  <% } else {

                    if (context != null) {
                      out.println("<span title=\"No index found at: " + path + "\"><a href='" +
                        "/manage/plugins/ftadmin/index_admin.jsp'>Not indexed</a></span>");
                    }
                  } %>
                </td>
              </tr>
            </table>
            <% } %>
           </div>
          </td>
        </tr>
      </tbody>
      </table>
    </div>
    <!-- content table: heading + intro + navigation + content + outro -->
    <div id="contentarea">
    <table class="contentTable" width="100%" cellspacing="0" cellpadding="10" border="0">
      <!-- heading and intro -->
      <tr valign="top">
        <td class="plugins" colspan=2>
          <template:get name="plugins"/>
        </td>
      </tr>
      <tr valign="top">
        <td class="intro" colspan="2">
          <template:get name='heading'/>
          <template:get name='intro'/>
        </td>
      </tr>
      <!-- navigation and content -->
      <tr valign="top">
        <td class="navigation" width="35%">
          <template:get name='navigation'/>&nbsp;
        </td>
        <td class="content" width="65%">
          <template:get name='content'/>&nbsp;
        </td>
      </tr>
      <!-- outro -->
      <tr valign="top">
        <td class="outro" colspan="2">
          <template:get name='outro'/>
        </td>
      </tr>
    </table>
    </div>
    <!-- footer table -->
    <table class="footerTable" width="100%" cellspacing="0" cellpadding="5" border="0">
      <tr>
        <td class="footer">
          <template:get name='footer-tagline'/>
        </td>
      </tr>
    </table>
  </body>
</html>
