<tolog:context topicmap="i18n.ltm">
Show that an empty groupBy attribute causes an error, even if the tag is correct otherwise.

<tolog:foreach query="instance-of($PERSON, person) order by $PERSON?"
                      groupBy="">
  <tolog:out var="PERSON"/>
</tolog:foreach>

</tolog:context>
