<tolog:context topicmap="i18n.ltm">
Scripts with families
  <tolog:foreach query="direct-instance-of($SCRIPT, script)
                        order by $SCRIPT desc?"
    ><tolog:set var="family"
               query="belongs-to(%SCRIPT% : containee, $FAMILY : container)?"/>
    * <tolog:out var="SCRIPT"
    /><tolog:if var="family">
      (<tolog:out var="family"/>)</tolog:if>
  </tolog:foreach>
</tolog:context>
