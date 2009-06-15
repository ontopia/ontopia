<logic:context tmparam="tm">
  <logic:set name="topicTypes">
    <tm:classes of="topic"/>
  </logic:set>
  <logic:set name="names">
    <tm:names of="topicTypes"/>
  </logic:set>  
  <logic:set name="splittedNames">
    <tm:splitter of="names"/>
  </logic:set>
  
  Splitted Names:
  
  <table><tr>
  <logic:foreach name="splittedNames">
    <td>
    <output:element name="a"><output:attribute 
    name="href">#<output:name 
    stringifier="net.ontopia.topicmaps.nav2.impl.framework.FirstUpperCaseStringifier"/></output:attribute><output:name 
    stringifier="net.ontopia.topicmaps.nav2.impl.framework.FirstUpperCaseStringifier"/></output:element> 
    </td>
  </logic:foreach>  
  </tr></table>  

  <table>
  <logic:foreach name="splittedNames">
    <tr><td>
    <output:element name="a"><output:attribute name="name"><output:name 
    stringifier="net.ontopia.topicmaps.nav2.impl.framework.FirstUpperCaseStringifier"/></output:attribute><output:name 
    stringifier="net.ontopia.topicmaps.nav2.impl.framework.FirstUpperCaseStringifier"/></output:element>
    </td></tr>
    <logic:foreach>
      <tr><td></td><td>
      * <output:name/>
      </td></tr>
    </logic:foreach>
  </logic:foreach>
  </table>
  
</logic:context>
