<%@ page import="
  java.io.StringReader,
  java.util.*,
  net.ontopia.topicmaps.core.*,
  net.ontopia.topicmaps.utils.ltm.*,
  net.ontopia.infoset.core.LocatorIF,
  net.ontopia.topicmaps.nav2.utils.*,
  net.ontopia.topicmaps.nav2.core.*"
%>

<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output' %>

<logic:context tmparam="tm" settm="topicmap">

<template:insert template='/views/template_%view%.jsp'>

<template:put name='title' body='true'>[Omnigator] LTM evaluation</template:put>

<template:put name='heading' body='true'>
  <h1 class="boxed">LTM content added</h1>
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
<%
 // Tomcat doesn't handle character encoding in requests properly,
 // so we need to do it. See bugs #622 and #1278.

 NavigatorApplicationIF navApp = NavigatorUtils.getNavigatorApplication(pageContext);
 String charenc = navApp.getConfiguration().getProperty("defaultCharacterEncoding");
 if (charenc != null && charenc.trim().equals(""))
   charenc = null;
 if (charenc != null)
   request.setCharacterEncoding(charenc); // weird that we need to do this

 TopicMapIF tm = navApp.getTopicMapById(request.getParameter("tm"));
 try {
   LocatorIF base = tm.getStore().getBaseAddress();
   StringReader in = new StringReader(request.getParameter("ltm"));
   LTMTopicMapReader reader = new LTMTopicMapReader(in, base);
   reader.importInto(tm);
   tm.getStore().commit();
 } catch (Exception e) {
   tm.getStore().abort();
   throw e;
 } finally {
   navApp.returnTopicMap(tm);
 }
%>

<p>
Your content has been successfully added to the topic map.
</p>

</template:put>



<%-- ============== Outsourced application wide standards ============== --%>
<template:put name='application' content='/fragments/application.jsp'/>
<template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
<template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

</template:insert>
</logic:context>
