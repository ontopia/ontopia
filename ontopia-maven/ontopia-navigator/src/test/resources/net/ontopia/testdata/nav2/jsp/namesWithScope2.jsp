<logic:context topicmap="ontopsi.xtm">

  <logic:set name="super-sub">
    <tm:lookup indicator="http://www.topicmaps.org/xtm/1.0/core.xtm#superclass-subclass"/>
  </logic:set>
  <logic:set name="super">
    <tm:lookup indicator="http://www.topicmaps.org/xtm/1.0/core.xtm#superclass"/>
  </logic:set>
  <logic:set name="sub">
    <tm:lookup indicator="http://www.topicmaps.org/xtm/1.0/core.xtm#subclass"/>
  </logic:set>

  <logic:set name="supername">
    <tm:name of="super-sub" basenameScope="super"/>
  </logic:set>
  <logic:set name="subname">
    <tm:name of="super-sub" basenameScope="sub"/>
  </logic:set>

  Supername: <output:name of="supername"/>
  Subname: <output:name of="subname"/>

</logic:context>
