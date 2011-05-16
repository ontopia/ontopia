<%@ page 
    import="
    net.ontopia.topicmaps.nav2.core.*,
    net.ontopia.topicmaps.nav2.utils.NavigatorUtils,
    net.ontopia.topicmaps.xml.*,
    net.ontopia.topicmaps.core.*,
    net.ontopia.utils.*,
    java.net.URLEncoder,
    net.ontopia.topicmaps.schema.impl.osl.cmdline.Generate,
    net.ontopia.topicmaps.schema.impl.osl.OSLSchema,
    net.ontopia.topicmaps.schema.impl.osl.OSLSchemaWriter"
%><%
  
  // retrieve configuration
  NavigatorApplicationIF navApp = NavigatorUtils.getNavigatorApplication(pageContext);

  // get request parameters
  String tmid = request.getParameter("tm");
  TopicMapIF tm = navApp.getTopicMapById(tmid);
  try {

    String type = request.getParameter("schema_type");
    if (type.equals("xml")) {
      type = "text/xml";
    } else {
      type = "application/octet-stream";
      response.setHeader("Content-disposition", "attachment; filename=" +
                         request.getParameter("filename"));
    }
  
    // Response header: contentType and charset determined by application
    // String charset = rconfig.getTopicmapConfig().getCharset();
    String charset = "utf-8";
    response.setContentType(type + "; charset=" + charset); 
   
    String format = request.getParameter("schema_format");
    if (format == null || format.equals("")) {
      // no other possibility at the moment
      format = "osl";
    }
    if (!format.equals("osl")) 
      throw new OntopiaRuntimeException("Unknown schema format!");
  
    // generate topic map schema
    Generate gen = new Generate();
    OSLSchema schema = gen.createSchema(tm);
  
    // deliver / write out schema
    OSLSchemaWriter writer;
    writer = new OSLSchemaWriter(out, charset);
    writer.write(schema);

  } finally {
    navApp.returnTopicMap(tm);
  }
%>
