<tolog:context topicmap="i18n.ltm">
Show that tolog:out escapes URLs and variable.
  <tolog:set var="httpAddress" value="http://www.foo.net?bar=two words"/>
  <tolog:out var="httpAddress"/>
</tolog:context>
