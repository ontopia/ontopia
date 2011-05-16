<logic:context tmparam="tm">

  <logic:set name="composers" comparator="off">
    <tm:tolog query='instance-of($A, composer) order by $A?'/>
  </logic:set>

  The composers are:
  <logic:foreach name="composers">
    <logic:bind>
      * <output:name of="A"/>
    </logic:bind>
  </logic:foreach>

</logic:context>
