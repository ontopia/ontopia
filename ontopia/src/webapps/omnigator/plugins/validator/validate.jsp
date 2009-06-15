<%@ page import="
  java.net.URLEncoder,
  java.io.File,
  java.io.Writer,
  java.io.StringWriter,
  java.util.Iterator,
  java.util.Collection,
  org.xml.sax.Locator,
  net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF,
  net.ontopia.topicmaps.nav2.core.UserIF,
  net.ontopia.topicmaps.nav2.utils.NavigatorUtils,
  net.ontopia.topicmaps.nav2.utils.FrameworkUtils,
  net.ontopia.topicmaps.schema.impl.osl.*,
  net.ontopia.topicmaps.schema.utils.HTMLValidationHandler,
  net.ontopia.topicmaps.core.*,
  net.ontopia.topicmaps.utils.TopicStringifiers,
  net.ontopia.utils.OntopiaRuntimeException,
  net.ontopia.utils.StringifierIF,
  net.ontopia.utils.StringUtils,
  net.ontopia.topicmaps.schema.core.*
  "
%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%!
class MVSValidationHandler extends HTMLValidationHandler {
  private String tmid;
  private String model;

  public MVSValidationHandler(Writer out, String tmid, String model) {
    super(out);
    this.tmid = tmid;
    this.model = model;
  }

  // --- overwrite method for creating link to topic

  protected String printTopic(TopicIF topic) {
    if (topic == null)
      return "&lt;null>";

    String name = stringifier.toString(topic);
    if (name.equals("[No name]"))
      name = topic.toString();

    String uri = "../../models/topic_" + model + ".jsp?tm=" + tmid +
                 "&amp;id=" + topic.getObjectId();

    return "<a href=\"" + uri + "\">" + name + "</a>";
  }
}
%>

<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'     %>
<logic:context tmparam="tm">

<%
// ---------------------------------------------------------------
NavigatorApplicationIF navApp = NavigatorUtils.getNavigatorApplication(pageContext);
%>

<template:insert template='/views/template_%view%.jsp'>
  <template:put name='title' body='true'>[Omnigator] Validation results</template:put>

  <template:put name='plugins' body='true'>
    <framework:pluginList separator=" | " group="topicmap"/>
  </template:put>

  <template:put name='heading' body='true'>
    <h1 class="boxed">Validation results</h1>
    <style><!-- th { text-align: left; vertical-align: top }
                td { vertical-align: top } --></style>
  </template:put>

  <template:put name='navigation' body='true'>
    <p>On the right-hand side are shown the results of validating this
    topic map against its schema.</p>
  </template:put>

<template:put name="content" body="true">
<%
  StringWriter writer = new StringWriter();
  String tmid = request.getParameter("tm");
  TopicMapIF tm = navApp.getTopicMapById(tmid);
  try {

    UserIF user = FrameworkUtils.getUser(pageContext);
    String model = user.getModel();

    SchemaIF schema = null;
    String schemaloc = application.getRealPath("WEB-INF/schemas/" + tmid + ".osl");
    try {
      OSLSchemaReader reader = new OSLSchemaReader(new File(schemaloc));
      schema = reader.read();
    } catch (java.io.FileNotFoundException e) {
      writer.write("<p>No schema available for this topic map (expected location: " +
                   "<tt>" + schemaloc + "</tt>).</p>");
    } catch (java.io.IOException e) {
      writer.write("<p><b>Error:</b> " + e.getMessage() + "</p>");
    } catch (SchemaSyntaxException e) {
      writer.write("<p><b>Schema syntax error:</b> " +
                   StringUtils.escapeHTMLEntities(e.getMessage()) + "</p>");
      Locator loc = e.getErrorLocation();
      writer.write("<p>Location: " + loc.getSystemId() + ":" +
                   loc.getLineNumber() + ":" + loc.getColumnNumber() + "</p>");
    }

    if (schema != null) {
      SchemaValidatorIF validator = schema.getValidator();
      MVSValidationHandler handler = new MVSValidationHandler(writer, tmid, model);
      validator.setValidationHandler(handler);
      validator.validate(tm);

      int errors = handler.getErrors();
      if (errors == 0)
        writer.write("<p><b>Congratulations! You have no errors in your topic map!</b></p>");
      else if (errors == 1)
        writer.write("<p>1 error. (Nearly there!)</p>");
      else if (errors < 10)
        writer.write("<p>" + errors + " error(s). (Getting close!)</p>");
      else
        writer.write("<p>" + errors + " error(s).</p>");
    }

  } finally {
    navApp.returnTopicMap(tm);
  }
%>

<%= writer.toString() %>
</template:put>

<%-- ============== Outsourced application wide standards ============== --%>
<template:put name='application' content='/fragments/application.jsp'/>
<template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
<template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>
</template:insert>
</logic:context>