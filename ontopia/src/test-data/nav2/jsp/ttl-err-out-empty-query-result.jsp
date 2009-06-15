<tolog:context topicmap="i18n.ltm">
Show that en empty query result in tolog:out causes an error.
  Try to output an empty query result.
  <tolog:out query="supports($S : supporter, $S : supported)?"/>"
  It shouldn't have worked.
</tolog:context>
