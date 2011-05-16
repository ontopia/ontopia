<logic:context tmparam="tm">

  <logic:set name="composers" comparator="off">
    <tm:tolog query='inspired-by($C, $A) order by $C, $A?' rulesfile="/jsp/rules.tl"/>
  </logic:set>

  The inspired composers are:
  <logic:foreach name="composers">
    <logic:bind>
      <output:name of="C"/>         <output:name of="A"/>
    </logic:bind>
  </logic:foreach>


  ===== DO IT AGAIN ===== (bug #994)
  <logic:set name="composers" comparator="off">
    <tm:tolog query='inspired-by($C, $A) order by $C, $A?' rulesfile="/jsp/rules.tl"/>
  </logic:set>

  The inspired composers are:
  <logic:foreach name="composers">
    <logic:bind>
      <output:name of="C"/>         <output:name of="A"/>
    </logic:bind>
  </logic:foreach>

</logic:context>
