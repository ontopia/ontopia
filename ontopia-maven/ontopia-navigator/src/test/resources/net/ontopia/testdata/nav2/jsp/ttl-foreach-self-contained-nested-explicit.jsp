<tolog:context topicmap="i18n.ltm">
List all people and their creations:

<tolog:foreach query="instance-of($PERSON, person) order by $PERSON?"
          groupBy="PERSON"
  ><tolog:out var="PERSON"/> created:
    <tolog:foreach query="created-by(%PERSON% : creator, $CREATION : creation)
            order by $CREATION?" groupBy="CREATION"
      >- <tolog:out var="CREATION"/>
    </tolog:foreach
></tolog:foreach>
</tolog:context>
