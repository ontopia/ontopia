<logic:context tmparam="tm">
  

  <logic:set name="topic1">
    <tm:lookup source="#topic1"/>
  </logic:set>

  <logic:set name="super1">
    <tm:superclasses of="topic1"/>
  </logic:set>

  <logic:set name="super2">
    <tm:superclasses of="topic1" level="1"/>
  </logic:set>

  <logic:set name="super3">
    <tm:superclasses of="topic1" level="2"/>
  </logic:set>

  <logic:set name="super4">
    <tm:superclasses of="topic1" level="3"/>
  </logic:set>


  Super set infinite (<output:count of="super1"/>):
  <logic:foreach name="super1">
    <output:name/>
  </logic:foreach>

  Super set 1 (<output:count of="super2"/>):
  <logic:foreach name="super2">
    <output:name/>
  </logic:foreach>

  Super set 2 (<output:count of="super3"/>):
  <logic:foreach name="super3">
    <output:name/>
  </logic:foreach>

  Super set 3 (<output:count of="super4"/>):
  <logic:foreach name="super4">
    <output:name/>
  </logic:foreach>

  

</logic:context>
