<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'          prefix='tolog'   %>
<% response.setContentType("text/html; charset=utf-8"); %>

<tolog:context topicmap="i18n.ltm">
<template:insert template='i18n-template.jsp'>

<template:put name="title" body="true">
Transcriptions and transliterations
</template:put>

<template:put name="type" body="true">
</template:put>


<template:put name="content" body="true">

<h2>Transcriptions</h2>

<table>
<tolog:foreach query="  instance-of($TXN, transcription),
                        transforms-from($TXN : method, $SRC : source),
                        transforms-to($TXN : method, $TGT : target)
                      order by $TXN?">

<tr><td><a href="transxion.jsp?id=<tolog:oid var="TXN"/>"><tolog:out var="TXN"/></a>
    <td><a href="language.jsp?id=<tolog:oid var="SRC"/>"><tolog:out var="SRC"/></a>
    <td><a href="script.jsp?id=<tolog:oid var="TGT"/>"><tolog:out var="TGT"/></a>

</tolog:foreach>
</table>


<h2>Transliterations</h2>

<table>
<tolog:foreach query="  instance-of($TXN, transliteration),
                        transforms-from($TXN : method, $SRC : source),
                        transforms-to($TXN : method, $TGT : target)
                      order by $TXN?">

<tr><td><a href="transxion.jsp?id=<tolog:oid var="TXN"/>"><tolog:out var="TXN"/></a>
    <td><a href="script.jsp?id=<tolog:oid var="SRC"/>"><tolog:out var="SRC"/></a>
    <td><a href="script.jsp?id=<tolog:oid var="TGT"/>"><tolog:out var="TGT"/></a>

</tolog:foreach>
</table>

</template:put>

</template:insert>
</tolog:context>
