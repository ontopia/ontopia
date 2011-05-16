<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/webed" prefix="webed" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/tolog" prefix="tolog" %>

<tolog:context topicmap="test.ltm">
<tolog:set var="empty" />

<webed:form actiongroup="attributesTest">
<webed:list action="listTest" collection="empty" id="ID" />
</webed:form>
</tolog:context>
