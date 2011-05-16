<logic:context tmparam="tm" settm="topicmap">

  <logic:set name="term"><tm:lookup source="#tt_termspell_en"/></logic:set>
  <logic:set name="de"><tm:lookup source="#de"/></logic:set>

  <framework:setcontext basename="de"/>

  <logic:set name="topics">
    <tm:instances of="term"/>
  </logic:set>

  list-start:
  <logic:foreach name="topics">
    <logic:set name="termNames" comparator="nameComparator">
      <tm:names contextFilter="user"/>
    </logic:set>
    * <output:name of="termNames"/>
  </logic:foreach>
  list-end.

  <framework:setcontext basename="None"/>
</logic:context>
