<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'          prefix='tolog'     %>

<% response.setContentType("text/html; charset=utf-8"); %>

<tolog:context topicmap="i18n.ltm">
<template:insert template='i18n-template.jsp'>
<tolog:set var="direction" reqparam="id"/>

<template:put name="title" body="true">
<tolog:out var="direction"/>
</template:put>

<template:put name="type" body="true">
( a writing direction )
</template:put>


<template:put name="content" body="true">
<%-- DESCRIPTION --%>
<tolog:if query="description(%direction%, $DESC)?">
  <p><tolog:out var="DESC"/></p>
</tolog:if>

<p>
The following scripts use this writing direction.
</p>

<table>
<tr class="top">
    <th>&#160;Name</th>   
    <th>&#160;<a href="script-types.jsp">Type</a></th>   
    <th>&#160;<a href="categories.jsp">Family</a></th></tr>

<tolog:foreach query="writing-direction(%direction% : direction, $SCRIPT : script),
                      object-id($SCRIPT, $SID),
                      direct-instance-of($SCRIPT, $TYPE),
                      object-id($TYPE, $TID),
                      { belongs-to($SCRIPT : containee, $FAMILY : container),
                        object-id($FAMILY, $FID) }
                      order by $SCRIPT?">

<tr> <!-- ROW FOR EACH SCRIPT NAME -->
<td>&#160;
  <a href="script.jsp?id=<tolog:out var="SID"/>"><tolog:out var="SCRIPT"/></a>
  &#160;&#160;&#160;</td>

<td>&#160;
  <a href="script-types.jsp?id=<tolog:out var="TID"/>"><tolog:out var="TYPE"/></a>
  &#160;&#160;&#160;</td>

<td>
  <tolog:if var="FAMILY">&#160;
    <a href="category.jsp?id=<tolog:out var="FID"/>"><tolog:out var="FAMILY"/></a>
  </tolog:if>
</td>
</tr>
</tolog:foreach>

</table>

</template:put>

</template:insert>
</tolog:context>
