<%@ page import="
	java.lang.reflect.Array,
	java.util.*,
	net.ontopia.topicmaps.cmdlineutils.statistics.TopicCounter,
	net.ontopia.topicmaps.cmdlineutils.statistics.TopicAssocDep,
	net.ontopia.topicmaps.core.*,
	net.ontopia.topicmaps.nav2.core.*,
	net.ontopia.topicmaps.nav2.utils.ContextUtils,
	net.ontopia.topicmaps.core.index.*,
	net.ontopia.utils.*,
	net.ontopia.topicmaps.utils.*" %>

<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output'    %>

<framework:response/>

<logic:context tmparam="tm" settm="topicmap">

  <template:insert template='/views/template_%view%.jsp'>
    <template:put name='title' body='true'>[Omnigator] Statistics</template:put>

    <template:put name='heading' body='true'>
      <h1 class="boxed">Statistics</h1>
    </template:put>

    <template:put name='toplinks' body='true'>
      <a href="../../index.jsp">Welcome</a> 
    </template:put>

    <template:put name='navigation' body='true'>


<%
  TopicMapIF topicmap = (TopicMapIF)ContextUtils.getSingleValue("topicmap", pageContext);

  String key = (String)request.getParameter("id");
  StringifierIF ts = TopicStringifiers.getDefaultStringifier();

  TopicIF topic = (TopicIF)topicmap.getObjectById(key);
  ClassInstanceIndexIF tindex = (ClassInstanceIndexIF)topicmap.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

  Collection topics = tindex.getTopics(topic);

%>

	 <h4>Total number of topics of this type: <%= topics.size() %></h4>
	 <br><br>

<%

 HashMap set = new HashMap();
 HashSet roles = new HashSet();
 Iterator it = topics.iterator();
 while (it.hasNext()) {
	 TopicIF t_tmp = (TopicIF)it.next();
	 Iterator teller = t_tmp.getRoles().iterator();
	 while (teller.hasNext()) {
		 roles.add((AssociationRoleIF)teller.next());
	 }
	 Iterator basenames = t_tmp.getTopicNames().iterator();
	 while (basenames.hasNext()) {
		 TopicNameIF bn = (TopicNameIF)basenames.next();
		 Iterator scopes = bn.getScope().iterator();
		 while (scopes.hasNext()) {
	TopicIF scope = (TopicIF)scopes.next();
	if (set.containsKey(scope)) {
		set.put(scope, new Integer(((Integer)set.get(scope)).intValue()+1));
	} else {
		set.put(scope, new Integer(1));
	}
		 }
	 }
 }

 if (set.size() > 0) {
%>
 <table border=1 width=100% cellspacing=0 cellpadding=2 class=text>
 <col /><col align="right" />
 <tr class=titleRow><td><b>Themes used to scope names on topics of this type</b></td><td>#&nbsp;</td></tr>
<%
	 it = set.keySet().iterator();
	 while (it.hasNext()) {
		 TopicIF output = (TopicIF)it.next();
		 ContextUtils.setSingleValue("topic", pageContext, output);
%>

 <tr><td>
     <a href="
       <output:link of="topicmap"
	            template="../../models/topic_%model%.jsp?tm=%topicmap%&id="
		    generator="modelLinkGenerator"/><output:objectid of="topic"/>">
       <output:name of="topic"/>
     </a>
 </td><td><%= ((Integer)set.get(output)).intValue() %>&nbsp;</td></tr>

<%
	 }//end of while
	 %></table><%
 } else {
	 %>No topics of this type have scoped names<%
 }
%>

<p><i>More statistical information will be provided in later versions of this plugin.</i>


    </template:put>
      
    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>
      
  </template:insert> 
</logic:context>
