<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/webed" prefix="webed" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/tolog" prefix="tolog" %>
<tolog:context topicmap="test.ltm" readonly="true">
  Topics: {<tolog:if query="select count($T) from topic-name($T, $N), value($N, \"testLinkTag\")?"><tolog:out var="T"/></tolog:if>}
  <webed:form actiongroup="attributesTest">
  </webed:form>
  <webed:form actiongroup="attributesTest" readonly="true">
    <tolog:set query="topicmap($TOPICMAP)?"/>
    <tolog:set var="ltm">[testLinkTag = "testLinkTag"]</tolog:set>
    <tolog:if query="topic($TOPIC) order by $TOPIC?">
      <webed:invoke action="ltm" params="TOPICMAP ltm" readonly="false"/>
    </tolog:if>
    <webed:link href=
        "/webedtest/test/LinkTag/testLinkTag.jsp" action="linkTest"
        readonly="false">
      add topic with name "testLinkTag".
    </webed:link>
  </webed:form>
  <webed:form actiongroup="testActionGroup">
  </webed:form>
</tolog:context>
