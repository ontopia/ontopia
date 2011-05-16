<tolog:context topicmap="i18n.ltm">
Scripts with families
  <tolog:foreach query="instance-of($SCRIPT, script),
                        { belongs-to($SCRIPT : containee, $FAMILY : container) }
                        order by $SCRIPT?">
    * <tolog:out var="SCRIPT"/>
      <tolog:if var="FAMILY">(<tolog:out var="FAMILY"/>)</tolog:if>
  </tolog:foreach>  
</tolog:context>
