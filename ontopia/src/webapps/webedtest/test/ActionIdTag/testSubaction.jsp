<!--
This test can't be executed automatically due to a problem with Httpunit.
The manual way of testing is as follows:
  load the following page (removing pollution):
  http://localhost:8080/webedtest/test/ActionIdTag/testSubactionForward.jsp
  click the button.
  
  load the following page:
  http://localhost:8080/webedtest/test/ActionIdTag/testSubaction.jsp
  click the left button
  
  observe that the action was executed by looking for the text "testSubactionTopic" in the top-left corner of the page.
  
  load the following page (removing pollution):
  http://localhost:8080/webedtest/test/ActionIdTag/testSubactionForward.jsp
  click the button.
  
  load the following page:
  http://localhost:8080/webedtest/test/ActionIdTag/testSubaction.jsp
  click the right button
  
  observe that the action was not executed by making sure the text "testSubactionTopic" does not appear on the page. -->

<%@ taglib uri='/WEB-INF/jsp/webed-form.tld'     prefix='webed'    %>
<%@ taglib uri='/WEB-INF/jsp/tolog.tld'          prefix='tolog'    %>

<tolog:context topicmap="test.ltm">
  <tolog:set query="topicmap($topicmap)?"/>
  <webed:form actiongroup="testActionGroup">
    <tolog:set var="ltm">[testSubactionTopic = "testSubactionTopic"]</tolog:set>
    <tolog:set var="actionId"><webed:actionid action="testActionIdSubactionDummy" control="button"><webed:invoke action="testActionIdSubaction" params="topicmap ltm"/></webed:actionid></tolog:set>
    <input type="submit" name="<tolog:out var="actionId"/>" id="sub"/>
    <webed:button action="testActionIdSubactionDummy" id="pure-dummy"/>
  </webed:form>
</tolog:context>
