<logic:context tmparam="tm" objparam="id" set="topic">

  <logic:set name="roles" comparator="occComparator">
    <tm:roles of="topic"/>
  </logic:set>
  <logic:set name="assocs" comparator="assocComparator">
    <tm:associations of="roles" contextFilter="user"/>
  </logic:set>
  <logic:set name="assocTypes">
    <tm:classesOf of="assocs"/>
  </logic:set>
  <logic:set name="assocTopics" comparator="assocComparator">
    <tm:associations of="topic" contextFilter="user"/>
  </logic:set>
	

  <logic:if name="assocTypes">
    <logic:then>
      <logic:foreach set="type">
      <output:name variantScope="types"/>
	<logic:set name="assocs" comparator="assocComparator">
	  <tm:filter instanceOf="type">
	    <value:copy of="assocTopics"/>
	  </tm:filter>
        </logic:set>

        <logic:foreach name="assocs">
	  <logic:set name="roles" comparator="assocRoleComparator">
	    <value:difference>
	      <tm:roles />
	      <tm:roles of="topic" />
	    </value:difference>
	  </logic:set>

          <logic:foreach name="roles">
	    <logic:set name="player" comparator="topicComparator"><tm:topics/></logic:set>
	    <logic:set name="roletype" comparator="assocRoleComparator"><tm:classesOf/></logic:set>
	    <output:name of="player"/> (<output:name of="roletype"/>)
	  </logic:foreach>
        </logic:foreach>
      </logic:foreach>

    </logic:then>
    <logic:else>
      No associations available for this topic.
    </logic:else>
  </logic:if>


</logic:context>
