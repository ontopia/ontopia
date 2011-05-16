
<logic:context tmparam="tm" objparam="id" set="topic">  

  <logic:set name="occinstances" comparator="occComparator">
    <tm:occurrences of="topic" type="external"/>
  </logic:set>
	
  Input Collection has <output:count of="occinstances"/> elements. 	
	
  <logic:if name="occinstances" greaterThan="1">
    <logic:then>greaterThan 1: TRUE</logic:then>
    <logic:else>greaterThan 1: FALSE</logic:else>
  </logic:if>
  
  <logic:if name="occinstances" sizeEquals="1">
    <logic:then>equalsSize 1: TRUE</logic:then>
    <logic:else>equalsSize 1: FALSE</logic:else>
  </logic:if>
  
  <logic:if name="occinstances" sizeEquals="8">
    <logic:then>equalsSize 8: TRUE</logic:then>
    <logic:else>equalsSize 8: FALSE</logic:else>
  </logic:if>
  
  <logic:if name="occinstances" lessThan="10">
    <logic:then>lessThan 10: TRUE</logic:then>
    <logic:else>lessThan 10: FALSE</logic:else>
  </logic:if>
  
</logic:context> 
