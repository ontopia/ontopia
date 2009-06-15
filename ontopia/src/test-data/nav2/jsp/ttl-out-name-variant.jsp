<tolog:context topicmap="i18n.ltm">
  <tolog:if query="instance-of($PERSON, person), 
                  topic-name($PERSON, $BASENAME),
                  variant($BASENAME, $VARIANT)?">
    Output the person : <tolog:out var="PERSON"/>
    Output the basename : <tolog:out var="BASENAME"/>
    Output the variant : <tolog:out var="VARIANT"/>
  </tolog:if>
</tolog:context>
