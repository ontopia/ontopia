<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/webed' prefix='webed' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog' %>

<tolog:context topicmap="test.ltm">
  <tolog:set query="topicmap($topicmap)?"/>
  <webed:form actiongroup="testActionGroup">
    <tolog:set var="topicLTM">
      [testFormUnregister]
    </tolog:set>
    <webed:button action="testFormUnregister" params="topicmap topicLTM"
                  text="testFormUnregister" id="testFormUnregister"/>
  </webed:form>
</tolog:context>
