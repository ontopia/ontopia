<logic:context tmparam="tm" settm="topicmap">

  <logic:set name="music"><tm:lookup source="opera-template.xtm#music"/></logic:set>
  <logic:set name="literature"><tm:lookup source="opera-template.xtm#literature"/></logic:set>
  <logic:set name="written-by"><tm:lookup source="opera-template.xtm#written-by"/></logic:set>

  <logic:set name="composer"><tm:lookup source="#leoncavallo"/></logic:set>
  <logic:set name="writer"><tm:lookup source="#byron"/></logic:set>

  <logic:set name="people">
    <value:union>
      <value:copy of="composer"/>
      <value:copy of="writer"/>
    </value:union>
  </logic:set>

  *******************************************
  *                 MUSIC                   *
  *******************************************

  <framework:setcontext association="music"/>

  <logic:set name="assocs" comparator="assocComparator">
    <tm:filter instanceOf="written-by">
      <tm:associations of="people" contextFilter="user"/>
    </tm:filter>
  </logic:set>


  <logic:foreach name="assocs">
    <logic:set name="roles" comparator="assocRoleComparator"><tm:roles/></logic:set>
    <logic:set name="players"><tm:topics of="roles"/></logic:set>
    <logic:foreach name="players" separator=" | ">
      <output:name />
    </logic:foreach>
  </logic:foreach>




  *******************************************
  *               LITERATURE                *
  *******************************************

  <framework:setcontext association="None"/>
  <framework:setcontext association="literature"/>

  <logic:set name="assocs" comparator="assocComparator">
    <tm:filter instanceOf="written-by">
      <tm:associations of="people" contextFilter="user"/>
    </tm:filter>
  </logic:set>

  <logic:foreach name="assocs">
    <logic:set name="roles" comparator="assocRoleComparator"><tm:roles/></logic:set>
    <logic:set name="players"><tm:topics of="roles"/></logic:set>
    <logic:foreach name="players" separator=" | ">
      <output:name />
    </logic:foreach>
  </logic:foreach>

  <framework:setcontext association="None"/>

</logic:context>
