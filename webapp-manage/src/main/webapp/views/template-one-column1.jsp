<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" import="net.ontopia.topicmaps.nav2.core.*" %>
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
    %>
    <link rel='stylesheet' href='/manage/<%= skinPath %>' >
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
            <img src="/manage/images/ontopoly.gif" alt="Omnigator logo"/><br/>

            <template:get name='toplinks'/>
          </td>
          <td align="right" valign="top" width="300">
            <!-- Right -->
            <div id="oksmenu">
              <a href="/" title="Go to application home page.">Home</a>&nbsp;|&nbsp;<a href="/manage/manage.jsp" title="Refresh sources, reload settings, manage plug-ins, create indexes, etc.">Manage</a>&nbsp;|&nbsp;<a href="http://www.ontopia.net/" title="Go to the Ontopia website.">Website</a>&nbsp;|&nbsp;<a href="http://groups.google.com/group/ontopia" title="Mailing list.">Support</a>&nbsp;|&nbsp;<a href="/about.jsp">About</a>
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
      <%-- No plugins in this version of admin console. --%>
      <%-- tr valign="top">
        <td class="plugins" colspan=2>
          <template:get name="plugins"/>
        </td>
      </tr --%>

      <tr valign="top">
        <td class="plugins" colspan=2>
          <template:get name="manageLinks"/>
        </td>
      </tr>

      <tr valign="top">
        <td class="intro" colspan="2">
          <template:get name='heading'/>
        </td>
      </tr>

      <template:get name='content'/>&nbsp;

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
