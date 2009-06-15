<html>
<logic:context tmparam="tm" objparam="id" set="topic">
  <logic:set name="names" comparator="nameComparator">
    <tm:names of="topic" />
  </logic:set>
  <h2>Names (<output:count of="names"/>)</h2>
  <logic:foreach name="names" set="myName">
    * <output:name of="myName"/>
  </logic:foreach>
</logic:context>
</html>
