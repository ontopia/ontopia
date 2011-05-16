<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/webed" prefix="webed" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/tolog" prefix="tolog" %>

<tolog:context topicmap="test.ltm">
<tolog:set var="test" query='subject-identifier($T, "http://psi.ontopia.net/test/test-topic")?'/>
<webed:form actiongroup="testActionGroup" lock="test">
 <webed:button action="testLocking" text="Unlock"/>
</webed:form>
</tolog:context>
