<%@ taglib uri="/WEB-INF/jsp/webed-form.tld" prefix="webed" %>
<%@ taglib uri="/WEB-INF/jsp/tolog.tld" prefix="tolog"     %>

<tolog:context topicmap="test.ltm">
<webed:form actiongroup="attributesTest" readonly="true">
 <webed:field type="textField" action="buttonTest" id="ID"/>
</webed:form> 
</tolog:context>
