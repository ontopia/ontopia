<logic:context tmparam="tm">
 
  <logic:set name="result">
    <tm:tolog query="select $A, count($B) from composed-by($A : composer, $B : opera) order by $B desc, $A asc?"/>
  </logic:set>
 
 
  <logic:foreach name="result">
    <logic:bind>
      <output:name of="A"/>
      <output:content of="B"/>
    </logic:bind>
  </logic:foreach>
 
</logic:context>
