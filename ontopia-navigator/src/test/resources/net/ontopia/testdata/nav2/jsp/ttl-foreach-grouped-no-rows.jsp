<tolog:context topicmap="i18n.ltm">
List all people, their creations and the occurrences of those creations:

  <tolog:foreach query="spoken-in($LANGUAGE : language, $REGION : region) order by $LANGUAGE?"
    groupBy="LANGUAGE">
    <tolog:foreach>
      - <tolog:out var="REGION"/>
    </tolog:foreach>
    <tolog:out var="LANGUAGE"/>
  </tolog:foreach>

</tolog:context>
