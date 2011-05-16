<logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">

  <logic:set name="occurrences" comparator="off">    
    <tm:occurrences of="topic" type="external"/>
  </logic:set>
    
  <logic:if name="occurrences"><logic:then>

    <logic:set name="occtypes"><tm:classesOf/></logic:set>

    <h2>More information</h2>
    <table>
      <logic:foreach name="occtypes" set="occtype">
  	<logic:set name="occs" comparator="occComparator">
  	  <tm:filter instanceOf="occtype">
  	    <tm:occurrences of="topic" type="external"/>
  	  </tm:filter>
  	</logic:set>
  	<logic:foreach name="occs">
  	  <tr>
	    <td><b><output:name of="occtype"/>:</b></td>
  	    <td><output:element name="a">
  		  <output:attribute name="href"><output:locator/></output:attribute>
  		  <output:locator/>
  		</output:element>
  	    </td>
	  </tr>
  	</logic:foreach>
      </logic:foreach>
    </table>

  </logic:then></logic:if>

</logic:context>
