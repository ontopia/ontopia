<%
  String tmid = request.getParameter("tm");
%>

<script language="JavaScript" src="query-samples.js" type="text/javascript">
</script>

<p>
To search the current topic map, please enter your <b>tolog</b> query
in the box on the right, or select a query from this list of example
queries:</p>
<p>
<select name="codeexample"
 onChange='javascript:insertExample(this.options[this.selectedIndex].value)'
 tabindex='1'>
<%
  if (tmid.equals("ItalianOpera.ltm")) {
%>
    <option value="">Example queries:</option>
    <option value="exPuccini">Puccini's operas</option>
    <option value="exPucciniSorted">Puccini's operas (sorted)</option>
    <option value="exShakespeare">Composers inspired by Shakespeare</option>
    <option value="exBornDied">Born and died in the same place</option>
    <option value="exComposers">Most prolific composers</option>
    <option value="exMecca">Cities with the most premieres</option>
    <option value="exTheatresByPremiere">Theatres with the most premieres</option>
    <option value="exOperasByPremiereDate">Operas by premiere date (paged)</option>
    <option value="exEnglishTitles">Operas that have English titles</option>
    <option value="exSuicides">Suicides (incomplete data)</option>
    <option value="exSettingsByCountry">Settings of operas by country</option>
    <option value="exNaryArias">Arias sung by more than one person</option>
    <option value="exInspiredBy">"Inspired by" as inference rule</option>
    <option value="exBibliography">Subtle bibliography query</option>
    <option value="exRecordings">Audio recordings</option>
    <option value="exNoDramatisPersonae">Operas with no dramatis personae</option>
    <option value="exNoVoiceType">Operas with missing voice types</option>
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
<p><b>tolog</b> is a language for querying topic maps that is supported by Ontopia,
<a href="http://www.tm4j.org/">TM4J</a> and other Topic Maps engines. It
is inspired by Datalog and based around the concept of predicates (which
map to associations, occurrences and other constructs in topic maps).
Topics can be referenced in numerous ways, including by local ID and by
subject identifier.* The language also supports inferencing through the
use of inference rules, which may be defined separately or embedded in
the query itself. For a full description of the language, see <a
href="../../docs/query/tutorial.html">The <b>tolog</b> Query Language
Tutorial</a>.</p>

<p><b>tolog</b> supports the complete
<a href="http://www.isotopicmaps.org/sam/">Topic Maps Data Model (TMDM)</a>
ISO standard and is an extremely powerful tool
for querying every aspect of a topic map. Some of the example
queries supplied with the Omnigator demonstrate this very clearly. (Try
for example the "English titles" query in <b><a
href="../../models/topicmap_complete.jsp?tm=ItalianOpera.ltm">ItalianOpera.ltm</a></b>,
which finds operas that have base names in the scope "English".)</p>

<p>Ontopia supporters are participating actively in the development of a
standard <a href="http://isotopicmaps.org/tmql/">Topic Maps Query Language
(TMQL)</a>, which incorporates many features from tolog. Users of
Ontopia will be provided with a migration path from
<b>tolog</b> once TMQL has been defined and implemented.</p>

<p>* Note that, although subject identifiers are generally more robust
than XML IDs (i.e., item identifiers), most of the example queries that
are distributed with the Omnigator use the latter because they are
easier to read.</p>
