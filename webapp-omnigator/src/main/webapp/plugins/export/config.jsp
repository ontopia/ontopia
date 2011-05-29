<%@ page language="java" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue'   prefix='tm'        %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output' %>
<framework:response/>

<%-- Export Config Page --%>

<logic:context tmparam="tm" settm="topicmap">

  <template:insert template='/views/template_%view%.jsp'>
    <template:put name='title' body='true'>[Omnigator] Export</template:put>
    <template:put name='head' body='true'>
      <script language="JavaScript">
        function setFilename(form, field, tmid, ext) {
          if (tmid.search(ext+'$') != -1) {
            window.document.forms[form][field].value=tmid;
          } else {
            window.document.forms[form][field].value=tmid+ext;
          }
        }
      </script>
    </template:put>
    <template:put name='heading' body='true'>
      <h1 class="boxed">Export</h1>
    </template:put>

    <template:put name='plugins' body='true'>
      <framework:pluginList separator=" | " group="topicmap"/>
    </template:put>

    <template:put name='navigation' body='true'>
      <p>
      Using this plug-in you can export the current topic map as an
      XML document, in
      <a href="http://www.topicmaps.org/xtm/1.0/">XTM 1.0</a>, 
      <a href="http://www.isotopicmaps.org/sam/sam-xtm/2006-06-19/">XTM 2.0</a>,
      <a href="http://www.itscj.ipsj.or.jp/sc34/open/1378.htm">XTM 2.1</a>, CXTM, TM/XML,
      or <a href="http://www.w3.org/RDF/">RDF</a> syntax, or you can export
      it to the much more readable LTM plain-text format. You can
      choose to either download it or view it in your browser.
      </p>
      <p>
      In addition this plug-in offers the possibility to generate
      a topic map schema from the information found in the current
      topic map. The syntax used for the schema representation is set
      to <a href="../../docs/schema/spec.html">Ontopia's Schema
      Language (OSL)</a>.
      </p>
    </template:put>

    <template:put name='content' body='true'>
      <center>
        <table border="0" cellpadding="2" cellspacing="2">
        <tr><th></th><th>Syntax</th>
            <th></th>
            <th></th><th>Action</th>

        <!-- Export functionality -->
        <form action="export.jsp" method="get" name="export">
        <input type="hidden" name="tm" value="<%= request.getParameter("tm") %>">
        <tr valign="top">
            <td><input type='radio' name='format' value='xtm1'
                 onClick="setFilename('export', 'filename', '<%= request.getParameter("tm") %>', '.xtm')">
            <td>XTM 1.0 syntax
            <td>&nbsp; &nbsp;
            <td><input type='radio' name='type' value='octet' checked='checked'>
            <td>Download
        <tr valign="top">
            <td><input type='radio' name='format' value='xtm2' checked='checked'
                 onClick="setFilename('export', 'filename', '<%= request.getParameter("tm") %>', '.xtm')">
            <td>XTM 2.0 syntax
            <td>&nbsp; &nbsp;
            <td>
            <td>Save as: <input type='text' name='filename' value='<%= request.getParameter("tm") %>.xtm'>
        <tr valign="top">
            <td><input type='radio' name='format' value='xtm21'
                 onClick="setFilename('export', 'filename', '<%= request.getParameter("tm") %>', '.xtm')">
            <td>XTM 2.1 syntax
            <td>&nbsp; &nbsp;
            <td><input type='radio' name='type' value='xml'>
            <td>View
        <tr><td><input type='radio' name='format' value='rdf'
                 onClick="setFilename('export', 'filename', '<%= request.getParameter("tm") %>', '.rdf')">
            <td>RDF/XML syntax
            <td>&nbsp; &nbsp;
            <td colspan='2' align='right'>
        <tr><td><input type='radio' name='format' value='cxtm'
                 onClick="setFilename('export', 'filename', '<%= request.getParameter("tm") %>', '.cxtm')">
            <td>CXTM syntax
            <td>&nbsp; &nbsp;
            <td colspan='2' align='right'>
        <tr><td><input type='radio' name='format' value='ltm'
                 onClick="setFilename('export', 'filename', '<%= request.getParameter("tm") %>', '.ltm')">
            <td>LTM syntax
            <td>&nbsp; &nbsp;
            <td>
        <tr><td><input type='radio' name='format' value='tmxml'
                 onClick="setFilename('export', 'filename', '<%= request.getParameter("tm") %>', '.tmx')">
            <td>TM/XML syntax
            <td>&nbsp; &nbsp;
            <td colspan='2' align='right'><input type='submit' value="Export">
        </form>

        <tr>
          <td colspan='5'>&nbsp;<br/><hr/><br/></td>
        </tr>

        <!-- Generate schema functionality -->
        <form action="generate-schema.jsp" method="get">
        <input type="hidden" name="tm" value="<%= request.getParameter("tm") %>">
        <tr valign="top"><td><input type='radio' name='schema_format' value='osl' checked='checked'>
            <td>OSL syntax
            <td>&nbsp; &nbsp;
            <td><input type='radio' name='schema_type' value='octet' checked='checked'>
            <td>Download
        <tr><td>&nbsp;<td>&nbsp;<td>&nbsp;<td>&nbsp;
            <td>Save as: <input type='text' name='filename' value='<%= request.getParameter("tm") %>.osl'>
        <tr><td>
            <td>
            <td>&nbsp; &nbsp;
            <td><input type='radio' name='schema_type' value='xml'>
            <td>View
        <tr><td colspan='3'></td>
            <td colspan='2' align='right'><input type='submit' value="Generate schema">
        </form>
        </table>
      </center>
    </template:put>

    <template:put name='outro' body='true'></template:put>

    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

  </template:insert>
</logic:context>
