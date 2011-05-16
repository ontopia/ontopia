<tolog:context topicmap="i18n.ltm">
Show that an error occurs when a nested grouping descendant is insufficiently grouped:

<tolog:foreach query="instance-of($PERSON, person),
        created-by($PERSON : creator, $CREATION : creation),
        occurrence($CREATION, $OCC) order by $PERSON, $CREATION?"
        groupBy="PERSON">
  <tolog:foreach groupBy="CREATION">
    <tolog:out var="CREATION"/>
  </tolog:foreach>
</tolog:foreach>

</tolog:context>
