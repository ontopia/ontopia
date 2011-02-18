
package net.ontopia.topicmaps.utils.sdshare.test;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import net.ontopia.test.*;
import net.ontopia.topicmaps.utils.sdshare.client.Fragment;
import net.ontopia.topicmaps.utils.sdshare.client.PushBackend;
import net.ontopia.topicmaps.utils.sdshare.client.SyncEndpoint;

/**
 * This class only tests the list slicing in the top-level method of
 * the PushBackend. It does *not* test the Atom serialization or the
 * actual HTTP posting of data. That's still TODO.
 */
public class PushBackendTest extends AbstractOntopiaTestCase {
  private TestBackend backend;
  private List<Fragment> original;
  
  public PushBackendTest(String name) {
    super(name);
    backend = new TestBackend();
    original = new ArrayList();
  }

  public void testEmpty() {
    backend.applyFragments(null, original);
    check();
  }

  public void test49() {
    makeFragments(49);
    backend.applyFragments(null, original);
    check();
  }

  public void test50() {
    makeFragments(50);
    backend.applyFragments(null, original);
    check();
  }

  public void test51() {
    makeFragments(51);
    backend.applyFragments(null, original);
    check();
  }

  public void test52() {
    makeFragments(52);
    backend.applyFragments(null, original);
    check();
  }
  
  // --- Test utilities

  private void check() {
    List<Fragment> received = backend.getFragments();
    assertEquals("wrong number of fragments after push",
                 original.size(), received.size());

    for (int ix = 0; ix < original.size(); ix++)
      assertEquals("mismatched fragment no " + ix,
                   original.get(ix).getTopicSIs(),
                   received.get(ix).getTopicSIs());
  }
  
  private void makeFragments(int number) {
    for (int ix = 0; ix < number; ix++) {
      Fragment f = new Fragment(null, ix, null);
      f.setTopicSIs(Collections.singleton("" + ix));
      original.add(f);
    }
  }

  // --- Special test backend

  static class TestBackend extends PushBackend {
    private List<Fragment> received;

    public TestBackend() {
      this.received = new ArrayList();
    }

    public List<Fragment> getFragments() {
      return received;
    }

    protected void applyFragments_(SyncEndpoint endpoint, List<Fragment> fragments) {
      for (Fragment f : fragments)
        received.add(f);
    }
  }
}
