<tolog:context topicmap="i18n.ltm">
  Output the (alphabetically) first province:
    <tolog:query name="Q">instance-of($PROVINCE, province) order by $PROVINCE?</tolog:query>
    <tolog:set query="Q"/>
    <tolog:out var="PROVINCE"/>        
</tolog:context>
