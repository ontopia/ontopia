<logic:context tmparam="tm">
  <logic:set name="person"><tm:lookup source="#giacosa"/></logic:set>
  <logic:set name="place"><tm:lookup source="geography.xtm#colleretto-parella"/></logic:set>

  <logic:set name="associations" comparator="assocTypeComparator">
    <tm:associated from="person" to="place" produce="associations"/>
  </logic:set>

  Associations: <output:count of="associations"/>
  <logic:foreach name="associations" set="assoc"><logic:set name="type"
    ><tm:classesOf of="assoc"/></logic:set><logic:set name="roles" comparator="assocRoleComparator"
    ><tm:roles of="assoc"/></logic:set>
    * <output:name of="type"/>
    <logic:foreach name="roles" set="role"><logic:set name="roletype"
      ><tm:classesOf of="role"/></logic:set><logic:set name="player"
      ><tm:topics of="role"/></logic:set>
      * (<output:name of="player"/>, <output:name of="roletype"/>)
    </logic:foreach>
  </logic:foreach>  


</logic:context>
