
<%@ page import="
    java.lang.reflect.Array,
    java.util.StringTokenizer,
    java.util.Collection,
    java.util.Iterator,
    net.ontopia.topicmaps.cmdlineutils.statistics.TopicCounter,
    net.ontopia.topicmaps.cmdlineutils.statistics.TopicAssocDep,
    net.ontopia.topicmaps.nav2.core.*,
    net.ontopia.topicmaps.nav2.utils.ContextUtils,
    net.ontopia.topicmaps.nav2.utils.FrameworkUtils,
    net.ontopia.topicmaps.core.*"  %>

<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output'    %>
<framework:response/>

<logic:context tmparam="tm" settm="topicmap">

  <template:insert template='/views/template_%view%.jsp'>
    <template:put name='title' body='true'>[Omnigator] Statistics</template:put>

<%
  TopicMapIF topicmap = (TopicMapIF)ContextUtils.getSingleValue("topicmap", pageContext);

  String key = (String)request.getParameter("assoc");
  TopicAssocDep assocdep = new TopicAssocDep(topicmap);
  Collection assocs = assocdep.getAssociationDetails(key);
  if (assocs != null) {
    StringTokenizer st = new StringTokenizer(key, "$");
    String s = st.nextToken();

%>

    <template:put name='heading' body='true'>
<!--      <h1 class="boxed">Statistics for association type "<%= s %>"</h1> -->
      <h1 class="boxed">Association Details</h1>
    </template:put>

    <template:put name='toplinks' body='true'>
      <a href="../../index.jsp">Welcome</a> 
    </template:put>

    <template:put name='navigation' body='true'>


<%
     String tmid = (String)request.getParameter("tm");

     UserIF user = FrameworkUtils.getUser(pageContext);
     String model = user.getModel();
     
     String[] roles = assocdep.getAssociationRoleTypesOrdered(key);
     
     StringBuffer buf = new StringBuffer();
     int counter = 0;
     Iterator it = assocs.iterator();
     while (it.hasNext()) {
       counter++;
       AssociationIF assoc = (AssociationIF)it.next();
       buf.append("<tr><td>" + counter);
       String[] tmp = assocdep.getAssociationDetails(key, assoc);
       for (int i = 0; i < tmp.length; i++)  {
     	 StringTokenizer strtok = new StringTokenizer(tmp[i], "$");
     	 String player = strtok.nextToken();
     	 String link = strtok.nextToken();
     	 buf.append("<td><a href='../../models/topic_" + model + ".jsp?tm="
     		      + tmid + "&id=" + link + "'>");
     	 buf.append(player + "</a></td>");
     
     	 //buf.append(tmp[i]);
       }
       buf.append("</tr>");
     }
%>

      <p><table class="shboxed" width="100%"><tr><td>
      <h2><b>Statistics for association type "<%= s %>"</b></h2>
      </td></tr>
      </table></p>

      <p>
      <table class="shboxed" width=100% cellspacing=0 cellpadding=2>
      <tr class=titleRow><th>&nbsp;
      
      
<%
      for (int ix = 0; ix < roles.length; ix++)
        out.print("<td><b>" + roles[ix] + "</b></td>");
%>
       </tr>
      <%= buf.toString() %> 
     </table>
     </p>

     <h4>Total number of associations of this type: <%= assocs.size() %></h4>

     <p><i>More statistical information will be provided in later versions of this plugin.</i>
   </template:put>
<%
  } else {
%>
  <template:put name='navigation' body='true'>
    <p><b>There is no information about this association : <%= key %></b></p>
  </template:put>
<%
}
%>

      
    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>
      
  </template:insert> 
</logic:context>
