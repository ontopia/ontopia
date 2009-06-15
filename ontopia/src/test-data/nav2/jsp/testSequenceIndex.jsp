
<logic:context tmparam="tm" objparam="id" set="topic">  

  <logic:set name="occinstances" comparator="occComparator">
    <tm:occurrences of="topic" type="external"/>
  </logic:set>
	
  <logic:if name="occinstances" greaterThan="1">
    <logic:then>
      <logic:foreach name="occinstances">
        <value>[<output:element name="link"><output:attribute name="href"><output:locator/></output:attribute><output:content of="sequence-index"/></output:element>]</value>
      </logic:foreach>
    </logic:then>
  </logic:if>
  
</logic:context> 
