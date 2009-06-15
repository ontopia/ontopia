<%@ taglib uri="/WEB-INF/jsp/webed-form.tld" prefix="webed" %>
<%@ taglib uri="/WEB-INF/jsp/tolog.tld" prefix="tolog"     %>

<tolog:context topicmap="test.ltm">
<webed:form actiongroup="testActionGroup">
<webed:button action="dummy" id="ID" text="BUTTON"/>
<webed:field type="textField" action="testNotExclusive" id="NET1">VALUE</webed:field>
<webed:field type="textField" action="testNotExclusive" id="NET2">VALUE</webed:field>
</webed:form>
</tolog:context>
