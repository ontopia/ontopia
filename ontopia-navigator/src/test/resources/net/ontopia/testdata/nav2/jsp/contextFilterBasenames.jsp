<logic:context tmparam="tm" settm="topicmap">

  <logic:set name="normal"><tm:lookup source="opera-template.xtm#normal"/></logic:set>
  <logic:set name="composer"><tm:lookup source="opera-template.xtm#composer"/></logic:set>

  <framework:setcontext basename="normal"/>

  <logic:set name="topics">
    <tm:instances of="composer"/>
  </logic:set>

  <logic:foreach name="topics">
    <logic:set name="names"><tm:names contextFilter="user"/></logic:set>
    <output:name of="names"/>
  </logic:foreach>

  <framework:setcontext basename="None"/>

</logic:context>
