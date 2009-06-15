<tolog:context topicmap="i18n.ltm">
Output the different creations and what types they are:

<tolog:foreach query="select $CREATION from
                      created-by($CREATOR : creator, $CREATION : creation)
                      order by $CREATION?"
  ><tolog:choose
    ><tolog:when query="instance-of(%CREATION%, alphabet)?">
      * <tolog:out var="CREATION" escape="false"/> (alphabet)
    </tolog:when

    ><tolog:when query="instance-of(%CREATION%, syllabary)?">
      * <tolog:out var="CREATION"/> (syllabary)
    </tolog:when

    ><tolog:otherwise>
      * <tolog:out var="CREATION" escape="false"/> (neither alphabet nor syllabary)
    </tolog:otherwise
  ></tolog:choose
></tolog:foreach>
</tolog:context>
