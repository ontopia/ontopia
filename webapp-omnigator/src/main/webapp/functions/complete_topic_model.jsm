<!--
     Definition of functions that are used inside the topic page
     for the complete model.
     ...........................................................................
-->

<module>

  <function name="heading_info">
    <logic:set name="types"><tm:classesOf of="topic"/></logic:set>
      <table class="boxed" width="100%" cellspacing="0" cellpadding="4" border="0"><tr>
        <td><h1><output:name basenameScope="types"/></h1></td>
        <td align="right"><logic:call name="topic_types" /></td>
      </tr></table>
  </function>

  <function name="nav_name_info">
    <ul>
      <logic:foreach name="names">
        <li><b><output:name /></b>
        <!-- === scoping themes of this base name === -->
        <logic:set name="themes"><tm:scope /></logic:set>
        <logic:if name="themes">
          <logic:then>
            - Scope: <i><logic:foreach name="themes" separator="; "><output:element
              name="a"><output:attribute name="href"><output:link
              template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
              generator="modelLinkGenerator"/></output:attribute><output:name/></output:element></logic:foreach></i>
          </logic:then>
        </logic:if>
        </li>
        <!-- === variants of this name === -->
        <logic:set name="variants" comparator="nameComparator">
          <tm:variants contextFilter="user"/>
        </logic:set>
        <logic:if name="variants">
          <logic:then>
            <ul>
              <logic:foreach name="variants">
                <li type='circle'><output:name />
                <!-- === scoping themes of this variant name === -->
                <logic:set name="themes"><tm:scope /></logic:set>
                <logic:if name="themes">
                  <logic:then>
                    - Scope: <i><logic:foreach name="themes" separator="; "><output:element
                      name="a"><output:attribute name="href"><output:link
                      template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                      generator="modelLinkGenerator"/></output:attribute><output:name/></output:element></logic:foreach></i>
                  </logic:then>
                </logic:if>
                </li>
              </logic:foreach>
            </ul>
           </logic:then>
           <!--
           <logic:else>
             [No variant names available.]
           </logic:else>
           -->
        </logic:if>
      </logic:foreach>
    </ul>
  </function>


  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
       function "nav_topic_info": showing information about the main
       important aspects about a topic (for example names).
       (displayed in 'navigation' box)
       ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <function name="nav_topic_info" params="topic">

        <!-- ================ Names =========================== -->
        <!-- untyped names -->
        <logic:set name="names" comparator="nameComparatorWithGenerality">
          <tm:filter instanceOf="{NONE}">
            <tm:names contextFilter="user"/>
          </tm:filter>
        </logic:set>
        <logic:if name="names">
        <logic:then>
          <table class="shboxed" width="100%"><tr><td>
          <h3><span title="Untyped base names and variant names of this topic">Untyped Names (<output:count/>)</span></h3>
          <logic:call name="nav_name_info"/>
          </td></tr></table>
        </logic:then>
        </logic:if>

        <!-- typed names -->
        <tolog:set query="select $all-typed-names from
          topic-name(%topic%, $all-typed-names),
          type($all-typed-names, $name-types)
          order by $all-typed-names?"/>
        <logic:set name="typed-names">
          <value:intersection>
            <tm:names contextFilter="user"/>
            <value:copy of="all-typed-names"/>
          </value:intersection>
        </logic:set>
        <logic:set name="name-types">
          <tm:classesOf of="typed-names"/>
        </logic:set>
        <logic:foreach name="name-types" set="name-type">
          <tolog:set var="_names" query="select $name from topic-name(%topic%, $name), type($name, %name-type%)?"/> 
          <logic:set name="names" comparator="nameComparator">
            <value:intersection>
              <value:copy of="_names"/>
              <value:copy of="typed-names"/>
          </value:intersection>
          </logic:set>
          <table class="shboxed" width="100%"><tr><td>
          <h3><output:element name="span"><output:attribute name="title">Names of type <tolog:out var='name-type'/></output:attribute><tolog:out var="name-type"/> (<output:count/>)</output:element></h3>
          <logic:call name="nav_name_info"/>
          </td></tr></table>
        </logic:foreach>

<!-- Unnecessary to flag lack of a name?
     <logic:else>
       No names available for this topic.
     </logic:else>
