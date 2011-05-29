<%@ page language="java"
    import="java.util.Collection,
    java.util.HashMap,
    java.util.ArrayList,
    java.util.Iterator,
    net.ontopia.topicmaps.core.*,
    net.ontopia.topicmaps.core.index.*,
    net.ontopia.topicmaps.nav.context.*,
    net.ontopia.topicmaps.nav2.core.*,
    net.ontopia.topicmaps.nav2.utils.ContextUtils,
    net.ontopia.topicmaps.nav2.utils.FrameworkUtils,
    net.ontopia.topicmaps.utils.*,
    net.ontopia.utils.*,
    net.ontopia.topicmaps.cmdlineutils.statistics.*"
%>

<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue'   prefix='tm'        %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'     prefix='output' %>
<framework:response/>

<%-- Export Config Page --%>

<logic:context tmparam="tm" settm="topicmap">
  <framework:checkUser/>

  <template:insert template='/views/template_%view%.jsp'>
    <template:put name='title' body='true'>[Omnigator] Statistics</template:put>

    <template:put name='heading' body='true'>
      <h1 class="boxed">Statistics</h1>
    </template:put>

    <template:put name='plugins' body='true'>
      <framework:pluginList separator=" | " group="topicmap"/>
    </template:put>

<%
  TopicMapIF topicmap = (TopicMapIF)ContextUtils.getSingleValue("topicmap", pageContext);

  UserIF user = FrameworkUtils.getUser(pageContext);
  UserFilterContextStore filter = user.getFilterContext();
  Collection scopes = filter.getScopeTopicNames(topicmap);

  TopicCounter tc = new TopicCounter(topicmap);
  tc.count();
  int sum = tc.getNumberOfTopics() + tc.getNumberOfAssociations() +
            tc.getNumberOfOccurrences();
