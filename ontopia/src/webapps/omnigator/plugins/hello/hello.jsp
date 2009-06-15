<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>

<template:insert template='/views/template_%view%.jsp'>
  <template:put name='title' body='true'>[Omnigator] Hello!</template:put>

  <template:put name='heading' body='true'>
    <h1 class="boxed">Hello!</h1>
  </template:put>

  <template:put name='toplinks' body='true'>
    <a href="../../models/index.jsp">Welcome</a>
  </template:put>

  <template:put name='navigation' body='true'>
    <p>
	Hello, world!
    </p>      
  </template:put>
    
  <%-- ============== Outsourced application-wide standards ============== --%>
  <template:put name='application' content='/fragments/application.jsp'/>
  <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
  <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>
    
</template:insert> 
