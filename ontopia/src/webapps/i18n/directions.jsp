<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'          prefix='tolog'     %>
<% response.setContentType("text/html; charset=utf-8"); %>

<tolog:context topicmap="i18n.ltm">
<template:insert template='i18n-template.jsp'>

<template:put name="title" body="true">
Writing directions
</template:put>

<template:put name="type" body="true">
</template:put>


<template:put name="content" body="true">
<p>
The following directions of writing are known to have been used
at least for one script throughout history.  
</p>

<table>
<tr><th>Direction <th>Scripts using

<tolog:foreach query="select $WD, $WDID, count($SCRIPT) from
                        instance-of($WD, direction),
                        object-id($WD, $WDID),
                        writing-direction($SCRIPT : script, $WD : direction)
                      order by $WD?">
  <tr><td><a href="direction.jsp?id=<tolog:out var="WDID"/>"><tolog:out var="WD"/></a>
          &nbsp;&nbsp;&nbsp;
      <td><tolog:out var="SCRIPT"/>
</tolog:foreach>
</table>

</template:put>

</template:insert>
</tolog:context>
