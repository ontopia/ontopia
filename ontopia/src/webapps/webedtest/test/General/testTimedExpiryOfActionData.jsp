<%@ taglib uri='/WEB-INF/jsp/webed-form.tld'     prefix='webed'    %>
<%@ taglib uri='/WEB-INF/jsp/tolog.tld'          prefix='tolog'    %>

<tolog:context topicmap="test.ltm">
  <tolog:set query="topicmap($topicmap)?"/>
  <webed:form actiongroup="testActionGroup">
    <tolog:set var="topicLTM">
      [testFormUnregister]
    </tolog:set>
    <webed:button action="testTimedExpiryOfActionData" params="topicmap topicLTM"
                  text="testTimedExpiryOfActionData"
                  id="testTimedExpiryOfActionData"/>
  </webed:form>
</tolog:context>
