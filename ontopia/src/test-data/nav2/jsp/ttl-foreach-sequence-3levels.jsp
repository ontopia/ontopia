<tolog:context topicmap="i18n.ltm">
List all people, their creations and the occurrences of those creations:

<tolog:foreach query="select $PERSON, $CREATION, $OCC, $OCCTYPE from
                      instance-of($PERSON, person), 
                      created-by($PERSON : creator, $CREATION : creation),
                      occurrence($CREATION, $OCC),
                      type($OCC, $OCCTYPE) 
                      order by $PERSON, $CREATION, $OCCTYPE, $OCC?"
                      groupBy="PERSON">
  <tolog:out var="PERSON"/>
  <tolog:foreach groupBy="CREATION"
    ><tolog:out var="CREATION"/>
    <tolog:foreach
      > - <tolog:out var="OCCTYPE"/> : <tolog:out var="OCC"/>
      <tolog:choose
      ><tolog:when var="sequence-first">Visited first occurrence</tolog:when
      ><tolog:otherwise>Visited occurrence number <tolog:out
              var="sequence-number"/></tolog:otherwise
      ></tolog:choose
      ><tolog:if var="sequence-last">, which was the last occurrence</tolog:if>
    </tolog:foreach>
    <tolog:choose
    ><tolog:when var="sequence-first">Visited first creation</tolog:when
    ><tolog:otherwise>Visited creation number <tolog:out var="sequence-number"
    /></tolog:otherwise
    ></tolog:choose
    ><tolog:if var="sequence-last">, which was the last creation</tolog:if>
  </tolog:foreach>
  <tolog:choose
  ><tolog:when var="sequence-first">Visited first person</tolog:when
  ><tolog:otherwise>Visited person number <tolog:out var="sequence-number"
  /></tolog:otherwise
  ></tolog:choose
  ><tolog:if var="sequence-last">, which was the last person</tolog:if>
</tolog:foreach>

</tolog:context>
