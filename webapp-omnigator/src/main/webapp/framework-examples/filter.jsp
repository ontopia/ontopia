<%@ page language="java" contentType="text/html" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic' prefix='logic' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output' prefix='output' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value' prefix='value' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue' prefix='tm' %>
<html>
  <head>
    <title>Filter Test [Topic-Page-Preview]</title>
    <link rel='stylesheet' href='skins/standard.css' />
  </head>

  <body>
      <h3>Testing the new Ontopia Tag Libraries for retrieving topic information</h3>
      <hr />
	  
      <logic:context tmparam="tm" objparam="id" set="topic">

        <h2><output:name /></h2>
        <hr />

	<!-- ================ Names =========================== -->
	  
	<logic:set name="names"><tm:names /></logic:set>
    	<logic:if name="names">
        <p>
    	  <logic:then>
    	    <h3>Names (<output:count of="names"/>)</h3>
    	    <ul>
    	      <logic:foreach name="names">
    		<li><output:name />
		<!-- === scoping themes of this base name === -->
		<logic:set name="themes"><tm:scope /></logic:set>
		<logic:if name="themes">
		  <logic:then>
  		  [<logic:foreach name="themes" separator=", ">
  		     <output:name />
  		  </logic:foreach>]
	          </logic:then>
		</logic:if>
	        </li>
		<!-- === variants of this name === -->
        	<logic:set name="variants"><tm:variants /></logic:set>
        	<logic:if name="variants">
        	  <logic:then>
        	    <ul>
        	      <logic:foreach name="variants">
        		<li><output:name />
        		<!-- === scoping themes of this variant name === -->
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
    
        <!-- ====================== Direct Supertypes ================ -->

	<logic:set name="superTypes"><tm:superclasses of="topic" level="1" /></logic:set>
	<logic:if name="superTypes">
          <logic:then>
    	    <h3>Types (<output:count of="superTypes" />)</h3>
	    <ul>
              <logic:foreach name="superTypes">
	        <li><output:name /></li>
              </logic:foreach>
	    </ul>
          </logic:then>
        </logic:if>

        <!-- ====================== Supertypes ================ -->

	<logic:set name="superTypes"><tm:superclasses of="topic" level="99" /></logic:set>
	<logic:if name="superTypes">
          <logic:then>
    	    <h3>All Supertypes (<output:count of="superTypes" />)</h3>
	    <ul>
              <logic:foreach name="superTypes">
	        <li><output:name /></li>
              </logic:foreach>
	    </ul>
          </logic:then>
        </logic:if>

        <!-- ================= Occurrences ==================== -->

	<logic:set name="occs"><tm:occurrences/></logic:set>
    	<logic:if name="occs">
        <p>
    	  <logic:then>
    	    <h3>Occurrences (<output:count of="occs" />)</h3>
    	    <ul>
    	      <logic:foreach name="occs">
    		<li><output:content /> <output:locator />
        	<!-- === scoping themes of this occurrence === -->
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
    	  <logic:else>
    	    No Occurrences for this topic available.
    	  </logic:else>
	</p>
        </logic:if>

	<logic:set name="occTypes"><tm:classesOf of="occs"/></logic:set>
    	<logic:if name="occTypes">
          <p>
    	  <logic:then>
    	    <h3>Occurrence Types (<output:count of="occTypes" />)</h3>
    	    <ul>
    	      <logic:foreach name="occTypes">
    		<li><output:name />
	        </li>
  	      </logic:foreach>
    	    </ul>
    	  </logic:then>
	  </p>
        </logic:if>


        <!-- ================= Plain and flat display of Associations ==================== -->

	<logic:set name="assocs"><tm:associations/></logic:set>
    	<logic:if name="assocs">
        <p>
    	  <logic:then>
    	    <h3>Associations (<output:count of="assocs" />)</h3>
    	    <ul>
    	      <logic:foreach name="assocs">
    		<li><output:name />
        	<!-- === scoping themes of this association === -->
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
    	  <logic:else>
    	    No Associations for this topic available.
    	  </logic:else>
	</p>
        </logic:if>

      
        <!-- ================= Categorized display of Associations ==================== -->

	<logic:set name="assocs"><tm:associations/></logic:set>
	<logic:set name="assocTypes"><tm:classesOf of="assocs"/></logic:set>
    	<logic:if name="assocTypes">
          <p>
    	  <logic:then>
    	    <h3>Related Subjects</h3>
    	    <ul>
    	      <logic:foreach name="assocTypes" set="type">
    		<li><b><output:name /></b>
		<logic:set name="superTypes"><tm:superclasses of="type" /></logic:set>
		<logic:if name="superTypes">
    	          <logic:then>
		    <ul>
    	            <logic:foreach name="superTypes">
		       <li><output:name /></li>
	            </logic:foreach>
		    </ul>
	          </logic:then>
	        </logic:if>
	<!--
		<logic:set name="assocs">
		  <tm:filter instanceOf="type"><tm:associations of=""/></tm:filter>
		</logic:set>
		<logic:if name="assocs">
    	          <logic:then>
		    <ul>
    	            <logic:foreach name="assocs">
		       <li><output:name /></li>
	            </logic:foreach>
		    </ul>
	          </logic:then>
	        </logic:if>
       -->
		      
		<!-- === Display Association Roles === -->
		<!-- Roles: { -->
		<logic:set name="roles"><tm:roles /></logic:set>
		<logic:foreach name="roles" separator=", ">
		      <!-- output:name / -->
		</logic:foreach>
		<!-- } -->
		    
	        </li>
  	      </logic:foreach>
    	    </ul>
    	  </logic:then>
    	  <logic:else>
    	    No Associations for this topic available.
    	  </logic:else>
	  </p>
        </logic:if>

        <!-- ================================================== -->
      
      <p>
	<!--
	<logic:set name="baustelle"><value:string>Under Construction</value:string></logic:set>
        <output:content of="baustelle" />
        -->
        </p>
      
      </logic:context>

      
    <hr />
    <p>
    <address>Author: <a href="mailto:niko@ontopia.net">Niko Schmuck</a></address><br />
<!-- Created: Tue Jul 10 18:33:24 CEST 2001 -->
<!-- hhmts start -->
Last modified: Fri Aug 10 10:52:52 CEST 2001
<!-- hhmts end -->
    <br />
    <% java.util.Date date = new java.util.Date(); %>
    Page generated at <%= date.toString() %>
    </p>
  </body>
</html>
