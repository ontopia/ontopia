<%@ taglib uri="/WEB-INF/jsp/webed-form.tld" prefix="webed" %>
<%@ taglib uri="/WEB-INF/jsp/tolog.tld" prefix="tolog"     %>

<tolog:context topicmap="test.ltm">
<tolog:set var="empty" />

<webed:form actiongroup="attributesTest">
<webed:list action="listTest" collection="empty" id="ID" />
</webed:form>
</tolog:context>
