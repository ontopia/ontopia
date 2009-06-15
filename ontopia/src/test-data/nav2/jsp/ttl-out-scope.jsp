<tolog:context topicmap="i18n.ltm">
  Find all topics with more than one basename.
  Note that in the following listing, uprintable characters come out as question marks.
  <tolog:foreach query="select $TOPIC, $N1, $S1 from 
          topic-name($TOPIC, $N1), scope($N1, $S1), 
          topic-name($TOPIC, $N2), $N1 /= $N2
          order by $TOPIC, $N1, $S1?"
          groupBy="TOPIC">
    The scoped basenames of <tolog:out var="TOPIC"/> are:
    <tolog:foreach>
       "<tolog:out var="N1" scope="S1"/>" in the scope of "<tolog:out var="S1"/>".
    </tolog:foreach>
  </tolog:foreach>
</tolog:context>
