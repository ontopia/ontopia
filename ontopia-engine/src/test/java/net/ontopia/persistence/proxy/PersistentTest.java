
package net.ontopia.persistence.proxy;

import junit.framework.TestCase;

public class PersistentTest extends TestCase {
  
  public PersistentTest(String name) {
    super(name);
  }
    
  // --- Test cases

  class DummyPersistent extends AbstractRWPersistent {
    public int _p_getFieldCount() {
      return 5;
    }
    public void detach() {
    }
  }
  
  public void testDirtyFlags() {
    PersistentIF dummy = new DummyPersistent();
    //! assertTrue("object is dirty when it shouldn't", !dummy.isDirty());
    //! assertTrue("object field is dirty when it shouldn't", !dummy.isDirty(1));
    //! dummy.setValue(1, new Object());
    //! assertTrue("object is not dirty when it should be", dummy.isDirty());
    //! assertTrue("object field is not dirty when it should be", dummy.isDirty(1));
    //! dummy.unsetValue(1, null);
    //! assertTrue("object is dirty when it shouldn't", !dummy.isDirty());
    //! assertTrue("object field is dirty when it shouldn't", !dummy.isDirty(1));
  }
  
  public void testLoadedFlags() {
    DummyPersistent dummy = new DummyPersistent();
    //! assertTrue("object field is loaded when it shouldn't", !dummy.isLoaded(1));
    //! dummy.setValue(1, new Object());
    //! assertTrue("object field is not loaded when it should be", dummy.isLoaded(1));
    //! dummy.unsetValue(1, null);
    //! assertTrue("object field is loaded when it shouldn't", !dummy.isLoaded(1));
  }

  //! public void testMaterialize() {
  //!   DummyPersistent dummy = new DummyPersistent();
  //!   assertTrue("object field is loaded when it shouldn't", !dummy.isLoaded(1));
  //!   dummy.materialize(1);
  //!   assertTrue("object field is loaded when it shouldn't", dummy.isLoaded(1));
  //! }
  
}
