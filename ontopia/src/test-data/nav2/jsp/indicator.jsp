<logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">

  <logic:set name="indicators">
    <tm:indicators of="topic" />
  </logic:set>

  <logic:if name="indicators"> 
    <logic:then>
      The topic (<output:name of="topic"/>) indicator(s):
      <logic:foreach name="indicators" set="ind">
         * <output:locator of="ind" />
      </logic:foreach>
    </logic:then>
    <logic:else>
      This topic (<output:name of="topic"/>) has no indicators.
    </logic:else>
  </logic:if>

</logic:context>
