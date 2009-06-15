<logic:context tmparam="tm">
  <logic:set name="classes">
    <tm:tolog query='select $B from instance-of($A, $B)?' select="B"/>
  </logic:set>

  <logic:foreach name="classes" set="type">
    <logic:set name="count"><tm:tolog query='select count($A) from instance-of($A, %type%)?' select="A"/></logic:set>
    <output:name of="type"/>: <output:content of="count"/>
  </logic:foreach>

</logic:context>
