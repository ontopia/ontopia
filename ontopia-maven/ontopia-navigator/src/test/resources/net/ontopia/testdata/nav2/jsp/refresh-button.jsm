<!--
     $Id: refresh-button.jsm,v 1.1 2005/05/24 09:02:26 grove Exp $

     Module file used with refresh-button.jsp.
-->
<module>

  <function name="refresh-button" params="">
    <!-- Refresh -->
    <tr>
       <td>
          <output:element name="button">
            <logic:set name="button">
              <tm:lookup source="#refreshButtonID"/>
            </logic:set>
            <logic:call name="getStyleDef"/>
            <output:attribute name="onclick">window.location.reload(true);</output:attribute>
            Refresh
          </output:element>
       </td>
    </tr>
  </function>

  <function name="getStyleDef" params="button,selectedButton">
    <output:attribute name="style">
      <logic:if name="button" equals="selectedButton">
        <logic:then>
          background-color: transparent;
          color: #000000;
        </logic:then>
        <logic:else>
          background-color: #3990D0;
          color: #FFFFFF;
        </logic:else>
      </logic:if>
          width:100%;
          border:1px solid black;
          padding-top: 0px;
          padding-right: 3px;
          padding-bottom: 0px;
          padding-left: 3px;
          font-weight: bold;
         cursor : pointer;
     </output:attribute>
</function>

</module>
