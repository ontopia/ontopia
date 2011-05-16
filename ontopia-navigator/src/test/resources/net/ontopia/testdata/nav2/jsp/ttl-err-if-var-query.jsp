<tolog:context topicmap="i18n.ltm">
Show that using both a variable and a query attribute in in tolog:if tag causes an error:

<tolog:if query="created-by(phags-pa : creation, $CREATOR : creator)?"
        var="FOO">
  <tolog:out var="CREATOR"/>
</tolog:if>
</tolog:context>
