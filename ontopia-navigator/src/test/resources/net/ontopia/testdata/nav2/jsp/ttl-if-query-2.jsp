<tolog:context topicmap="i18n.ltm">
<tolog:if query="created-by(phags-pa : creation, $CREATOR : creator)?">
Created by: 
  <tolog:foreach query="created-by(phags-pa : creation, $CREATOR : creator)?"
                 separator=", "
    ><tolog:out var="CREATOR" escape="false"/></tolog:foreach>
</tolog:if>
</tolog:context>
