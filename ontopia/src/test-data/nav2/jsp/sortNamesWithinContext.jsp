<logic:context tmparam="tm" settm="topicmap">

  <logic:set name="term"><tm:lookup source="#tt_termspell_en"/></logic:set>
  <logic:set name="de"><tm:lookup source="#de"/></logic:set>

  <framework:setcontext basename="de"/>

  <logic:set name="topics">
    <tm:instances of="term"/>
  </logic:set>
  <logic:set name="names" comparator="nameComparator">
    <tm:names of="topics" contextFilter="user"/>
  </logic:set>

  list-start:
  <logic:foreach name="names">
    * <output:name/>
  </logic:foreach>
  list-end.

  <framework:setcontext basename="None"/>
</logic:context>
