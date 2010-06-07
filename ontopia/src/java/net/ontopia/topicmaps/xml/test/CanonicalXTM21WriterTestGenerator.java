
//$Id: CanonicalXTM2WriterTestGenerator.java,v 1.4 2008/07/04 10:22:20 lars.garshol Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.*;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import net.ontopia.utils.FileUtils;
import net.ontopia.test.TestCaseGeneratorIF;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.ImportExportUtils;

public class CanonicalXTM21WriterTestGenerator implements TestCaseGeneratorIF {

  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = AbstractCanonicalTestCase.getTestDirectory();
    String base = root + File.separator + "xtm21" + File.separator;
     
    File indir = new File(base + "in" + File.separator);
     
    File[] infiles = indir.listFiles();
    if (infiles == null)
      return Collections.EMPTY_SET.iterator();
     
    for (int i = 0; i < infiles.length; i++) {
      String name = infiles[i].getName();
      if (name.endsWith(".xtm"))
        tests.add(makeTestCase(name, base));
    }

    return tests.iterator();
  }

  protected AbstractCanonicalTestCase makeTestCase(String name, String base) {
    return new CanonicalTestCase(name, base);
  }

  // --- Test case class

  public class CanonicalTestCase extends AbstractCanonicalTestCase {
    protected String base;
    protected String filename;
      
    public CanonicalTestCase(String filename, String base) {
      super("testFile");
      this.filename = filename;
      this.base = base;
    }
  
    public void testFile() throws IOException {
      verifyDirectory(base, "out");
   
      // Path to the input topic map
      String in = base + File.separator + "in" + File.separator + filename;
      // Path to the baseline
      String baseline = base + File.separator + "baseline" + File.separator 
        + filename + ".cxtm";
      // Path to the canonicalized output.
      String out = base + File.separator + "out" + File.separator 
        + "tmp-" + filename + ".cxtm";
      // Path to the temporary file
      String tmp = base + File.separator + "out" + File.separator 
        + "tmp-" + filename;
  
      // Import topic map from arbitrary source.
      TopicMapIF tm = new XTMTopicMapReader(new File(in)).read();
      LocatorIF base = tm.getStore().getBaseAddress();

      // Export to XTM 2.1
      XTMTopicMapWriter writer = new XTMTopicMapWriter(tmp);
      writer.setVersion(XTMVersion.XTM_2_1);
      // Do not omit the item identifiers
      writer.setExportSourceLocators(true);
      writer.write(tm);

      // Import again from exported file
      tm = ImportExportUtils.getReader(tmp).read();

      // Fix item identifiers for canonicalization
      TestUtils.fixItemIds(tm, base);

      // Output CXTM
      FileOutputStream os = new FileOutputStream(out);
      new CanonicalXTMWriter(os).write(tm);
      os.close();
      
      // compare results
      assertTrue("The test file " + filename + " is different from the baseline: " + out + " " + baseline,
                 FileUtils.compare(out, baseline));
    }
  }
}
