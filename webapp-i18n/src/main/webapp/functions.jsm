
<module>

<!-- ===== OCCURRENCES ============================== 
     Displays the occurrences of a topic in a table.  -->

<function name="occurrences" params="topic">

<logic:set name="description">
  <tm:lookup source="#description"/>
</logic:set>
<logic:set name="occurrences">    
  <tm:filter instanceOf="description" invert="true">
    <tm:occurrences of="topic" type="external"/>     
  </tm:filter>
</logic:set>


<logic:if name="occurrences"><logic:then>
<logic:set name="occtypes"><tm:classesOf/></logic:set>

<h2>More information</h2>

<table>
<logic:foreach name="occtypes" set="occtype">
<logic:set name="occs">
  <tm:filter instanceOf="occtype">
    <tm:occurrences of="topic" type="external"/>
  </tm:filter>
</logic:set>

<logic:foreach name="occs">
  <logic:set name="scope"><tm:scope/></logic:set>

<tr><td><b><output:name of="occtype"/>:&#160;&#160;&#160;</b></td>
    <td><output:element name="a">
          <output:attribute name="href"><output:locator/></output:attribute>
          <output:locator/>
        </output:element>&#160;&#160;&#160;
    </td>
    <td>
      <logic:foreach name="scope" separator=", "><output:name/></logic:foreach>
    </td></tr>
</logic:foreach>
</logic:foreach>
</table>
</logic:then></logic:if>

</function>

<!-- ===== SCRIPTS =====================================
     Displays a set of scripts in a table with metadata. -->

<function name="scripts" params="scripts">

<logic:if name="scripts"><logic:then>
<table>
<tr class="top">
    <th>&#160;Name</th>   
    <th>&#160;<a href="script-types.jsp">Type</a></th>   
    <th>&#160;<a href="categories.jsp">Family</a></th></tr>

<logic:set name="belongs-to">
  <tm:lookup indicator="http://psi.ontopia.net/i18n/#belongs-to"/></logic:set>

<logic:foreach name="scripts" max="300">
  <logic:set name="script"><tm:topics/></logic:set>
  <logic:set name="type"><tm:classesOf of="script"/></logic:set>
  <logic:set name="family">
    <tm:associated type="belongs-to" from="script"/>
  </logic:set>

<tr> <!-- ROW FOR EACH SCRIPT NAME -->
<td>&#160;
  <output:element name="a">
    <output:attribute name="href">script.jsp?id=<output:id of="script"/></output:attribute>
    <output:name/>
  </output:element>
  &#160;&#160;&#160;</td>

<td>&#160;
  <output:element name="a">
    <output:attribute name="href">script-types.jsp?id=<output:id of="type"/></output:attribute>
    <output:name of="type"/>
  </output:element>&#160;&#160;&#160;</td>

<td>
  <logic:if name="family"><logic:then>&#160;
  <output:element name="a">
    <output:attribute name="href">category.jsp?id=<output:id of="family"/></output:attribute>
    <output:name of="family"/>
  </output:element>
  </logic:then></logic:if>
</td>
</tr>
</logic:foreach>

</table>
</logic:then></logic:if>

</function>

<!-- ===== CAT_SCRIPTS ============================= 
     Displays the scripts of a category in a table.  -->

<function name="cat_scripts" params="">
   <logic:set name="scripts">
    <tm:filter instanceOf="script">
    <tm:associated startrole="container" type="belongs-to" endrole="containee"/>
    </tm:filter>
   </logic:set>

    <table>
    <logic:foreach name="scripts">
      <logic:set name="type"><tm:classesOf/></logic:set>
      <logic:set name="period">
      	<tm:filter instanceOf="http://psi.ontopia.net/i18n/#period-of-use">
      	  <tm:occurrences/>
      	</tm:filter>
      </logic:set>

      <tr>
      <td><output:element name="a">
            <output:attribute name="href">script.jsp?id=<output:id/></output:attribute>
            <output:name/>
          </output:element>
           &#160;&#160;&#160;</td>
      <td><output:element name="a">
            <output:attribute name="href">script-types.jsp#id<output:id of="type"/></output:attribute>
            <output:name of="type"/>
          </output:element>
          &#160;&#160;&#160;</td>

      <td><logic:if name="period"><logic:then>
            <output:content of="period"/>
          </logic:then></logic:if></td></tr>
    </logic:foreach>
    </table>
</function>


<!-- ===== SHOW-TRANSXIONS ===================================== 
     Displays the transliterations and transcriptions of a script
     or language in a table.  -->

<function name="show-transxions" params="topic">
<logic:set name="transxions-from">
  <tm:tolog query="
     transforms-from(%topic% : source, $TXN : method),
     transforms-to($TXN : method, $TARGET : target),
     instance-of($TXN, $TYPE)?"/>
</logic:set>
<logic:set name="transxions-to">
  <tm:tolog query="
     transforms-to(%topic% : target, $TXN : method),
     transforms-from($TXN : method, $SOURCE : source),
     instance-of($TXN, $TYPE)?"/>
</logic:set>
<logic:set name="transxions">
  <value:union>
    <value:copy of="transxions-from"/>
    <value:copy of="transxions-to"/>
  </value:union>
</logic:set>

<logic:if name="transxions"><logic:then>
<h2>Transliterations/transcriptions</h2>

<!-- transxions away from this topic -->
<logic:if name="transxions-from"><logic:then>
<table>
<tr><th>Name   </th><th>Target   </th><th>Type</th></tr>

<logic:foreach name="transxions-from"><logic:bind>
<tr><td><output:element name="a">
        <output:attribute name="href">transxion.jsp?id=<output:id of="TXN"/></output:attribute><output:name of="TXN"/></output:element></td>
    <td><output:element name="a">
        <output:attribute name="href">script.jsp?id=<output:id of="TARGET"/></output:attribute><output:name of="TARGET"/></output:element></td>
    <td><output:name of="TYPE"/></td></tr>
</logic:bind></logic:foreach>
</table>
</logic:then></logic:if>

<!-- transxions to this topic -->
<logic:if name="transxions-to"><logic:then>
<table>
<tr><th>Name   </th><th>Source   </th><th>Type</th></tr>

<logic:foreach name="transxions-to"><logic:bind>
<tr><td><output:element name="a">
        <output:attribute name="href">transxion.jsp?id=<output:id of="TXN"/></output:attribute><output:name of="TXN"/></output:element></td>
    <td><output:element name="a">
        <output:attribute name="href">script.jsp?id=<output:id of="SOURCE"/></output:attribute><output:name of="SOURCE"/></output:element></td>
    <td><output:name of="TYPE"/></td></tr>
</logic:bind></logic:foreach>
</table>
</logic:then></logic:if>

</logic:then></logic:if>
</function>

<!-- ===== FIND_ROOT ============================= 
     Find the root ancestor of a script.  -->

<function name="find_root" params="script" return="root">
  <logic:set name="parent">
    <tm:associated from="script" type="derived-from" endrole="predecessor"/>
  </logic:set>

  <logic:set name="root">
    <logic:if name="parent">
      <logic:then>
        <logic:call name="find_root">
          <logic:set name="script">
            <value:copy of="parent"/>
          </logic:set>
        </logic:call>
      </logic:then>

      <logic:else>
        <value:copy of="parent"/>
      </logic:else>
    </logic:if>
  </logic:set>
</function>    
</module>
