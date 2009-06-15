<logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">
  <logic:set name="occurrences" comparator="occComparator">    
    <tm:occurrences of="topic" type="internal"/>
  </logic:set>
  <logic:foreach name="occurrences" set="myOcc">
    * <output:content of="myOcc"/>
  </logic:foreach>
</logic:context>
