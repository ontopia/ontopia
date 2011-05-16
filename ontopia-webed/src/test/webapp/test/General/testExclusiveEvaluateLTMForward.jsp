<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/tolog" prefix="tolog" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/webed" prefix="webed" %>
<tolog:context topicmap="test.ltm">
  <tolog:foreach query='topic-name($T, $N)?'>
    <tolog:out var="N"/>
  </tolog:foreach> 
</tolog:context>
