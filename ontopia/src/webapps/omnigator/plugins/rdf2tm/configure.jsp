<%--

TODO

 - make save.jsp preserve any unknown properties in the file?

 - idea: extend merge plugin to allow manual merging on occurrences?

--%>
<%@ page import="
  java.io.StringWriter,
  java.util.*,
  net.ontopia.utils.*,
  net.ontopia.infoset.core.LocatorIF,
  net.ontopia.topicmaps.core.*,
  net.ontopia.topicmaps.entry.*,
  net.ontopia.topicmaps.nav2.core.*,
  net.ontopia.topicmaps.utils.rdf.*"
%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue'   prefix='tm'        %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output' %>

<%
  String tmid = request.getParameter("tm");
%>

<logic:context tmparam="tm" settm="topicmap">

<template:insert template='/views/template_%view%.jsp'>
<template:put name='title' body='true'>[Omnigator] RDF2TM Mappings</template:put>

<template:put name='heading' body='true'>
  <script language="JavaScript" src="query-samples.js" type="text/javascript">
  </script>
  <h1 class="boxed">RDF2TM Mappings</h1>
</template:put>

<%
UserIF user = (UserIF)session.getAttribute("ontopiaUser");
String skin = user.getSkin();
%>
<template:put name='skin' body='true'>skins/<%= skin %>.css</template:put>

<template:put name='plugins' body='true'>
  <framework:pluginList separator=" | " group="topicmap"/>
</template:put>

<template:put name="navigation" body="true">

<p>This plug-in helps you configure the RDF to Topic Maps mappings in
your Omnigator. Specify the desired mapping for each property using the
selection box and press Confirm to accept proposed and/or changed
mappings.</p>

<p>Each RDF property can be mapped to a characteristic (a basename,
occurrence, or association) or to an identifier (subject identifier,
subject locator, or source locator); or else it can be ignored.</p>

