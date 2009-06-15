<!-- test case for bug #624 -->
<logic:context tmparam="tm" objparam="id" set="topic">
  <logic:set name="scope">
     <value:union>
       <value:copy of="topic"/>
     </value:union>
  </logic:set>
  <logic:set name="names">
    <tm:name of="topic" basenameScope="scope"/>
  </logic:set>
  Names (<output:count of="names"/>)
  <logic:foreach name="names" set="myName">
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
