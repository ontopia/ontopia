
// $Id: EncryptionUtilsTest.java,v 1.4 2008/05/16 13:57:55 geir.gronmo Exp $

package net.ontopia.utils.test;

import java.io.*;
import java.util.*;

import net.ontopia.utils.*;
import net.ontopia.test.AbstractOntopiaTestCase;

public class EncryptionUtilsTest extends AbstractOntopiaTestCase {

  String baseDir;
  
  public EncryptionUtilsTest(String name) {
    super(name);
  }

  public void setUp() throws IOException {
    String root = getTestDirectory();
    verifyDirectory(root, "various");
    baseDir = root + File.separator + "various";
  }

  public void testVerifyWritten() throws IOException {
    createEncryptedFile("plainTest.jsm", "plainTestEncrypted.jsm");

    assertTrue("Read in file is not like encrypted base line file.",
               compareToBaseline("plainTestEncrypted.jsm", "baseline-plainTestEncrypted.jsm"));
  }

  // --- Internal helper methods

  protected void createEncryptedFile(String in_name, String out_name) throws IOException {
    // create encrypted dummy file
    File in_file = new File(baseDir, in_name);
    File out_file = new File(baseDir, out_name);
    EncryptionUtils.encrypt(in_file, out_file);
  }

  protected boolean compareToBaseline(String out_name, String baseline_name) throws IOException {
    return FileUtils.compare(new File(baseDir, out_name), new File(baseDir, baseline_name));
  }

}
