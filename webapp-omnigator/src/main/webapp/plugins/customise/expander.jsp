<%@ page language="java" %>

<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%
/**
 * This page takes a URL in the request parameter ("redirect")
 * and redirects to the top frame.
 * 
 * This page will be called when a user changes their view
 * and the view change has been nested in a frameset, eg. info window
 * in the frameset version of the site, and the new view needs to be
 * targetted to the _top, eg no_frames topic.jsp.
 */
%>

  <template:insert template='/views/template_%view%.jsp'>
  
    <template:put name='title' body='true'>User preferences updated</template:put>
    <template:put name='head' body='true'>
      <meta http-equiv="Refresh" content="2;URL=<%=request.getParameter("redirect")%>">
    </template:put>
  
    <template:put name='toplinks' body='true'>
      <%-- place your links here --%>
    </template:put>
  	  
    <template:put name='heading' body='true'>	  
      <h1 class="boxed">User preferences updated</h1>
    </template:put>
  	  
    <template:put name='intro' body='true'>
      <p>Thank you for updating your preferences. Enabling Javascript
      and CSS on your browser will improve your topic map experience.</p>
      <p><b><a href="<%= request.getParameter("redirect") %>"
      target="_top">Continue</b></p>
    </template:put>
    	  
    <template:put name='outro' body='true'><%-- unused --%></template:put>

    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>
	      
  </template:insert>
