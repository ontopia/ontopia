
// $Id: DB2TMTestGenerator.java,v 1.14 2008/07/17 10:50:03 geir.gronmo Exp $

package net.ontopia.topicmaps.db2tm.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.ontopia.test.TestCaseGeneratorIF;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.db2tm.*;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.topicmaps.xml.test.AbstractCanonicalTestCase;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapWriter;

public class DB2TMTestGenerator implements TestCaseGeneratorIF {

  private static final boolean DEBUG_LTM = false; // keep off in CVS
  protected boolean recanonicalizeSource = true;

  /**
   * @return The test cases generated by this.
   */
  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = AbstractCanonicalTestCase.getTestDirectory();
    String base = root + File.separator + "db2tm" + File.separator;

    // Create test cases for each xml config file in 'in'.
    File indir = new File (base + "in" + File.separator);
    File[] infiles = indir.listFiles ();
    if (infiles != null)
      for (int i = 0; i < infiles.length; i++) {
        String name = infiles[i].getName ();
        if (name.endsWith (".xml"))
          tests.add (new GeneralTestCase (name, base));
      }

    // Create rescan test cases for each xml config file in 'in/rescan'.
    File rescanDir = new File (base + "in" + File.separator + "rescan");
    File[] rescanFiles = rescanDir.listFiles ();
    if (rescanFiles != null)
      for (int i = 0; i < rescanFiles.length; i++) {
        String name = rescanFiles[i].getName ();
        if (name.endsWith (".xml"))
          tests.add (new RescanTestCase (name, base));
      }

    // Create error test cases for each xml config file in 'error'.
    File errorDir = new File (base + "error" + File.separator);
    File[] errorFiles = errorDir.listFiles ();
    if (errorFiles != null)
      for (int i = 0; i < errorFiles.length; i++) {
        String name = errorFiles[i].getName ();
        if (name.endsWith (".xml"))
          tests.add (new ErrorTestCase (name, base));
      }

    // Return all the test cases that were generated.
    return tests.iterator();
  }

  // --- Test case class

  public class GeneralTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;

    public GeneralTestCase(String filename, String base) {
      super("testFile");
      this.filename = filename;
      this.base = base;
    }

    public void testFile() throws IOException {
      verifyDirectory(base, "out");

      String name = filename.substring(0, filename.length() - 4);

      // Path to the config file.
      String cfg = base + "in" + File.separator + filename;

      // Path to the topic map seed.
      String in = base + "in" + File.separator + name + ".ltm";
      
      // Path to the cxtm version of the output topic map.
      String cxtm = base + "out" + File.separator + name + ".cxtm";
      
      // Path to the baseline.
      String baseline = base + "baseline" + File.separator + name + ".cxtm";
      
      // Import the topic map seed.
      TopicMapIF topicmap = ImportExportUtils.getReader("file:" + in).read();
      
      // Extend the topic map seed with the the config file.
      DB2TM.add(cfg, topicmap);
      
      // Export the result topic map to ltm, for manual inspection purposes.
      if (DEBUG_LTM) {
        String ltm = base + "out" + File.separator + name + ".ltm";
        (new LTMTopicMapWriter(new FileOutputStream(ltm))).write(topicmap);
      }
      
      // Export the result topic map to cxtm
      FileOutputStream out = new FileOutputStream(cxtm);
      (new CanonicalXTMWriter(out)).write(topicmap);
      out.close();
      
      // Check that the cxtm output matches the baseline.
      assertTrue("The canonicalized conversion from " + filename
          + " does not match the baseline: " + cxtm + " " + baseline, FileUtils.compare(cxtm, baseline));
    }
  }

  public class ErrorTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;

    public ErrorTestCase(String filename, String base) {
      super("testFile");
      this.filename = filename;
      this.base = base;
    }

    public void testFile() throws IOException {
      String name = filename.substring(0, filename.length() - ".xml".length());

      // Path to the config file.
      String cfg = base + "error" + File.separator + filename;

      // Path to the topic map seed.
      String in = base + "error" + File.separator + name + ".ltm";
      String default_in = base + "error" + File.separator + "default.ltm";
      
      // Import the topic map seed.
      TopicMapIF topicmap;
      File infile = new File(in);
      if (infile.exists())
        topicmap = ImportExportUtils.getReader("file:" + in).read();
      else
        topicmap = ImportExportUtils.getReader("file:" + default_in).read();
               
      try {
        // Extend the topic map seed with the the config file.
        DB2TM.add(cfg, topicmap);
        
        fail("The conversion from " + cfg
          + " executed without error. It should have caused an error.");
      } catch (DB2TMException e) {
      } catch (OntopiaRuntimeException e) {
      }
    }
  }

  public class RescanTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;

    public RescanTestCase(String filename, String base) {
      super("testFile");
      this.filename = filename;
      this.base = base;
    }

    public void testFile() throws IOException {
      verifyDirectory(base, "out");

      String name = filename.substring(0, filename.length() - ".xml".length());

      String rescanDir = base + "in" + File.separator + "rescan";
      
      // Path to the config file.
      String cfg = rescanDir + File.separator + filename;

      // Path to the topic map seed.
      String in = rescanDir + File.separator + name + ".ltm";
      
      // Path to the cxtm version of the output topic map.
      String cxtm = base + "out" + File.separator + "rescan-" + name + ".cxtm";
      
      // Path to the baseline.
      String baseline = base + "baseline" + File.separator + "rescan-" + name + ".cxtm";
      
      // Import the topic map seed.
      TopicMapIF topicmap = ImportExportUtils.getReader("file:" + in).read();
      LocatorIF baseloc = topicmap.getStore().getBaseAddress();
      
      // Run DB2TM processes
      RelationMapping mapping = RelationMapping.read(new File(cfg));

      // Prepare files
      File target = new File(rescanDir + File.separator + name + ".csv");
      File before = new File(rescanDir + File.separator + name + "-before.csv");
      File after = new File(rescanDir + File.separator + name + "-after.csv");
      
      // Copy before-file
      FileUtils.copyFile(before, target);
                         
      // Add relations topic topicmap
      Processor.addRelations(mapping, null, topicmap, baseloc);

      // Copy after-file
      FileUtils.copyFile(after, target);
      
      // Rescan relations
      Processor.synchronizeRelations(mapping, null, topicmap, baseloc);

      // Get rid of temporary target file
      FileUtils.deleteFile(target);
      
      // Export the result topic map to ltm, for manual inspection purposes.
      if (DEBUG_LTM) {
        String ltm = base + "out" + File.separator + "rescan-" + name + ".ltm";
        (new LTMTopicMapWriter(new FileOutputStream(ltm))).write(topicmap);
      }
      
      // Export the result topic map to cxtm
      FileOutputStream out = new FileOutputStream(cxtm);
      (new CanonicalXTMWriter(out)).write(topicmap);
      out.close();
      
      // Check that the cxtm output matches the baseline.
      assertTrue("The canonicalized conversion from " + filename
          + " does not match the baseline: " + cxtm + " " + baseline, FileUtils.compare(cxtm, baseline));
    }
  }
  
}
