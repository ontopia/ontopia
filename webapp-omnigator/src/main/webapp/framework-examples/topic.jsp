<%@ page language="java" contentType="text/html" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'   prefix='logic'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'  prefix='output' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value'   prefix='value'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue' prefix='tm'     %>

<%-- Demo Page for outputing information about one topic --%>

<html>
  <head>
    <title>Topic-Page-Preview</title>
    <link rel='stylesheet' href='../skins/standard.css' />
  </head>

  <body>
      <p><table class="shboxed" width="100%"><tr><td>
      <logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">

        <h2 class="boxed"><output:name /></h2>

	<!-- ================ Names =========================== -->
	  
	<logic:set name="names" comparator="nameComparator">
	  <tm:names/></logic:set>
    	<logic:if name="names">
        <p>
    	  <logic:then>
    	    <h3>Names</h3>
    	    <ul>
    	      <logic:foreach name="names">
    		<li><b><output:name /></b>
		<%-- === scoping themes of this base name === --%>
		<logic:set name="themes"><tm:scope /></logic:set>
		<logic:if name="themes">
		  <logic:then>
  		  [<logic:foreach name="themes" separator=", ">
  		     <output:name />
  		  </logic:foreach>]
	          </logic:then>
		</logic:if>
	        </li>
		<%-- === variants of this name === --%>
        	<logic:set name="variants"><tm:variants /></logic:set>
		Variant Names (<output:count of="variants"/>)<br>
        	<logic:if name="variants">
        	  <logic:then>
        	    <ul>
        	      <logic:foreach name="variants">
        		<li><output:name />
        		<%-- === scoping themes of this variant name === --%>
        		<logic:set name="themes"><tm:scope /></logic:set>
        		<logic:if name="themes">
        		  <logic:then>
        		  [<logic:foreach name="themes" separator=", ">
        		     <output:name />
        		  </logic:foreach>]
        		  </logic:then>
        		</logic:if>
			</li>
        	      </logic:foreach>
        	    </ul>
        	   </logic:then>
		   <!--
        	   <logic:else>
        	     [No Variant Names available.]
        	   </logic:else>
                   -->
        	</logic:if>
  	      </logic:foreach>
    	    </ul>
    	  </logic:then>
    	  <logic:else>
    	    No Names for this topic available.
    	  </logic:else>
	</p>
        </logic:if>

	<hr />
        <!-- ====================== Supertypes ================ -->

	<logic:set name="superTypes">
	  <tm:superclasses of="topic" level="99" />
	</logic:set>
	<logic:if name="superTypes">
          <logic:then>
            <p>
    	    <h3>Supertypes</h3>
	    <ul>
              <logic:foreach name="superTypes">
	        <li><a href="<output:link template="topic.jsp?tm=%topicmap%&id=%id%"/>"><output:name /></a></li>
              </logic:foreach>
	    </ul>
            </p>
          </logic:then>
        </logic:if>

	<hr />
        <!-- ================= Associations ==================== -->

	<logic:set name="roles" comparator="off">
	  <tm:roles of="topic"/>
	</logic:set>
	<logic:set name="assocs" comparator="assocComparator">
	  <tm:associations of="roles"/>
	</logic:set>
	<logic:set name="assocTypes">
	  <tm:classesOf of="assocs"/>
	</logic:set>
	<logic:set name="assocTopics" comparator="off">
	  <tm:associations of="topic"/>
	</logic:set>
	
    	<logic:if name="assocTypes">
        <p>
    	  <logic:then>
    	    <h3>Related Subjects</h3>
    	    <ul>
    	      <logic:foreach set="type">
                <li><b><output:name variantScope="superTypes"/></b>
		      
  		  <logic:set name="assocs" comparator="assocComparator">
  		    <tm:filter instanceOf="type">
  		      <value:copy of="assocTopics"/>
  		    </tm:filter>
  		  </logic:set>
  		  <ul>
  		    <logic:foreach name="assocs">
                      <logic:set name="roles">
			<value:difference>
			  <tm:roles />
		          <tm:roles of="topic" /> <%-- remove ourselves --%>
		        </value:difference>
		      </logic:set>
		      <logic:foreach name="roles">
		        <logic:set name="player"><tm:topics/></logic:set>
			<logic:set name="roletype"><tm:classesOf/></logic:set>
			<li><a href="<output:link of="player" template="topic.jsp?tm=%topicmap%&id=%id%"/>"><output:name of="player"/></a> (<a href="<output:link of="roletype" template="topic.jsp?tm=%topicmap%&id=%id%"/>"><output:name of="roletype"/></a>)</li>
		      </logic:foreach>
		      <%-- hr /--%>
  		    </logic:foreach>
  		  </ul>

		</li>
  	      </logic:foreach>
    	    </ul>
    	  </logic:then>
    	  <logic:else>
    	    No Associations for this topic available.
    	  </logic:else>
	</p>
        </logic:if>

	<hr />
        <!-- ================= Occurrences ==================== -->

	<logic:set name="occs" comparator="off">
	  <tm:occurrences/>
	</logic:set>

	<logic:set name="occsTypes">
	  <tm:classesOf of="occs"/>
	</logic:set>
	
    	<logic:if name="occsTypes">
        <p>
    	  <logic:then>
    	    <h3>Resources</h3>
    	    <ul>
    	      <logic:foreach name="occsTypes" set="type">
                  <li><b><output:name /></b>
		  
  		    <logic:set name="myoccs" comparator="off">
  		      <tm:filter instanceOf="type">
  			<value:copy of="occs"/>
  		      </tm:filter>
  		    </logic:set>
  		    <ul>
  		      <logic:foreach name="myoccs">
    
  			<li><output:content /> <a href="<output:locator />"><output:locator /></a>
  			<%-- === scoping themes of this occurrence === --%>
  			<logic:set name="themes"><tm:scope /></logic:set>
  			<logic:if name="themes">
  			  <logic:then>
  			  [<logic:foreach name="themes" separator=", ">
  			     <output:name />
  			  </logic:foreach>]
  			  </logic:then>
  			</logic:if>
  			</li>
  
  		      </logic:foreach>
  		    </ul>
		  </li>
  			  
  	      </logic:foreach>
    	    </ul>
    	  </logic:then>
    	  <logic:else>
    	    No Occurrences for this topic available.
    	  </logic:else>
	</p>
        </logic:if>

	<hr />	
        <!-- ================= Instances ==================== -->

	<logic:set name="instances">
	  <tm:instances of="topic" as="topic"/>
	</logic:set>
	
    	<logic:if name="instances">
        <p>
    	  <logic:then>
    	    <h3>Topics of this Type</h3>
    	    <ul>
    	      <logic:foreach name="instances">
	        <li><a href="<output:link template="topic.jsp?tm=%topicmap%&id=%id%"/>"><output:name/></a></li>
	      </logic:foreach>
	    </ul>
	  </logic:then>
	</p>
	</logic:if>

	
	<hr />
        <!-- ================= Players of this Role ==================== -->

	<logic:set name="rolesOfPlayers" comparator="off">
	  <tm:instances of="topic" as="role"/>
	</logic:set>
	<logic:set name="rolePlayers">
	  <tm:topics of="rolesOfPlayers"/>
	</logic:set>
	
    	<logic:if name="rolePlayers">
        <p>
    	  <logic:then>
    	    <h3>Players of this Role</h3>
    	    <ul>
    	      <logic:foreach name="rolePlayers">
	        <li><a href="<output:link template="topic.jsp?tm=%topicmap%&id=%id%"/>"><output:name/></a></li>
	      </logic:foreach>
	    </ul>
	  </logic:then>
	</p>
	</logic:if>
	
	<hr />
        <!-- =============== TopicMap Link ==================== -->

	<logic:set name="tmname"><tm:reifier of="topicmap" /></logic:set>
        <h4 class="boxed">
	  View Index of the 
  	  <logic:if name="tmname">
  	    <logic:then>
  	      <a href="<output:link of="topicmap" template="topicmap.jsp?tm=%topicmap%"/>"><output:name/></a>.
  	    </logic:then>
  	    <logic:else>
  	      <a href="<output:link of="topicmap" template="topicmap.jsp?tm=%topicmap%"/>">Topic Map</a>.
  	    </logic:else>
  	  </logic:if>
  	</h4>

	<!-- ================================================== -->
	
      </logic:context>
      </td></tr></table></p>


    <p>
     Testing the new Ontopia Tag Libraries for retrieving topic information.<br />
<!-- Created: Tue Jul 10 18:33:24 CEST 2001 -->
<!-- hhmts start -->
Last modified: Tue Aug 21 00:11:11 CEST 2001
<!-- hhmts end -->
    <br />
    <% java.util.Date date = new java.util.Date(); %>
    Page generated at <%= date.toString() %>
    </p>
    <address>Author: <a href="mailto:niko@ontopia.net">Niko Schmuck</a></address>
  </body>
</html>
