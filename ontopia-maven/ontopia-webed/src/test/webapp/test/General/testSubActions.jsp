<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/webed" prefix="webed" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/tolog" prefix="tolog" %>

<tolog:context topicmap="test.ltm">
<tolog:set var="value" value="SUB-ACTION"/>
<webed:form actiongroup="testActionGroup">
<webed:button action="dummy" id="SA" text="SubAction">
 <webed:invoke action="subAction" params="value"/>
</webed:button>
<webed:button action="noSubAction" id="NSA" text="NO-SUB-ACTION"/>
</webed:form>
</tolog:context>