-->

        <!-- ================= Main subjects =============== -->
        <logic:set name="reifiedObjs" comparator="off">
          <tm:filter is="topicmap" invert="yes">
            <tm:reified of="topic"/>
          </tm:filter>
        </logic:set>
        <logic:set name="refTopics">
          <tm:topics of="reifiedObjs"/>
        </logic:set>

        <logic:if name="refTopics">
          <logic:then>
          <table class="shboxed" width="100%"><tr><td>
            <h3><span title="Topics involved in the association or occurrence that is reified by this topic">Reification Topics (<output:count/>)</span></h3>
            <ul>
            <logic:foreach name="refTopics">
              <li><output:element name="a"><output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                  <output:name/></output:element></li>
            </logic:foreach>
            </ul>
          </td></tr></table>
          </logic:then>
        </logic:if>

        <!-- ================= Hierarchy info ==================== -->
        <logic:set name="is-hierarchical">
          <value:intersection>
            <tm:classesOf of="topic"/>
            <tm:lookup indicator="http://www.techquila.com/psi/hierarchy/#hierarchical-relation-type"/>
          </value:intersection>
        </logic:set>

        <logic:if name="is-hierarchical">
          <logic:then>
            <logic:set name="role-types">
              <tm:tolog query="select $RTYPE from
                                 type($ASSOC, %topic%),
                                 association-role($ASSOC, $ROLE),
                                 type($ROLE, $RTYPE)?" select="RTYPE"/>
            </logic:set>
            <logic:set name="super">
              <value:intersection>
                <tm:instances><tm:lookup indicator="http://www.techquila.com/psi/hierarchy/#superordinate-role-type"/></tm:instances>
                <value:copy of="role-types"/>
              </value:intersection>
            </logic:set>
            <logic:set name="sub">
              <value:intersection>
                <tm:instances><tm:lookup indicator="http://www.techquila.com/psi/hierarchy/#subordinate-role-type"/></tm:instances>
                <value:copy of="role-types"/>
              </value:intersection>
            </logic:set>
            <logic:set name="rogues">
              <value:difference>
                <value:copy of="role-types"/>
                <value:union>
                  <value:copy of="super"/>
                  <value:copy of="sub"/>
                </value:union>
              </value:difference>
            </logic:set>

            <table class="shboxed" width="100%"><tr><td>
              <h3><span title="Role types used in this association type">Role Types (<output:count of="role-types"/>)</span></h3>

            <ul>
              <li><b>Parent role</b></li>
              <ul>
              <logic:foreach name="super">
                <li><output:element name="a"><output:attribute name="href"
                  ><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                  generator="modelLinkGenerator"/></output:attribute
                  ><output:name/></output:element></li>
              </logic:foreach></ul>

              <li><b>Child role</b></li>
              <ul>
              <logic:foreach name="sub">
                <li><output:element name="a"><output:attribute name="href"
                  ><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                  generator="modelLinkGenerator"/></output:attribute
                  ><output:name/></output:element></li>
              </logic:foreach></ul>

              <logic:if name="rogues">
                <logic:then>
                  <li><b>Rogue roles</b></li>
                  <ul>
                    <logic:foreach><li><output:element name="a"><output:attribute name="href"
                ><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                generator="modelLinkGenerator"/></output:attribute
                ><output:name/></output:element></li></logic:foreach>
                  </ul>
                </logic:then>
              </logic:if>
            </ul>

            </td></tr></table>
          </logic:then>
        </logic:if>


        <!-- ================= Unary associations ============== -->

        <!-- tolog query to grab all unary associations in topicAssocs -->
        <logic:set name="unaryAssocs" comparator="off">
          <tm:tolog query="/* #OPTION: optimizer.reorder=false */
                           select $A, $T from
                           role-player($R1, %topic%),
                           association-role($A, $R1),
                           association($A),
                           type($A, $T),
                           not(association-role($A, $R2), $R1 /= $R2)
                           order by $T?" select="A"/>
        </logic:set>
        <logic:if name="unaryAssocs"><logic:then>
          <table class="shboxed" width="100%"><tr><td>
            <h3><span title="Unary associations in which this topic plays a role">Unary associations (<output:count/>)</span></h3>
            <ul>
              <logic:foreach name="unaryAssocs">
                <logic:set name="assocType">
                  <tm:classesOf/>
                </logic:set>
                <li><b><logic:if name="assocType"><logic:then>
                  <output:element name="a">
                  <output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator" of="assocType"/></output:attribute>
                    <span title="Association type"><output:name of="assocType"/></span></output:element>
                  </logic:then><logic:else>
                    <output:property name="msg.UntypedAssoc"/>
                  </logic:else></logic:if></b>
                  </li>
              </logic:foreach>
            </ul>
           </td></tr></table>
         </logic:then>
        </logic:if>

        <!-- ================= Other Associations ==================== -->
        <logic:set name="topicAssocs" comparator="off">
          <value:difference>
            <tm:associations contextFilter="user" />
            <value:copy of="unaryAssocs"/>
          </value:difference>
        </logic:set>
        <logic:if name="topicAssocs"><logic:then>
          <table class="shboxed" width="100%"><tr><td>
            <h3><span title="Topics with which this topic is associated (grouped by association type)">Associations (<output:count/>)</span></h3>
            <ul>
              <tm:associationTypeLoop name="topic" contextFilter="user"
                                      setAssociations="assocs"
                                      setAT="assocType" setART="roleType">

                <logic:set name="isUnary">
                  <!-- have to do a test within the loop in order to only output > nary -->
                  <value:intersection>
                    <value:copy of="assocs"/>
                    <value:copy of="unaryAssocs"/>
                  </value:intersection>
                </logic:set>
                <logic:if name="isUnary"><logic:else>

                  <li><b><logic:if name="assocType"><logic:then>
                  <logic:set name="rolescope">
                    <value:union>
                      <value:copy of="roleType"/>
                      <framework:getcontext context="basename"/>
                    </value:union>
                  </logic:set>

                  <output:element name="a">
                  <output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator" of="assocType"/></output:attribute>
                    <span title="Association type"><logic:if name="roleType"><logic:then><output:name of="assocType" basenameScope="rolescope"/></logic:then><logic:else><output:name
                     of="assocType"/></logic:else></logic:if></span></output:element>
                  </logic:then><logic:else>
                    <output:property name="msg.UntypedAssoc"/>
                  </logic:else></logic:if></b>

                    <!-- ===== List binary associations ===== -->
                    <logic:set name="roles" comparator="assocRoleComparator">
                      <value:difference>
                        <tm:roles of="assocs" cardinality="binary"/>
                        <logic:if name="roleType"><logic:then>
                          <tm:filter instanceOf="roleType">
                            <tm:roles of="topic"/>
                          </tm:filter>
                        </logic:then><logic:else>
                          <tm:roles of="topic"/>
                        </logic:else></logic:if>
                      </value:difference>
                    </logic:set>
                    <logic:if name="roles"><logic:then>
                    <ul>
                     <logic:foreach name="roles" set="role">
                      <logic:set name="player" comparator="off"><tm:topics/></logic:set>
                      <logic:set name="roletype" comparator="off"><tm:classesOf/></logic:set>
                      <logic:set name="assoc" comparator="off"><tm:associations of="role"/></logic:set>
                      <logic:set name="scope"><tm:scope of="assoc"/></logic:set>
                      <logic:set name="context">
                        <!-- context variable sets basenameScope when outputting name of role playing topic -->
                        <value:union><value:copy of="topic"/><value:copy of="roletype"/></value:union>
                      </logic:set>
                      <logic:if name="player"><logic:then>
                          <li type='circle'><output:element name="a">
                            <output:attribute name="href"><output:link of="player"
                             template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                             generator="modelLinkGenerator"/></output:attribute>

                             <output:element name="span"><output:attribute
                              name="title">role type: <output:name
                              of='roletype'/><logic:if name="scope"><logic:then>; scope: <logic:foreach
                              name="scope" separator="; "><output:name/></logic:foreach></logic:then></logic:if></output:attribute><logic:if
                              equals="topic"><logic:then>[self]</logic:then><logic:else><output:name
                              of="player" basenameScope="context"/></logic:else></logic:if></output:element></output:element>

                              <!-- <reificationSupport> try to retrieve topic that reifies this association -->
                              <logic:set name="reifiedAssoc" comparator="off"><tm:reifier of="assoc"/></logic:set>
                              <logic:if name="reifiedAssoc"><logic:then>
                                <span class="reified"><output:element name="a">
                                  <output:attribute name="href"><output:link of="reifiedAssoc" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                                  more...</output:element></span>
                              </logic:then></logic:if>
                              <!-- </reificationSupport> -->

                        </li>
                      </logic:then></logic:if>
                     </logic:foreach>
                    </ul>
                  </logic:then></logic:if>
                  <!-- ===== List n-ary associations ===== -->
