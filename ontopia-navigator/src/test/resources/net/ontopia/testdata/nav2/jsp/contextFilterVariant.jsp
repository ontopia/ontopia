<logic:context tmparam="tm" settm="topicmap">

  <logic:set name="composer"><tm:lookup source="opera-template.xtm#composer"/></logic:set>
  <logic:set name="sort"><tm:lookup source="ontopsi.xtm#sort"/></logic:set>

  <framework:setcontext variant="sort"/>

  <logic:set name="topics"><tm:instances of="composer"/></logic:set>

  <output:count of="topics"/>  

  <logic:foreach name="topics">
    <logic:set name="sortname"><tm:variants contextFilter="user"/></logic:set>
    <output:name of="sortname"/>
  </logic:foreach>

  <framework:setcontext variant="None"/>

</logic:context>
