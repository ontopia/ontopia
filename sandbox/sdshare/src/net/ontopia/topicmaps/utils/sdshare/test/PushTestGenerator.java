
package net.ontopia.topicmaps.utils.sdshare.test;

import java.io.*;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import org.xml.sax.SAXException;
import net.ontopia.test.*;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.FileUtils;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.utils.sdshare.client.FeedReaders;
import net.ontopia.topicmaps.utils.sdshare.client.SyncEndpoint;
import net.ontopia.topicmaps.utils.sdshare.client.FragmentFeed;
import net.ontopia.topicmaps.utils.sdshare.client.OntopiaBackend;

// HOW IT WORKS

// test cases go

//   initial TM
//   for feed in feeds:
//     push feed
//     CXTM check against baseline

// file pattern

//   test-case-name.ctm
//   test-case-name-1.atom
//   test-case-name-1.cxtm (baseline)
//   test-case-name-1-out.cxtm (output)
//   test-case-name-2.atom
//   test-case-name-2.cxtm (baseline)
//   test-case-name-2-out.cxtm (output)
//   test-case-name-3.atom
//   test-case-name-3.cxtm (baseline)
//   test-case-name-3-out.cxtm (output)
//   ...

/**
 * This class scans the push test directory for test cases, then
 * applies all the fragments to the one snapshot in each directory,
 * testing against CXTM baselines as it goes.
 */
public class PushTestGenerator implements TestCaseGeneratorIF {

  public Iterator generateTests() {
    Set tests = new CompactHashSet();
    String root = AbstractOntopiaTestCase.getTestDirectory();
    String base = root + File.separator + "sdshare" +
      File.separator + "push";
        
    File indir = new File(base);

    File[] infiles = indir.listFiles();
    if (infiles == null)
      return java.util.Collections.EMPTY_SET.iterator();
        
    for (int ix = 0; ix < infiles.length; ix++) {
      if (infiles[ix].getPath().endsWith(".ctm"))
        tests.add(new PushTestCase(infiles[ix]));
    }

    return tests.iterator();
  }
  
  // --- Test case class

  public class PushTestCase extends AbstractOntopiaTestCase {
    private File tmfile;
    private String testcasename;
        
    public PushTestCase(File tmfile) {
      super("testPush");
      this.tmfile = tmfile;
      this.testcasename = tmfile.getName().substring(0, tmfile.getName().length() - 4);
    }
    
    public void testPush() throws IOException, SAXException {
      File base = tmfile.getParentFile();
      TopicMapIF topicmap = ImportExportUtils.getReader(tmfile).read();
      List<File> feeds = scan(base, testcasename);
      Collections.sort(feeds);

      TestBackend backend = new TestBackend(topicmap);
      SyncEndpoint endpoint = new SyncEndpoint(null); // dummy; not used
      for (File feedfile : feeds) {
        FragmentFeed feed = FeedReaders.readPostFeed(new FileReader(feedfile));
        backend.applyFragments(endpoint, feed.getFragments());

        String name = feedfile.getName().substring(0, feedfile.getName().length() - 5);
        // canonicalize
        String out = base.getPath() + File.separator + name + "-out.cxtm";
        FileOutputStream fos = new FileOutputStream(out);
        new CanonicalXTMWriter(fos).write(topicmap);
        fos.close();
        
        // compare with baseline
        assertTrue("topic map canonicalized wrongly after feed " + name,
                   FileUtils.compare(out,
                                     base.getPath() + File.separator +
                                     name + ".cxtm"));
      }
    }

    private List<File> scan(File base, String pattern) throws IOException {
      List<File> files = new ArrayList<File>();
      File[] infiles = base.listFiles();
      for (int ix = 0; ix < infiles.length; ix++)
        if (infiles[ix].getName().startsWith(pattern) &&
            infiles[ix].getName().endsWith(".atom"))
          files.add(infiles[ix]);
      return files;
    }
  }

  // --- Special test backend

  public class TestBackend extends OntopiaBackend {
    private TopicMapIF topicmap;

    public TestBackend(TopicMapIF topicmap) {
      this.topicmap = topicmap;
    }

    protected TopicMapStoreIF getStore(String id) {
      return topicmap.getStore();
    }
  }
}
