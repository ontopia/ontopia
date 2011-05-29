<%@ page language="java" 
    import="
    java.util.*,
    net.ontopia.topicmaps.entry.*,
    net.ontopia.topicmaps.nav2.core.*,
    net.ontopia.topicmaps.nav2.utils.NavigatorUtils,
    net.ontopia.topicmaps.nav2.plugins.*" 
%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%-- Plugin Administration page --%>

<logic:context tmparam="tm" settm="topicmap">

  <%
    // retrieve configuration 
    NavigatorApplicationIF navApp = NavigatorUtils.getNavigatorApplication(pageContext);
    NavigatorConfigurationIF navConf = navApp.getConfiguration();
  %>

  <template:insert template='/views/template_%view%.jsp'>
    <template:put name='title' body='true'>[Omnigator] Plug-ins</template:put>

    <template:put name='heading' body='true'>
      <h1 class="boxed">Plug-ins</h1>
    </template:put>

    <template:put name='plugins' body='true'>
      <framework:pluginList separator=" | " group="topicmap"/>
    </template:put>

    <%-- ============================================================= --%>
    <template:put name='navigation' body='true'>

      <p>Here you can see all the plug-ins you have currently installed
      and what the status of each is. If any of them are in the error
      state, you can find their error messages in the log4j log.</p>
      
      <p>Note that changes to the activation of the plug-ins will be
      lost when the server is rebooted, or the application configuration
      reloaded.</p>
      
      <p>To make your changes permanent across sessions you must change the
      individual plugin.xml files.</p>
      
      <%
      List messages = new ArrayList();
      Iterator it = navConf.getPlugins().iterator();
      List groups = navConf.getPluginGroups();
      
      if (request.getParameter("apply") != null) {
      	      while (it.hasNext()) {
      		      PluginIF plugin = (PluginIF) it.next();
                      // (de-)activate plugin
      		      String setting = request.getParameter(plugin.getId());
      		      if (setting == null && plugin.getState() == PluginIF.ACTIVATED) {
      			      messages.add("Deactivated " + plugin.getId() + "<br>");
      			      plugin.setState(PluginIF.DEACTIVATED);
      		      } else if (setting != null && plugin.getState() == PluginIF.DEACTIVATED) {
      			      messages.add("Activated " + plugin.getId() + "<br>");
      			      plugin.setState(PluginIF.ACTIVATED);
      		      }
                      // loop over all groups and assign correctlty
                      Iterator itG = groups.iterator();
                      boolean changedSomething = false;
                      plugin.resetGroups();
                      while (itG.hasNext()) {
                        String groupId = (String) itG.next();
                        setting = request.getParameter(plugin.getId() + "$" + groupId);
                        if (setting != null) {
                          plugin.addGroup(groupId);
                          changedSomething = true;
                        }
                      }
                      // if (changedSomething) 
                      //  messages.add("Changed group setting of " + plugin.getId() + "<br>");
      	      }
              messages.add("Assigned plugins to groups<br>");
      }
      
      if (!messages.isEmpty()) {
      	      out.println("<table class=shboxed width='100%' cellpadding='10'><tr><td><h3>Change Report</h3>");
      	      for (int ix = 0; ix < messages.size(); ix++)
      		      out.println(messages.get(ix));
              out.println("</td></tr></table>");
      }
      %>
    </template:put>


    <%-- ============================================================= --%>
    <template:put name='content' body='true'>
      <form method="post" action="plugins.jsp">
      <input type="hidden" name="apply" value="apply" />
      <input type="hidden" name="tm" value="<%= request.getParameter("tm") %>" />
      <table>
      <tr>
        <th align='left'>Title</th>
      	<th align='left'>Description</th>
      	<th align='left'>Activate</th>
      	<th align='left'>Group(s):</th>
        <%
        List groups = navConf.getPluginGroups();
        Iterator itG = groups.iterator();
        while (itG.hasNext()) {
          String groupId = (String) itG.next();
          %>
      	  <th align='left'><%= groupId %></th>
        <%
        }
        %>
        </tr>
      <%
      // display the plugins alphabetically
      String errmsg = "<font color='red'><b>ERROR</b></font>";
      Iterator it = navConf.getOrderedPlugins().iterator();
      while (it.hasNext()) {
      	      PluginIF plugin = (PluginIF) it.next();
      
      	      String activated = errmsg;
      	      if (plugin.getState() == PluginIF.ACTIVATED)
      		      activated = "<input name='" + plugin.getId() + "' type='checkbox' checked='checked'>";
      	      else if (plugin.getState() == PluginIF.DEACTIVATED)
      		      activated = "<input name='" + plugin.getId() + "' type='checkbox'>";
      %>
      	      <tr valign=top>
      	        <td><b><%= plugin.getTitle() %></b></td>
      		<td><%= plugin.getDescription() %></td>
      		<td align='center'><%= activated %></td>
		<td></td>
		<%
                  groups = navConf.getPluginGroups();
                  itG = groups.iterator();
                  while (itG.hasNext()) {
                      String curGroupId = (String) itG.next();
                      String belongsToGroup = "";
                      if (plugin.getState() != PluginIF.ERROR) {
                        if (plugin.getGroups().contains(curGroupId))
                          belongsToGroup = "<input name='" + plugin.getId() + "$" + curGroupId +
                                           "' type='checkbox' checked='checked' />";
                        else
                          belongsToGroup = "<input name='" + plugin.getId() + "$" + curGroupId +
                                           "' type='checkbox' />";
                      }
                  %>
		  <td><%= belongsToGroup %></td>
		<%
                  }
                %>
	        </tr>
      <%
      }
      %>
      <tr><td colspan='7' align='right'><input type='submit' value="  Apply  "
                                              /></td></tr>
      </table>
      </form>
      
    </template:put>

	
    <template:put name='outro' body='true'></template:put>
      
    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>
      
  </template:insert> 
</logic:context>
