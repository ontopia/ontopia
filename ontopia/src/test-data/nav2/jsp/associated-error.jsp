<logic:context tmparam="tm" objparam="id" set="topic">

  <logic:set name="composed-by">
    <tm:lookup source="opera-template.xtm#composed-by"/>
  </logic:set>

  <logic:set name="associated">
    <tm:associated from="topic" type="compooooooooooooooooooooosed-by"/>
  </logic:set>
  
  <logic:if name="associated">
  <logic:then>
    Operas composed by <output:name of="topic"/> :
    <logic:foreach name="associated" >
      * <output:name />
    </logic:foreach>
  </logic:then>
  </logic:if>

</logic:context>
