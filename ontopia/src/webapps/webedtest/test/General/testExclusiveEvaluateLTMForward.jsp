<%@ taglib uri="/WEB-INF/jsp/tolog.tld"          prefix="tolog"    %>
<%@ taglib uri="/WEB-INF/jsp/webed-form.tld"     prefix="webed"    %>
<tolog:context topicmap="test.ltm">
  <tolog:foreach query='topic-name($T, $N)?'>
    <tolog:out var="N"/>
  </tolog:foreach> 
</tolog:context>
