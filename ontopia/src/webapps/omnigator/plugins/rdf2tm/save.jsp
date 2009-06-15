<%@ page import="
  java.io.*,
  java.util.*,
  org.xml.sax.helpers.*,
  net.ontopia.xml.*,
  net.ontopia.utils.*,
  net.ontopia.infoset.core.LocatorIF,
  net.ontopia.topicmaps.core.*,
  net.ontopia.topicmaps.entry.*,
  net.ontopia.topicmaps.nav2.core.*,
  net.ontopia.topicmaps.utils.rdf.*"
%><%!

  private static void outputProperty(PrettyPrinter pp, String prop, String val,
                                     AttributeListImpl atts)
    throws IOException {

    atts.addAttribute("rdf:resource", "CDATA", val);
    pp.startElement(prop, atts);
    atts.clear();
    pp.endElement(prop);

  }

  private static void outputMapping(PrettyPrinter pp,
                                    RDFPropertyMapping mapping,
                                    AttributeListImpl atts)
    throws IOException {

    atts.addAttribute("rdf:about", "CDATA", mapping.getProperty());
    pp.startElement("rdf:Description", atts);
    atts.clear();

    outputProperty(pp, "rtm:maps-to", mapping.getMapsTo(), atts);

    if (mapping.getInScope() != null)
      outputProperty(pp, "rtm:in-scope", mapping.getInScope(), atts);
    if (mapping.getSubjectRole() != null)
      outputProperty(pp, "rtm:subject-role", mapping.getSubjectRole(), atts);
    if (mapping.getObjectRole() != null)
      outputProperty(pp, "rtm:object-role", mapping.getObjectRole(), atts);
    if (mapping.getType() != null)
      outputProperty(pp, "rtm:type", mapping.getType(), atts);
    
    pp.endElement("rdf:Description");
    
  }

%><%
  // ---------------------------------------------------------------
  // find the file to save to
  String file = request.getParameter("mapfile");
  file = net.ontopia.utils.StringUtils.replace(file, "+", " ");

  // read in the existing mapping
  Map mappings = RDFIntroSpector.getPropertyMappings(URIUtils.getURI(file).getAddress(), false);

  String prefix = "http://psi.ontopia.net/rdf2tm/#";

  // parse request parameters to modify mapping
  int props = Integer.parseInt(request.getParameter("propcount"));
  for (int ix = 0; ix < props; ix++) {
    String prop = request.getParameter("prop" + ix);
    String mapsto = request.getParameter("propmap" + ix);
    if (mapsto == null)
      continue;

    RDFPropertyMapping mapping = (RDFPropertyMapping) mappings.get(prop);
    if (mapping == null) {
      mapping = new RDFPropertyMapping(prop);
      mappings.put(prop, mapping);
    }

    if (mapsto.equals("ignore")) {
      mappings.remove(prop);
      continue;
    }

    if (mapsto.equals("scoped basename")) {
      mapping.setInScope(prop);
      mapsto = "basename";
    }

    if (mapsto.equals("association")) {
      if (mapping.getSubjectRole() == null)
        mapping.setSubjectRole(prefix + "subject");
      if (mapping.getObjectRole() == null)
        mapping.setObjectRole(prefix + "object");
    }

    mapping.setMapsTo(prefix + mapsto);
  }

  // helpers
  AttributeListImpl atts = new AttributeListImpl();

  // walk through the mappings and save them
  Writer outf = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
  PrettyPrinter pp = new PrettyPrinter(outf, "utf-8");
  pp.startDocument();

  atts.addAttribute("xmlns:rdf", "CDATA", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
  atts.addAttribute("xmlns:rtm", "CDATA", prefix);
  pp.startElement("rdf:RDF", atts);
  atts.clear();

  Iterator it = mappings.keySet().iterator();
  while (it.hasNext()) {
    String uri = (String) it.next();
    RDFPropertyMapping mapping = (RDFPropertyMapping) mappings.get(uri);
    outputMapping(pp, mapping, atts);
  }

  pp.endElement("rdf:RDF");
  pp.endDocument();
  outf.close();

  // redirect to TM reload
  String tmid = request.getParameter("tm");
  response.sendRedirect("http://localhost:8080/manage/manage.jsp?action=reload&id=" + tmid + "&redirect=/omnigator/models/topicmap_complete.jsp?tm%3D" + tmid);
%>
