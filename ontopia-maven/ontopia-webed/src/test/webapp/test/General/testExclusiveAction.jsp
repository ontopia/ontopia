<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/webed" prefix="webed" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/tolog" prefix="tolog" %>

<tolog:context topicmap="test.ltm">
<webed:form actiongroup="testActionGroup">
<webed:button action="dummy" id="ID" text="BUTTON"/>
<webed:field type="textField" action="testNotExclusive" id="AT" >VALUE</webed:field>
<webed:field type="textField" action="testExclusive" id="ET" >VALUE</webed:field>
</webed:form>
</tolog:context>