%>


    <template:put name='navigation' body='true'>

      <h3>Overall statistics</h3>
      <table class="shboxed"  width=100% cellspacing=0 cellpadding=2>
      <col width="100%"/><col align="right"/>
      <tr class=titleRow><td><b>Topic Map Objects</b></td><td>#&nbsp;</td></tr>
      <tr><td>Topics</td><td><%= tc.getNumberOfTopics() %>&nbsp;</td></tr>
      <tr><td>Associations</td><td><%= tc.getNumberOfAssociations() %>&nbsp;</td></tr>
      <tr><td>Occurrences</td><td><%= tc.getNumberOfOccurrences() %>&nbsp;</td></tr>
      <tr class=titleRow><td><b>Total TAOs</b></td><td><b><%= sum %>&nbsp;</b></td></tr>
      </table>

      <br><br><br>


    	<h3>Statistics for individual object types</h3>
	<%
        ClassInstanceIndexIF tindex = (ClassInstanceIndexIF) topicmap.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
        Collection types = tindex.getTopicTypes();
      	%>
      	<table class="shboxed"  width=100% cellspacing=0 cellpadding=2 class=text>
      	  <col width="100%"/><col align="right"/>
      	  <tr class=titleRow><td><b>Topic Types</b></td><td>#&nbsp;</td></tr>
      	  <tr><td><i>Number of different topic
	types</i></td><td><i><%= types.size() %></i>&nbsp;</td></tr>

	  <%


    ContextUtils.setValue("types", pageContext, types);

    %>

    <logic:foreach name="types" set="type" comparator="topicComparator">

      <%

      TopicIF topic = (TopicIF)ContextUtils.getSingleValue("type", pageContext);
      Collection tmp = tindex.getTopics(topic);
      ContextUtils.setValue("topictypes", pageContext, tmp);

      %>

      <tr><td>
      <a href="
   		  <output:link of="topicmap"
   			       template="../../models/topic_%model%.jsp?tm=%topicmap%&id="
   			       generator="modelLinkGenerator"/><output:objectid of="type"/>">
   		  <output:name of="type"/>
   		</a>
      </td><td>
	        <output:count of="topictypes"/>
	    </td></tr>
	
    </logic:foreach>
   	</table>
      	
      	<br>
	      	
        <%
	types = tindex.getAssociationTypes();
	%>

      	<table class="shboxed"  width=100% cellspacing=0 cellpadding=2 class=text>
      	  <col width="100%"/><col align="right"/>
      	  <tr class=titleRow><td><b>Association Types</b></td><td>#&nbsp;
      	  <tr><td><i>Number of different association types</i></td>
	  <td><i><%= types.size() %></i>&nbsp;</td></tr>
	  <%


    ContextUtils.setValue("types", pageContext, types);

    %>

    <logic:foreach name="types" set="type" comparator="topicComparator">

      <%

      TopicIF topic = (TopicIF)ContextUtils.getSingleValue("type", pageContext);
      Collection tmp = tindex.getAssociations(topic);
      ContextUtils.setValue("assoctypes", pageContext, tmp);

      %>

      <tr><td>
      <a href="
   		  <output:link of="topicmap"
   			       template="../../models/topic_%model%.jsp?tm=%topicmap%&id="
   			       generator="modelLinkGenerator"/><output:objectid of="type"/>">
   		  <output:name of="type"/>
   		</a>
      </td><td>
	        <output:count of="assoctypes"/>
	    </td></tr>
	
    </logic:foreach>
   	</table>

      	
      	<br>

        <%
	types = tindex.getOccurrenceTypes();
	%>
      	
      	<table class="shboxed"  width=100% cellspacing=0 cellpadding=2 class=text>
      	  <col width="100%"/><col align="right"/>
      	  <tr class=titleRow><td><b>Occurrence Types</b></td><td>#&nbsp;</td></tr>
      	  <tr><td><i>Number of different occurrence types</i></td>
	  <td><i><%= types.size() %></i>&nbsp;</td></tr>
	  <%


    ContextUtils.setValue("types", pageContext, types);

    %>

    <logic:foreach name="types" set="type" comparator="topicComparator">

      <%

      TopicIF topic = (TopicIF)ContextUtils.getSingleValue("type", pageContext);
      Collection tmp = tindex.getOccurrences(topic);
      ContextUtils.setValue("occurtypes", pageContext, tmp);

      %>

      <tr><td>
      <a href="
   		  <output:link of="topicmap"
   			       template="../../models/topic_%model%.jsp?tm=%topicmap%&id="
   			       generator="modelLinkGenerator"/><output:objectid of="type"/>">
   		  <output:name of="type"/>
   		</a>
      </td><td>
	        <output:count of="occurtypes"/>
	    </td></tr>
	
    </logic:foreach>

  	</table>
   	<br><br><br>
    </template:put>

    <template:put name='content' body='true'>

    <h3>Association structure summary</h3>

     <%
     TopicAssocDep assocs = new TopicAssocDep(topicmap);
     Collection associations = assocs.getAssociations();

     String[] data = assocs.sortAlpha(associations);
     int i = 0, last = data.length;
     %>
      <table class="shboxed" width=100% cellspacing=0 cellpadding=2 class=text>
      <col /><col align="right"/><col /><col />
      <tr class="titleRow"><td><b>Type</b></td><td><b>#</b></td><td><b>Role types</b></td><td><b>Role player types</b></td></tr>
      <%
        while (i < last) {
	  String key = data[i];
	  String id = assocs.getAssociationTypeId(key);
	
	  // Gets the association type
	  TopicIF assoc_type = (TopicIF)topicmap.getObjectById(id);
          if (assoc_type == null) {
            i++;
            continue;
          }
	  ContextUtils.setSingleValue("assoc_type", pageContext, assoc_type);

	  %>
	  <tr><td>
            <logic:if name="assoc_type"><logic:then>
   		<a href="
   		  <output:link of="topicmap"
   			       template="../../models/topic_%model%.jsp?tm=%topicmap%&id="
   			       generator="modelLinkGenerator"/><output:objectid of="assoc_type"/>">
   		  <output:name of="assoc_type"/>
   		</a>
            </logic:then><logic:else>null</logic:else></logic:if>
	  </td><%

	  // Gets the number of times it occurs
	  int num = assocs.getNumberOfOccurrences(key);
	  %>
	  <td><%= num %></td><%
	  // Send the key as parameter
	  // Gets the roletypes.
	  Iterator roletypes = assocs.getAssociationRoleTypes(key).keySet().iterator();
	  Collection roles = new ArrayList();
	  while (roletypes.hasNext()) {
	    TopicIF topic = (TopicIF)topicmap.getObjectById((String)roletypes.next());
            if (topic != null) ////////// WORKAROUND ////////////
  	      roles.add(topic); //// BUG #536
	  }
	  ContextUtils.setValue("roletypes", pageContext, roles);

	  %>
	  <td>
	    <logic:foreach name="roletypes" separator=" | " set="roletype">
	      <a href="<output:link of="roletype"
	                template="../../models/topic_%model%.jsp?tm=%topicmap%&id="
			generator="modelLinkGenerator"/>
			<output:objectid of="roletype"/>"><output:name of="roletype"/></a>
	    </logic:foreach>
	  </td>
	  <%

	  // Gets the role players
	  Iterator role_names = assocs.getAssociationRoles(key).keySet().iterator();
	  roles = new ArrayList();
	  while (role_names.hasNext()) {
	    TopicIF topic = (TopicIF)topicmap.getObjectById((String)role_names.next());
      if (topic != null)
        roles.add(topic);
	  }
	  ContextUtils.setValue("rolenames", pageContext, roles);

	  %>
	  <td>
	    <logic:foreach name="rolenames" separator=" | ">
	      <a href="
          <output:link of="topicmap"
	                template="../../models/topic_%model%.jsp?tm=%topicmap%&id="
             			generator="modelLinkGenerator"/><output:objectid />">
          <output:name /></a>
	    </logic:foreach>		

	  </td></tr>
	  <%
	  i++;	
	}
        %>
      </table>
    </template:put>

    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

  </template:insert>
</logic:context>
