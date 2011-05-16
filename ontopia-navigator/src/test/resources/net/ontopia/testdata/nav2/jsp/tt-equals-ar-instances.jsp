<!-- Related to bug #217 -->

<logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">

  <!-- ================= Instances ==================== -->
  <logic:set name="instances">
    <tm:instances of="topic" as="topic"/>
  </logic:set>

  Instances:
  <logic:if name="instances">
    <logic:then>
      <logic:foreach name="instances">
        * <output:name/>
      </logic:foreach>
    </logic:then>
    <logic:else>
      none.
    </logic:else>
  </logic:if>

  
  <!-- ================= Players of this Role ==================== -->
  <logic:set name="rolesOfPlayers" comparator="off">
    <tm:instances of="topic" as="role"/>
  </logic:set>
  <logic:set name="rolePlayers">
    <tm:topics of="rolesOfPlayers"/>
  </logic:set>
  
  Players of this Role:
  <logic:if name="rolePlayers">
    <logic:then>
      <logic:if name="rolePlayers" equals="instances">
        <logic:then>[same as instances]</logic:then>
        <logic:else>[not same as instances]
          <logic:foreach name="rolePlayers">
            * <output:name/>
          </logic:foreach>
        </logic:else>
      </logic:if>
    </logic:then>
    <logic:else>
      none.
    </logic:else>
  </logic:if>
  
</logic:context>
