<tolog:foreach query="
  instance-of($SECTION, o:section),
  not(o:contained-in($SECTION : o:containee, $PARENT : o:container)),
  o:order($SECTION, $ORDER),
  { o:internal-url($SECTION, $URL) }
  order by $ORDER?
  ">

    <tolog:choose>
      <tolog:when var="URL">
        <p><a href="<tolog:out var="URL"/>"
      </tolog:when>
      <tolog:otherwise>
        <p><a href="section.jsp?id=<tolog:id var="SECTION"/>"
      </tolog:otherwise>
    </tolog:choose>

    <tolog:choose>
      <tolog:when var="topic">
        <tolog:if query="%SECTION% = %section%?">class=topselected</tolog:if>
      </tolog:when>
      <tolog:otherwise>
        <tolog:if query="%SECTION% = %section%?">class=selected</tolog:if> 
      </tolog:otherwise>
    </tolog:choose>
       >
 
       <tolog:choose>
         <tolog:when query="%SECTION% = %section%,
                            o:contained-in($PAGE : o:containee, %SECTION% : o:container),
                            instance-of($PAGE, o:page)?">
           <img src="images/menu-arrow-down.png">
         </tolog:when>
         <tolog:when query="o:contained-in($PAGE : o:containee, %SECTION% : o:container),
                            instance-of($PAGE, o:page)?">
           <img src="images/menu-arrow-right.png">
         </tolog:when>
         <tolog:otherwise>
         </tolog:otherwise>
       </tolog:choose>

       <tolog:out var="SECTION"/></a></p>

  <div class=ider
    <tolog:if query="%SECTION% = %section%?">id=selected</tolog:if>
      > </div>

  <tolog:if query="%SECTION% = %section%?">
    <div class=children>
    <tolog:foreach query="
      o:contained-in($PAGE : o:containee, %SECTION% : o:container),
      instance-of($PAGE, o:page),
      o:order($PAGE, $ORDER)
      order by $ORDER?
    ">
    <p class="child"
      ><a href="page.jsp?id=<tolog:id var="PAGE"/>"
       <tolog:if query="%topic% = %PAGE%?">class=selected</tolog:if>
         ><tolog:out var="PAGE"/></a></p>
  
    <div class=ider
      <tolog:if query="%topic% = %PAGE%?">id=selected</tolog:if>
        > </div>
    </tolog:foreach>
    </div>
  </tolog:if>

</tolog:foreach>
