<logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">

  <!-- load the module file which contains the function definition -->
  <logic:include file="names.jsm"/>

  <!-- execute the function -->
  <logic:set name="names" comparator="nameComparator">
    <logic:call name="getNames">
      <logic:set name="topic">
        <value:copy of="topic"/>
      </logic:set>
    </logic:call>
  </logic:set>

  <!-- print out the value of the return variable -->
  <h2>Names (<output:count of="names"/>)</h2>
  <ul>
    <logic:foreach name="names" set="myName">
      <li><output:name of="myName"/></li>
    </logic:foreach>
  </ul>
 
</logic:context>
