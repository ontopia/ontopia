<tolog:context topicmap="i18n.ltm">
Chinese is spoken in:

<tolog:foreach query="{ spoken-in(chinese-l : language, $PLACE : country) |
                        spoken-in(chinese-l : language, $PLACE : province) },
                      object-id($PLACE, $PID)
                      order by $PLACE?"
  ><tolog:choose
    ><tolog:when query="instance-of(%PLACE%, country)?">
      * <tolog:out var="PLACE"/> (country)
    </tolog:when

    ></tolog:choose
></tolog:foreach>
</tolog:context>
