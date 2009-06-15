<logic:context tmparam="tm" objparam="id" set="topic">
  <logic:set name="variants" comparator="nameComparator">
    <tm:variants of="topic" />
  </logic:set>
  Variant names (<output:count of="variants"/>)
  <logic:foreach name="variants" set="myVariant">
    * <output:name of="myVariant"/>
  </logic:foreach>
</logic:context>
