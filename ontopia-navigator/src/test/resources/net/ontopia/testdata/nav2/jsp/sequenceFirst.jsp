<logic:context tmparam="tm" objparam="id" set="topic">
  <logic:set name="names" comparator="nameComparator">
    <tm:names of="topic" />
  </logic:set>
  Name(s) of this topic:
  <logic:foreach name="names" set="myName" separator=" ">
    <logic:if name="sequence-first">
      <logic:then>
        <output:name of="myName"/>
      </logic:then><logic:else>
        and <output:name of="myName"/>
      </logic:else>
     </logic:if>
  </logic:foreach>
</logic:context>
