<%@ page language="java" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output'    %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value'     prefix='value'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue'   prefix='tm'        %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%-- Topic Page - Model: 'simple' --%>

<framework:meminfo name="overall">
<logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">

  <%-- Read in declarations of functions that should be called on this page --%>
  <logic:include file="/functions/application.jsm"/>

  <template:insert template='/views/template_%view%.jsp'>
    <template:put name='title' body='true'>[Omnigator] <output:name /></template:put>

    <template:put name='heading' body='true'>
        <h1 class="boxed"><output:name /></h1>
    </template:put>

    <template:put name='toplinks' body='true'>
      <framework:pluginList separator=" | " group="topic"/>
    </template:put>

          
    <template:put name='navigation' body='true'>

        <!-- ================ Names =========================== -->
          
        <framework:meminfo name="names">
        <logic:set name="names" comparator="nameComparator">
          <tm:names contextFilter="user"/>
        </logic:set>
        <table class="shboxed" width="100%"><tr><td>
            <logic:if name="names">
              <logic:then>
                <h3>Names</h3>
                <ul>
                  <logic:foreach name="names">
                    <li><b><output:name /></b>
                <%-- === scoping themes of this base name === --%>
                <logic:set name="themes"><tm:scope /></logic:set>
                <logic:if name="themes">
                  <logic:then>
                    - Scope: <i><logic:foreach name="themes" separator=", "><output:name /></logic:foreach></i>
                  </logic:then>
                </logic:if>
                </li>
                <%-- === variants of this name === --%>
                <logic:set name="variants" comparator="nameComparator"><tm:variants contextFilter="user"/></logic:set>
                <logic:if name="variants">
                  <logic:then>
                    <ul>
                      <logic:foreach name="variants">
                        <li type='circle'><output:name />
                        <%-- === scoping themes of this variant name === --%>
                        <logic:set name="themes"><tm:scope /></logic:set>
                        <logic:if name="themes">
                          <logic:then>
                            - Scope: <i><logic:foreach name="themes" separator=", "><output:name /></logic:foreach></i>
                          </logic:then>
                        </logic:if>
                        </li>
                      </logic:foreach>
                    </ul>
                   </logic:then>
                   <%--
                   <logic:else>
                     [No variant names available.]
                   </logic:else>
                   --%>
                </logic:if>
                </logic:foreach>
                </ul>
              </logic:then>
              <logic:else>
                No names available for this topic.
              </logic:else>
        </logic:if>
        </td></tr></table>
        </framework:meminfo>
    
        <!-- ====================== Types ==================== -->

        <framework:meminfo name="types">
        <logic:set name="types">
          <tm:superclasses of="topic" level="1" />
        </logic:set>
        <logic:if name="types">
          <logic:then>
            <table class="shboxed" width="100%"><tr><td>
                <h3>Types</h3>
            <ul>
              <logic:foreach>
                <li><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name /></a></li>
              </logic:foreach>
            </ul>
            </td></tr></table>
          </logic:then>
        </logic:if>
        </framework:meminfo>

        <!-- ================= Metadata ======================= -->

        <logic:set name="metadata" comparator="off">
          <tm:occurrences type="internal" contextFilter="user"/>
        </logic:set>
        <logic:set name="metadataTypes">
          <tm:classesOf of="metadata"/>
        </logic:set>
        
            <logic:if name="metadataTypes">
              <logic:then>
          <table class="shboxed" width="100%"><tr><td>
                <h3>Metadata</h3>
                <ul>
                  <logic:foreach name="metadataTypes" set="type">
                  <li><b><output:name /></b>
                  
                      <logic:set name="mymetadata" comparator="off">
                        <tm:filter instanceOf="type">
                          <value:copy of="metadata"/>
                        </tm:filter>
                      </logic:set>
                      <ul>
                        <logic:foreach name="mymetadata">
                              <li type='circle'><output:content />
                          <%-- === scoping themes of this internal occurrence === --%>
                          <logic:set name="themes"><tm:scope /></logic:set>
                          <logic:if name="themes">
                            <logic:then>
                            - Scope: <i><logic:foreach name="themes" separator=", "><output:name /></logic:foreach></i>
                            </logic:then>
                          </logic:if>
                          </li>
  
                        </logic:foreach>
                      </ul>
                  </li>
                            
                </logic:foreach>
                </ul>
            </td></tr></table>
              </logic:then>
        </logic:if>

        <!-- untyped inline occurrences -->              
        <logic:set name="untypedMetadata" comparator="occComparator">
          <tm:filter instanceOf="{NONE}">
            <tm:occurrences of="topic" type="internal" contextFilter="user"/>
          </tm:filter>
        </logic:set>
        <logic:if name="untypedMetadata">
          <logic:then>
            <table class="shboxed" width="100%"><tr><td>
                  <h3>Untyped Metadata</h3>
                      <ul>
                        <logic:foreach name="untypedMetadata">
                              <li type='circle'><output:content />
                          <!-- === scoping themes of this internal occurrence === -->
                          <logic:set name="themes"><tm:scope /></logic:set>
                          <logic:if name="themes">
                            <logic:then>
                            - Scope: <i><logic:foreach name="themes" separator=", "><output:name /></logic:foreach></i>
                            </logic:then>
                          </logic:if>
                          </li>
  
                        </logic:foreach>
                      </ul>
             </td></tr></table>
          </logic:then>
        </logic:if>

              
        <!-- ================= Associations ==================== -->

        <framework:meminfo name="assocs">
        <logic:set name="topicAssocs">
          <tm:associations />
        </logic:set>
        <table class="shboxed" width="100%"><tr><td>
        <logic:if name="topicAssocs"><logic:then>
              <h3>Related subjects</h3>
            <ul>
              <tm:associationTypeLoop name="topic" setAssociations="assocs" setAT="assocType" setART="roleType">

                  <li><b><output:element name="a">
                  <output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator" of="assocType"/></output:attribute>
                    <output:name of="assocType" basenameScope="roleType"/>
                  </output:element></b>

                    <ul>
                    <!-- ===== List binary associations ===== -->
                    <logic:set name="roles" comparator="assocRoleComparator">
                      <tm:roles of="assocs" remove="topic" cardinality="binary" />
                    </logic:set>
                    <logic:if name="roles"><logic:then>
                    <logic:foreach name="roles">
                      <logic:set name="player" comparator="off"><tm:topics/></logic:set>
                      <logic:set name="roletype" comparator="off"><tm:classesOf/></logic:set>
                      <li type='circle'><output:element name="a">
                        <output:attribute name="href"><output:link of="player" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                        <output:name of="player"/>
                      </output:element>
                      (<output:element name="a">
                        <output:attribute name="href"><output:link of="roletype" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                        <output:name of="roletype"/>
                      </output:element>)</li>
                    </logic:foreach>
                    </logic:then></logic:if>
                    </ul>
                  <!-- ===== List n-ary associations ===== -->
                  <table class="nary" border="0" width="100%" cellspacing="0" cellpadding="0">
                  <logic:foreach name="assocs">
                      <logic:set name="roles" comparator="assocRoleTypeComparator">
                      <tm:filter instanceOf="roleType" invert="true">
                            <tm:roles cardinality="nary"/>
                      </tm:filter>
                      </logic:set>
                      <logic:if name="roles"><logic:then>
                      <tr class="nary-row"><td><ul>
                        <logic:foreach name="roles">
                          <logic:set name="player" comparator="off"><tm:topics/></logic:set>
                          <logic:set name="roletype" comparator="off"><tm:classesOf/></logic:set>
                          <li type='circle'><output:element name="a">
                            <output:attribute name="href"><output:link of="player" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                            <output:name of="player"/>
                          </output:element>
                          (<output:element name="a">
                            <output:attribute name="href"><output:link of="roletype" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                            <output:name of="roletype"/>
                          </output:element>)</li>
                        </logic:foreach>
                      </ul></td></tr>
                      </logic:then></logic:if>
                    </logic:foreach><!-- assocs (nary) -->
                  </table>
                  </li>

              </tm:associationTypeLoop>
            </ul>
              </logic:then>
              <logic:else>
                No associations available for this topic.
              </logic:else>
        </logic:if>
        </td></tr></table>
        </framework:meminfo>

        <!-- ================= Topics with occurrences of this type ==== -->

        <logic:set name="occInstances" comparator="occComparator">
          <tm:instances of="topic" as="occurrence"/>
        </logic:set>
        <logic:set name="occInstTopics" comparator="topicComparator">
          <tm:topics of="occInstances"/>
        </logic:set>
              <logic:if name="occInstTopics">
              <logic:then>
          <table class="shboxed" width="100%"><tr><td>
                <h3>Topics with occurrences of this type</h3>
                <ul>
                  <logic:foreach>
                <li><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name/></a></li>
                </logic:foreach>
                </ul>
          </td></tr></table>
              </logic:then>
        </logic:if>
            
    </template:put>
    <template:put name='content' body='true'>
      
        <!-- ================= Subject Indicators ============= -->

        <logic:set name="indicators" comparator="locComparator">
          <tm:indicators/>
        </logic:set>
        
            <logic:if name="indicators">
              <logic:then>
          <table class="shboxed" width="100%"><tr><td>
                <h3>Subject Indicators</h3>
                <ul>
                  <logic:foreach name="indicators">
                <li><a href="<output:locator/>"><output:locator/></a></li>
                </logic:foreach>
                </ul>
          </td></tr></table>
              </logic:then>
        </logic:if>
                              
        <!-- ================= Occurrences ==================== -->

        <framework:meminfo name="occurrences">
        <logic:set name="occs" comparator="off">
          <tm:occurrences type="external" contextFilter="user"/>
        </logic:set>
        <logic:set name="occsTypes">
          <tm:classesOf of="occs"/>
        </logic:set>
        
        <table class="shboxed" width="100%"><tr><td>
            <logic:if name="occsTypes">
              <logic:then>
                <h3>Resources</h3>
                <ul>
                  <logic:foreach name="occsTypes" set="type">
                  <li><b><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name /></a></b>
                  
                      <logic:set name="myoccs" comparator="off">
                        <tm:filter instanceOf="type">
                          <value:copy of="occs"/>
                        </tm:filter>
                      </logic:set>
                      <ul>
                        <logic:foreach name="myoccs">
                          <li type='circle'><a href="<output:locator />"><output:locator /></a>
                          <%-- === scoping themes of this occurrence === --%>
                          <logic:set name="themes"><tm:scope /></logic:set>
                          <logic:if name="themes">
                            <logic:then>
                            - Scope: <i><logic:foreach name="themes" separator=", "><output:name /></logic:foreach></i>
                            </logic:then>
                          </logic:if>
                          </li>
  
                        </logic:foreach>
                      </ul>
                  </li>
                            
                </logic:foreach>
                </ul>
              </logic:then>
              <logic:else>
                No occurrences available for this topic.
              </logic:else>
        </logic:if>
        </td></tr></table>
        </framework:meminfo>

        
        <!-- ================= Instances ==================== -->

        <framework:meminfo name="instances">
        <logic:set name="instances">
          <tm:instances of="topic" as="topic"/>
        </logic:set>
        
            <logic:if name="instances">
              <logic:then>
            <table class="shboxed" width="100%"><tr><td>
                <h3>Topics of this Type</h3>
                <ul>
                  <logic:foreach name="instances">
                <li><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name/></a></li>
              </logic:foreach>
            </ul>
            </td></tr></table>
          </logic:then>
        </logic:if>
        </framework:meminfo>

        <!-- ================= Role Players ============================= -->

        <%-- Retrieve all associations that are typed by this topic --%>
        <logic:set name="assocInstances" comparator="off">
          <tm:instances of="topic" as="association"/>
        </logic:set>
        <%-- Get all association roles for associations from last step  --%>
        <logic:set name="assocInstRoles" comparator="off">
          <tm:roles of="assocInstances"/>
        </logic:set>
        <%-- To order association instances, get types of association roles --%>
        <logic:set name="assocInstRoleTypes">
          <tm:classesOf of="assocInstRoles"/>
        </logic:set>

        <%-- loop over all types of association roles --%>    
              <logic:if name="assocInstRoleTypes">
              <logic:then>
          <table class="shboxed" width="100%"><tr><td>
                <h3>Role Players</h3>
                <ul>
              <logic:foreach set="roleType">
                <li>
                  <b><output:name/></b>
                  <ul>
                  <%-- get association roles for current role type --%>
                    <logic:set name="assocRoles" comparator="off">
                      <tm:filter instanceOf="roleType">
                        <value:copy of="assocInstRoles"/>
                      </tm:filter>
                    </logic:set>
                  <%-- avoid duplicates so retrieve set of players for association role --%>
                  <logic:set name="players"><tm:topics of="assocRoles"/></logic:set>
                  <%-- loop over all topics which play the specified role in a association --%>
                  <logic:foreach name="players">
                    <li type='circle'><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name/></a></li>
                  </logic:foreach></ul></li>
                </logic:foreach>
                </ul>
          </td></tr></table>
              </logic:then>
        </logic:if>

        <%-- These two last sections are commented out, because when they
             are in the page becomes too big for tomcat to handle. This fixes
             bug #301. --%>
            
        <!-- ================= Players of this Role ==================== -->

        <%-- logic:set name="rolesOfPlayers" comparator="off">
          <tm:instances of="topic" as="role"/>
        </logic:set>
        <logic:set name="rolePlayers">
          <tm:topics of="rolesOfPlayers"/>
        </logic:set>
        
            <logic:if name="rolePlayers">
              <logic:then>
            <table class="shboxed" width="100%"><tr><td>
                <h3>Players of this Role</h3>
                <ul>
                  <logic:foreach name="rolePlayers">
                <li><a href="<output:link template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name/></a></li>
              </logic:foreach>
            </ul>
            </td></tr></table>
          </logic:then>
        </logic:if --%>

        <!-- ================= Occurrence instances =================== -->

              <%-- logic:if name="occInstances">
              <logic:then>
          <table class="shboxed" width="100%"><tr><td>
                <h3>Occurrences Instances</h3>
                <ul>
                  <logic:foreach>
                 <logic:set name="myTopic" comparator="off"><tm:topics/></logic:set>
                <li><a href="<output:locator/>"><output:locator/></a> <output:content/>
                (<a href="<output:link of="myTopic" template="topic_%model%.jsp?tm=%topicmap%&id=%id%" generator="modelLinkGenerator"/>"><output:name of="myTopic"/></a>)
                </li>
                </logic:foreach>
                </ul>
          </td></tr></table>
              </logic:then>
        </logic:if --%>

            
    </template:put>
          
    <template:put name='outro' body='true'></template:put>
      
    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>
      
  </template:insert> 
</logic:context>
</framework:meminfo>
