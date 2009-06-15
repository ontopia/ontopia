<logic:context tmparam="tm" settm="topicmap" objparam="id" set="topic">

  <logic:set name="biography"><tm:lookup source="opera-template.xtm#biography"/></logic:set>
  <logic:set name="composer"><tm:lookup source="opera-template.xtm#composer"/></logic:set>

  <framework:setcontext association="biography"/>

  <logic:set name="composers"><tm:instances of="composer"/></logic:set>
  
  <logic:set name="roles" comparator="assocRoleComparator"><tm:roles of="composers" contextFilter="user"/></logic:set>

  <logic:set name="assocs"><tm:associations of="topic" contextFilter="user"/></logic:set>

  <output:count of="roles"/>

  <logic:foreach name="roles">
    <logic:set name="topics"><tm:topics /></logic:set><output:name of="topics" />
  </logic:foreach>
<!--
    <logic:set name="roles"><tm:roles /></logic:set>
    <logic:foreach name="roles">
      <logic:set name="role"><tm:topics /></logic:set>
      <output:name of="role"/>
    </logic:foreach>
  </logic:foreach>
-->
  <framework:setcontext association="None"/>

</logic:context>
