<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template' prefix='template' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%-- Simple template page with no topicmap specific information --%>
<template:insert template='/views/template_no_frames.jsp'>

  <template:put name='title' body='true'><%-- Window Title --%></template:put>
  <template:put name='head' body='true'></template:put>
  <template:put name='application' content='/fragments/application.jsp'/>
  <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>

  <template:put name='toplinks' body='true'>
    <%-- place your links here --%>
  </template:put>
	
  <template:put name='heading' body='true'>
    <%-- headline --%>	  
  </template:put>
	
  <template:put name='intro' body='true'></template:put>

  <%-- =============================================================== --%>
  <template:put name='navigation' body='true'>
    <%-- left hand side: navigation --%>
  </template:put>

  <%-- =============================================================== --%>
  <template:put name='content' body='true'>
    <%-- right hand side: content --%>
  </template:put>
	
  <template:put name='outro' body='true'></template:put>
  <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

</template:insert>
