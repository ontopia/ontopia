<logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">

  <logic:set name="subjaddrs">
    <tm:subjectAddress of="topic" />
  </logic:set>

  <logic:if name="subjaddrs"> 
    <logic:then>
      The topic (<output:name of="topic"/>) has subject address:
      <logic:foreach name="subjaddrs" set="addr">
         * <output:locator of="addr" />
      </logic:foreach>
    </logic:then>
    <logic:else>
      This topic (<output:name of="topic"/>) has no subject address.
    </logic:else>
  </logic:if>

</logic:context>
