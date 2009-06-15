<%@ page
    import="
    net.ontopia.topicmaps.nav2.core.*,
    net.ontopia.topicmaps.nav2.utils.FrameworkUtils"
%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic' %>

<logic:context tmparam="tm" settm="topicmap">

<template:insert template='/views/template_%view%.jsp'>

<template:put name='title' body='true'>[Omnigator] Add LTM</template:put>

<template:put name='heading' body='true'>
  <h1 class="boxed">Add to topic map with LTM</h1>
</template:put>
<%
UserIF user = FrameworkUtils.getUser(pageContext);
String skin = user.getSkin();
%>
<template:put name='skin' body='true'>skins/<%= skin %>.css</template:put>

<template:put name='plugins' body='true'>
  <framework:pluginList separator=" | " group="topicmap"/>
</template:put>

<template:put name="navigation" body="true">
  <p class=text>This plugin lets you add contents to an existing topic map by
  writing the new content in 
  <a href="http://www.ontopia.net/download/ltm.html">LTM</a> format.
  The LTM content is evaluated against the current topic map and
  inserted into it.
</template:put>

<template:put name="content" body="true">
  <form method=post action="ltm_eval.jsp">
  <input type=hidden name=tm value='<%= request.getParameter("tm") %>'>
  <textarea name="ltm" cols=60 rows=20>
  </textarea>
  <p><input type=submit value=Add></p>
  </form>
</template:put>

<%-- ============== Outsourced application wide standards ============== --%>
<template:put name='application' content='/fragments/application.jsp'/>
<template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
<template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

</template:insert>
</logic:context>
