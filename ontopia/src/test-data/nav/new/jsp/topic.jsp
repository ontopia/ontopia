<logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">
  <output:objectid of="topic"/>
<!-- 
<logic:context tmparam="tm">
  <output:name/>
  <logic:set name="assocs">
    <tm:associations/>
  </logic:set>
  <logic:set name="lookupObj"><tm:lookup objectid="1" /></logic:set>
  <output:name of="lookupObj" />
  <logic:set name="lookupObj">
    <tm:lookup objectid="245" />
  </logic:set>  
  <output:name of="lookupObj" />

  <logic:set name="la-boheme"><tm:lookup source="#la-boheme" /></logic:set>
  <logic:set name="tosca"><tm:lookup source="#tosca" /></logic:set>

  <logic:set name="resultNames">
    <value:union>
      <tm:associations of="tosca" />
      <tm:associations of="la-boheme" />
     </value:union>
  </logic:set>

  <logic:if name="resultNames">
    <logic:then>
      <output:count of="resultNames"/>
    </logic:then>
  </logic:if>
-->
</logic:context>
