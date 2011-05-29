<%@ page language="java" contentType="text/html" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic' prefix='logic' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output' prefix='output' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value' prefix='value' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue' prefix='tm' %>

<html>
  <head>
    <title>Lookup Test [Topic-Page-Preview]</title>
    <link rel='stylesheet' href='skins/standard.css' />
  </head>

  <body>
      <h3>Testing the new Ontopia Tag Libraries for retrieving topic information</h3>
      <hr />
	  
      <logic:context tmparam="tm">

        <!-- ============== Lookup Topic Map Objects =============== -->

	<ul>
	  
  	  <logic:set name="lookupObj"><tm:lookup objectid="1" /></logic:set>
  	  <li><output:name of="lookupObj" /></li>
  	        
  	  <logic:set name="lookupObj"><tm:lookup indicator="#operatm-tm" /></logic:set>
  	  <li><output:name of="lookupObj" /></li>
  
  	  <logic:set name="lookupObj"><tm:lookup source="#puccini" /></logic:set>
  	  <li><output:name of="lookupObj" /></li>
  
  	  <logic:set name="lookupObj"><tm:lookup subject="#puccini" /></logic:set>
  	  <li><output:name of="lookupObj" /></li>

          <jsp:include page="external.html" flush="true"/>
  
        </ul>

        <!-- ================================================== -->
      
      </logic:context>
      
    <hr />

    <p>
    <address>Author: <a href="mailto:niko@ontopia.net">Niko Schmuck</a></address><br />
<!-- Created: Tue Jul 10 18:33:24 CEST 2001 -->
<!-- hhmts start -->
Last modified: Thu Nov  1 17:43:34 CET 2001
<!-- hhmts end -->
    <br />
    <% java.util.Date date = new java.util.Date(); %>
    Page generated at <%= date.toString() %>
    </p>
  </body>
</html>
