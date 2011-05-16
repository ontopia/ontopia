<logic:context tmparam="tm" settm="topicmap">
  <logic:set name="topics">
    <tm:topics><value:copy of="topicmap"/></tm:topics>
  </logic:set>
  [[[START]]]
<logic:foreach name="topics" max="500">  * <output:name/>
</logic:foreach>
  [[[END]]]
</logic:context>