<tolog:if query='select $A from
role-player($R1, %topic%),
type($A, %assocType%),
association-role($A, $R1),
association-role($A, $R2),
association-role($A, $R3),
$R1 /= $R2,
$R1 /= $R3,
$R2 /= $R3?'>
                  <table class="nary" border="0" width="100%" cellspacing="0" cellpadding="0">
                  <logic:foreach name="assocs">
                      <logic:set name="roles" comparator="assocRoleTypeComparator">
                        <value:difference>
                          <tm:roles cardinality="nary"/>
                          <logic:if name="roleType"><logic:then>
                            <tm:filter instanceOf="roleType">
                              <tm:roles of="topic"/>
                            </tm:filter>
                          </logic:then><logic:else>
                            <tm:roles of="topic"/>
                          </logic:else></logic:if>
                        </value:difference>
                      </logic:set>
                      <logic:if name="roles"><logic:then>
                      <tr class="nary-row"><td><ul>
                        <logic:foreach name="roles" set="role">

                          <logic:set name="player" comparator="off"><tm:topics/></logic:set>
                          <logic:set name="roletype" comparator="off"><tm:classesOf/></logic:set>

                          <logic:set name="assoc" comparator="off"><tm:associations of="role"/></logic:set>
                          <logic:set name="scope"><tm:scope of="assoc"/></logic:set>
                          <logic:set name="context">
                            <!-- context variable sets basenameScope when outputting name of role playing topic -->
                            <value:union><value:copy of="topic"/><value:copy of="roletype"/></value:union>
                          </logic:set>

                         <logic:if name="player"><logic:then>
                            <li type='circle'>

                             <output:element name="span"><output:attribute
                              name="title"><logic:if name="scope"><logic:then>; scope: <logic:foreach
                              name="scope" separator="; "><output:name/></logic:foreach></logic:then></logic:if></output:attribute>

                              <i><output:element name="a"><output:attribute name="href"><output:link of="roletype"
                               template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                               generator="modelLinkGenerator"/></output:attribute><output:name
                               of='roletype'/></output:element></i>: 

 
                              <output:element name="a">
                               <output:attribute name="href"><output:link of="player"
                                template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                                generator="modelLinkGenerator"/></output:attribute>

                                <logic:if equals="topic"><logic:then>[self]</logic:then><logic:else><output:name
                                of="player" basenameScope="context"/></logic:else></logic:if></output:element>

                             </output:element>

                              <!-- <reificationSupport> try to retrieve topic that reifies this association -->
                              <logic:set name="assoc" comparator="off"><tm:associations/></logic:set>
                              <logic:set name="reifiedAssoc" comparator="off"><tm:reifier of="assoc"/></logic:set>
                              <logic:if name="reifiedAssoc"><logic:then>
                                <span class="reified"><output:element name="a">
                                  <output:attribute name="href"><output:link of="reifiedAssoc" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                                  more...</output:element></span>
                              </logic:then></logic:if>
                              <!-- </reificationSupport> -->

                            </li>
                          </logic:then></logic:if>
                        </logic:foreach>
                      </ul></td></tr>
                      </logic:then></logic:if>
                    </logic:foreach><!-- assocs (nary) -->
                  </table>
