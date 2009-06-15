<tolog:context topicmap="i18n.ltm">
  Output the (alphabetically) first province:
    <tolog:set query="instance-of($PROVINCE, province) order by $PROVINCE?"/>
    <tolog:out var="PROVINCE"/>        
</tolog:context>
