<tolog:context topicmap="i18n.ltm">
Show that grouping by (groupBy) columns that the result hasn't been ordered by (order by) causes an error:

<tolog:foreach query="select $PERSON, $CREATION, $OCC, $OCCTYPE from
                      instance-of($PERSON, person), 
                      created-by($PERSON : creator, $CREATION : creation),
                      occurrence($CREATION, $OCC),
                      type($OCC, $OCCTYPE) 
                      order by $PERSON?"
                      groupBy="PERSON">
  <tolog:out var="PERSON"/>
  <tolog:foreach groupBy="CREATION">
    <tolog:out var="CREATION"/>
    <tolog:foreach>
      - <tolog:out var="OCCTYPE"/> : <tolog:out var="OCC"/>
    </tolog:foreach>
  </tolog:foreach>
</tolog:foreach>

</tolog:context>
