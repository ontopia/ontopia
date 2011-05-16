<tolog:context topicmap="i18n.ltm">
Scripts with families
  <tolog:foreach query="instance-of($SCRIPT, script)
                        order by $SCRIPT?">
    * <tolog:out var="SCRIPT"/>
      <tolog:if query="belongs-to(%SCRIPT% : containee, $FAMILY : container)?"
        >(<tolog:out var="FAMILY"/>)</tolog:if>
  </tolog:foreach>  
</tolog:context>
