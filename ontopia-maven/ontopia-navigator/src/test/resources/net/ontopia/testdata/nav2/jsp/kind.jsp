<!-- Tests whether the 'is' attribute of <filter> works as it should. -->

<logic:context tmparam="tm" settm="topicmap" objparam="id" set="topic">

  <!-- (1) Give us only the occurrences -->
    
  <logic:set name="occurrences" comparator="occComparator">
    <tm:filter is="occurrence">
      <value:union>
        <tm:occurrences of="topic"/>
        <tm:names of="topic"/>
        <tm:sourceLocators of="topic"/>
        <tm:roles of="topic"/>
      </value:union>
    </tm:filter>
  </logic:set>

  The topic <i><output:name of="topic"/></i> has the following occurrences:
  <logic:foreach name="occurrences" set="occ">
    * <output:locator of="occ" />
  </logic:foreach>
    
</logic:context>
