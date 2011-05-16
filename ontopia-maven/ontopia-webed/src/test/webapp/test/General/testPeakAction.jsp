<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/webed" prefix="webed" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/tolog" prefix="tolog" %>

<tolog:context topicmap="test.ltm">
<webed:form actiongroup="testActionGroup">
<webed:button action="peakAction" id="BTN" text="Peak"/>
<webed:field type="textField" action="dummy" id="FLD">VALUE</webed:field>
</webed:form>
</tolog:context>