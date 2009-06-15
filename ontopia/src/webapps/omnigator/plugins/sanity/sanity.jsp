
<!--	$Id: sanity.jsp,v 1.6 2007/09/14 11:14:39 geir.gronmo Exp $	-->

<%@ page import="
  java.util.*,
  net.ontopia.topicmaps.cmdlineutils.sanity.AssociationSanity,
  net.ontopia.topicmaps.cmdlineutils.sanity.NoNames,
  net.ontopia.topicmaps.cmdlineutils.sanity.DuplicateOccurrences,
  net.ontopia.topicmaps.cmdlineutils.sanity.DuplicateNames,
  net.ontopia.topicmaps.core.*,
  net.ontopia.topicmaps.core.index.*,
  net.ontopia.topicmaps.nav2.core.*,
  net.ontopia.topicmaps.nav2.utils.ContextUtils" %>

<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'    prefix='logic'    %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'   prefix='output'   %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value'    prefix='value'    %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue'  prefix='tm'       %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template' prefix='template' %>
<framework:response/>

<logic:context tmparam="tm" settm="topicmap">

  <template:insert template='/views/template_%view%.jsp'>
    <template:put name='title' body='true'>[Omnigator] Statistics</template:put>

    <template:put name='heading' body='true'>
      <h1 class="boxed">Statistics</h1>
    </template:put>

    <template:put name='toplinks' body='true'>
      <a href="../../models/index.jsp">Welcome</a> 
    </template:put>

    <template:put name='navigation' body='true'>
<%

  TopicMapIF topicmap = (TopicMapIF)ContextUtils.getSingleValue("topicmap", pageContext);

  AssociationSanity sanity = new AssociationSanity(topicmap);
  sanity.traverse();

  HashMap duplicate  = sanity.getDuplicateAssociations();
  HashMap number     = sanity.getNumberOfDuplicates();
  HashMap assoctypes = sanity.getAssociationTypes();

%>


   <table  class="shboxed"  width=100% cellspacing=0 cellpadding=2>

   <tr class=titleRow><th>Association  <th>Value   <th>Number of times
<%
   Iterator it = duplicate.keySet().iterator();
   while (it.hasNext()) {
     String s = (String)it.next();
     StringTokenizer st = new StringTokenizer(s, "$");
     String association = st.nextToken();

     ContextUtils.setSingleValue("assoctype", pageContext, assoctypes.get(s));
     
     %>
       <tr><td><output:name of="assoctype"/><td>     
     <%


     while (st.hasMoreTokens()) {
       String value = st.nextToken();
       String attribute = st.nextToken();

       if (attribute.startsWith("id")) {
         String id = attribute.substring(2, attribute.length());
         try {
           Integer.parseInt(id);
           TopicIF t_topic = (TopicIF)topicmap.getObjectById(id);
	   ContextUtils.setSingleValue("assocrole", pageContext, t_topic);

           %>
	     role : <output:name of="assocrole"/> - player : <%= value %><br>
	   <%
	 } catch (NumberFormatException e) {
	  // do nothing
         }
       } else {
         %> role : <%= attribute %> - player : <%= value %><br> <%
       }
     }
     Integer i = (Integer)number.get(s);
     %><td><%= i.intValue() %><%
   }
  %>

  </table>
  </template:put>
  </template:insert>
</logic:context>
