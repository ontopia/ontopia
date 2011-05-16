<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/webed" prefix="webed" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/tolog" prefix="tolog" %>

<tolog:context topicmap="test.ltm">
<webed:form actiongroup="testActionGroup" readonly="true">
  <webed:field type="textField" action="fieldTest" id="FLD"> </webed:field>
</webed:form>
</tolog:context>
