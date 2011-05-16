<!--
     $Id: names.jsm,v 1.2 2002/02/19 11:55:50 niko Exp $

     A very basic module file which contains functions for
     retrieving and showing the base names available for the
     given topic.
-->
<module>

  <function name="outputNames" params="names">
    <h2>Names (<output:count of="names"/>)</h2>
    <ul>
    <logic:foreach name="names" set="myName">
      <li><output:name of="myName"/></li>
    </logic:foreach>
    </ul>
  </function>

  <function name="getNames" params="topic" return="names">
    <logic:set name="names" comparator="nameComparator">
      <tm:names of="topic" />
    </logic:set>
  </function>

</module>
