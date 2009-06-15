<logic:context tmparam="tm" objparam="id" set="topic">
  <logic:set name="names" comparator="nameComparator">
    <tm:names of="topic" />
  </logic:set>
  Name(s) of this topic: <logic:foreach name="names" set="name"
    separator=""><logic:if name="sequence-last"><logic:then> and </logic:then><logic:else><logic:if name="sequence-first"><logic:else>, </logic:else></logic:if></logic:else></logic:if><output:name of="name"/></logic:foreach>.
</logic:context>
