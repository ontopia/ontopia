<%@ taglib uri="/WEB-INF/jsp/webed-form.tld" prefix="webed" %>
<%@ taglib uri="/WEB-INF/jsp/tolog.tld" prefix="tolog"     %>
<tolog:context topicmap="test.ltm">
  <webed:form actiongroup="testActionGroup">
    <tolog:foreach query="topic-name($TOPIC, $N), value($N, \"testLinkTag\")?">
      <webed:invoke action="delete" params="TOPIC"/>
    </tolog:foreach>
    <webed:button action="dummy" text="Submit"/>
  </webed:form>
</tolog:context>
