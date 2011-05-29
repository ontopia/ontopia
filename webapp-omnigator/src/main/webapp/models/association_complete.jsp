<%@ page language="java" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output'    %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value'     prefix='value'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue'   prefix='tm'        %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog' %>
<framework:response/>

<%-- Association Page - Model: 'complete' --%>

<logic:context tmparam="tm" settm="topicmap">

  <logic:set name="assoc"><tm:lookup objectid='<%=request.getParameter("id")%>'/></logic:set>
  <template:insert template='/views/template_%view%.jsp'>
    <template:put name='title' body='true'>[Omnigator] association <output:objectid of="assoc"/></template:put>
    <logic:set name="assocType"><tm:classesOf of="assoc"/></logic:set>

    <template:put name='heading' body='true'>
      <table class="boxed" width="100%" cellspacing="0" cellpadding="4" border="0"><tr>
        <td><tolog:choose>
              <tolog:when query="reifies($REIFIER, %assoc%)?"><h1><a href='<output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator" of="assocType"/>'><tolog:out var="REIFIER"/></a></h1></tolog:when>
              <tolog:otherwise><h1>Association details</h1></tolog:otherwise>
            </tolog:choose></td>
        <td align="right"><logic:if name="assocType"><logic:then><b>Type: <output:name of="assocType"/>&nbsp;</b></logic:then></logic:if></td>
      </tr></table>
    </template:put>

    <template:put name='plugins' body='true'>
      <%-- =============== TopicMap Link ==================== --%>
      <logic:set name="tm_topic" comparator="off"><tm:reifier of="topicmap" /></logic:set>
      <logic:set name="shortname"><tm:lookup indicator="http://psi.ontopia.net/basename/#short-name"/></logic:set>
        <logic:if name="tm_topic">
          <logic:then>
            <a href="<output:link of="topicmap" template="topicmap_%model%.jsp?tm=%topicmap%" generator="modelLinkGenerator"/>"><output:name basenameScope="shortname"/></a>
          </logic:then>
          <logic:else>
            <a href="<output:link of="topicmap" template="topicmap_%model%.jsp?tm=%topicmap%" generator="modelLinkGenerator"/>">Index Page</a>
          </logic:else>
        </logic:if>
      <framework:pluginList separator=" | " preSeparator="true" group="topic"/>
    </template:put>

    <template:put name='plugins' body='true'>
      <%-- =============== TopicMap Link ==================== --%>
      <logic:set name="tm_topic" comparator="off"><tm:reifier of="topicmap" /></logic:set>
      <logic:set name="shortname"><tm:lookup indicator="http://psi.ontopia.net/basename/#short-name"/></logic:set>
        <logic:if name="tm_topic">
          <logic:then>
            <a href="<output:link of="topicmap" template="topicmap_%model%.jsp?tm=%topicmap%" generator="modelLinkGenerator"/>"><output:name basenameScope="shortname"/></a>
          </logic:then>
          <logic:else>
            <a href="<output:link of="topicmap" template="topicmap_%model%.jsp?tm=%topicmap%" generator="modelLinkGenerator"/>">Index Page</a>
          </logic:else>
        </logic:if>
      <framework:pluginList separator=" | " preSeparator="true" group="topic"/>
    </template:put>

    <template:put name='navigation' body='true'>
          <table class="shboxed" width="100%"><tr><td>
            <h3><span title="Topic that types this association (if any)">Association type</span></h3>
                  <b><logic:if name="assocType"><logic:then>
                  <output:element name="a">
                  <output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator" of="assocType"/></output:attribute>
                    <span title="Association type"><output:name of="assocType"/></span>
                  </output:element>
                  </logic:then><logic:else>
                    <output:property name="msg.UntypedAssoc"/>
                  </logic:else></logic:if></b>
          </td></tr></table>
    </template:put>

    <template:put name='content' body='true'>

          <logic:set name="roles" comparator="assocRoleTypeComparator"><tm:roles of="assoc"/></logic:set>
          <table class="shboxed" width="100%"><tr><td>
            <h3><span title="Topics that play roles in this association">Role players (<output:count of="roles"/>)</span></h3>

                  <logic:if name="roles"><logic:then>
                  <!-- ===== List role players ===== -->
                  <table class="nary" border="0" width="100%" cellspacing="0" cellpadding="0">
                        <tr class="nary-row"><td><b>role player</b></td><td><b>role type</b></td></tr>
                        <logic:foreach name="roles">
                          <logic:set name="player" comparator="off"><tm:topics/></logic:set>
                          <logic:set name="roletype" comparator="off"><tm:classesOf/></logic:set>
                          <tr class="nary-row"><td>
                          <logic:if name="player"><logic:then>
                            <output:element name="a">
                              <output:attribute name="href"><output:link of="player"
                               template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                               generator="modelLinkGenerator"/></output:attribute>
                              <output:name of="player"/>
                            </output:element></logic:then><logic:else>- no role player -</logic:else></logic:if></td>
                            <td><output:element name="a"><output:attribute name="href"><output:link of="roletype"
                              template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                              generator="modelLinkGenerator"/></output:attribute><output:name of="roletype"/></output:element>
<%--
                              <!-- <reificationSupport> try to retrieve topic that reifies this association -->
                              <logic:set name="assoc" comparator="off"><tm:associations/></logic:set>
                              <logic:set name="reifiedAssoc" comparator="off"><tm:reifier of="assoc"/></logic:set>
                              <logic:if name="reifiedAssoc"><logic:then>
                                <span class="reified"><output:element name="a">
                                  <output:attribute name="href"><output:link of="reifiedAssoc" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                                  more...</output:element></span>
                              </logic:then></logic:if>
                              <!-- </reificationSupport> -->
--%>
                            </td></tr>
                        </logic:foreach>
                  </table>
                  </logic:then><logic:else>No role players</logic:else></logic:if>
           </td></tr></table>

      <table width='100%'><tr><td>
        <%-- DO NOT DELETE (this special insertion is for Netscape Navigator) --%>
      </td></tr></table>
    </template:put>

    <template:put name='outro' body='true'>
      <!-- === Source Locators === -->
      <logic:set name="sourceLocators">
        <tm:sourceLocators of="assoc"/>
      </logic:set>
      <logic:if name="sourceLocators"><logic:then>
        <pre class="comment">Object id: <output:objectid of="assoc"/>
Item identifier(s):
  <logic:foreach name="sourceLocators">[<output:locator/>]
  </logic:foreach></pre>
      </logic:then>
      <logic:else>
        <pre class="comment">Object id: <output:objectid of="assoc"/></pre>
      </logic:else>
      </logic:if>
    </template:put>

    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

  </template:insert>
</logic:context>
