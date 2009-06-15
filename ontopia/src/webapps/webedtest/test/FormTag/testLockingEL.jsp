<%@ taglib uri="/WEB-INF/jsp/webed-form.tld" prefix="webed" %>
<%@ taglib uri="/WEB-INF/jsp/tolog.tld" prefix="tolog"     %>

<tolog:context topicmap="test.ltm">
<tolog:set var="test" query='subject-identifier($T, "http://psi.ontopia.net/test/test-topic")?' scope="session"/>
<webed:form actiongroup="testActionGroup" lock="test">
</webed:form>
</tolog:context>