<p>A table of existing mappings (usually <b>mapping.rdff</b> in the
Omnigator's <b>topicmaps</b> directory) is used to record mappings for
later use.</p>

<p>When the Omnigator encounters a property for which no mapping exists
it makes a "best guess" proposal based on the kinds of values exhibited
by the property: Properties whose values are literals are mapped to
occurrences; those whose values are URIrefs or blank nodes are mapped to
associations. These proposed mappings can sometimes be improved as
follows:</p>

<ul>
<li><p>Some properties whose values are literals (e.g. dc:title) are
more appropriately mapped to basenames (or scoped basenames).</p></li>
<li><p>Some properties whose values are URIs (e.g. foaf:homepage) are
more appropriately mapped to (external) occurrences.</p></li>
<li><p>Other properties whose values are URIs (e.g.
skos:subjectIndicator) might be more appropriately mapped to an
identifier.</p></li>
</ul>

<p>Properties are used to type the resulting occurrences and
associations, or to scope basenames. Thus, for example, dc:description
properties mapped to occurrences will result in occurrences of type
dc:description; foaf:knows properties mapped to associations will
result in associations of type foaf:knows; and foaf:nick properties
mapped to scoped basenames will result in basenames scoped by
foaf:nick.</p>

<p>The role types of associations are set to the predefined subjects
rdf2tm:subject and rdf2tm:object (where rdf2tm is the namespace
&lt;http://psi.ontopia.net/rdf2tm/&gt;).</p>

<p>The technical details of this mapping are described in an
<a href="http://www.ontopia.net/topicmaps/materials/rdf2tm.html">Ontopia
Technical Report</a>.</p>

</template:put>

<template:put name="content" body="true">

<%
  String[] alternatives = {"ignore", "basename", "scoped basename",
                           "occurrence", "association",
                           "instance-of", "source-locator", "subject-locator",
                           "subject-identifier"};

  // ---------------------------------------------------------------
  // retrieve configuration
  NavigatorApplicationIF navApp = (NavigatorApplicationIF) application
                                 .getAttribute(NavigatorApplicationIF.NAV_APP_KEY);
  // get the topic map
  TopicMapIF topicmap = navApp.getTopicMapById(tmid);

  // read the mapping file to find all mappings given externally
  TopicMapReferenceIF reference = topicmap.getStore().getReference();
  if (!(reference instanceof RDFTopicMapReference)) {
    response.sendRedirect("error.jsp?summary=Not+an+RDF+topic+map&errormsg=This+is+not+an+RDF+topic+map,+and+so+this+plug-in+cannot+find+the+global+RDF+mapping+file,+nor+perform+any+other+useful+service.&tm=" + request.getParameter("tm"));
    return;
  }

  String file = ((RDFTopicMapReference) reference).getMappingFile();
  if (file == null) {
    response.sendRedirect("error.jsp?summary=No+mapping+file&errormsg=The+source+from+which+this+topic+map+was+read+was+not+configured+with+a+mapping+file+and+so+this+plug-in+does+not+know+where+to+save+the+mapping+and+cannot+continue.&tm=" + request.getParameter("tm"));
    return;
  }

  String fileuri = URIUtils.getURI(file).getAddress();
  Map old_mappings = RDFIntroSpector.getPropertyMappings(fileuri, false);

  // read RDF file to find all properties (and existing mappings)
  LocatorIF base = topicmap.getStore().getBaseAddress();
  if (base == null) {
    response.sendRedirect("error.jsp?summary=No+base+URI&errormsg=The+topic+map+does+not+have+a+base+URI,+which+will+cause+this+page+to+crash.&tm=" + request.getParameter("tm"));
    return;
  }
  Map mappings = RDFIntroSpector.getPropertyMappings(base.getExternalForm(), true,
                                                     old_mappings);

  // sort the properties
  List props = new ArrayList(mappings.keySet());
  Collections.sort(props);
%>

<form method=post action="save.jsp">

<h2 title="Confirm proposed mappings and/or changes to existing mappings"><input type=submit value="Confirm"></h2>

<table width="100%">

<%
  // display the properties
  Iterator it = props.iterator();
  int propix = 0;

  // Make an indentation string of a given (indentWidth) number of spaces.
  int indentWidth = 2;
  String indentString="";
  for (int indi = 0; indi < indentWidth; indi++)
    indentString += "&nbsp;";

  String uriPre0 = "";

  boolean putHeader = false;

  while (it.hasNext()) {
    String uri1 = (String) it.next();
    if (old_mappings.containsKey(uri1)) {
      mappings.remove(uri1); // needed to get count right
      continue; // FIXME: mappings may have been overridden in this file
    }

    if (!putHeader) {
      putHeader = true;
  %>
    <tr><td colspan="2"><h3>Proposed mappings</h3></td></tr>
  <%
    }

    RDFPropertyMapping mapping1 = (RDFPropertyMapping) mappings.get(uri1);
    String mapsto1 = mapping1.getMapsTo();
    int pos1 = mapsto1.lastIndexOf('#');
    mapsto1 = mapsto1.substring(pos1 + 1);

    if (mapping1.getInScope() != null && mapping1.getInScope().equals(uri1) &&
        mapsto1.equals("basename"))
      mapsto1 = "scoped basename";

    // Divide the URI into the parts before and after the hash.
    int splitPoint1 = uri1.indexOf('#');
    if (splitPoint1 == -1)
      splitPoint1 = uri1.lastIndexOf('/');
    splitPoint1++;
    String uriPre1 = uri1.substring(0, splitPoint1),
          uriPost1 = uri1.substring(splitPoint1);


   if (!uriPre0.equals(uriPre1)) { %>
  <tr><td colspan=2><hr/><strong><%= uriPre1 %></strong>
<% }        %>
  <tr><td width="10%"><select name="propmap<%= propix %>">
    <% for (int ix = 0; ix < alternatives.length; ix++) { %>
    <option <%= alternatives[ix].equals(mapsto1) ? "selected" : "" %>
      value="<%=alternatives[ix]%>"><%= alternatives[ix] %>
    </option>
    <% } %>
  </select>
  <input type=hidden name=prop<%= propix %> value="<%= uri1 %>">
  <td><%= indentString + uriPost1 %>
<%
    propix++;
    uriPre0 = uriPre1;
  }
%>

<tr><td colspan="2"><h3>Existing mappings</h3></td></tr>

<%
  // sort the properties
  props = new ArrayList(old_mappings.keySet());
  Collections.sort(props);

  // display the properties
  it = props.iterator();

  uriPre0 = "";
  while (it.hasNext()) {
    String uri1 = (String) it.next();

    RDFPropertyMapping mapping1 = (RDFPropertyMapping) old_mappings.get(uri1);
    String mapsto1 = mapping1.getMapsTo();
    int pos1 = mapsto1.lastIndexOf('#');
    mapsto1 = mapsto1.substring(pos1 + 1);

    if (mapping1.getInScope() != null && mapping1.getInScope().equals(uri1) &&
        mapsto1.equals("basename"))
      mapsto1 = "scoped basename";

    // Divide the URI into the parts before and after the hash.
    int splitPoint1 = uri1.indexOf('#');
    if (splitPoint1 == -1)
      splitPoint1 = uri1.lastIndexOf('/');
    splitPoint1++;
    String uriPre1 = uri1.substring(0, splitPoint1),
          uriPost1 = uri1.substring(splitPoint1);

   if (!uriPre0.equals(uriPre1)) { %>
  <tr><td colspan=2><hr/><strong><%= uriPre1 %></strong>
<% } %>
  <tr>
  <td width="10%"><select name="propmap<%= propix %>">
    <% for (int ix = 0; ix < alternatives.length; ix++) { %>
    <option <%= alternatives[ix].equals(mapsto1) ? "selected" : "" %>
      value="<%=alternatives[ix]%>"><%= alternatives[ix] %>
    </option>
    <% } %>
  </select>
  <input type=hidden name=prop<%= propix %> value="<%= uri1 %>">
  <td><%= indentString + uriPost1 %>
<%
    propix++;

    uriPre0 = uriPre1;
  }

%>
</table>

<hr/>

<input type=hidden name=propcount value=<%= (mappings.size() + old_mappings.size()) %>>
<input type=hidden name=tm value=<%= tmid %>>
<input type=hidden 
       name=mapfile <%-- URI-escaping value to avoid bug #1845 --%>
       value=<%= net.ontopia.utils.StringUtils.replace(file, " ", "+") %>>
<h2 title="Confirm proposed mappings and/or changes to existing
mappings"><input type=submit value="Confirm"></h2>
</form>

</template:put>



    <template:put name='outro' body='true'></template:put>

    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

  </template:insert>
</logic:context>
