<%@ taglib uri='/WEB-INF/jsp/webed-form.tld'     prefix='webed'    %>
<%@ taglib uri='/WEB-INF/jsp/tolog.tld'          prefix='tolog'    %>

<tolog:context topicmap="test.ltm">
  <tolog:set query="topicmap($topicmap)?"/>
  <webed:form actiongroup="testActionGroup">
    <tolog:foreach query='select $T, $N from topic-name($T, $N), value-like($N, "testSubactionTopic")?'>
      testSubactionTopic
      <webed:invoke action="testActionIdSubactionDelete" params="T"/>
    </tolog:foreach>
    <webed:button action="testActionIdSubactionDummy" id="refresh"/>
  </webed:form>
</tolog:context>
