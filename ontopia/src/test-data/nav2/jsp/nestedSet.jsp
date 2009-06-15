<logic:context tmparam="tm">

  <logic:set name="topic">
    <tm:lookup source="#tosca" />
  </logic:set>

  <logic:set name="topic_name">
    <logic:if name="topic">
      <logic:then>
        <tm:name of="topic" />
      </logic:then>
      <logic:else>
        <value:string>{No proper topic object found.}</value:string>
      </logic:else>
    </logic:if>
  </logic:set>

  And the name of the topic is ... <output:name of="topic_name" />.

</logic:context>
