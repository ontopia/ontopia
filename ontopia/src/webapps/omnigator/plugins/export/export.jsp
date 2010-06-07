<%@ page 
    import="
    net.ontopia.topicmaps.nav2.core.*,
    net.ontopia.topicmaps.nav2.utils.NavigatorUtils,
    net.ontopia.topicmaps.xml.*,
    net.ontopia.topicmaps.core.*,
    net.ontopia.utils.*,
    net.ontopia.topicmaps.utils.rdf.RDFTopicMapWriter,
    net.ontopia.topicmaps.utils.ltm.LTMTopicMapWriter"
%><% 
  // retrieve configuration
  NavigatorApplicationIF navApp = NavigatorUtils.getNavigatorApplication(pageContext);

  // get request parameters
  String tmid = request.getParameter("tm");
  TopicMapIF tm = navApp.getTopicMapById(tmid);
  try {

    String format = request.getParameter("format");
    String type = request.getParameter("type");
    if (type.equals("xml")) {
      if (format.equals("ltm"))
        type = "text/plain";
      else
        type = "text/xml";
    } else {
      type = "application/octet-stream";
      response.setHeader("Content-disposition", "attachment; filename=" +
                         request.getParameter("filename"));
    }
  
    // Response header: contentType determined by application
    //                  charset always utf-8
    String charset = "utf-8";
    response.setContentType(type + "; charset=" + charset); 
   
    TopicMapWriterIF writer;
    if (format.equals("xtm1")) 
      writer = new XTMTopicMapWriter(out, charset);
    else if (format.equals("xtm2")) 
      writer = new XTM2TopicMapWriter(out, charset);
    else if (format.equals("xtm21")) 
      writer = new XTM21TopicMapWriter(out, charset);
    else if (format.equals("rdf")) 
      writer = new RDFTopicMapWriter(out);
    else if (format.equals("cxtm")) {
      writer = new CanonicalXTMWriter(out);
    } else if (format.equals("ltm")) 
      writer = new LTMTopicMapWriter(out);
    else if (format.equals("tmxml")) 
      writer = new TMXMLWriter(out, charset);
    else
      throw new OntopiaRuntimeException("Unknown export format!");
  
    writer.write(tm);

  } finally {
    navApp.returnTopicMap(tm);
  }
%>
