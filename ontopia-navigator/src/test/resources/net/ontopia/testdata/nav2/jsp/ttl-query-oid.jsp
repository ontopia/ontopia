<tolog:context topicmap="i18n.ltm">
Test if oid works when reusing queries.

  <tolog:query name="theQuery">
    direct-instance-of($ABUGIDA, abugida) order by $ABUGIDA?
  </tolog:query
  ><tolog:if query="theQuery"
  >
  <tolog:out query="theQuery"
  /> has object id: "<tolog:oid
  query="theQuery"
  />" - and symbolic id: "<tolog:id
  query="theQuery"
  />"
  </tolog:if>
</tolog:context>
