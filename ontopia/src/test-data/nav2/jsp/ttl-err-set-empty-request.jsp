<tolog:context topicmap="i18n.ltm">
  tolog:set should work, but produce an empty collection.
  tolog:out should fail.
  <tolog:set var="id" reqparam="id"/>
  <tolog:out var="id"/>
</tolog:context>
