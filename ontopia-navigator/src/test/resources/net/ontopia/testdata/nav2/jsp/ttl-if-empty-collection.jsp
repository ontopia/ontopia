<tolog:context topicmap="i18n.ltm">
Output no more (because the if test should fail on empty collection)...
  <tolog:set query="supports($S : supporter, $S : supported)?"/>
  <tolog:if var="S">
    Found an "S". It is "<tolog:out var="S"/>".
  </tolog:if>
</tolog:context>
