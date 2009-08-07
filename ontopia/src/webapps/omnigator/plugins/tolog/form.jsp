<%@ page
    import="
    net.ontopia.topicmaps.nav2.core.*,
    net.ontopia.topicmaps.nav2.utils.FrameworkUtils"
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
<template:put name='title' body='true'>[Omnigator] Query</template:put>

<template:put name='heading' body='true'>
  <script language="JavaScript" src="query-samples.js" type="text/javascript">
  </script>
  <h1 class="boxed">Query</h1>
</template:put>

<%
UserIF user = FrameworkUtils.getUser(pageContext);
String skin = user.getSkin();
%>
<template:put name='skin' body='true'>skins/<%= skin %>.css</template:put>

<template:put name='plugins' body='true'>
  <framework:pluginList separator=" | " group="topicmap"/>
</template:put>

<template:put name="navigation" body="true">
<form action="query.jsp" method="get" name="queryform">

<p>
To search the current topic map, please enter your <b>tolog</b> query
in the box on the right, or select a query from this list of example
queries:</p>
<p>
<select name="codeexample"
 onChange='javascript:insertExample(this.options[this.selectedIndex].value)'
 tabindex='1'>
<%
  if (tmid.equals("opera.hytm")) {
%>
    <option value="">Example queries:</option>
    <option value="exPuccini">Puccini's operas</option>
    <option value="exPucciniSorted">Puccini's operas (sorted)</option>
    <option value="exShakespeare">Composers inspired by Shakespeare</option>
    <option value="exBornDied">Born and died in the same place</option>
    <option value="exComposers">Most prolific composers</option>
    <option value="exMecca">Cities with the most opera premieres</option>
    <option value="exOperasByPremiereDate">Operas by premiere date (paged)</option>
    <option value="exEnglishTitles">Operas that have English titles</option>
    <option value="exSuicides">Suicides (incomplete data)</option>
    <option value="exNoDramatisPersonae">Operas with no dramatis personae</option>
    <option value="exSettingsByCountry">Settings of operas by country</option>
    <option value="exNaryArias">Arias sung by more than one person</option>
    <option value="exInspiredBy">"Inspired by" as inference rule</option>
    <option value="exBibliography">Subtle bibliography query</option>
    <option value="exRecordings">Audio recordings</option>
<%
  } else if (tmid.equals("opera.xtm")) {
%>
    <option value="">Example queries:</option>
    <option value="exPuccini2">Puccini's operas</option>
    <option value="exPucciniSorted2">Puccini's operas (sorted)</option>
    <option value="exShakespeare2">Composers inspired by Shakespeare</option>
    <option value="exBornDied2">Born and died in the same place</option>
    <option value="exComposers2">Most prolific composers</option>
    <option value="exMecca2">Cities with the most opera premieres</option>
    <option value="exOperasByPremiereDate2">Operas by premiere date (paged)</option>
    <option value="exEnglishTitles2">Operas that have English titles</option>
    <option value="exSuicides2">Suicides (incomplete data)</option>
    <option value="exNoDramatisPersonae2">Operas with no dramatis personae</option>
    <option value="exSettingsByCountry2">Settings of operas by country</option>
    <option value="exNaryArias2">Arias sung by more than one person</option>
    <option value="exInspiredBy2">"Inspired by" as inference rule</option>
    <option value="exBibliography2">Subtle bibliography query</option>
    <option value="exRecordings2">Audio recordings</option>
<%
  } else if (tmid.equals("opera.ltm")) {
%>
    <option value="">Example queries:</option>
    <option value="exPuccini3">Puccini's operas</option>
    <option value="exPucciniSorted3">Puccini's operas (sorted)</option>
    <option value="exShakespeare3">Composers inspired by Shakespeare</option>
    <option value="exBornDied3">Born and died in the same place</option>
    <option value="exComposers3">Most prolific composers</option>
    <option value="exMecca3">Cities with the most opera premieres</option>
    <option value="exOperasByPremiereDate3">Operas by premiere date (paged)</option>
    <option value="exEnglishTitles3">Operas that have English titles</option>
    <option value="exSuicides3">Suicides (incomplete data)</option>
    <option value="exSettingsByCountry3">Settings of operas by country</option>
    <option value="exNaryArias3">Arias sung by more than one person</option>
    <option value="exInspiredBy3">"Inspired by" as inference rule</option>
    <option value="exBibliography3">Subtle bibliography query</option>
    <option value="exRecordings3">Audio recordings</option>
    <option value="exNoDramatisPersonae3">Operas with no dramatis personae</option>
    <option value="exNoVoiceType3">Operas with missing voice types</option>
<%
  } else if (tmid.equals("i18n.ltm")) {
%>
    <option value="">Example queries:</option>
    <option value="exTypeSummary">Script type summary</option>
    <option value="exFamilies">Largest script families</option>
    <option value="exScripts">Most used scripts</option>
<%
  } else if (tmid.equals("xmltools-tm.xtm")) {
%>
    <option value="">Example queries:</option>
    <option value="exStdUsed">Most used standards</option>
<%
  } else if (tmid.equals("TapsasConcerts.xtm")
            |tmid.equals("KanzakisConcerts.rdf")
            |tmid.equals("KanzakisConcerts.rdf*TapsasConcerts.xtm")
            |tmid.equals("TapsasConcerts.xtm*KanzakisConcerts.rdf") ) {
%>
    <option value="">Example queries:</option>
    <option value="exBComposers">"B" composers and their works </option>
    <option value="exConcertsByDate">Concerts by date </option>
    <option value="exNoSortName">Missing sort names </option>
<%
  } else {
%>
    <option value="">Example queries:</option>
    <option value="exOccurrenceTopics">Resources that are both occurrences AND subjects</option>
<%
  }
