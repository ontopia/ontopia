<logic:context tmparam="tm" settm="topicmap">
  <logic:set name="scripts">
    <tm:tolog query="instance-of($SCRIPT, script)?" select="SCRIPT"/>
  </logic:set>
  [[[START]]]
<logic:foreach name="scripts" max="500">  * <output:name/>
</logic:foreach>
  [[[END]]]
</logic:context>
