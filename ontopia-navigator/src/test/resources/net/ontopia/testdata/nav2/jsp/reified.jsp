<logic:context tmparam="tm" settm="topicmap">

  <!-- (1) BaseName reified by Topic -->
    
  <logic:set name="topic">
    <tm:lookup source="#t.theKingName" />
  </logic:set>
    
  <logic:set name="reifiedBaseNames">
    <tm:reified of="topic"/>
  </logic:set>

  <logic:if name="reifiedBaseNames"> 
    <logic:then>
      The topic <i><output:name of="topic"/></i> reifies the following basename(s):
      <logic:foreach name="reifiedBaseNames" set="reifiedObj">
         * <output:name of="reifiedObj" />
      </logic:foreach>
    </logic:then>
    <logic:else>
      No basename reified by this topic.
    </logic:else>
  </logic:if>

    
  <!-- (2) Occurrence reified by Topic -->
    
  <logic:set name="topic">
    <tm:lookup indicator="#occ2" />
  </logic:set>
    
  <logic:set name="reifiedOccurrences">
    <tm:reified of="topic"/>
  </logic:set>

  <logic:if name="reifiedOccurrences"> 
    <logic:then>
      The topic <i><output:name of="topic"/></i> reifies the following occurrence(s):
      <logic:foreach name="reifiedOccurrences" set="reifiedObj">
         * <output:content of="reifiedObj" />
      </logic:foreach>
    </logic:then>
    <logic:else>
      No occurrence reified by this topic.
    </logic:else>
  </logic:if>


  <!-- (3) Association reified by Topic -->
    
  <logic:set name="topic">
    <tm:lookup indicator="#influence" />
  </logic:set>
    
  <logic:set name="reifiedAssociations">
    <tm:reified of="topic"/>
  </logic:set>

  <logic:if name="reifiedAssociations"> 
    <logic:then>
      The topic <i><output:name of="topic"/></i> reifies association(s)
      with the following player(s) involved:
      <logic:foreach name="reifiedAssociations" set="reifiedObj">
        <logic:set name="roles" comparator="assocRoleComparator">
          <tm:roles of="reifiedObj" />
	</logic:set>
        <logic:foreach name="roles" set="role">
	  <logic:set name="player" comparator="off"><tm:topics/></logic:set>
	  * <output:name of="player" />
	</logic:foreach>
      </logic:foreach>
    </logic:then>
    <logic:else>
      No association reified by this topic.
    </logic:else>
  </logic:if>
    
</logic:context>
