<%@ taglib uri="/WEB-INF/jsp/webed-form.tld" prefix="webed" %>
<%@ taglib uri="/WEB-INF/jsp/tolog.tld" prefix="tolog"     %>

<tolog:context topicmap="test.ltm">
<tolog:set var="value" value="SUB-ACTION"/>
<webed:form actiongroup="testActionGroup">
<webed:button action="dummy" id="SA" text="SubAction">
 <webed:invoke action="subAction" params="value"/>
</webed:button>
<webed:button action="noSubAction" id="NSA" text="NO-SUB-ACTION"/>
</webed:form>
</tolog:context>
