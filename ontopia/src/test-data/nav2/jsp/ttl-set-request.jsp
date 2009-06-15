<tolog:context topicmap="i18n.ltm">
  Setting variables from the request.
  tolog:set processes requests correctly if all three values are displayed.
  <tolog:set var="id" reqparam="id"/>
  <logic:foreach name="id">
    <output:name/>
  </logic:foreach>
</tolog:context>
