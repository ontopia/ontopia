<tolog:context topicmap="i18n.ltm">
Test if set works when reusing queries.

  <tolog:query name="theQuery">
    direct-instance-of($ABUGIDA, abugida) order by $ABUGIDA?
  </tolog:query
  ><tolog:set var="theVariable" query="theQuery"
  /><tolog:out var="theVariable"/>
</tolog:context>
