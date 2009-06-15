<%@ taglib uri="/WEB-INF/jsp/tolog.tld"          prefix="tolog"    %>
<%@ taglib uri="/WEB-INF/jsp/webed-form.tld"     prefix="webed"    %>
<tolog:context topicmap="test.ltm">
  <tolog:set query="topicmap($TOPICMAP)?"/>
  <webed:form actiongroup="testActionGroup">
    <tolog:set var="createGroupLTM">
      [%new% : role = "%testExcludedAction%"]
    </tolog:set>
    <tolog:set var="NAME" value="The name"/>
    <webed:button action="testExclusiveEvaluateLTM"
                  params="TOPICMAP createGroupLTM" text="Create New Group"
                  id="button">
    </webed:button>
    <webed:field action="testExcludedAction" type="textField" params="NAME"
                 id="field">
      <tolog:out var="NAME"/>
    </webed:field><br>
  </webed:form>
</tolog:context>
