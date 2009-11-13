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
<template:put name='title' body='true'>[Omnigator] Query</template:put>

<template:put name='heading' body='true'>
  <script language="JavaScript">
	  function changeDescription(proc) {
		  url = proc + '.jsp?tm=<%= tmid %>'
		  
		  var pageRequest = false // variable to hold ajax object

		  if (!pageRequest && typeof XMLHttpRequest != 'undefined')
		     pageRequest = new XMLHttpRequest()

		  if (pageRequest){ //if pageRequest is not false
		     pageRequest.open('GET', url, false) //get page synchronously 
		     pageRequest.send(null)
		     document.getElementById('ProcessorDescription').innerHTML=pageRequest.responseText
		     dhtmlLoadScript('query-samples-' + proc + '.js')
		     document.queryform.query.value = ''
		  }
	  }

	  function dhtmlLoadScript(url)
	  {
	     var e = document.createElement("script");
	     e.src = url;
	     e.type="text/javascript";
	     document.getElementsByTagName("head")[0].appendChild(e);
	  }

	  window.onload = function () {
		changeDescription('tolog')
	  }
  </script>
  <h1 class="boxed">Query</h1>
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
<form action="query.jsp" method="get" name="queryform">

<p><b>Query language:</b>
<select name="processor"
 onChange='javascript:changeDescription(this.options[this.selectedIndex].value)'
 tabindex='1'>
    <option value="tolog">Tolog (builtin)</option>
<%
// look for the toma QueryProcessor implementation
try {
  Class.forName("net.ontopia.topicmaps.query.toma.impl.basic.BasicQueryProcessor", true, Thread.currentThread().getContextClassLoader());
%>
    <option value="toma">Toma</option>
<%  
} catch (ClassNotFoundException e) {}
%>
    
</select> 

<div id="ProcessorDescription">
</div>
</template:put>

<template:put name="content" body="true">
<table width="100%">
<tr><td>
<b>Query:</b><br>
<font size="+1"><textarea name="query" rows="15" cols="62" tabindex="2"><%= (request.getParameter("query") == null ? "" : request.getParameter("query")) %></textarea></font>

<tr><td>
    <input type=submit name=search value="Search" tabindex="3">
    <input type=submit name=update value="Update" tabindex="4">
    <input type=radio name="executeQuery" value="normal" checked="checked"> Run query
    <input type=radio name="executeQuery" value="trace"> Show trace
    <input type=radio name="executeQuery" value="analyze"> Analyze query

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
