<tolog:context topicmap="i18n.ltm">
Show that an error occurs when leaving out the query attribute and outer query has no grouping:

<tolog:foreach query="instance-of($PERSON, person)?">
  <tolog:out var="PERSON"/>
  <tolog:foreach>
    <tolog:out var="CREATION"/>
  </tolog:foreach>
</tolog:foreach>

</tolog:context>
