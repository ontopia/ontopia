<logic:context tmparam="tm" objparam="id" set="topic">
  <logic:set name="names" comparator="nameComparator">
    <tm:names of="topic" />
  </logic:set>
  Names (<output:count of="names"/>)
  <logic:foreach name="names" set="myName">
    * <output:name of="myName"/>
  </logic:foreach>
</logic:context>
