<tolog:context topicmap="i18n.ltm"
><tolog:set var="lang" reqparam="id"/>
Chinese is spoken in:

<tolog:query name="languageQuery">
  { spoken-in(%lang% : language, $PLACE : country) |
    spoken-in(%lang% : language, $PLACE : province) },
  object-id($PLACE, $PID)
  order by $PLACE?
</tolog:query
><tolog:foreach query="{ spoken-in(%lang% : language, $PLACE : country) |
                        spoken-in(%lang% : language, $PLACE : province) },
                      object-id($PLACE, $PID)
                      order by $PLACE?"
  ><tolog:choose
    ><tolog:when query="instance-of(%PLACE%, country)?">
      * <tolog:out var="PLACE"/> (country)
    </tolog:when

    ><tolog:otherwise>
      * <tolog:out var="PLACE"/> (province)
    </tolog:otherwise
  ></tolog:choose
></tolog:foreach>
</tolog:context>
