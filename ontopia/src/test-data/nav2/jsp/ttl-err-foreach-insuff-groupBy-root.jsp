<tolog:context topicmap="i18n.ltm">
Show that an error occurs when a query is insufficiently grouped.

<tolog:foreach query="instance-of($SCRIPT, script),
                      belongs-to($SCRIPT : containee, $FAMILY : container)
                      order by $SCRIPT?" groupBy="SCRIPT">
  <tolog:out var="SCRIPT"/>
</tolog:foreach>  

</tolog:context>
