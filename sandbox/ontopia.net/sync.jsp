<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog'%>
<%@ page language="java" import="
  java.io.FileInputStream,
  java.io.FileOutputStream,
  javax.xml.transform.TransformerFactory,
  javax.xml.transform.Transformer,
  javax.xml.transform.stream.StreamSource,
  javax.xml.transform.stream.StreamResult,
  net.ontopia.utils.DeciderIF,
  net.ontopia.utils.DeciderUtils,
  net.ontopia.topicmaps.core.TopicMapIF,
  net.ontopia.topicmaps.utils.ImportExportUtils,
  net.ontopia.topicmaps.utils.TopicMapSynchronizer,
  net.ontopia.topicmaps.nav2.utils.ContextUtils
"
  contentType="text/html; charset=utf-8"%>
<tolog:context topicmap="ontopia.xtm">
  <tolog:set var="topicmap" query="topicmap($TM)?"/>

<%
  // STEP #1: transform RSS to XTM
  String path = pageContext.getServletContext().getRealPath("resources/rss2xtm.xslt");
  FileInputStream is = new FileInputStream(path);
  FileOutputStream fos = new FileOutputStream("/tmp/ontopia.xtm");
  StreamSource style = new StreamSource(is);
  TransformerFactory factory = TransformerFactory.newInstance();
  StreamSource src = new StreamSource("http://ontopia.wordpress.com/feed/");
  StreamResult result = new StreamResult(fos);

  Transformer trans = factory.newTransformer(style);
  trans.transform(src, result);
  fos.close();

  // STEP #2: load XTM
  TopicMapIF source = ImportExportUtils.getReader("/tmp/ontopia.xtm").read();
  TopicMapIF target = (TopicMapIF) 
    ContextUtils.getSingleValue("topicmap", pageContext);

  // STEP #3: synchronize
  DeciderIF t = DeciderUtils.getTrueDecider();
  String q = 
    "{ instance-of($T, i\"http://psi.ontopedia.net/Blogging/Blog\") | " +
    "  instance-of($T, i\"http://psi.ontopedia.net/Blogging/Post\") }?";
  TopicMapSynchronizer.update(target, q, t,
                              source, q, t);
%>
OK
</tolog:context>