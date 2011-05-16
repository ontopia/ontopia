<logic:context tmparam="tm" objparam="id" set="topic">  

  <logic:set name="composer">
    <tm:lookup indicator="http://psi.ontopia.net/opera/#composer"/>
  </logic:set>
	
  Comparing topic <output:name of="topic"/> to the
  topic <output:name of="composer"/> which has been looked up by it's PSI:
	
  <logic:if name="composer" equals="topic">
    <logic:then>They are the same.</logic:then>
    <logic:else>Something is WRONG, the test says that they are not equal!</logic:else>
  </logic:if>

</logic:context>
