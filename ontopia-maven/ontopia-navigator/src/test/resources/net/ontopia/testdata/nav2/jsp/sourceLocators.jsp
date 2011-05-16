<logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">

  <logic:set name="sourceLocators">
    <tm:sourceLocators of="topic" />
  </logic:set>

  <logic:if name="sourceLocators"> 
    <logic:then>
      The topic (<output:name of="topic"/>) source locator(s):
      <logic:foreach name="sourceLocators" set="loc">
         * <output:locator of="loc" />
      </logic:foreach>
    </logic:then>
    <logic:else>
      This topic (<output:name of="topic"/>) has no source locators.
    </logic:else>
  </logic:if>

</logic:context>
