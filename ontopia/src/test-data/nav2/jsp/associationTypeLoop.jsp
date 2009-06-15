<logic:context tmparam="tm" objparam="id" set="topic">

        <!-- ================= Associations ==================== -->
        <logic:set name="topicAssocs" comparator="off">
          <tm:associations of="topic"/>
        </logic:set>
        <logic:if name="topicAssocs"><logic:then>
          Related subjects
          <ul>
            <tm:associationTypeLoop name="topic"
                                    setAssociations="assocs"
                                    setAT="assocType"
                                    setART="roleType">

              <li>[Association type] <output:name of="assocType"
                                                  basenameScope="roleType"/>
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
                    <ul>[binary association(s)]
                    <logic:foreach name="roles" set="role">
                      <logic:set name="player" comparator="off"><tm:topics/></logic:set>
                      <logic:set name="roletype" comparator="off"><tm:classesOf/></logic:set>
                      <logic:if name="player"><logic:then>
                        <li>[Player] <output:name of="player"/>
                            [Role type] <output:name of='roletype'/>
                        </li>
                      </logic:then></logic:if>
                    </logic:foreach>
                    </ul>
                  </logic:then></logic:if>
  
                  <!-- ===== List n-ary associations ===== -->
                  
                  <logic:foreach name="assocs" comparator="assocComparator">
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
                      <ul>[n-ary association]
                        <logic:foreach name="roles">
                          <logic:set name="player" comparator="off"><tm:topics/></logic:set>
                          <logic:set name="roletype" comparator="off"><tm:classesOf/></logic:set>
                          <logic:if name="player"><logic:then>
                            <li>[Player] <output:name of="player"/>
                                [Role type] <output:name of="roletype"/>
                            </li>
                          </logic:then></logic:if>
                        </logic:foreach>
                      </ul>
                      </logic:then></logic:if>
                  </logic:foreach><!-- assocs (nary) -->
              </li>

            </tm:associationTypeLoop>
          </ul>
        </logic:then></logic:if>

</logic:context>
