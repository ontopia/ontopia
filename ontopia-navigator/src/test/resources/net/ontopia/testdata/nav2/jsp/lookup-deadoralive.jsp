<logic:context tmparam="tm">
    
  <logic:set name="lookupObj">
    <tm:lookup objectid="1" />
  </logic:set>
  lookup by objectid: <output:name of="lookupObj" />
      
  <logic:set name="lookupObj">
    <tm:lookup source="#elvis" />
  </logic:set>
  lookup by source: <output:name of="lookupObj" />
	
  <logic:set name="lookupObj">
    <tm:lookup indicator="#occ2" />
  </logic:set>
  lookup by indicator: <output:name of="lookupObj" />
	  
  <logic:set name="lookupObj">
    <tm:lookup subject="http://sunsite.unc.edu:80/elvis/elvishom.html" />
  </logic:set>
  lookup by subject: <output:name of="lookupObj" />
	    
</logic:context>
