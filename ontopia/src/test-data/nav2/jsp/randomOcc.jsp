<logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">

  <logic:set name="occurrences" comparator="off">
    <tm:filter randomElement="true">
      <tm:occurrences of="topic" type="external"/>
    </tm:filter>
  </logic:set>
    
  <logic:if name="occurrences"><logic:then>

  	<logic:foreach name="occurrences">
  	  <output:locator/>
  	</logic:foreach>

  </logic:then></logic:if>

</logic:context>
