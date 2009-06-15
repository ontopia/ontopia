<logic:context tmparam="tm" settm="topicmap">

  <logic:set name="biography"><tm:lookup source="opera-template.xtm#biography"/></logic:set>
  <logic:set name="composer"><tm:lookup source="opera-template.xtm#composer"/></logic:set>

  <framework:setcontext association="biography"/>

  <logic:set name="topics"><tm:associations of="composer" /></logic:set>
  
  <output:count of="topics"/>

  <logic:foreach name="topics">
    * <output:name />
  </logic:foreach>

</logic:context>
