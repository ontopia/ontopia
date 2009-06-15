<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'   prefix='tolog' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>

<% response.setContentType("text/html; charset=utf-8"); %>

<tolog:context topicmap="i18n.ltm">
<template:insert template='i18n-template.jsp'>

<template:put name="title" body="true">
Search for scripts
</template:put>

<template:put name="content" body="true">
<p>
Here you can search for scripts using the form below. The search will
find scripts that comply with <em>all</em> your search criteria. The
search has been implemented using 
<a href="http://www.ontopia.net/topicmaps/materials/tolog.html">tolog</a>.
</p>

<form method=get action=search.jsp>
<table>

<!-- ===== TYPES ==================== -->

<tr><th>Type:
    <td><select name="type">
<option value="any">Any</option>
<tolog:foreach query="{ $TYPE = script |
                        subclass-of(script : superclass, $TYPE : subclass) }
                      order by $TYPE?">
  <option value="<tolog:oid var="TYPE"/>"><tolog:out var="TYPE"/></option>
</tolog:foreach>
</select>

<!-- ===== CATEGORIES ==================== -->

<tr><th>Category:
    <td><select name="category">
<option value="any">Any</option>
<tolog:foreach query="{ instance-of($CAT, script-category) |
                        instance-of($CAT, script-group)    |
                        instance-of($CAT, script-family)   } order by $CAT?">
  <option value="<tolog:oid var="CAT"/>"><tolog:out var="CAT"/></option>
</tolog:foreach>
</select>


<!-- ===== COUNTRIES ==================== -->

<tr><th>Country:
    <td><select name="country">
<option value="any">Any</option>
<tolog:foreach query="instance-of($C, country) order by $C?">
  <option value="<tolog:oid var="C"/>"><tolog:out var="C"/></option>
</tolog:foreach>
</select>


<!-- ===== DIRECTIONS ==================== -->

<tr><th>Writing direction:
    <td><select name="direction">
<option value="any">Any</option>
<tolog:foreach query="instance-of($D, direction) order by $D?">
  <option value="<tolog:oid var="D"/>"><tolog:out var="D"/></option>
</tolog:foreach>
</select>


<tr><td colspan=2><input type=submit value="Search">
</table>
</form>
</template:put>

</template:insert>
</tolog:context>
