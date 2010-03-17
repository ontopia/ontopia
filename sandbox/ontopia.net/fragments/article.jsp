<%@ include file="declarations.jsp"%>

  <p>
    <tolog:choose>
      <tolog:when var="URL">
        <b><a href="<tolog:out var="URL"/>"
            ><tolog:out var="ARTICLE"/></a></b>
      </tolog:when>
      <tolog:otherwise>
        <b><a href="..."
            ><tolog:out var="ARTICLE"/></a></b>
      </tolog:otherwise>
    </tolog:choose>

     <nobr><span class=byline>
     <tolog:if query="o:author-of(%ARTICLE% : o:work, $AUTHOR : o:author)?">
       | By
     </tolog:if>

     <tolog:foreach query="o:author-of(%ARTICLE% : o:work, $AUTHOR : o:author)?">
       <tolog:choose>
         <tolog:when var="sequence-first"> </tolog:when>
         <tolog:otherwise> and </tolog:otherwise>
       </tolog:choose>
       <tolog:out var="AUTHOR"/>
     </tolog:foreach>

     <tolog:if var="SIZE">
       | <tolog:out var="FORMAT"/> <tolog:out var="SIZE"/>
     </tolog:if>

     </span></nobr>
     <br>
  <tolog:out var="DESC"/></p>
