<logic:context>
  <logic:set name="strings" comparator="off">
    <value:sequence>
      <value:string>1</value:string>
      <value:string>2</value:string>
      <value:string>3</value:string>
      <value:string>4</value:string>
    </value:sequence>
  </logic:set>
  Sequence of strings: (<output:count of="strings"/>)
  <logic:foreach name="strings">
    * <output:content/>
  </logic:foreach>
</logic:context>
