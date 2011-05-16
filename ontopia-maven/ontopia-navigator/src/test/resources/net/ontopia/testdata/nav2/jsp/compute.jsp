<logic:context tmparam="tm" objparam="id">

  <logic:set name="la-boheme">
    <tm:lookup source="#la-boheme" />
  </logic:set>
  
  <logic:set name="tosca">
    <tm:lookup source="#tosca" />
  </logic:set>

  <logic:set name="assocsTosca"> 
    <tm:associations of="tosca"/>
  </logic:set>

  <logic:set name="assocsLa-boheme">
    <tm:associations of="la-boheme"/>
  </logic:set>

  Number of associations that "tosca" plays in: <output:count of="assocsTosca"/>
  Number of associations that "la-boheme" plays in: <output:count of="assocsLa-boheme"/>



</logic:context>
