<logic:context tmparam="tm">

  <!-- load the module file which contains the function definition -->
  <logic:include file="jsp/refresh-button.jsm"/>

  <!-- call method -->
  <logic:set name="selectedButton"/>
  <logic:call name="refresh-button"/>

</logic:context>
