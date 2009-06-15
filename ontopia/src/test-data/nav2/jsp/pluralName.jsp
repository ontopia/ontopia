<logic:context tmparam="tm" objparam="id" set="topic">

  <logic:set name="plural">
    <tm:lookup indicator="http://psi.ontopia.net/xtm/basename/plural"/>
  </logic:set>

  <output:name of="topic" variantScope="plural"/> 
    
</logic:context>
