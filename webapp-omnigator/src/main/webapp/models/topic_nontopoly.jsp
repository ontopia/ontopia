<%@ page language="java" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'          prefix='tolog'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output'    %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value'     prefix='value'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue'   prefix='tm'        %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%-- Topic Page - Model: 'complete' --%>

<logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">

  <%-- Read in declarations of functions that should be called on this page --%>
  <logic:include file="/functions/application.jsm"/>
  <%-- Test whether this is an ontopoly-tm or not --%>
  <logic:set name="ontopoly-tm">
    <tm:lookup indicator="http://psi.ontopia.net/ontology/system-topic"/>
  </logic:set>
  <logic:if name="ontopoly-tm"><logic:then>
    <logic:include file="/functions/nontopoly_topic_model.jsm"/>
  </logic:then><logic:else>
  <%-- If not, load complete model instead of nontopoly model --%>
    <logic:include file="/functions/complete_topic_model.jsm"/>
  </logic:else></logic:if>

  <template:insert template='/views/template_%view%.jsp'>
    <template:put name='title' body='true'>
      [Omnigator] <output:name />
    </template:put>

    <template:put name='heading' body='true'>
      <logic:call name="heading_info"/>
    </template:put>

    <template:put name='plugins' body='true'>
      <framework:pluginList separator=" | " group="topic"/>
    </template:put>

    <template:put name='navigation' body='true'>
      <logic:call name="nav_topic_info"/>
      <table width='100%'><tr><td>
        <%-- DO NOT DELETE (this special insertion is for Netscape Navigator) --%>
      </td></tr></table>
    </template:put>

    <template:put name='content' body='true'>
      <logic:call name="con_topic_info">
      </logic:call>
    </template:put>

    <template:put name='outro' body='true'>
      <logic:call name="con_topic_outro">
      </logic:call>
    </template:put>


    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

  </template:insert>
</logic:context>
