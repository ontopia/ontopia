<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/webed" prefix="webed" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/tolog" prefix="tolog" %>

<tolog:context topicmap="test.ltm">
<tolog:set var="checked" query='subject-identifier($topic, "http://psi.ontopia.net/test/no-topic")?' />
<webed:form actiongroup="attributesTest">
 <webed:checkbox action="checkboxTest" id="ID" state="checked"/>
</webed:form>
</tolog:context>
