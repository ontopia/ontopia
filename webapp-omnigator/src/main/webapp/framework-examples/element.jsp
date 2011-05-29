<%@ page language="java" contentType="text/html" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'   prefix='logic'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'  prefix='output' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value'   prefix='value'  %>

<%-- Demo Page for testing the usage of the element/attribute tags --%>

<html>
  <head>
    <title>Element Test Page</title>
  </head>

  <body>
    <h1>Element Test Page</h1>

    <logic:context>

    <logic:set name="testStr">
	<value:string>TestString</value:string>
    </logic:set>

    <p>
	Output of testStr: <output:content of="testStr"/>
    </p>

    <p>Begin of element tag:
    <output:element name="a">
	<output:attribute name="href">http://www.ontopia.net</output:attribute>
	<output:attribute name="target">_external</output:attribute>
	Link to our homepage.
    </output:element>
    ... the end.
    </p>

    </logic:context>
	
    <hr>
    <address><a href="mailto:niko@ontopia.net">Niko Schmuck</a></address>
<!-- Created: Tue Aug 28 12:34:04 CEST 2001 -->
<!-- hhmts start -->
Last modified: Tue Aug 28 13:22:03 CEST 2001
<!-- hhmts end -->
  </body>
</html>
