<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'         prefix='tolog'   %>
<% response.setContentType("text/html; charset=utf-8"); %>

<tolog:context topicmap="i18n.ltm">

<tolog:set var="tmtopic" query="
  select $TMTOPIC from
    topicmap($TM),
    reifies($TMTOPIC, $TM)?"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<title><tolog:out var="tmtopic"/></title>
<link rel=stylesheet type="text/css" href="tools.css">

<h1><tolog:out var="tmtopic"/></h1>

<p>
This is a site about the scripts of the world, and the languages they
are used to write. The site provides information about the scripts and
languages with links to other sites with in-depth descriptions of
each. The site is a web application developed using 
<a href="http://www.ontopia.net/solutions/navigator.html">the Ontopia Navigator framework</a>, 
based on a topic map written by hand by 
<a href="http://www.garshol.priv.no/">Lars Marius Garshol</a>.
</p>

<p> The site contains information about
<tolog:out query="select count($SCRIPT) from instance-of($SCRIPT, script)?"/>
different scripts, which you can access through several different
indexes. On the right you can see the site structure visualized.
 </p>

<table width="100%"><tr><td valign=top>
<ul>
  <li><a href="scripts.jsp">by script</a>,
  <li><a href="languages.jsp">by language</a>,
  <li><a href="countries.jsp">by country</a>,
  <li><a href="script-types.jsp">by type</a>,
  <li><a href="categories.jsp">by category</a>, 
  <li><a href="transxions.jsp">by transcriptions/translations</a>, or
  <li><a href="directions.jsp">by writing direction</a>.
</ul>

<td align=right>

<map name=diagram>
  <area coords="8,10,135,25"     href="categories.jsp"   alt="by catgory">
  <area coords="115,81,170,96"   href="scripts.jsp"      alt="by script">
  <area coords="20,147,116,161"  href="script-types.jsp" alt="by type">
  <area coords="232,83,303,99"   href="languages.jsp"    alt="by language">
  <area coords="323,121,386,136" href="countries.jsp"    alt="by country">
</map>

<img src="structure.gif" usemap="#diagram" alt="" border="0">
</tr></table>

<p>You can also do a <a href="search-form.jsp">structured search</a>
for the scripts you are interested in, or do a simple full-text search:</p>

<form action="fulltext.jsp" method="get">
<input type=text name=query>
<input type=submit value="Search">
</form>

<p>
If you find errors or omissions you would like to see corrected on
this site I would be very happy to be informed about them. This is
<tolog:out query="version(%tmtopic%, $VERSION)?"/>
of the topic map, last changed on
<tolog:out query="Date(%tmtopic%, $DATE)?"/>
</p>

<p>
The information on this site has mostly been collected from:
</p>

<ul>
  <li><a href="http://www.amazon.com/exec/obidos/ASIN/0195079930">The
  World's Writing Systems</a>, by William Bright and Peter T. Daniels.
  <li><a href="http://www.amazon.com/exec/obidos/ASIN/0804816549">Writing
  Systems of the World</a>, by Akira Nakanishi.
  <li>The Unicode mailing list
</ul>

<table width="100%"><tr><td>
<p>
Some information has also been taken from related sites:
</p>

<ul>
<tolog:foreach query="site-about(script, $URL)?">
  <li><a href="<tolog:out var="URL"/>"><tolog:out var="URL"/></a>
</tolog:foreach>
</ul>

<p>
The errors, however, are all mine.
</p>

<td valign=bottom> <a href="http://www.topicmap.com"><img
src="tm-logo.gif" border=0 align=right></a></table>

<hr>

<address>
<a href="mailto:larsga@garshol.priv.no">Lars Marius Garshol</a>,
<a href="http://www.ontopia.net/">Ontopian</a>.
</address>

</html>
</tolog:context>
