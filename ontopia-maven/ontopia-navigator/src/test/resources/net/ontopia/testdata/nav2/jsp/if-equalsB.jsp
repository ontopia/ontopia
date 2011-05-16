<logic:context tmparam="tm" objparam="id" set="topic">  

  <logic:set name="place">
    <tm:lookup indicator="http://psi.ontopia.net/geography/#place"/>
  </logic:set>
  set <output:name of="place"/>
      
  <logic:set name="country">
    <tm:lookup indicator="http://psi.ontopia.net/geography/#country"/>
  </logic:set>
  set <output:name of="country"/>

  <logic:set name="set">
    <tm:lookup indicator="http://psi.ontopia.net/opera/#setting"/>
  </logic:set>
  set <output:name of="set"/>
      
  <logic:set name="settings">
    <!-- tm:filter instanceOf="place" -->
      <tm:associated from="topic" type="set"/>
    <!-- /tm:filter -->
  </logic:set>

  Listing settings for opera <output:name of="topic"/>
  <output:name of="settings"/> (<output:count of="settings"/>)
      
  <logic:foreach name="settings" set="setting">
    <logic:set name="type"><tm:classesOf of="setting"/></logic:set>
    Setting <output:name of="setting"/> of type: <output:name of="type"/>.
    type: <output:id of="type"/> 
    country: <output:id of="country"/> 
    <logic:if name="type" equals="country">
      <logic:then>Opera is set in a country.</logic:then>
      <logic:else>Opera is *not* set in a country.</logic:else>
    </logic:if>
  </logic:foreach>

</logic:context>
