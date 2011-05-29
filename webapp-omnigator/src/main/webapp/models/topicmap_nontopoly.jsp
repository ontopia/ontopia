<%@ page language="java" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'          prefix='tolog'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output'    %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value'     prefix='value'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue'   prefix='tm'        %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%-- TopicMap Page - Model: 'complete' --%>

<%
  // get the page view selection
  String pageView = request.getParameter("pageView");
  // if no view mode is yet set, fall back to default mode: "ontology"
  if (pageView == null || pageView.equals(""))
    pageView = "ontology";
%>

<logic:context tmparam="tm" settm="topicmap">

  <%-- Read in declarations of functions that should be called on this page --%>
  <logic:include file="/functions/application.jsm"/>
  <%-- Test whether this is an ontopoly-tm or not --%>
  <logic:set name="ontopoly-tm">
    <tm:lookup indicator="http://psi.ontopia.net/ontology/system-topic"/>
  </logic:set>
  <logic:if name="ontopoly-tm"><logic:then>
    <logic:include file="/functions/nontopoly_topicmap_model.jsm"/>
  </logic:then><logic:else>
  <%-- If not, load complete model instead of nontopoly model --%>
    <logic:include file="/functions/complete_topicmap_model.jsm"/>
  </logic:else></logic:if>

  <template:insert template='/views/template_%view%.jsp'>

    <%-- =============== Reified topicmap name ============ --%>
    <logic:set name="tm_topic"><tm:reifier of="topicmap" /></logic:set>

    <template:put name='title' body='true'>[Omnigator]
      <logic:if name="tm_topic">
    <logic:then><output:name of="tm_topic"/></logic:then>
    <logic:else>[Unnamed Topic Map]</logic:else>
      </logic:if>
    </template:put>

    <template:put name='heading' body='true'>
      <h1 class="boxed">
      <logic:if name="tm_topic">
    <logic:then><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name of="tm_topic"/></a></logic:then>
    <logic:else>Index Page</logic:else>
      </logic:if>
      </h1>
    </template:put>

    <template:put name='plugins' body='true'>
      <framework:pluginList separator=" | " group="topicmap"/>
    </template:put>

    <logic:set name="nonames">
      <tm:tolog select="TOPIC" query="select $TOPIC from topic($TOPIC), not(topic-name($TOPIC, $NAME))?"/>
    </logic:set>

    <template:put name='navigation' body='true'>
      <table class="shboxed" width="100%"><tr><td>
  <h3>Topic Map Overview</h3>
        <!-- =================== PageView selection ======================= -->
    <ul>
      <li><% if (pageView.equals("ontology")) { %>
      <b><span title="Overview of all typing topics">Ontology</span></b>
    <% } else { %>
            <a href="<output:link template="topicmap_%model%.jsp?tm=%topicmap%&pageView=ontology" generator="modelLinkGenerator"/>"><span title="Overview of all typing topics">Ontology</span></a>
    <% } %></li>
      <li><% if (pageView.equals("master")) { %>
      <b><span title="Complete list of topics">Master Index</span></b>
    <% } else { %>
      <a href="<output:link template="topicmap_%model%.jsp?tm=%topicmap%&pageView=master" generator="modelLinkGenerator"/>"><span title="Complete list of topics">Master Index</span></a>
    <% } %></li>
      <li><% if (pageView.equals("individuals")) { %>
      <b><span title="List of all topics except typing topics">Index of Individuals</span></b>
    <% } else { %>
      <a href="<output:link template="topicmap_%model%.jsp?tm=%topicmap%&pageView=individuals" generator="modelLinkGenerator"/>"><span title="List of all topics except typing topics">Index of Individuals</span></a>
    <% } %></li>
      <li><% if (pageView.equals("themes")) { %>
      <b><span title="List of topics used to define scopes">Index of Themes</span></b>
    <% } else { %>
            <a href="<output:link template="topicmap_%model%.jsp?tm=%topicmap%&pageView=themes" generator="modelLinkGenerator"/>"><span title="List of topics used to define scopes">Index of Themes</span></a>
    <% } %></li>
    <logic:if name="nonames"><logic:then>
      <li><% if (pageView.equals("nonames")) { %>
      <b><span title="List of topics that have no base name">Unnamed Topics (<output:count/>)</span></b>
    <% } else { %>
            <a href="<output:link template="topicmap_%model%.jsp?tm=%topicmap%&pageView=nonames" generator="modelLinkGenerator"/>"><span title="List of topics that have no base name">Unnamed Topics (<output:count/>)</span></a>
    <% } %></li>
    </logic:then></logic:if>
    </ul>
      </td></tr></table>

    <logic:call name="show-hierarchies"/>

    <logic:call name="show_metadata"/>

    </template:put>

    <!-- ================================================================== -->
    <template:put name='content' body='true'>
      <% if (pageView.equals("master")) { %>
        <logic:call name="view_master">
        </logic:call>
      <% } else if (pageView.equals("individuals")) { %>
        <logic:call name="view_individuals">
        </logic:call>
      <% } else if (pageView.equals("themes")) { %>
        <logic:call name="view_themes">
        </logic:call>
      <% } else if (pageView.equals("ontology")) { %>
        <logic:call name="view_ontology">
        </logic:call>
      <% } else if (pageView.equals("nonames")) { %>
        <logic:call name="view_nonames">
        </logic:call>
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
