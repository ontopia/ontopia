<%@ page
    import="
    net.ontopia.topicmaps.nav2.core.*,
    net.ontopia.topicmaps.nav2.utils.FrameworkUtils"
%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue'   prefix='tm'        %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output' %>

<%
  String tmid = request.getParameter("tm");
%>

<logic:context tmparam="tm" settm="topicmap">
  <template:insert template='/views/template_%view%.jsp'>

  <template:put name='title' body='true'>[Admin] Query</template:put>

  <template:put name='heading' body='true'>
    <h1 class="boxed">Query</h1>
  </template:put>

  <template:put name='manageLinks' body='true'>
    <tr valign="top">
      <td class="plugins" colspan=2>
        <a href="/manage/manage.jsp">Manage</a>
      </td>
    </tr>
  </template:put>

<%
UserIF user = FrameworkUtils.getUser(pageContext);
String skin = user.getSkin();
%>
<template:put name='skin' body='true'>skins/<%= skin %>.css</template:put>

<template:put name='plugins' body='true'>
  <framework:pluginList separator=" | " group="topicmap"/>
</template:put>

<template:put name="content" body="true">
</template:put>

<template:put name="navigation" body="true">
<form action="query.jsp" method="get" name="queryform">
<table width="100%">
<tr><td>
<font size="+1"><textarea name="query" rows="15" cols="62" tabindex="2"><%= (request.getParameter("query") == null ? "" : request.getParameter("query")) %></textarea></font>

<tr><td>
    <input type=submit value="Search" tabindex="3">
    <input type=reset  value="Reset">
    <input type=checkbox name="trace"> Show trace

</table>
<input type=hidden value="<%= tmid %>" name=tm>
</form>
</template:put>
    <template:put name='outro' body='true'></template:put>

    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

</template:insert>
</logic:context>