</tolog:if>
                  </li>

                </logic:else></logic:if><!-- not unary assoc -->

              </tm:associationTypeLoop>
            </ul>
           </td></tr></table>
         </logic:then>
        </logic:if>

        <!-- ================= Topics with occurrences of this type ==== -->
        <logic:set name="occInstances" comparator="off">
          <tm:instances of="topic" as="occurrence"/>
        </logic:set>
        <logic:set name="occInstTopics" comparator="topicComparator">
          <tm:topics of="occInstances"/>
        </logic:set>
        <logic:if name="occInstTopics">
          <logic:then>
          <table class="shboxed" width="100%"><tr><td>
                <h3><span title="Topics that have occurrences of this type">Topics (<output:count/>)</span></h3>
                <ul>
                  <logic:foreach>
                <li><output:element name="a">
                  <output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                  <output:name/></output:element></li>
                </logic:foreach>
                </ul>
          </td></tr></table>
          </logic:then>
        </logic:if>

        <!-- ================= Names scoped by this topic ============== -->
        <logic:set name="scopedNames">
          <tm:tolog select="T" query="select $T from topic-name($T, $N), scope($N, %topic%)?"/>
        </logic:set>
        <logic:if name="scopedNames">
          <logic:then>
          <table class="shboxed" width="100%"><tr><td>
                <h3><span title="Topics that have names that are scoped by this topic">Scoped Names (<output:count/>)</span></h3>
                <ul>
                  <logic:foreach>
                    <li><output:name basenameScope="topic"/> (<output:element name="a"><output:attribute
                     name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                     generator="modelLinkGenerator"/></output:attribute><output:name/></output:element>)</li>
                  </logic:foreach>
                </ul>
          </td></tr></table>
          </logic:then>
        </logic:if>

        <!-- ================= Association types scoped by this topic ============== -->
        <logic:set name="scopedAssocTypes">
          <tm:tolog select="T" query="/* #OPTION: optimizer.reorder=false */ select $T from scope($myAssoc, %topic%), type($myAssoc, $T), association($myAssoc) order by $T?"/>
        </logic:set>
        <logic:if name="scopedAssocTypes">
          <logic:then>
          <table class="shboxed" width="100%"><tr><td>
                <h3><span title="Types of associations that are scoped by this topic">Scoped Association Types (<output:count/>)</span></h3>
                <ul>
                  <logic:foreach>
                    <li><output:element name="a"><output:attribute
                     name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                     generator="modelLinkGenerator"/></output:attribute><output:name/></output:element></li>
                  </logic:foreach>
                </ul>
          </td></tr></table>
          </logic:then>
        </logic:if>

  </function>


  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
       function "con_topic_info": showing topic information about subject
       indicators, occurrences, instances and more (inside 'content' box)
       ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <function name="con_topic_info" params="topic">

        <!-- ================= Subject Addresses ============= -->
        <logic:set name="subjectAddresses" comparator="locComparator">
          <tm:subjectAddress/>
        </logic:set>

        <logic:if name="subjectAddresses">
          <logic:then>
          <table class="shboxed" width="100%"><tr><td>
                <h3><span title="Locator that refers to the information resource that is the subject of the topic">Subject Locator (<output:count/>)</span></h3>
                <ul>
                <logic:foreach name="subjectAddresses">
                <li><output:element name="a">
                  <output:attribute name="href"><output:locator/></output:attribute>
                  <output:locator/></output:element></li>
                </logic:foreach>
                </ul>
          </td></tr></table>
          </logic:then>
        </logic:if>

        <!-- ================= Subject Indicators ============= -->
        <logic:set name="indicators" comparator="locComparator">
          <tm:indicators/>
        </logic:set>

        <logic:if name="indicators">
          <logic:then>
          <table class="shboxed" width="100%"><tr><td>
                <h3><span title="Resources that indicate the subject of the topic">Subject Identifiers (<output:count/>)</span></h3>
                <ul>
                <logic:foreach name="indicators">
                <li><output:element name="a">
                  <output:attribute name="href"><output:locator/></output:attribute>
                  <output:locator/></output:element></li>
                </logic:foreach>
                </ul>
          </td></tr></table>
          </logic:then>
        </logic:if>

        <!-- ================= Hierarchy position ==================== -->
        <logic:set name="has-hierarchy">
          <tm:lookup indicator="http://www.techquila.com/psi/hierarchy/#hierarchical-relation-type"/>
        </logic:set>

        <logic:set name="hierarchicals">
          <logic:if name="has-hierarchy">
            <logic:then>
               <tm:tolog query='using h for i"http://www.techquila.com/psi/hierarchy/#"
                                     select $ATYPE from
                                     role-player($ROLE, %topic%),
                                     association-role($ASSOC, $ROLE),
                                     type($ASSOC, $ATYPE),
                                     instance-of($ATYPE, h:hierarchical-relation-type)?'
                        select="ATYPE"/>
            </logic:then>
            <logic:else>
              <value:copy of="has-hierarchy"/>
            </logic:else>
          </logic:if>
        </logic:set>

        <logic:foreach name="hierarchicals" set="assoctype">
          <table class="shboxed" width="100%"><tr><td>
          <h3><output:element name="a"><output:attribute name="href"
            ><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
            generator="modelLinkGenerator"/></output:attribute>Hierarchy (<output:name
            />)</output:element></h3>

          <logic:set name="roletypetypes">
            <value:union>
              <tm:lookup indicator="http://www.techquila.com/psi/hierarchy/#superordinate-role-type"/>
              <tm:lookup indicator="http://www.techquila.com/psi/hierarchy/#subordinate-role-type"/>
            </value:union>
          </logic:set>

          <logic:if name="roletypetypes" sizeEquals="2">
            <logic:then>
              <logic:call name="show-hierarchy-position">
                <logic:set name="ancestors"><value:copy of="topic"/></logic:set>

                <logic:set name="super">
                  <tm:tolog query='
                            using h for i"http://www.techquila.com/psi/hierarchy/#"
                            select $RTYPE from
                            type($ASSOC, %assoctype%),
                            association-role($ASSOC, $ROLE),
                            type($ROLE, $RTYPE),
                            instance-of($RTYPE, h:superordinate-role-type)?'
                            select="RTYPE"/>
                </logic:set>

                <logic:set name="sub">
                  <tm:tolog query='
                            using h for i"http://www.techquila.com/psi/hierarchy/#"
                            select $RTYPE from
                            type($ASSOC, %assoctype%),
                            association-role($ASSOC, $ROLE),
                            type($ROLE, $RTYPE),
                            instance-of($RTYPE, h:subordinate-role-type)?'
                            select="RTYPE"/>
                </logic:set>
              </logic:call>
            </logic:then>
          </logic:if>

          </td></tr></table>
        </logic:foreach>

        <!-- ================= Occurrences ==================== -->

        <!-- ..... Internal typed occurrences ............. -->
        <logic:set name="multiline-text"><tm:lookup indicator="http://psi.ontopia.net/xtm/occurrence-type/multiline-text"/></logic:set>
        <logic:set name="code-text"><tm:lookup indicator="http://psi.ontopia.net/xtm/occurrence-type/code-text"/></logic:set>
        <!-- find all internal occurrences of this topic in the current context -->
        <logic:set name="occs" comparator="off">
          <tm:occurrences of="topic" type="internal" contextFilter="user"/>
        </logic:set>
        <logic:set name="occTypes">
          <tm:classesOf of="occs"/>
        </logic:set>
        <logic:if name="occTypes">
          <logic:then>
            <!-- reset occs to contain just typed internal occurrences of the current *context* -->
            <logic:set name="occs">
              <value:intersection>
                <value:copy of="occs"/>
                <tm:tolog select="OCCS" query="
                select $OCCS from
                occurrence(%topic%, $OCCS),
                type($OCCS, $TYPES),
                value($OCCS, $VAL)?"/>
              </value:intersection>
            </logic:set>
          <table class="shboxed" width="100%"><tr><td>
                <h3><span title="ResourceData occurrences of this type (grouped by occurrence type)">Internal Occurrences (<output:count of="occs"/>)</span></h3>
                <ul>
                  <logic:foreach name="occTypes" set="type">
                  <li><b><output:element name="a"><output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                    <span title="Occurrence type"><output:name/></span></output:element></b>
                      <logic:set name="myoccs" comparator="occComparator">
                        <tm:filter instanceOf="type">
                          <value:copy of="occs"/>
                        </tm:filter>
                      </logic:set>
                      <ul>
                        <logic:foreach name="myoccs" set="myocc">
                          <logic:set name="typetype"><tm:classesOf of="type"/></logic:set>
                          <li type='circle'><logic:if name="typetype"><logic:then><logic:if equals="multiline-text"><logic:then><pre
                          class="multiline-text"><output:content of="myocc"/></pre></logic:then><logic:else><logic:if
                          name="typetype" equals="code-text"><logic:then><pre class="code-text"><output:content
                          of="myocc"/></pre></logic:then><logic:else><output:content
                          of="myocc"/></logic:else></logic:if></logic:else></logic:if></logic:then><logic:else><output:content
                          of="myocc"/></logic:else></logic:if>

                        <!-- <reificationSupport> try to retrieve topic that reifies this occurrence -->
                        <logic:set name="reifiedOcc" comparator="off"><tm:reifier/></logic:set>
                        <logic:if name="reifiedOcc"><logic:then>
                          <span class="reified"><output:element name="a">
                            <output:attribute name="href"><output:link of="reifiedOcc" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                            more...</output:element></span>
                        </logic:then></logic:if>
                        <!-- </reificationSupport> -->
                          <!-- === scoping themes of this internal occurrence === -->
                          <logic:set name="themes"><tm:scope /></logic:set>
                          <logic:if name="themes">
                            <logic:then>
                            - Scope: <i><logic:foreach name="themes" separator="; "><output:element
                              name="a"><output:attribute name="href"><output:link
                              template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                              generator="modelLinkGenerator"/></output:attribute><output:name/></output:element></logic:foreach></i>
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

        <!-- ..... Internal untyped occurrences ............. -->
        <logic:set name="untypedOccs" comparator="occComparator">
          <tm:filter instanceOf="{NONE}">
            <tm:occurrences of="topic" type="internal" contextFilter="user"/>
          </tm:filter>
        </logic:set>
        <logic:if name="untypedOccs">
          <logic:then>
            <table class="shboxed" width="100%"><tr><td>
                  <h3><span title="Untyped resourceData occurrences">Untyped Internal Occurrences (<output:count/>)</span></h3>
                      <ul>
                        <logic:foreach name="untypedOccs">
                        <li type='circle'><span style="white-space: pre-line"><output:content/></span>
                        <!-- <reificationSupport> try to retrieve topic that reifies this occurrence -->
                        <logic:set name="reifiedOcc" comparator="off"><tm:reifier/></logic:set>
                        <logic:if name="reifiedOcc"><logic:then>
                          <span class="reified"><output:element name="a">
                            <output:attribute name="href"><output:link of="reifiedOcc" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                            more...</output:element></span>
                        </logic:then></logic:if>
                        <!-- </reificationSupport> -->
                          <!-- === scoping themes of this internal occurrence === -->
                          <logic:set name="themes"><tm:scope /></logic:set>
                          <logic:if name="themes">
                            <logic:then>
                            - Scope: <i><logic:foreach name="themes" separator="; "><output:element
                              name="a"><output:attribute name="href"><output:link
                              template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                              generator="modelLinkGenerator"/></output:attribute><output:name/></output:element></logic:foreach></i>
                            </logic:then>
                          </logic:if>
                        </li>
                        </logic:foreach>
                      </ul>
             </td></tr></table>
          </logic:then>
        </logic:if>

        <!-- ..... External typed occurrences ............. -->
        <logic:set name="occs" comparator="off">
          <tm:occurrences of="topic" type="external" contextFilter="user"/>
        </logic:set>
        <logic:set name="occsTypes">
          <tm:classesOf of="occs"/>
        </logic:set>
        <logic:if name="occsTypes">
          <logic:then>
            <logic:set name="occs">
              <value:intersection>
                <value:copy of="occs"/>
                <tm:tolog select="OCCS" query="
                select $OCCS from
                occurrence(%topic%, $OCCS),
                type($OCCS, $TYPES),
                resource($OCCS, $VAL)?"/>
              </value:intersection>
            </logic:set>
            <table class="shboxed" width="100%"><tr><td>
                <h3><span title="ResourceRef occurrences of this topic (grouped by occurrence type)">External Occurrences (<output:count of="occs"/>)</span></h3>
                <ul>
                  <logic:foreach name="occsTypes" set="type">
                  <li><b><output:element name="a">
                    <output:attribute name="href"><output:link
                      template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                      generator="modelLinkGenerator"/></output:attribute>
                    <span title="Occurrence type"><output:name/></span></output:element></b>

                      <logic:set name="myoccs" comparator="occComparator">
                        <tm:filter instanceOf="type"><value:copy of="occs"/></tm:filter>
                      </logic:set>
                      <ul>
                        <logic:foreach name="myoccs" set="myocc">
                          <!-- look for topic whose subject address is the same as the URI of this occurrence -->
                          <logic:set name="occname"><tm:lookup as="subject"><tm:locator of="myocc"/></tm:lookup></logic:set>
                          <!-- output list element containing either name or locator of resource -->
                          <li type='circle'><logic:if name="occname"><logic:then>
                          <output:element name="a"><output:attribute name="href"><output:link
                          template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                          generator="modelLinkGenerator"/></output:attribute><output:name/></output:element>
                          <output:element name="a"><output:attribute name="href"><output:locator of="myocc"/></output:attribute>
                          <img src="/omnigator/images/link.png" border="0"/></output:element>
                          </logic:then><logic:else><output:element name="a"><output:attribute
                          name="href"><output:locator of="myocc"/></output:attribute><output:locator
                          of="myocc"/></output:element></logic:else></logic:if>
                          <!-- <reificationSupport> look for topic that reifies this occurrence -->
                          <logic:set name="reifiedOcc" comparator="off"><tm:reifier/></logic:set>
                          <logic:if name="reifiedOcc"><logic:then>
                            <span class="reified"><output:element name="a">
                              <output:attribute name="href"><output:link of="reifiedOcc" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                              more...</output:element></span>
                          </logic:then></logic:if>
                          <!-- </reificationSupport> -->
                          <!-- === scoping themes of this occurrence === -->
                          <logic:set name="themes"><tm:scope /></logic:set>
                          <logic:if name="themes">
                            <logic:then>
                            - Scope: <i><logic:foreach name="themes" separator="; "><output:element
                              name="a"><output:attribute name="href"><output:link
                              template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                              generator="modelLinkGenerator"/></output:attribute><output:name/></output:element></logic:foreach></i>
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

        <!-- ..... External untyped occurrences ............. -->
        <logic:set name="untypedOccs" comparator="occComparator">
          <tm:filter instanceOf="{NONE}">
            <tm:occurrences of="topic" type="external" contextFilter="user"/>
          </tm:filter>
        </logic:set>
        <logic:if name="untypedOccs">
          <logic:then>
            <table class="shboxed" width="100%"><tr><td>
                <h3><span title="Untyped resourceRef occurrences">Untyped External Occurrences (<output:count/>)</span></h3>
                <ul>
                  <logic:foreach name="untypedOccs" set="myocc">
                    <!-- look for topic whose subject address is the same as the URI of this occurrence -->
                    <logic:set name="occname"><tm:lookup as="subject"><tm:locator of="myocc"/></tm:lookup></logic:set>
                    <!-- output list element containing either name or locator of resource -->
                    <li type='circle'><logic:if name="occname"><logic:then><output:element name="a"><output:attribute
                    name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                    generator="modelLinkGenerator"/></output:attribute><output:name/></output:element>
                    <output:element name="a"><output:attribute name="href"><output:locator of="myocc"/></output:attribute>
                    <img src="/omnigator/images/link.png" border="0"/></output:element>
                    </logic:then><logic:else><output:element name="a"><output:attribute
                    name="href"><output:locator of="myocc"/></output:attribute><output:locator
                    of="myocc"/></output:element></logic:else></logic:if>
                    <!-- <reificationSupport> look for topic that reifies this occurrence -->
                    <logic:set name="reifiedOcc" comparator="off"><tm:reifier/></logic:set>
                     <logic:if name="reifiedOcc"><logic:then>
                      <span class="reified"><output:element name="a">
                        <output:attribute name="href"><output:link of="reifiedOcc" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                         more...</output:element></span>
                    </logic:then></logic:if>
                    <!-- </reificationSupport> -->
                    <!-- === scoping themes of this occurrence === -->
                    <logic:set name="themes"><tm:scope/></logic:set>
                    <logic:if name="themes">
                      <logic:then>
                      - Scope: <i><logic:foreach name="themes" separator="; "><output:element
                        name="a"><output:attribute name="href"><output:link
                        template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                        generator="modelLinkGenerator"/></output:attribute><output:name/></output:element></logic:foreach></i>
                      </logic:then>
                    </logic:if>
                    </li>
                  </logic:foreach>
                </ul>
            </td></tr></table>
          </logic:then>
        </logic:if>

        <!-- ================= Instances ==================== -->
        <tolog:set var="instances" query="select $T from instance-of($T, %topic%) order by $T?"/>

        <logic:if name="instances">
          <logic:then>
            <table class="shboxed" width="100%"><tr><td>
            <h3><span title="Topics which are instances of this topic type">Topics of this Type (<output:count/>)</span></h3>
            <ul>
              <logic:foreach name="instances">
                <li><output:element name="a">
                  <output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                  <output:name/></output:element></li>
              </logic:foreach>
            </ul>
            </td></tr></table>
          </logic:then>
        </logic:if>

      <logic:set name="is-hierarchical">
        <value:intersection>
          <tm:classesOf of="topic"/>
          <tm:lookup indicator="http://www.techquila.com/psi/hierarchy/#hierarchical-relation-type"/>
        </value:intersection>
      </logic:set>

      <logic:if name="is-hierarchical">
      <logic:then>
        <!-- ================= Hierarchy display ============================= -->
        <logic:externalFunction name="tree" fqcn="net.ontopia.topicmaps.nav2.webapps.omnigator.DisplayHierarchyFunction" />

        <table class="shboxed" width="100%"><tr><td>
           <h3 style="margin-bottom: 12pt"><span title="The hierarchy formed by this association type">Hierarchy</span></h3>
        <logic:call name="tree"/>
        </td></tr></table>
      </logic:then>

      <logic:else>
        <!-- ================= Role Players ============================= -->
        <!-- Retrieve all associations that are typed by this topic -->
        <logic:set name="assocInstances" comparator="off">
          <tm:instances of="topic" as="association"/>
        </logic:set>
        <!-- Get all association roles for associations from last step  -->
        <logic:set name="assocInstRoles" comparator="off">
          <tm:roles of="assocInstances"/>
        </logic:set>
        <!-- To order association instances, get types of association roles -->
        <logic:set name="assocInstRoleTypes">
          <tm:classesOf of="assocInstRoles"/>
        </logic:set>

        <!-- loop over all types of association roles -->
        <logic:if name="assocInstRoleTypes">
          <logic:then>
          <table class="shboxed" width="100%"><tr><td>
              <h3><span title="Topics that play roles in associations of this type">Role Players (<output:count/>)</span></h3>
              <ul>
              <logic:foreach set="roleType">
                <li>
                    <b><output:element name="a">
                      <output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                      <output:name/></output:element></b>
                <ul>
                  <!-- get association roles for current role type -->
                  <logic:set name="assocRoles" comparator="off">
                    <tm:filter instanceOf="roleType">
                      <value:copy of="assocInstRoles"/>
                    </tm:filter>
                  </logic:set>
                  <!-- avoid duplicates so retrieve set of players for association role -->
                  <logic:set name="players"><tm:topics of="assocRoles"/></logic:set>
                  <!-- loop over all topics which play the specified role in a association -->
                  <logic:foreach name="players">
                    <li type='circle'><output:element name="a">
                      <output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                      <output:name/></output:element></li>
                  </logic:foreach></ul></li>
              </logic:foreach>
              </ul>
          </td></tr></table>
          </logic:then>
        </logic:if>
      </logic:else></logic:if>

        <!-- ================= Players of this Role ==================== -->
        <logic:set name="rolesOfPlayers" comparator="off">
          <tm:instances of="topic" as="role"/>
        </logic:set>
        <logic:set name="rolePlayers">
          <tm:topics of="rolesOfPlayers"/>
        </logic:set>
        <logic:if name="rolePlayers">
          <logic:then>
            <!-- check if not identical to topics of this type -->
            <logic:if name="rolePlayers" equals="instances">
              <logic:then></logic:then>
              <logic:else>
                <table class="shboxed" width="100%"><tr><td>
                <h3><span title="Topics that play this role in associations">Players of this Role (<output:count/>)</span></h3>
                <ul>
                <logic:foreach name="rolePlayers">
                  <li><output:element name="a">
                    <output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
                    <output:name/></output:element></li>
                </logic:foreach>
                </ul>
                </td></tr></table>
              </logic:else>
            </logic:if>
          </logic:then>
        </logic:if>

        <!-- ================= Occurrence instances =================== -->
        <logic:set name="occInstances" comparator="occComparator">
          <tm:instances of="topic" as="occurrence"/>
        </logic:set>
        <logic:if name="occInstances">
          <logic:then>
          <table class="shboxed" width="100%"><tr><td>
          <h3><span title="Occurrences of this type">Occurrences of this Type (<output:count/>)</span></h3>
          <ul>
            <logic:foreach>
              <logic:set name="myTopic" comparator="off"><tm:topics/></logic:set>
              <li><output:element name="a">
                <output:attribute name="href"><output:locator/></output:attribute><output:locator/></output:element>
                <output:content/> (<output:element name="a"><output:attribute name="href"><output:link
                of="myTopic" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                generator="modelLinkGenerator"/></output:attribute><output:name of="myTopic"/></output:element>)
              </li>
            </logic:foreach>
          </ul>
          </td></tr></table>
          </logic:then>
        </logic:if>

        <!-- ================= Scoped Occurrences ===================== -->
        <logic:set name="scopedOccs">
          <tm:tolog query="occurrence($myTopic, $myOcc), scope($myOcc, %topic%)?"/>
        </logic:set>
        <logic:if name="scopedOccs">
          <logic:then>
          <table class="shboxed" width="100%"><tr><td>
          <h3><span title="Occurrences that are scoped by this topic">Scoped Occurrences (<output:count/>)</span></h3>
          <ul>
            <logic:foreach><logic:bind>
              <li><output:element name="a">
                <output:attribute name="href"><output:locator of="myOcc"/></output:attribute>
                <output:locator of="myOcc"/></output:element><span style="white-space: pre-line"><output:content of="myOcc"/></span>
                (<output:element name="a"><output:attribute name="href"><output:link of="myTopic"
                    template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
                    generator="modelLinkGenerator"/></output:attribute><output:name of="myTopic"/></output:element>)
              </li>
            </logic:bind></logic:foreach>
          </ul>
          </td></tr></table>
          </logic:then>
        </logic:if>

  </function>


  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
       function "con_topic_outro": displaying topic source locators.
       ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <function name="con_topic_outro" params="topic">
    <!-- === Source Locators === -->
    <logic:set name="sourceLocators">
      <tm:sourceLocators of="topic"/>
    </logic:set>
    <logic:if name="sourceLocators"><logic:then>
      <pre class="comment">Object id: <output:objectid of="topic"/>
