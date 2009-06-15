<%@ taglib uri='/WEB-INF/jsp/webed-form.tld'     prefix='webed'    %>
<%@ taglib uri='/WEB-INF/jsp/tolog.tld'          prefix='tolog'    %>

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
