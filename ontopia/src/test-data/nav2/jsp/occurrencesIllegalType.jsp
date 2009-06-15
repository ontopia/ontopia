<!-- Related to bug #489 -->
<logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">
  <logic:set name="occurrences" comparator="occComparator">
    <tm:occurrences of="topic" type="FOO" />
    <!-- invalid type value -->
  </logic:set>
</logic:context>
