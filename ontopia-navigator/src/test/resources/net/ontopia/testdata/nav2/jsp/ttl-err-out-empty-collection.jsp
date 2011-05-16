<tolog:context topicmap="i18n.ltm">
Show that en empty collection variable in tolog:out causes an error.
  <tolog:set query="supports($S : supporter, $S : supported)?"/>
  Try to output an empty variable.
  <tolog:out var="S"/>"
  It shouldn't have worked.
</tolog:context>
