<logic:context tmparam="tm" objparam="id" set="topic">
  <logic:set name="topics">
    <tm:classes of="topic"/>
  </logic:set>
  
  Topics of this type (<output:count of="topics"/>)
  
  <logic:foreach name="topics" set="t_type">
    * <output:name of="t_type"/>
  </logic:foreach>
</logic:context>
