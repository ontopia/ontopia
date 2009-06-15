<logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">

  <!-- load the module file which contains the function definition -->
  <logic:include file="names.jsm" />

  <!-- execute the function -->
  <logic:call name="outputNames">
    <logic:set name="names" comparator="nameComparator">
      <tm:names of="topic" />
    </logic:set>
  </logic:call>

</logic:context>
