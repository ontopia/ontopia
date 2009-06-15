<logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">
  <logic:set name="occurrences" comparator="occComparator">
    <tm:occurrences of="topic" />
  </logic:set>
  Occurrences (<output:count of="occurrences"/>)
  <logic:foreach name="occurrences" set="myOcc">
    * <output:content of="myOcc"/> [<output:locator of="myOcc"/>]
  </logic:foreach>
</logic:context>
