<logic:context tmparam="tm" objparam="id" set="topic">
  <logic:set name="associated">
    <tm:associated from="topic" type="foo"/>
  </logic:set>

   The topics associated with <output:name of="topic"/> and the type foo:
    <logic:foreach name="associated" set="assoc">
      * <output:name of="assoc"/>
    </logic:foreach>

</logic:context>
