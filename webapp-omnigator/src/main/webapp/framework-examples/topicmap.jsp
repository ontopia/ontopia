<%@ page language="java" contentType="text/html" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'   prefix='logic'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'  prefix='output' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value'   prefix='value'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue' prefix='tm'     %>
<html>
  <head>
    <title>TopicMap-Page-Preview</title>
    <link rel='stylesheet' href='../skins/standard.css' />
  </head>

  <body>
      <p><table class="shboxed" width="100%"><tr><td>
      <logic:context tmparam="tm" settm="topicmap">

        <!-- =============== Reified topicmap name ============ -->
	  
	<logic:set name="tmname"><tm:reifier of="topicmap" /></logic:set>
        <h2 class="boxed">
  	  <logic:if name="tmname">
  	    <logic:then>
  	      <output:name of="tmname" />
  	    </logic:then>
  	    <logic:else>
  	      Topic Map
  	    </logic:else>
  	  </logic:if>
  	</h2>

        <!-- ============== List of topic types =============== -->

	<logic:set name="topics"><tm:classes of="topic"/></logic:set>
	<p>
        <h3>Subject indexes (<output:count of="topics"/>)</h3>
        <ul>
	  <logic:foreach name="topics" start="0" max="5">
            <li><a href="<output:link template="topic.jsp?tm=%topicmap%&id=%id%"/>"><output:name /></a></li>
          </logic:foreach>
	</ul>
	</p>

        <!-- ================================================== -->
      
	<logic:set name="assoctypes"><tm:classes of="association"/></logic:set>
        <p>
	<h3>Relationship indexes (<output:count of="assoctypes"/>)</h3>
        <ul>
	  <logic:foreach name="assoctypes" start="0" max="20">
            <li><a href="<output:link template="topic.jsp?tm=%topicmap%&id=%id%"/>"><output:name /></a></li>
          </logic:foreach>
	</ul>
        </p>

        <!-- ================================================== -->
      
	<p>
        <!--
	<logic:set name="baustelle"><value:string>Under Construction</value:string></logic:set>
        <output:content of="baustelle" />
        -->
        </p>

      </logic:context>
      </td></tr></table></p>

      
    <hr />
    <p>
     Testing the new Ontopia Tag Libraries for retrieving topic map information.<br />
<!-- Created: Tue Jul 10 18:33:24 CEST 2001 -->
<!-- hhmts start -->
Last modified: Thu Aug  9 15:19:24 CEST 2001
<!-- hhmts end -->
    <br />
    <% java.util.Date date = new java.util.Date(); %>
    Page generated at <%= date.toString() %>
    </p>
    <address>Author: <a href="mailto:niko@ontopia.net">Niko Schmuck</a></address><br />
  </body>
</html>
