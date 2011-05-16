<%@ page language="java" 
           import="net.ontopia.topicmaps.core.TopicMapStoreIF,
                   net.ontopia.topicmaps.impl.rdbms.Stats,
                   net.ontopia.topicmaps.entry.TopicMapReferenceIF,
                   net.ontopia.topicmaps.nav2.utils.NavigatorUtils,
                   net.ontopia.topicmaps.nav2.core.*,
                   net.ontopia.topicmaps.nav2.utils.FrameworkUtils" %>

<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue'   prefix='tm'        %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output' %>

<%
  //get id of topic map from request parameter
  String refkey = request.getParameter("tm");
  java.util.Map stats = null;

  //get topic map reference
  TopicMapReferenceIF ref = NavigatorUtils.getNavigatorApplication(pageContext).getTopicMapRepository().getReferenceByKey(refkey);

  //get store from reference and write report
  TopicMapStoreIF store = ref.createStore(true);
  try {
    stats = Stats.getStatistics(store.getTopicMap());
    stats = new java.util.TreeMap(stats);
  } finally {
    store.close();
  }
%>

<logic:context tmparam="tm" settm="topicmap">
  <template:insert template='/views/template_%view%.jsp'>
    <template:put name='title' body='true'>[Admin] Topic Map statistics for <%= refkey %></template:put>

    <template:put name='heading' body='true'>
      <h1 class="boxed">Topic Map statistics for <%= refkey %></h1>
    </template:put>

    <template:put name='manageLinks' body='true'>
      <tr valign="top">
        <td class="plugins" colspan=2>
          <a href="/manage/manage.jsp">Manage</a>
        </td>
      </tr>
    </template:put>

    <%
      UserIF user = FrameworkUtils.getUser(pageContext);
      String skin = user.getSkin();
    %>

    <template:put name='skin' body='true'>skins/<%= skin %>.css</template:put>

    <template:put name='plugins' body='true'>
      <framework:pluginList separator=" | " group="topicmap"/>
    </template:put>

    <template:put name="content" body="true">
      <%-- Space to put comments in case we ever want to do so --%>
    </template:put>

    <template:put name="navigation" body="true">
      <table>
        <% 
          java.util.Iterator i = stats.keySet().iterator();
          while (i.hasNext()) { 
            Object key = i.next();
            Object value = stats.get(key);
        %>
            <tr>
              <td><%= key %></td>
              <td><%= value %></td>
            </tr>
        <% 
          }
        %>
      </table>

      <p>Report generated: <%= new java.util.Date() %></p>
    </template:put>

    <template:put name='outro' body='true'></template:put>

    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>
  </template:insert>
</logic:context>
