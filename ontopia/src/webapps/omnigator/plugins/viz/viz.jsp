<HTML>
<BODY onResize="resize()" onLoad="resize()">
<SCRIPT LANGUAGE="JavaScript">
  /**
    * Resizes the vizlet relative to the browser window.
    * This method gets called whenever the browser window
    * changes size, as relative height (percentage) doesn't
    * work, i.e. is not a documented/widely supported feature 
    * of the applet tag in html.
    */
  function resize() {
    var w_newWidth,w_newHeight;
    // var w_maxWidth=1600, w_maxHeight=1200;
    // There are different ways of getting the browser width
    // and height in IE vs other browsers.
    if (navigator.appName.indexOf("Microsoft") != -1) {
      // Get browser width and height in Internet Explorer
      w_newWidth=document.body.clientWidth;
      w_newHeight=document.body.clientHeight;
    } else {
      // Get browser width and height in other browsers.
      // NOTE: This was copied from a program clearly tailored
      // to Netscape.
      // I've tested it in Firefox and Opera, and it works fine,
      // but it may be possible to do some browser-dependent
      // fine-tuning here, if desired.
      var netscapeScrollWidth=15;
      w_newWidth=window.innerWidth - netscapeScrollWidth;
      w_newHeight=window.innerHeight - netscapeScrollWidth;
    }
    // The use of max-width was there when I downloaded this code.
    // I don't really see the need to have bounds, and the initial
    // width was too small (gave white space area when maximizing
    // the window.
    // if (w_newWidth > w_maxWidth)
    //     w_newWidth = w_maxWidth;
    // if (w_newHeight > w_maxHeight)
    //     w_newHeight = w_maxHeight;
    document.vizlet.width = w_newWidth - 10;
    document.vizlet.height = w_newHeight - 120;
    window.scroll(0,0);
  }
</SCRIPT>

<%@ page import="net.ontopia.topicmaps.nav2.core.*,
                 net.ontopia.topicmaps.nav2.utils.ContextUtils,
                 net.ontopia.topicmaps.nav2.utils.FrameworkUtils,
                 net.ontopia.topicmaps.core.*,
                 java.util.Iterator,
                 net.ontopia.topicmaps.xml.XTMFragmentExporter,
                 net.ontopia.infoset.core.LocatorIF" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue'   prefix='tm'        %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output' %>

<logic:context tmparam="tm" settm="topicmap">

<template:insert template='/views/template2_%view%.jsp'>
<template:put name='title' body='true'>[Omnigator] Vizigator</template:put>

<%
UserIF user = FrameworkUtils.getUser(pageContext);
String skin = user.getSkin();
%>
<template:put name='skin' body='true'>skins/<%= skin %>.css</template:put>

<template:put name="navigation" body="true">
</template:put>

<template:put name='plugins' body='true'>
  <framework:pluginList separator=" | " group="topicmap"/>
</template:put>

<template:put name="content" body="true">
  <%
    // must have a current topic on this JSP page
    if (request.getParameter("id") != null) {
  %>

  <logic:set name="topic">
    <tm:lookup parameter="id"/>
  </logic:set>

  <logic:if name="topic">
  <logic:then>
  <%
    String tmid = request.getParameter("tm");
    TopicIF topic = (TopicIF) ContextUtils.getSingleValue("topic", pageContext);

    String idtype = "source";
    String idvalue = null;
    Iterator it = topic.getItemIdentifiers().iterator();
    if (it.hasNext())
      idvalue = ((LocatorIF) it.next()).getExternalForm();

    if (idvalue == null) {
      it = topic.getSubjectIdentifiers().iterator();
      if (it.hasNext()) {
        idvalue = ((LocatorIF) it.next()).getExternalForm();
        idtype = "indicator";
      }
    }

    if (idvalue == null) {
      it = topic.getSubjectLocators().iterator();
      if (it.hasNext()) {
        idvalue = ((LocatorIF) it.next()).getExternalForm();
        idtype = "subject";
      }
    }
    
    // last hope, use virtual locator
    if (idvalue == null) {
      idvalue = XTMFragmentExporter.makeVirtualReference(topic, tmid);
      idtype = "source";
    }
    
  %>

  <applet name="vizlet" width="2000" height="2000" alt="Ontopia Vizlet"
          code="net.ontopia.topicmaps.viz.Vizlet.class"
          archive="ontopia-vizlet.jar">
    <param name="tmrap"    value="/omnigator/plugins/viz/">
    <param name="config"   value="/omnigator/plugins/viz/config.jsp?tm=<%= tmid %>">
    <param name="tmid"     value="<%= tmid    %>">
    <param name="idtype"   value="<%= idtype  %>">
    <param name="idvalue"  value="<%= idvalue %>">
    <param name="propTarget"    value="VizletProp">
    <param name="gotoTarget"    value="">
    <param name="controlsVisible"    value="true">
    <param name="locality"    value="1">
    <param name="max-locality"    value="5">

    Apparently you don't have applet support turned on in your
    browser, or your browser does not support Java applets. Please
    turn the support for applets on, and reload this page.

  </applet>
  </logic:then>
  <logic:else>

  <h1>No such topic</h1>

  <p>No topic with ID <tt><%= request.getParameter("id") %></tt> found.</p>

  </logic:else>
  </logic:if>

  <% } else {
     // we go here if no 'id' parameter %>

  <p>No 'id' parameter was found. This plug-in will only visualize the
  topic map starting from a specific topic, and so this parameter is
  required. This means, for example, that you cannot put this plug-in
  on the topic map page.</p>

  <% } %>
</template:put>

    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

  </template:insert>
</logic:context>
</body>
</html>
