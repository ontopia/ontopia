<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/webed" prefix="webed" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/tolog" prefix="tolog" %>

<tolog:context topicmap="test.ltm">
 <tolog:set var="value" query='subject-identifier($T, "http://psi.ontopia.net/test/test-topic")?'/>
<webed:form actiongroup="attributesTest">
<webed:invoke action="invokeTest" value="value"/>
</webed:form>
</tolog:context>
