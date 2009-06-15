<%@ taglib uri="/WEB-INF/jsp/webed-form.tld" prefix="webed" %>
<%@ taglib uri="/WEB-INF/jsp/tolog.tld" prefix="tolog"     %>

<tolog:context topicmap="test.ltm">
<webed:form actiongroup="attributesTest" readonly="true">
 <webed:invoke action="buttonTest" readonly="false"/>
</webed:form> 
</tolog:context>
