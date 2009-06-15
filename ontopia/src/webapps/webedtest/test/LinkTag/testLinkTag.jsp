<%@ taglib uri="/WEB-INF/jsp/webed-form.tld" prefix="webed" %>
<%@ taglib uri="/WEB-INF/jsp/tolog.tld" prefix="tolog"     %>
<tolog:context topicmap="test.ltm">
  Topics: {<tolog:if query="select count($T) from topic-name($T, $N), value($N, \"testLinkTag\")?"><tolog:out var="T"/></tolog:if>}
  <webed:form actiongroup="testActionGroup">
  </webed:form>
  <webed:form actiongroup="testActionGroup">
    <tolog:set query="topicmap($TOPICMAP)?"/>
    <tolog:set var="ltm">[testLinkTag = "testLinkTag"]</tolog:set>
    <tolog:if query="topic($TOPIC) order by $TOPIC?">
      <webed:invoke action="ltm" params="TOPICMAP ltm"/>
    </tolog:if>
    <webed:link href=
        "/webedtest/test/LinkTag/testLinkTag.jsp">
      add topic with name "testLinkTag".
    </webed:link>
  </webed:form>
  <webed:form actiongroup="testActionGroup">
  </webed:form>
</tolog:context>
