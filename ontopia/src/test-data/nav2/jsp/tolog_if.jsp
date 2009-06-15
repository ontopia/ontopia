<logic:context tmparam="tm">
  <logic:set name="mistakes">
    <tm:tolog query='instance-of($A, person), instance-of($A, place)?'/>
  </logic:set>
  <logic:if name="mistakes">
    <logic:then>Serious mistake in tolog tag. <output:debug of="mistakes"
                /></logic:then>
    <logic:else>All well</logic:else>
  </logic:if>
</logic:context>
