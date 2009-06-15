<tolog:context topicmap="i18n.ltm">
List all people, their creations and the occurrences of those creations:

<tolog:foreach query="select $PERSON, $CREATION, $OCC, $OCCTYPE from
                      instance-of($PERSON, person), 
                      created-by($PERSON : creator, $CREATION : creation),
                      occurrence($CREATION, $OCC),
                      type($OCC, $OCCTYPE) 
                      order by $PERSON, $CREATION, $OCCTYPE, $OCC?"
                      groupBy="PERSON CREATION">
  <tolog:out var="PERSON"/>
  <tolog:out var="CREATION"/>
  <tolog:foreach
    > - <tolog:out var="OCCTYPE"/> : <tolog:out var="OCC"/>
  </tolog:foreach>
</tolog:foreach>

</tolog:context>
