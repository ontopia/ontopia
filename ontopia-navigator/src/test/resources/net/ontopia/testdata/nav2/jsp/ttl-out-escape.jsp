<tolog:context topicmap="mini.ltm">
Test that escaping of characters works correctly in tolog:out:

<tolog:if query="type($WEBSITE, website), type($SOMETHING, something)?">
  <tolog:out var="SOMETHING"/>
  <tolog:out var="WEBSITE"/>
</tolog:if>

</tolog:context>