%>
</select>
</p>
<p><b>tolog</b> is a topic map query language developed by Ontopia (and
also supported by the Open Source topic map engine <a
href="http://www.tm4j.org/">TM4J</a>). It is inspired by
Datalog and based around the concept of predicates (which map to
associations, occurrences and other constructs in topic maps). Topics
can be referenced in numerous ways, including by XML ID and by subject
identifier.* The language also supports inferencing through the use of
inference rules. For a full description, see <a
href="../../docs/query/tutorial.html">The <b>tolog</b> Query Language
Tutorial</a>.</p>

<p>As of version 2.0 of the Ontopia Knowledge Suite (and version 007 of
the Omnigator) tolog supports the complete 
<a href="http://www.isotopicmaps.org/sam/">Topic Maps Data Model (TMDM)</a>
ISO standard and is now an extremely powerful tool
for querying every aspect of a topic map. Some of the new example
queries supplied with the Omnigator demonstrate this very clearly. (Try
for example the "English titles" query in <b><a
href="../../models/topicmap_complete.jsp?tm=opera.ltm">opera.ltm</a></b>,
which finds operas that have base names in the scope "English".)</p>

<p><b>tolog</b> also supports inference rules, which may be defined
separately or embedded in the query itself. Refer to <a
href="../../docs/query/tutorial.html">The <b>tolog</b> Query Language
Tutorial</a> for more details.</p>

<p>Ontopia is participating actively in the development of a standard
<a href="http://isotopicmaps.org/tmql/">Topic Maps Query Language
(TMQL)</a>, which incorporates some features from tolog. Users of
Ontopia's products will be provided with a migration path from
<b>tolog</b> once TMQL has been defined and implemented.</p>

<p>* Note that, although subject identifiers are generally more robust
than XML IDs (i.e., item identifiers), most of the example queries that
are distributed with the Omnigator use the latter because they are
easier to read.</p>

</template:put>

<template:put name="content" body="true">
<table width="100%">
<tr><td>
<b>Query:</b><br>
<font size="+1"><textarea name="query" rows="15" cols="62" tabindex="2"><%= (request.getParameter("query") == null ? "" : request.getParameter("query")) %></textarea></font>

<tr><td>
    <input type=submit value="Search" tabindex="3">
    <input type=reset  value="Reset">
    <input type=radio name="executeQuery" value="normal" checked="checked"> Run query
    <input type=radio name="executeQuery" value="trace"> Show trace
    <input type=radio name="executeQuery" value="analyze"> Analyze query

</table>
<input type=hidden value="<%= tmid %>" name=tm>
</form>
</template:put>



    <template:put name='outro' body='true'></template:put>

    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

  </template:insert>
</logic:context>
