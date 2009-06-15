<logic:context tmparam="tm" objparam="id" set="topic">

  <logic:set name="associated">
    <tm:associated from="topic"/>
  </logic:set>
  
  <logic:if name="associated">
  <logic:then>
    The topics associated with <output:name of="topic"/> :
    <logic:foreach name="associated" >
      * <output:name />
    </logic:foreach>
  </logic:then>
  </logic:if>

</logic:context>
