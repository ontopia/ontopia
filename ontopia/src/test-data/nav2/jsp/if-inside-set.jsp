<!-- For further information see bug #315 -->

<logic:context tmparam='tm' settm='topicmap' objparam='id' set="topic">

<logic:set name='names' comparator='off'>
  <tm:names />
</logic:set>

<p>Number of names: <output:count of='names'/></p>

<logic:set name='variants' comparator='off'>
  <logic:if name='names'>
    <logic:then>
      <tm:variants/>
    </logic:then>
  </logic:if>
</logic:set>

<p>Total number of variants: <output:count of='variants'/></p>
    
</logic:context>
