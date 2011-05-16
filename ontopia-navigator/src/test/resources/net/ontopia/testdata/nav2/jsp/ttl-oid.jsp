<tolog:context topicmap="i18n.ltm">
Illustrate the output of a query that returns a one row, one column result.
  
  <tolog:foreach 
          query="direct-instance-of($ABUGIDA, abugida) order by $ABUGIDA?">
    <tolog:out var="ABUGIDA"/> has object id: "<tolog:oid var="ABUGIDA"/>"
    - and symbolic id: "<tolog:id var="ABUGIDA"/>"
  </tolog:foreach>
</tolog:context>