Item identifier(s):
<logic:foreach name="sourceLocators">[<output:locator/>]
</logic:foreach></pre>
    </logic:then>
    <logic:else>
      <pre class="comment">Object id: <output:objectid of="topic"/></pre>
    </logic:else>
    </logic:if>
  </function>


  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
       function "topic_types": generating topic type information
       ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <function name="topic_types" params="topic">
    <!-- ====================== Types ==================== -->
    <logic:if name="types">
      <logic:then>
        <b><span title="Classes of which this topic is an instance">Type(s)</span>:
        <logic:foreach separator="; "><output:element name="a"><output:attribute
          name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
          generator="modelLinkGenerator"/></output:attribute><output:name/></output:element></logic:foreach></b>
      </logic:then>
    </logic:if>
  </function>


  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
       function "show-hierarchy-position": shows topic position in hierarchy
       ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <function name="show-hierarchy-position" params="topic assoctype super sub">
    <logic:set name="parent">
      <tm:associated from="topic" type="assoctype" startrole="sub" endrole="super"/>
    </logic:set>
    <logic:set name="loop">
      <value:intersection>
        <value:copy of="parent"/>
        <value:copy of="ancestors"/>
      </value:intersection>
    </logic:set>

    <logic:if name="loop">
      <logic:then>
        <p><b>Error:</b> Loop in the hierarchy.</p>
      </logic:then>

      <logic:else>
        <logic:if name="parent">
          <logic:then><!-- first accumulate the ancestors -->
            <logic:call name="show-hierarchy-position">
              <logic:set name="topic"><value:copy of="parent"/></logic:set>
              <logic:set name="ancestors">
                <value:union>
                  <value:copy of="ancestors"/>
                  <value:copy of="parent"/>
                </value:union>
              </logic:set>
            </logic:call>
          </logic:then>

          <logic:else> <!-- then, display them in reverse order -->
            <logic:call name="unwind-hierarchy"/>
          </logic:else>
        </logic:if>
      </logic:else>
    </logic:if>
  </function>

  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
       function "unwind-hierarchy": used by show-hierarchy-position to
       display the hierarchy once accumulated
       ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <function name="unwind-hierarchy" params="topic assoctype super sub ancestors">
    <logic:set name="children"> <!-- useful to do the final ... -->
      <tm:associated from="topic" type="assoctype" startrole="super" endrole="sub"/>
    </logic:set>

    <logic:set name="child">
      <value:intersection>
        <value:copy of="children"/>
        <value:copy of="ancestors"/>
      </value:intersection>
    </logic:set>

    <ul>
      <li><output:element name="a"><output:attribute name="href"
        ><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" of="topic"
        generator="modelLinkGenerator"/></output:attribute><output:name of="topic"
        /></output:element></li>

      <logic:if name="child">
        <logic:then>
          <logic:call name="unwind-hierarchy">
            <logic:set name="topic"><value:copy of="child"/></logic:set>
          </logic:call>
        </logic:then>
        <logic:else>
          <logic:if name="children">
            <logic:then>
              <ul><li>...</li></ul>
            </logic:then>
          </logic:if>
        </logic:else>
      </logic:if>
    </ul>
  </function>

</module>
