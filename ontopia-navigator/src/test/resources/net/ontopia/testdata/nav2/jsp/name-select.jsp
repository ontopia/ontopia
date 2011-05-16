<logic:context tmparam="tm" objparam="id" set="topic">
  <logic:set name="name" comparator="nameComparator">
    <tm:name of="topic"/>
  </logic:set>
  Names (<output:count of="name"/>)
  <logic:foreach name="name" set="myName">
    * <output:name of="myName"/>
    <!-- === scoping themes of this base name === -->
    <logic:set name="themes"><tm:scope of="myName"/></logic:set>
    <logic:if name="themes">
      <logic:then>
   	- Scope: 
	<logic:foreach name="themes" set="myTheme" separator=",">
	  <output:name of="myTheme"/>
	</logic:foreach>
      </logic:then>
    </logic:if>
  </logic:foreach>
</logic:context>
