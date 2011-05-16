<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template' prefix='template'%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'         prefix='tolog'   %>

<% response.setContentType("text/html; charset=utf-8"); %>

<tolog:context topicmap="i18n.ltm">
<template:insert template='i18n-template.jsp'>

<template:put name="title" body="true">
Scripts by name
</template:put>


<template:put name="content" body="true">
<p>
This page contains all the names of all the scripts on this site,
sorted by name. Note that many scripts have more than one name,
and so will be listed more than once in this list.
</p>

<table>
<tr class="top">
    <th>&#160;Name</th>   
    <th>&#160;<a href="script-types.jsp">Type</a></th>   
    <th>&#160;<a href="categories.jsp">Family</a></th></tr>

<tolog:foreach query="
           instance-of($SCRIPT, script),
           topic-name($SCRIPT, $TNAME),
           not(scope($TNAME, native-name)),
           direct-instance-of($SCRIPT, $TYPE),
           value($TNAME, $NAME),
           object-id($SCRIPT, $SCRIPTID),
           object-id($TYPE, $TYPEID),
           { belongs-to($SCRIPT : containee, $FAMILY : container),
             object-id($FAMILY, $FAMILYID) }
         order by $NAME?">

<tr> <!-- ROW FOR EACH SCRIPT NAME -->
<td>&#160;
  <a href="script.jsp?id=<tolog:out var="SCRIPTID"/>"><tolog:out var="NAME"/></a>
  &#160;&#160;&#160;</td>

<td>&#160;
  <a href="script-types.jsp?id=<tolog:out var="TYPEID"/>"><tolog:out var="TYPE"/></a>
  &#160;&#160;&#160;</td>

<td>
  <tolog:if var="FAMILY">&#160;
  <a href="category.jsp?id=<tolog:out var="FAMILYID"/>"><tolog:out var="FAMILY"/></a>
  </tolog:if>
</td>
</tr>
</tolog:foreach>

</table>

</template:put>

</template:insert>
</tolog:context>
