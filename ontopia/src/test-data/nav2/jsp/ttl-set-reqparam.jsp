<tolog:context topicmap="reification.xtm">
  Setting variables from the request.
  Should display the association type, and the number of roles
  <tolog:set var="assoc" reqparam="id"/>

  <tolog:if var="assoc">
  Association type: <tolog:out query="type(%assoc%, $TYPE)?"/>
  Number of roles: <tolog:out query="select count($ROLE) from
                      association-role(%assoc%, $ROLE)?"/>
  </tolog:if>

</tolog:context>
