<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'  prefix='tolog'      %>
<% response.setContentType("text/html; charset=utf-8"); %>

<tolog:context topicmap="i18n.ltm">
  <tolog:set var="script" reqparam="id"/>
  <tolog:set var="scriptid" query="object-id(%script%, $SCRIPTID)?"/>

<template:insert template='i18n-template.jsp'>

<template:put name="title" body="true">
<tolog:out var="script"/>
</template:put>

<template:put name="type" body="true">
( a script )
</template:put>


<template:put name="content" body="true">

<table>

<tolog:if query="direct-instance-of(%script%, $CLASS),
                 object-id($CLASS, $CLASSID)?">
<tr><th>Type of script:    
    <td><a href="script-types.jsp#id<tolog:out var="CLASSID"/>"><tolog:out var="CLASS"/></a> 
</tolog:if>

<tolog:if query="belongs-to(%script% : containee, $FAMILY : container),
                 object-id($FAMILY, $FAMILYID)?">
<tr><th>Script category:   
    <td><a href="category.jsp?id=<tolog:out var="FAMILYID"/>"><tolog:out var="FAMILY"/></a></td>
</tolog:if>

<tolog:if query="number-of-characters(%script%, $NUM)?">
<tr><th>Number of characters:    
    <td><tolog:out var="NUM"/>
</tolog:if>

<tolog:if query="created-by(%script% : creation, $CREATOR : creator)?">
<tr><th>Created by: <td>
  <tolog:foreach query="created-by(%script% : creation, $CREATOR : creator)?"
                 separator=", "
    ><tolog:out var="CREATOR"/></tolog:foreach>
</tolog:if>

<tolog:if query="deciphered-by(%script% : script, $P : person)?">
<tr><th>Deciphered by: <td>
  <tolog:foreach query="deciphered-by(%script% : script, $P : person)?"
    ><tolog:out var="P"/>, </tolog:foreach>
</tolog:if>

<tolog:if query="period-of-use(%script%, $PERIOD)?">
<tr><th>Period of use:     <td><tolog:out var="PERIOD"/>
</tolog:if>

<tolog:if query="writing-direction(%script% : script, $DIR : direction),
                 object-id($DIR, $DIRID)?">
<tr><th>Writing-direction: 
    <td><a href="direction.jsp?id=<tolog:out var="DIRID"/>"><tolog:out var="DIR"/></a>
</tolog:if>

<tolog:if query="derived-from(%script% : successor, $PARENT : predecessor),
                 object-id($PARENT, $PARENTID)?">
<tr><th>Parent script:     
    <td><a href="script.jsp?id=<tolog:out var="PARENTID"/>"><tolog:out var="PARENT"/></a>
    (<i><a href="ftree.jsp?id=<tolog:out var="scriptid"/>">show family tree</a></i>)
</tolog:if>

<tr><th>Names:             
    <td><tolog:foreach query="topic-name(%script%, $NAME)?" separator=", "
          ><tolog:out var="NAME"/></tolog:foreach>
</table>

<tolog:if query="description(%script%, $DESC)?">
<p><tolog:out var="DESC"/></p>
</tolog:if>


<tolog:if query="written-in(%script% : script, $LANG : language)?">
<p>
Languages written with this script:
</p>

<ul>
<tolog:foreach query="written-in(%script% : script, $LANG : language),
                      object-id($LANG, $LANGID)
                      order by $LANG?">
  <li><a href="language.jsp?id=<tolog:out var="LANGID"/>"
        ><tolog:out var="LANG"/></a></li>
</tolog:foreach>
</ul>
</tolog:if>


<tolog:if query="derived-from(%script% : predecessor, $CHILD : successor)?">
<p>
The following scripts were created based on this script (<i><a
href="ftree.jsp?id=<tolog:out var="scriptid"/>">show family
tree</a></i>):
</p>

<ul>
<tolog:foreach query="derived-from(%script% : predecessor, $CHILD : successor),
                      object-id($CHILD, $CHILDID)
                      order by $CHILD?">
  <li><a href="script.jsp?id=<tolog:out var="CHILDID"/>"><tolog:out var="CHILD"/></a></li>
</tolog:foreach>
</ul>
</tolog:if>

<!-- ===== TRANSXIONS =============================== -->
<tolog:set var="topic" reqparam="id"/>
<jsp:include page="fragments/transxions.jsp"/>

<!-- ===== OCCURRENCES ============================== -->
<jsp:include page="fragments/occurrences.jsp"/>

</template:put>

</template:insert>
</tolog:context>
