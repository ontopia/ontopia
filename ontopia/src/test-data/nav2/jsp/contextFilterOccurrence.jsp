<logic:context tmparam="tm" settm="topicmap">

  <logic:set name="online"><tm:lookup source="opera-template.xtm#online"/></logic:set>
  <logic:set name="composer"><tm:lookup source="opera-template.xtm#composer"/></logic:set>

  <framework:setcontext occurrence="online"/>

  <logic:set name="topics"><tm:instances of="composer" /></logic:set>
  
  <output:count of="topics"/>

  <logic:foreach name="topics">
    * <output:name />
      <logic:set name="occs" comparator="occComparator">
        <tm:occurrences contextFilter="user" type="external"/>
      </logic:set>
      <logic:foreach name="occs">
        - <output:locator />
      </logic:foreach>
  </logic:foreach>

  <framework:setcontext occurrence="None"/>

</logic:context>
