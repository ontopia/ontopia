<logic:context tmparam="tm" settm="topicmap">
  <logic:include file="jsp/showTopicSet.jsm"/>
  <logic:set name="context"><framework:getcontext context="basename"/></logic:set>Basename context: <output:count of="context"/>
  <logic:set name="context"><framework:getcontext context="variant"/></logic:set>Variant context: <output:count of="context"/>
  <logic:set name="context"><framework:getcontext context="occurrence"/></logic:set>Occurrence context: <output:count of="context"/>
  <logic:set name="context"><framework:getcontext context="association"/></logic:set>Association context: <output:count of="context"/>

  <logic:set name="biography"><tm:lookup source="opera-template.xtm#biography"/></logic:set>
  <logic:set name="english"><tm:lookup source="opera-template.xtm#english"/></logic:set>
  <logic:set name="online"><tm:lookup source="opera-template.xtm#online"/></logic:set>

  <framework:setcontext basename    = "english"  />
  <framework:setcontext variant     = "english"  />
  <framework:setcontext occurrence  = "online"   />
  <framework:setcontext association = "biography"/>

  ----- Base name context --------------------------------------------------
  <logic:call name="show-topic-set">
    <logic:set name="topics"><framework:getcontext context="basename"/></logic:set>
  </logic:call>
  ----- Variant context ----------------------------------------------------
  <logic:call name="show-topic-set">
    <logic:set name="topics"><framework:getcontext context="variant"/></logic:set>
  </logic:call>
  ----- Occurrence context -------------------------------------------------
  <logic:call name="show-topic-set">
    <logic:set name="topics"><framework:getcontext context="occurrence"/></logic:set>
  </logic:call>
  ----- Association context ------------------------------------------------
  <logic:call name="show-topic-set">
    <logic:set name="topics"><framework:getcontext context="association"/></logic:set>
  </logic:call>

</logic:context>
