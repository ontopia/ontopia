<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/webed' prefix='webed' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog' %>

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
