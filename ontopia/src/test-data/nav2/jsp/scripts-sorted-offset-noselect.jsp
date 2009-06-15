<logic:context tmparam="tm" settm="topicmap">
  <logic:set name="scripts" comparator="off">
    <tm:tolog query="  instance-of($SCRIPT, script)
                     order by $SCRIPT
                     limit 20 offset 10?"/>
  </logic:set>
  [[[START]]]
<logic:foreach name="scripts" max="500"><logic:bind>  * <output:name of="SCRIPT"/></logic:bind>
</logic:foreach>
  [[[END]]]
</logic:context>
