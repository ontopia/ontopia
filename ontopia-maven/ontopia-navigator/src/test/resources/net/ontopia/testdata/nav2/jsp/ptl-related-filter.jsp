<tolog:context topicmap="i18n.ltm">
<tolog:set var="topic" reqparam="id"/>

<tolog:out var="topic"/>
<portlet:related topic="topic" var="headings"
  filterQuery='
     import "http://psi.ontopia.net/tolog/string/" as str
     select $L from 
       topic-name(%topic%, $N), 
       value($N, $V),
       str:length($V, $L),
       $L = 7
     ?
  '>
  <c:forEach items="${headings}" var="heading">
===== <c:out value="${heading.title}"/>
    <c:forEach items="${heading.children}" var="assoc">
 * <tolog:out var="assoc.player"/>
    </c:forEach>
  </c:forEach>
</portlet:related>

</tolog:context>

