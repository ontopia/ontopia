<!-- A very basic module file with one demonstration function -->
<module>
  <function name="names" params="names">
    <h2>Names (<output:count of="names"/>)</h2>
    <ul>
    <logic:foreach name="names" set="myName">
      <li><output:name of="myName"/></li>
    </logic:foreach>
    </ul>
  </function>
</module>
