<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog' %>

<tolog:context topicmap="xmltools-tm.xtm">
<title>The standards</title>
<h1>The standards</h1>

<p>
Here you can find all the standards supported or used by the tools
on this site:
</p>

<ul>
  <tolog:foreach query="instance-of($STD, TMTT_Standard) order by $STD?">
    <li><a href="standard.jsp?id=<tolog:oid var="STD"/>"><tolog:out var="STD"/></a></li>
  </tolog:foreach>
</ul>

</tolog:context>
