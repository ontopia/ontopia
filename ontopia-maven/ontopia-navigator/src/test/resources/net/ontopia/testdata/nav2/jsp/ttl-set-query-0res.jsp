<tolog:context topicmap="i18n.ltm">
  Set a variable to using an empty query result.
    <tolog:set query="supports($S : supporter, $S : supported)?"/>
  Test if it is bound...
    <tolog:if var="S">
      Something is wrong. 
      T shouldn't be bound, but is bound to <tolog:out var="S"/>
    </tolog:if>
  ...if nothing else was printed, then it wasn't bound (or empty).
</tolog:context>
