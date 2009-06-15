<logic:context>

  <logic:set name="dummy_string">
    <logic:if>
      <logic:then>
        <value:string>{default variable set.}</value:string>
      </logic:then>
      <logic:else>
        <value:string>{No default variable found.}</value:string>
      </logic:else>
    </logic:if>
  </logic:set>

  And the result is ... <output:name of="dummy_string" />.

</logic:context>
