<%@ page language="java" 
    import="
    java.util.*, 
    net.ontopia.topicmaps.entry.*, 
    net.ontopia.topicmaps.nav2.core.*, 
    net.ontopia.topicmaps.nav2.utils.NavigatorUtils, 
    net.ontopia.topicmaps.nav2.utils.FrameworkUtils, 
    net.ontopia.topicmaps.utils.*, 
    net.ontopia.topicmaps.impl.basic.InMemoryStoreFactory, 
    net.ontopia.topicmaps.core.*" 
%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%-- Trigger Topic Map Process --%>

<%
  // checking if a TM is selected (bug #1228)
  String tmid1 = request.getParameter("tm1");
  String tmid2 = request.getParameter("tm2");
  if (tmid2 == null) {
    response.sendRedirect("select.jsp?tm=" + tmid1 + "&error=No+topic+map+selected");
    return;
  }
%> 

<logic:context tmparam="tm">
<%
// ---------------------------------------------------------------
// retrieve configuration
NavigatorApplicationIF navApp = NavigatorUtils.getNavigatorApplication(pageContext);

UserIF user = FrameworkUtils.getUser(pageContext);
String model = user.getModel();
%>

  <template:insert template='/views/template_%view%.jsp'>
    <template:put name='title' body='true'>[Omnigator] Topic maps merged</template:put>

    <template:put name='heading' body='true'>
      <h1 class="boxed">Topic maps merged</h1>
    </template:put>

    <template:put name='toplinks' body='true'>
      <a href="../../models/index.jsp">Welcome</a>
      | <a href="../../models/topicmap_<%= model %>.jsp?tm=<%= request.getParameter("tm1")%>">Original Topic Map</a>
    </template:put>

    <template:put name='navigation' body='true'>

      <%
       String newid = tmid1 + "*" + tmid2;
       TopicMapRepositoryIF repository = navApp.getTopicMapRepository();
       TopicMapReferenceIF mergedref = repository.getReferenceByKey(newid);

       if (mergedref == null) {

         // Create "merge" reference
         TopicMapStoreFactoryIF sfactory = new InMemoryStoreFactory();
         List refkeys = new ArrayList();
         refkeys.add(tmid1);
         refkeys.add(tmid2);
         TopicMapReferenceIF ref = new MergeReference(newid, newid, sfactory, repository, refkeys);
         DefaultTopicMapSource source = new DefaultTopicMapSource(ref);

         // Add reference to repository
      	 repository.addSource(source);
      	 repository.refresh();
       }

      %>

      <p class=text>
      The two topic maps have been merged. You will now be redirected to the new topic map. If you are not redirected please click <a href="/omnigator/models/topicmap_<%= model %>.jsp?tm=<%= newid %>" tabindex="1">here</a>.
      </p>

      <% response.sendRedirect("/omnigator/models/topicmap_" + model + ".jsp?tm=" + newid); %>

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
