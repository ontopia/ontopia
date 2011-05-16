<module>

  <!-- This function does a <tm:lookup> to see if this works in
  functions, or if it breaks because it can't retrieve the context
  tag. -->

  <function name="lookup">
    <logic:set name="oslo">
      <tm:lookup source="#oslo"/>
    </logic:set>

    <output:name of="oslo"/>
  </function>

  <!-- This function does a <tm:tolog> to see if this works in
  functions, or if it breaks because it can't retrieve the context
  tag. -->

  <function name="tolog">
    <logic:set name="oslo">
      <tm:tolog select="CITY" query="
      select $CITY from contained-in(norway : container, $CITY : containee)?"/>
    </logic:set>

    <output:name of="oslo"/>
  </function>
</module>
