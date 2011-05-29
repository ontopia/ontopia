
package net.ontopia.topicmaps.impl.rdbms;

public class CanonicalExporterXTMROTests extends CanonicalExporterXTMTests {

  public CanonicalExporterXTMROTests(String root, String filename) {
    super(root, filename);
  }

  protected boolean getExportReadOnly() {
    return true;
  }
  
}





