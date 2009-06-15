<logic:context tmparam="tm">
  <logic:set name="a"><tm:lookup source="#composer"/></logic:set>
  <logic:set name="b"><tm:lookup source="#librettist"/></logic:set>
  <logic:set name="puccini"><tm:lookup source="#puccini"/></logic:set>
  <logic:set name="mascagni"><tm:lookup source="#mascagni"/></logic:set>
  <logic:set name="verdi"><tm:lookup source="#verdi"/></logic:set>
  <logic:set name="composers">
    <value:union>
      <logic:if name="a" equals="b">
        <logic:else>
          <value:copy of="puccini"/>
          <value:copy of="mascagni"/>
        </logic:else>
      </logic:if>
      <value:copy of="verdi"/>
    </value:union>
  </logic:set>

Results are:
<logic:foreach name="composers" set="composer">
  * <output:name of="composer"/>
</logic:foreach>

If there are two results, we have bug #1476. If we have three, all is
fine.
</logic:context>