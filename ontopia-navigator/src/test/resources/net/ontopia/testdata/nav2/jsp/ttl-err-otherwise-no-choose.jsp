<tolog:context topicmap="i18n.ltm">
Show that a 'when' test fails on an empty collection
  <tolog:set query="supports($S : supporter, $S : supported)?"/>
  <tolog:otherwise>
    Got into otherwise (shouldn't happen).
  </tolog:otherwise>
</tolog:context>
