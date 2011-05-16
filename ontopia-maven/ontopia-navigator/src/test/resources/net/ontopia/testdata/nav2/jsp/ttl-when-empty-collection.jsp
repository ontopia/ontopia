<tolog:context topicmap="i18n.ltm">
Show that a 'when' test fails on an empty collection
  <tolog:set query="supports($S : supporter, $S : supported)?"/>
  <tolog:choose>
    <tolog:when var="S">
      Matched when (shouldn't happen).
    </tolog:when>
    <tolog:otherwise>
      Did not match when (good).
    </tolog:otherwise>
  </tolog:choose>
</tolog:context>
