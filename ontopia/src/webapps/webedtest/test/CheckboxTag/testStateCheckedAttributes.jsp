<%@ taglib uri="/WEB-INF/jsp/webed-form.tld" prefix="webed" %>
<%@ taglib uri="/WEB-INF/jsp/tolog.tld" prefix="tolog"     %>

<tolog:context topicmap="test.ltm">
<tolog:set var="checked" query='subject-identifier($topic, "http://psi.ontopia.net/test/test-topic")?' />
<webed:form actiongroup="attributesTest">
 <webed:checkbox action="checkboxTest" id="ID" state="checked"/>
</webed:form>
</tolog:context>
