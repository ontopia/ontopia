<%@ taglib uri='/WEB-INF/jsp/webed-form.tld'     prefix='webed'    %>
<%@ taglib uri='/WEB-INF/jsp/tolog.tld'          prefix='tolog'    %>

<tolog:context topicmap="test.ltm">
  <tolog:set query="topicmap($topicmap)?"/>
  <tolog:set var="checked"/>
  <webed:form actiongroup="testActionGroup">
    <tolog:set var="ltm">[testCheckboxSubactionTopic = "testCheckboxSubactionTopic"]</tolog:set>
    <webed:button action="testCheckboxSubactionDummy" text="submit"/>
    <webed:checkbox id="checkbox" state="checked" 
                    action="testCheckboxSubactionDummy">
      <webed:invoke action="testCheckboxSubaction" params="topicmap ltm"/>
    </webed:checkbox>
  </webed:form>
</tolog:context>
