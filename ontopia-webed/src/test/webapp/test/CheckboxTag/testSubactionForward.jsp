<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/webed' prefix='webed' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog' %>

<tolog:context topicmap="test.ltm">
  <tolog:set query="topicmap($topicmap)?"/>
  <tolog:set var="checked"/>
  <webed:form actiongroup="testActionGroup">
    <tolog:foreach query='select $T, $N from topic-name($T, $N), value($N, "testCheckboxSubactionTopic")?'>
      testCheckboxSubactionTopic
      <webed:invoke action="testCheckboxSubactionDelete" params="T"/>
    </tolog:foreach>
    <tolog:set var="ltm">[testCheckboxSubactionTopic = "testCheckboxSubactionTopic"]</tolog:set>
    <webed:button action="testCheckboxSubactionDummy" text="submit"/>
  </webed:form>
</tolog:context>
