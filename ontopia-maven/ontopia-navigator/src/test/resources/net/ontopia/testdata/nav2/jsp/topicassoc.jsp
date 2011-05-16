<logic:context tmparam="tm" objparam="id" set="topic">
  <logic:set name="assocs">
    <tm:associated from="topic" />
  </logic:set>
  Associations (<output:count of="assocs"/>)
  <logic:foreach name="assocs" set="assocTopic">
    * <output:name of="assocTopic"/>
  </logic:foreach>
</logic:context>
