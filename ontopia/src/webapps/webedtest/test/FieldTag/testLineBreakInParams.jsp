<%@ taglib uri="/WEB-INF/jsp/webed-form.tld" prefix="webed" %>
<%@ taglib uri="/WEB-INF/jsp/tolog.tld" prefix="tolog"     %>
<tolog:context topicmap="test.ltm">
  <webed:form actiongroup="testActionGroup">
    <webed:field type="textField" action="testAction" params="param1
                         param2">
    </webed:field>
  </webed:form>
</tolog:context>
