<logic:context tmparam="tm" settm="topicmap">
  <logic:set name="topics" comparator="off">
    <tm:topics of="topicmap"/>
  </logic:set>
  <logic:set name="basenames" comparator="off">
    <tm:names of="topics" uniqueValues="true"/>
  </logic:set>
  
  Base names (<output:count of="basenames"/>)
  <logic:foreach max="1000" name="basenames" set="myName" comparator="topicNameComparator">
    * <output:name of="myName"/>
  </logic:foreach>
	
  Base names descending (<output:count of="basenames"/>)
  <logic:foreach max="1000" name="basenames" set="myName" comparator="topicNameComparator" order="descending">
    * <output:name of="myName"/>
  </logic:foreach>
  
</logic:context>
