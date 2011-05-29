<%@ page language="java" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output'    %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value'     prefix='value'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue'   prefix='tm'        %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%-- TopicMap Page - Model: 'basic' --%>

<%
  // get the page view selection
  String pageView = request.getParameter("pageView");
  if (pageView == null || pageView.equals(""))
    pageView = "ontology";
%>

<logic:context tmparam="tm" settm="topicmap">

  <%-- Read in declarations of functions that should be called on this page --%>
  <logic:include file="/functions/application.jsm"/>
  <logic:set name="shortname"><tm:lookup indicator="http://psi.ontopia.net/basename/#short-name"/></logic:set>

  <template:insert template='/views/template_%view%.jsp'>

    <%-- =============== Reified topicmap name ============ --%>
    <logic:set name="tm_topic"><tm:reifier of="topicmap" /></logic:set>

    <template:put name='title' body='true'>[Omnigator]
      <logic:if name="tm_topic">
        <logic:then><output:name of="tm_topic" basenameScope="shortname"/></logic:then>
        <logic:else>Index</logic:else>
      </logic:if>
    </template:put>

    <template:put name='heading' body='true'>
      <h1 class="boxed">
      <logic:if name="tm_topic">
        <logic:then><output:name of="tm_topic" basenameScope="shortname"/></logic:then>
        <logic:else>Index</logic:else>
      </logic:if>
      </h1>
    </template:put>

    <template:put name='toplinks' body='true'>
        <a href="index.jsp">Home</a>
      <framework:pluginList separator=" | " preSeparator="true" group="topicmap"/>
    </template:put>


    <template:put name='navigation' body='true'>

      <!-- =================== PageView selection ======================= -->
      <table class="shboxed" width="100%"><tr><td>
      <h3>Select View</h3>
        <ul>
          <li><% if (pageView.equals("ontology")) { %>
          <b>Index of Indexes</b>
        <% } else { %>
            <a href="<output:link template="topicmap_%model%.jsp?tm=%topicmap%&pageView=ontology" generator="modelLinkGenerator"/>">Ontology</a>
        <% } %></li>
          <li><% if (pageView.equals("master")) { %>
          <b>Master Index</b>
        <% } else { %>
          <a href="<output:link template="topicmap_%model%.jsp?tm=%topicmap%&pageView=master" generator="modelLinkGenerator"/>">Master Index</a>
        <% } %></li>
        </ul>
      </td></tr></table>
    </template:put>

    <template:put name='content' body='true'>

      <% if (pageView.equals("master")) { %>
      <!-- ============== List of all topics ================ -->
      <logic:set name="topics"><tm:topics of="topicmap"/></logic:set>
      <table class="shboxed" width="100%"><tr><td>
      <h3>Master Index (<output:count of="topics"/>)</h3>
      <ul>
      <logic:foreach name="topics">
          <li><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name /></a></li>
        </logic:foreach>
      </ul>
      </td></tr></table>

      <% } else if (pageView.equals("ontology")) { %>
      <!-- ============== List of topic types =============== -->
      <logic:set name="topics" comparator="off"><tm:tolog select="T" query="
        select $T, count($A) from
        {
         direct-instance-of($x, $T),
           role-player($role1, $x), association-role($assoc, $role1),
           association-role($assoc, $role2), role-player($role2, $y),
           role-player($A, $y),
           $role1 /= $role2,
           $role1 /= $A
        }
        order by $A desc limit 10 offset 0?"/></logic:set>

      <table class="shboxed" width="100%"><tr><td>
      <h3>Index of Indexes</h3>
      <ul>
      <logic:foreach name="topics" set="topic">
          <logic:set name="count"><tm:tolog select="TYPE" query="select count($TYPE) from direct-instance-of($TYPE, %topic%)?"/></logic:set>
          <li><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name/></a>&nbsp;&nbsp;&nbsp;&nbsp;(<output:content of="count"/>)</li>
      </logic:foreach>
      </ul>
      </td></tr></table>

     <!-- ================================================== -->
     <% } %>

    </template:put>

    <template:put name='outro' body='true'>
    </template:put>

    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

  </template:insert>
</logic:context>
