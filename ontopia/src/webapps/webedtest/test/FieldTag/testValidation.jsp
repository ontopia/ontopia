<%@ taglib uri="/WEB-INF/jsp/webed-form.tld" prefix="webed" %>
<%@ taglib uri="/WEB-INF/jsp/tolog.tld" prefix="tolog"     %>
<tolog:context topicmap="test.ltm">
  <webed:form actiongroup="testActionGroup">
  </webed:form>
  <webed:form actiongroup="testActionGroup">
  <tolog:set var="pattern">foo|bar</tolog:set>
    <webed:field type="textField" action="dummy" id="field1" pattern="pattern">VALUE</webed:field>
    <webed:field type="textField" action="dummy" id="field2">VALUE</webed:field>
    <webed:link href="/webedtest/test/FieldTag/testValidation.jsp">
      Validate.
    </webed:link>
  </webed:form>
  <webed:form actiongroup="testActionGroup">
  </webed:form>
</tolog:context>
