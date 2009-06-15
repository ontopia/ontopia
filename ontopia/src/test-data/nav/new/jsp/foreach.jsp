<logic:context tmparam="tm">
  <logic:set name="la-boheme"><tm:lookup source="#la-boheme" /></logic:set>
  <logic:set name="tosca"><tm:lookup source="#tosca" /></logic:set>

  <logic:set name="resultNames">
    <value:union>
      <tm:associations of="tosca" />
      <tm:associations of="la-boheme" />
     </value:union>
  </logic:set>

  <logic:foreach name="resultNames">
    <logic:set name="player">
      <tm:topics/>
    </logic:set>
    <output:name of="player"/>
  </logic:foreach>

</logic:context>
