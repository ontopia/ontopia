<tolog:context topicmap="i18n.ltm">
  Output the (alphabetically) first province:
    <tolog:set query="instance-of($PROVINCE, province) order by $PROVINCE?"/>
    <tolog:set query="instance-of($PROVINCE1, province) order by $PROVINCE1?"/>
    Province: <tolog:out var="PROVINCE"/>        
    Province1: <tolog:out var="PROVINCE1"/>        
</tolog:context>
