<tolog:context topicmap="i18n.ltm">
List all people, their creations and the occurrences of those creations:

<tolog:foreach query="select $PERSON, $CREATION, $OCC, $OCCTYPE from
                      instance-of($PERSON, person), 
                      created-by($PERSON : creator, $CREATION : creation),
                      occurrence($CREATION, $OCC),
                      type($OCC, $OCCTYPE) 
                      order by $PERSON, $CREATION, $OCCTYPE, $OCC limit 1?"
                      groupBy="PERSON">
  <tolog:if var="sequence-first">FIRST PERSON</tolog:if
  ><tolog:if var="sequence-last"> LAST PERSON</tolog:if
  > person# <tolog:out var="sequence-number"/>
  <tolog:out var="PERSON"
  
  /><tolog:foreach groupBy="CREATION">
    <tolog:if var="sequence-first">FIRST CREATION</tolog:if
    ><tolog:if var="sequence-last"> LAST CREATION</tolog:if
    > CREATION# <tolog:out var="sequence-number"/>
    <tolog:out var="CREATION"
    
    /><tolog:foreach>
      - <tolog:out var="OCCTYPE"/><tolog:out var="OCC"/>
      <tolog:if var="sequence-first">first occurrence</tolog:if
      ><tolog:if var="sequence-last"> last occurrence</tolog:if
      > occurrence# <tolog:out var="sequence-number"/>
    </tolog:foreach>
    <tolog:if var="sequence-first">first creation</tolog:if
    ><tolog:if var="sequence-last"> last creation</tolog:if
    > creation# <tolog:out var="sequence-number"/>
  </tolog:foreach>
    <tolog:if var="sequence-first">first person</tolog:if
    ><tolog:if var="sequence-last"> last person</tolog:if
    > person# <tolog:out var="sequence-number"/>
</tolog:foreach>

</tolog:context>
