<%@ page language="java"%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%-- Omnigator: Page handling OOM errors  --%>
  
<template:insert template='/views/template_no_frames.jsp'>

  <template:put name='title' body='true'>[Omnigator] Out of memory</template:put>
  <template:put name='heading' body='true'>
      <h1 class='boxed'>Omnigator: Out of memory</h1>
  </template:put>
  <template:put name='toplinks' body='true'>
      <a href="/omnigator/models/index.jsp">Back to Omnigator's Welcome Page.</a>
  </template:put>
  
  <template:put name='navigation' body='true'>
   <center>
     <table border="0" with="100%"><tr><td>
        <img src='/omnigator/images/baustelle.gif' width="80" height="90" />
      </td></tr></table>
    </center>
  </template:put>
  <template:put name='content' body='true'>
      
    <h3 class="boxed"><font color="red"><strong>Out of memory!</strong></font></h3>

    <p>The Omnigator has run out of memory. Most likely this happened
    because you have too many big topic maps loaded. Here is what you
    can do to solve this problem:</p>

    <ul>
      <li>Go to the <a href="/manage/manage.jsp">Manage
      page</a> and drop the topic maps you don't need to have loaded.

      <li>The Java Virtual Machine will only use memory up to a fixed
      limit, which defaults to 65Mb or 128Mb. You can increase this
      limit to give the Omnigator more memory. Try setting the
      <tt>JAVA_OPTS</tt> environment variable to <tt>-Xmx256M</tt>,
      for example, and restart Tomcat. (Make sure that Tomcat sees the
      new value.)  </ul>

    <p>
    <b>Note:</b> By default the Omnigator uses the in-memory topic map
    backend of Ontopia, which stores the entire topic map in memory.
    This works well, but only until the topic maps reach a certain
    size.  Ontopia includes an RDBMS backend
    which stores the topic maps in a database and only caches parts of
    the topic maps in memory. This backend can handle much larger
    topic maps, and the Omnigator (and other Navigator Framework
    applications) can be configured to use it instead. So if your
    topic map is too big for the Omnigator it need not be too big for
    Ontopia to handle.
    </p>
  </template:put>

  <%-- Constants --%>
  <template:put name='application' content='/fragments/application.jsp'/>
  <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
  <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

  <%-- Unused --%>
  <template:put name='intro' body='true'></template:put>
  <template:put name='outro' body='true'></template:put>
  <template:put name='head' body='true'></template:put>

</template:insert>
