<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/webed" prefix="webed" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/tolog" prefix="tolog" %>

<tolog:context topicmap="test.ltm">
<tolog:set var="value" value="VALUE"/>
<webed:form actiongroup="attributesTest">
<webed:invoke action="invokeTest" value="value" />
</webed:form>
</tolog:context>
