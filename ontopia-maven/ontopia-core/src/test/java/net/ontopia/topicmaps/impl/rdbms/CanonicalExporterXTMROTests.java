
// $Id: CanonicalExporterXTMTests.java,v 1.8 2004/11/19 12:52:47 grove Exp $

package net.ontopia.topicmaps.impl.rdbms;

public class CanonicalExporterXTMROTests extends CanonicalExporterXTMTests {

  public CanonicalExporterXTMROTests(String root, String filename) {
    super(root, filename);
  }

  protected boolean getExportReadOnly() {
    return true;
  }
  
}





