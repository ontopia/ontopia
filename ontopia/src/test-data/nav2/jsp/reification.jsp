<logic:context tmparam="tm" settm="topicmap">
  <logic:set name="topic">
    <tm:lookup indicator="http://test.ontopia.net/#topic" />
  </logic:set>

  <logic:set name="variantnames">
    <tm:variants of="topic"/>
  </logic:set>

  <logic:set name="reifyingVariants">
    <tm:reifier of="variantnames"/>
  </logic:set>

  <logic:if name="reifyingVariants">
    The topics that reifies <output:name of="topic"/> - variants:
    <logic:foreach name="reifyingVariants">
      * <output:name />
    </logic:foreach>
  </logic:if>

  <logic:set name="occurrences">
    <tm:occurrences of="topic"/>
  </logic:set>

  <logic:set name="reifyingOccurrences">
    <tm:reifier of="occurrences"/>
  </logic:set>

  <logic:if name="reifyingOccurrences">
    The topics that reifies <output:name of="topic"/> - occurrences:
    <logic:foreach name="reifyingOccurrences">
      * <output:name />
    </logic:foreach>
  </logic:if>

<!-- This part of the test is commented out because roles cannot be reified -->
<!--
  <logic:set name="roles">
    <tm:roles of="topic"/>
  </logic:set>

  <logic:set name="reifyingRoles">
    <tm:reifier of="roles"/>
  </logic:set>

  <logic:if name="reifyingRoles">
    The topics that reifies <output:name of="topic"/> - roles:
    <logic:foreach name="reifyingRoles">
      * <output:name />
    </logic:foreach>
  </logic:if>
-->

  <logic:set name="associations">
    <tm:associations of="topic"/>
  </logic:set>

  <logic:set name="reifyingAssociations">
    <tm:reifier of="associations"/>
  </logic:set>

  <logic:if name="reifyingAssociations">
    The topics that reifies <output:name of="topic"/> - associations:
    <logic:foreach name="reifyingAssociations">
      * <output:name />
    </logic:foreach>
  </logic:if>
</logic:context>
