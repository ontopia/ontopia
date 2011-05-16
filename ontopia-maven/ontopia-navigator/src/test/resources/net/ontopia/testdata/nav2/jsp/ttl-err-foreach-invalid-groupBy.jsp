<tolog:context topicmap="i18n.ltm">
Show that an invalid groupBy attribute causes an error.

<tolog:foreach query="instance-of($PERSON, person) order by $PERSON?"
                      groupBy="nelSON">
  <tolog:out var="PERSON"/>
</tolog:foreach>

</tolog:context>
