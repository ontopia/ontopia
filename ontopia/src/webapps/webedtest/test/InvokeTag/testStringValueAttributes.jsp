<%@ taglib uri="/WEB-INF/jsp/webed-form.tld" prefix="webed" %>
<%@ taglib uri="/WEB-INF/jsp/tolog.tld" prefix="tolog"     %>

<tolog:context topicmap="test.ltm">
<tolog:set var="value" value="VALUE"/>
<webed:form actiongroup="attributesTest">
<webed:invoke action="invokeTest" value="value" />
</webed:form>
</tolog:context>
