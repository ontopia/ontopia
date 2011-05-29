<%@ page language="java" contentType="text/html" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic' prefix='logic' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output' prefix='output' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value' prefix='value' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue' prefix='tm' %>

<html>
  <head>
    <title>Topic SetTest-Page-Preview</title>
    <link rel='stylesheet' href='skins/standard.css' />
  </head>

  <body>
      <h3>Testing the new Ontopia Tag Libraries for retrieving topic information</h3>
      <hr />
	  
      <logic:context tmparam="tm" objparam="id">

  	<logic:set name="la-boheme"><tm:lookup source="#la-boheme" /></logic:set>
  	<logic:set name="tosca"><tm:lookup source="#tosca" /></logic:set>

	<!-- ================ union =========================== -->

	<logic:set name="resultNames">
	  <value:union>
            <tm:associations of="tosca" />
            <tm:associations of="la-boheme" />
	  </value:union>
        </logic:set>

        <logic:set name="assocsTosca"><tm:associations of="tosca" /></logic:set>
        <logic:set name="assocsLa-boheme"><tm:associations of="la-boheme" /></logic:set>

	Calculation of Union of
        Assocs of Tosca: <output:count of="assocsTosca"/>,
        Assocs of La-Boheme: <output:count of="assocsLa-boheme"/><br />

        <logic:if name="resultNames">
        <p>
    	  <logic:then>
    	    <h3>Resulting Associations (<output:count of="resultNames"/>)</h3>
    	    <ul>
    	      <logic:foreach name="resultNames">
		<logic:set name="player"><tm:topics/></logic:set>
    		<li><output:name of="player"/></li>
  	      </logic:foreach>
    	    </ul>
    	  </logic:then>
    	  <logic:else>
    	    No Resulting Associations for this topic available.
    	  </logic:else>
	</p>
        </logic:if>
    
        <hr />
	  
	<!-- ================ intersection =========================== -->

	<logic:set name="resultNames">
	  <value:intersection>
            <tm:associations of="tosca" />
            <tm:associations of="la-boheme" />
	  </value:intersection>
        </logic:set>

        <logic:set name="assocsTosca"><tm:associations of="tosca" /></logic:set>
        <logic:set name="assocsLa-boheme"><tm:associations of="la-boheme" /></logic:set>

	Calculation of Intersection of
        Assocs of Tosca: <output:count of="assocsTosca"/>,
        Assocs of La-Boheme: <output:count of="assocsLa-boheme"/><br />

        <logic:if name="resultNames">
        <p>
    	  <logic:then>
    	    <h3>Resulting Associations (<output:count of="resultNames"/>)</h3>
    	    <ul>
    	      <logic:foreach name="resultNames">
    		<li><!-- output:name / --></li>
  	      </logic:foreach>
    	    </ul>
    	  </logic:then>
    	  <logic:else>
    	    No Resulting Associations for this topic available.
    	  </logic:else>
	</p>
        </logic:if>
    
        <hr />

        <!-- ================ difference =========================== -->

	<logic:set name="assocs">
	  <value:difference>
            <tm:associations of="tosca" />
            <tm:associations of="la-boheme" />
	  </value:difference>
        </logic:set>

        <logic:set name="assocsTosca"><tm:associations of="tosca" /></logic:set>
        <logic:set name="assocsLa-boheme"><tm:associations of="la-boheme" /></logic:set>

	Calculation of Difference of
        Assocs of Tosca: <output:count of="assocsTosca"/>,
        Assocs of La-Boheme: <output:count of="assocsLa-boheme"/><br />

        <logic:if name="assocs">
        <p>
    	  <logic:then>
    	    <h3>Resulting Associations (<output:count of="assocs"/>)</h3>
    	    <ul>
  		    <logic:foreach name="assocs">
                      <logic:set name="roles">
			<value:difference>
			  <tm:roles />
		          <tm:roles of="topic" /> <!-- remove ourselves -->
		        </value:difference>
		      </logic:set>
		      <logic:foreach name="roles">
		        <logic:set name="player"><tm:topics/></logic:set>
			<li><a href="<output:link of="player" template="topic.jsp?tm=%topicmap%&id=%id%"/>"><output:name of="player"/></a></li>
		      </logic:foreach>
  		    </logic:foreach>

    	    </ul>
    	  </logic:then>
    	  <logic:else>
    	    No Resulting Associations for this topic available.
    	  </logic:else>
	</p>
        </logic:if>
        
        <!-- ================================================== -->
      
      </logic:context>

      
    <hr />
    <p>
    <address>Author: <a href="mailto:niko@ontopia.net">Niko Schmuck</a></address><br />
<!-- Created: Tue Jul 10 18:33:24 CEST 2001 -->
<!-- hhmts start -->
Last modified: Thu Oct 18 13:35:52 CEST 2001
<!-- hhmts end -->
    <br />
    <% java.util.Date date = new java.util.Date(); %>
    Page generated at <%= date.toString() %>
    </p>
  </body>
</html>
