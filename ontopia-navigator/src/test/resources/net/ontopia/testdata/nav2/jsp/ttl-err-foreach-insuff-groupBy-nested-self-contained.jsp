<tolog:context topicmap="i18n.ltm">
Show that an error occurs when a nested (but self contained) query is insufficiently grouped:

<tolog:foreach query="instance-of($PERSON, person) order by $PERSON?">
  <tolog:foreach query="created-by(%PERSON% : creator, $CREATION : creation),
          occurrence($CREATION, $CREOCC) order by $CREATION?" 
          groupBy="CREATION">
        <tolog:out var="CREATION"/>
  </tolog:foreach>
</tolog:foreach>

</tolog:context>
