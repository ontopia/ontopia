<%@ page language="java" 
    import="
    java.util.*, 
    net.ontopia.topicmaps.utils.DuplicateSuppressionUtils, 
    net.ontopia.topicmaps.core.TopicMapIF, 
    net.ontopia.topicmaps.nav2.core.*, 
    net.ontopia.topicmaps.nav2.utils.ContextUtils,
    net.ontopia.topicmaps.nav2.utils.FrameworkUtils,
    net.ontopia.topicmaps.core.*" 
%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%-- Trigger topic map duplicate suppress process --%>

<logic:context tmparam="tm" settm="topicmap">
<%
// ---------------------------------------------------------------

UserIF user = FrameworkUtils.getUser(pageContext);
String model = user.getModel();
%>

  <template:insert template='/views/template_%view%.jsp'>
    <template:put name='title' body='true'>[Omnigator] Duplicates suppressed</template:put>

    <template:put name='heading' body='true'>
      <h1 class="boxed">Duplicates suppressed</h1>
    </template:put>

    <template:put name='toplinks' body='true'>
      <framework:pluginList separator=" | " group="welcome"/>
    </template:put>

    <template:put name='navigation' body='true'>

      <%
         // remove duplicates
         TopicMapIF tm = (TopicMapIF)ContextUtils.getSingleValue("topicmap", pageContext);
         DuplicateSuppressionUtils.removeDuplicates(tm);

      %>

      <p class=text>
      The duplicates in the  topic map have now been removed.  You will now be redirected to the new topic map. If you are not redirected please click  <a href="/omnigator/models/topicmap_<%= model %>.jsp?tm=<%= request.getParameter("tm") %>" tabindex="1">here</a>.
      </p>

      <% response.sendRedirect("/omnigator/models/topicmap_" + model + ".jsp?tm=" + request.getParameter("tm")); %>

    </template:put>
	
    <template:put name='content' body='true'>
    </template:put>

    <template:put name='outro' body='true'></template:put>

    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

  </template:insert>
</logic:context>
