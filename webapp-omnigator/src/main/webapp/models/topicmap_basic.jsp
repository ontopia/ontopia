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

  <template:insert template='/views/template_%view%.jsp'>

    <%-- =============== Reified topicmap name ============ --%>
    <logic:set name="tm_topic"><tm:reifier of="topicmap" /></logic:set>

    <template:put name='title' body='true'>[Omnigator]
      <logic:if name="tm_topic">
          <logic:then><output:name of="tm_topic"/></logic:then>
          <logic:else>Topic Map</logic:else>
      </logic:if>
    </template:put>

    <template:put name='heading' body='true'>
      <h1 class="boxed">
      <logic:if name="tm_topic">
          <logic:then><output:name of="tm_topic"/></logic:then>
          <logic:else>Topic Map</logic:else>
      </logic:if>
      </h1>
    </template:put>

    <template:put name='toplinks' body='true'>
        <a href="index.jsp">Welcome</a>
        <framework:pluginList separator=" | " preSeparator="true" group="topicmap"/>
    </template:put>


    <template:put name='navigation' body='true'>

      <!-- =================== PageView selection ======================= -->
      <table class="shboxed" width="100%"><tr><td>
        <h3>Topic Map Overview</h3>
          <ul>
            <li><% if (pageView.equals("ontology")) { %>
            <b>Ontology</b>
          <% } else { %>
            <a href="<output:link template="topicmap_%model%.jsp?tm=%topicmap%&pageView=ontology" generator="modelLinkGenerator"/>">Ontology</a>
          <% } %></li>
            <li><% if (pageView.equals("master")) { %>
            <b>Master Index</b>
          <% } else { %>
            <a href="<output:link template="topicmap_%model%.jsp?tm=%topicmap%&pageView=master" generator="modelLinkGenerator"/>">Master Index</a>
          <% } %></li>
            <li><% if (pageView.equals("themes")) { %>
            <b>Themes</b>
          <% } else { %>
            <a href="<output:link template="topicmap_%model%.jsp?tm=%topicmap%&pageView=themes" generator="modelLinkGenerator"/>">Themes</a>
          <% } %></li>
          </ul>
      </td></tr></table>
    </template:put>

    <template:put name='content' body='true'>

      <% if (pageView.equals("master")) { %>
      <!-- ============== List of all topics ================ -->
      <logic:set name="topics"><tm:topics of="topicmap"/></logic:set>
      <table class="shboxed" width="100%"><tr><td>
      <h3>Complete subject index (<output:count of="topics"/>)</h3>
      <ul>
        <logic:foreach name="topics">
          <li><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name /></a></li>
        </logic:foreach>
      </ul>
      </td></tr></table>

      <% } else if (pageView.equals("themes")) { %>
      <!-- ============== List of topic themes ================ -->
      <logic:set name="topicThemes"><tm:themes of="topic"/></logic:set>
      <logic:if name="topicThemes"><logic:then>
      <table class="shboxed" width="100%"><tr><td>
      <h3>Topic Themes (<output:count/>)</h3>
      <ul>
        <logic:foreach>
          <li><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name /></a></li>
        </logic:foreach>
      </ul>
      </td></tr></table>
      </logic:then></logic:if>

      <!-- ============== List of basename themes ================ -->
      <logic:set name="basenameThemes"><tm:themes of="basename"/></logic:set>
      <logic:if name="basenameThemes"><logic:then>
      <table class="shboxed" width="100%"><tr><td>
      <h3>Base Name Themes (<output:count/>)</h3>
      <ul>
        <logic:foreach>
          <li><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name /></a></li>
        </logic:foreach>
      </ul>
      </td></tr></table>
      </logic:then></logic:if>

      <!-- ============== List of variant themes ================ -->
      <logic:set name="variantThemes"><tm:themes of="variant"/></logic:set>
      <logic:if name="variantThemes"><logic:then>
      <table class="shboxed" width="100%"><tr><td>
      <h3>Variant Name Themes (<output:count/>)</h3>
      <ul>
        <logic:foreach>
          <li><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name /></a></li>
        </logic:foreach>
      </ul>
      </td></tr></table>
      </logic:then></logic:if>

      <!-- ============== List of occurrence themes ================ -->
      <logic:set name="occurrenceThemes"><tm:themes of="occurrence"/></logic:set>
      <logic:if name="occurrenceThemes"><logic:then>
      <table class="shboxed" width="100%"><tr><td>
      <h3>Occurrence Themes (<output:count/>)</h3>
      <ul>
        <logic:foreach>
          <li><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name /></a></li>
        </logic:foreach>
      </ul>
      </td></tr></table>
      </logic:then></logic:if>

      <!-- ============== List of association themes ================ -->
      <logic:set name="associationThemes"><tm:themes of="association"/></logic:set>
      <logic:if name="associationThemes"><logic:then>
      <table class="shboxed" width="100%"><tr><td>
      <h3>Association Themes (<output:count/>)</h3>
      <ul>
        <logic:foreach>
          <li><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name /></a></li>
        </logic:foreach>
      </ul>
      </td></tr></table>
      </logic:then></logic:if>

      <% } else if (pageView.equals("ontology")) { %>
      <!-- ============== List of topic types =============== -->
      <logic:set name="topics"><tm:classes of="topic"/></logic:set>

      <table class="shboxed" width="100%"><tr><td>
      <h3>Subject Indexes (<output:count of="topics"/>)</h3>
      <ul>
        <logic:foreach name="topics" start="0" max="200">
          <li><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name /></a></li>
        </logic:foreach>
      </ul>
      <!-- framework:topicTypesList
            typePrefix="<ul>"
            typePostfix="</ul>"
            typeTemplate="<li><a href='%link%'>%name%</a></li>"
            linkTemplate="topic_%model%.jsp?tm=%topicmap%&id=%id%"
            linkGenerator="modelLinkGenerator"
      / -->
      </td></tr></table>

      <!-- ============== List of association types ========= -->

      <logic:set name="assoctypes"><tm:classes of="association"/></logic:set>

      <logic:if name="assoctypes"><logic:then>
          <table class="shboxed" width="100%"><tr><td>
          <h3>Relationship Indexes (<output:count/>)</h3>
          <ul>
            <logic:foreach>
              <li><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name /></a></li>
            </logic:foreach>
          </ul>
          </td></tr></table>
      </logic:then></logic:if>

      <!-- ============== List of role types ========= -->

      <logic:set name="roletypes"><tm:classes of="role"/></logic:set>

      <logic:if name="roletypes"><logic:then>
        <table class="shboxed" width="100%"><tr><td>
        <h3>Role Indexes (<output:count/>)</h3>
        <ul>
          <logic:foreach>
            <li><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name /></a></li>
          </logic:foreach>
        </ul>
        </td></tr></table>
      </logic:then></logic:if>

      <!-- ============== List of occurrence types ========= -->

      <logic:set name="occtypes"><tm:classes of="occurrence"/></logic:set>

      <logic:if name="occtypes"><logic:then>
        <table class="shboxed" width="100%"><tr><td>
        <h3>Resource Indexes (<output:count/>)</h3>
        <ul>
          <logic:foreach>
            <li><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name /></a></li>
          </logic:foreach>
        </ul>
        </td></tr></table>
      </logic:then></logic:if>

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
