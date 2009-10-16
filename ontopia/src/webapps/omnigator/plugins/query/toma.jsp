<%
  String tmid = request.getParameter("tm");
%>

<p>
To search the current topic map, please enter your <b>toma</b> query
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
  } else {
%>
    <option value="">Example queries:</option>
<%
  }
%>
</select>
</p>
<p><b>toma</b> is a language for querying topic maps. It is inspired by 
SQL and based around the concept of path expressions.
For a full description of the language, see <a
href="http://code.google.com/p/ontopia/wiki/TomaImplementation">The introduction to <b>toma</b></a>.</p>
