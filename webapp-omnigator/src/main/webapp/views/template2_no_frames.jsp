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
    <link rel='stylesheet' href='/omnigator/<%= skinPath %>' >
    <template:get name='head'/>
  </head>
  <body>
    <!-- header table -->
    <table width="100%" cellspacing="0" cellpadding="5" border="0">
      <tr>
        <td colspan="2" class="plugins">
          <template:get name='plugins'/>
        </td>
      </tr>
    </table>
    <!-- content table: heading + intro + navigation + content + outro -->
    <table width="100%" cellspacing="0" cellpadding="5" border="0">
      <!-- navigation and content -->
      <tr valign="top">
        <td class="content">
          <template:get name='content'/>&nbsp;
        </td>
      </tr>
    </table>
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
