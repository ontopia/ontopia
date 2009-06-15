<html>
<tolog:context topicmap="i18n.ltm">
i18n.ltm languages:

  <tolog:foreach query="direct-instance-of($O, language) order by $O?">
    <tolog:out var="O"/>
  </tolog:foreach>
</tolog:context>

<tolog:context topicmap="opera.ltm">
opera.ltm operas:

  <tolog:foreach query="direct-instance-of($O, opera) order by $O?">
    <tolog:out var="O"/>
  </tolog:foreach>
</tolog:context>
</html>
