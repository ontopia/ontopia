<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/webed" prefix="webed" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/tolog" prefix="tolog" %>

<tolog:context topicmap="test.ltm">
<webed:form actiongroup="testActionGroup">
 <tolog:set var="tm" query="topicmap($TM)?"/>
 <tolog:set var="topic" query='subject-identifier($T, "http://psi.ontopia.net/test/test-topic")?'/>
 <tolog:set var="string" value="STRING"/>
 <tolog:set var="empty" />
 <webed:button action="testActionParams" text="Test" params="tm topic string empty"/>
</webed:form>
</tolog:context>
