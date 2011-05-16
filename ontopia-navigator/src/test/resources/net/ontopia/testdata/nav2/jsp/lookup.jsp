<logic:context tmparam="tm">
    
  <logic:set name="lookupObj">
    <tm:lookup objectid="1" />
  </logic:set>
  lookup by objectid: <output:name of="lookupObj" />
      
  <logic:set name="lookupObj">
    <tm:lookup source="opera.xtm#la-scala" />
  </logic:set>
  lookup by source: <output:name of="lookupObj" />
	
  <logic:set name="lookupObj">
    <tm:lookup indicator="#operatm-tm" />
  </logic:set>
  lookup by indicator: <output:name of="lookupObj" />
	  
  <logic:set name="lookupObj">
    <tm:lookup subject="http://www.mascagni.org/ONTOPIA" />
  </logic:set>
  lookup by subject: <output:name of="lookupObj" />

  <logic:set name="lookupObj">
    <tm:lookup basename="Italian Opera topic map" />
  </logic:set>
  lookup by basename: <output:name of="lookupObj" />

  <logic:set name="lookupObj">
    <tm:lookup variant="Hillern, Wilhelmine" />
  </logic:set>
  lookup by variant: <output:name of="lookupObj" />

  <logic:set name="lookupObj">
    <tm:lookup as="subject">
   	  <tm:subjectAddress>
        <tm:lookup subject="http://www.mascagni.org/ONTOPIA" />
 	    </tm:subjectAddress>
    </tm:lookup>
  </logic:set>
  lookup by as-subject locator: <output:name of="lookupObj" />

  <logic:set name="lookupObj">
    <tm:lookup as="indicator">
   	  <tm:indicators>
        <tm:lookup indicator="#operatm-tm" />
 	    </tm:indicators>
    </tm:lookup>
  </logic:set>
  lookup by as-indicator locator: <output:name of="lookupObj" />

  <logic:set name="lookupObj">
    <tm:lookup as="source">
   	  <tm:sourceLocators>
        <tm:lookup source="opera.xtm#la-scala" />
 	    </tm:sourceLocators>
    </tm:lookup>
  </logic:set>
  lookup by as-source locator: <output:name of="lookupObj" />

  <logic:set name="levilly">
    <tm:topics>
      <tm:lookup basename="Le Villi" />
    </tm:topics>
  </logic:set>
  <output:name of="levilly"/>

  <logic:set name="lookupObj">
    <tm:locator>
      <tm:occurrences of="levilly"/>
    </tm:locator>
  </logic:set>
  occurrence tm:locator retrieval 1: <output:locator of="lookupObj" />

  <logic:set name="levilly-occs">
    <tm:occurrences of="levilly"/>
  </logic:set>
  <logic:set name="lookupObj">
    <tm:locator of="levilly-occs"/>
  </logic:set>
  occurrence tm:locator retrieval 2: <output:locator of="lookupObj" />

</logic:context>
