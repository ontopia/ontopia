<logic:context tmparam="tm">

  <logic:set name="composer"><tm:lookup indicator="http://psi.ontopia.net/opera/#composer"/></logic:set>

  <logic:set name="result">
    <tm:tolog query="instance-of($A, %composer%) order by $A?"/>
  </logic:set>  

  <logic:foreach name="result">
    <logic:bind>
      * <output:name of="A"/>
    </logic:bind>
  </logic:foreach>
</logic:context>
