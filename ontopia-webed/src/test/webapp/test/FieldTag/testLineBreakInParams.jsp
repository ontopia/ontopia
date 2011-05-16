<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/webed" prefix="webed" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/tolog" prefix="tolog" %>
<tolog:context topicmap="test.ltm">
  <webed:form actiongroup="testActionGroup">
    <webed:field type="textField" action="testAction" params="param1
                         param2">
    </webed:field>
  </webed:form>
</tolog:context>
