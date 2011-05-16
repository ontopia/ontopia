<logic:context tmparam="tm">

  <logic:set name="myAssocs">
    <tm:classes of="association" />
  </logic:set>

  <logic:set name="myTopics">
    <tm:classes of="topic" />
  </logic:set>

  <logic:set name="myOccur">
    <tm:classes of="occurrence" />
  </logic:set>

  <logic:set name="myRole">
    <tm:classes of="role" />
  </logic:set>

  Number of associations (<output:count of="myAssocs" />)
  Number of topics (<output:count of="myTopics" />)
  Number of occurrences (<output:count of="myOccur" />)
  Number of roles (<output:count of="myRole" />)
</logic:context>
