<tolog:context topicmap="i18n.ltm">
  Output the (alphabetically) first province:
    
  <tolog:query var="theQuery" query="instance-of($ABUGIDA, abugida) order by $ABUGIDA?"/>
  <tolog:if query="theQuery">
    First reference to first abugida: <tolog:out var="ABUGIDA"/>
  </tolog:if>
  <tolog:if query="theQuery">
    Second reference to first abugida: <tolog:out var="ABUGIDA"/>
    Now list all abugidas:
    <tolog:foreach query="theQuery">
      - Another abugida: <tolog:out var="ABUGIDA"/>
    </tolog:foreach>
    
    And just for the heck of it, let's list them again:
    <tolog:foreach query="theQuery">
      - Again, another abugida: <tolog:out var="ABUGIDA"/>
    </tolog:foreach>
  </tolog:if>
</tolog:context>
