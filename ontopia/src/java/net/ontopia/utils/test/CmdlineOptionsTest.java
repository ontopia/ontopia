
// $Id: CmdlineOptionsTest.java,v 1.2 2007/09/03 13:08:34 geir.gronmo Exp $

package net.ontopia.utils.test;

import java.util.*;
import net.ontopia.test.*;
import net.ontopia.utils.*;

public class CmdlineOptionsTest extends AbstractOntopiaTestCase {
  private CmdlineOptions options;
  private Listener listener;
  
  public CmdlineOptionsTest(String name) {
    super(name);
  }

  // --- Test cases

  public void testSimple() throws CmdlineOptions.OptionsException {
    String[] argv = {"propfile", "tmfile.xtm"};
    setupRDBMS(argv);
    options.parse();

    String args[] = options.getArguments();
    assertTrue("Wrong number of arguments", args.length == 2);
    assertTrue("First argument is wrong", args[0].equals("propfile"));
    assertTrue("Second argument is wrong", args[1].equals("tmfile.xtm"));
    assertTrue("tmid options incorrectly set", listener.getOption('t') == null);
  }

  public void testWithOption() throws CmdlineOptions.OptionsException {
    String[] argv = {"--tmid=140", "propfile", "tmfile.xtm"};
    setupRDBMS(argv);
    options.parse();

    String args[] = options.getArguments();
    assertTrue("Wrong number of arguments", args.length == 2);
    assertTrue("First argument is wrong", args[0].equals("propfile"));
    assertTrue("Second argument is wrong", args[1].equals("tmfile.xtm"));
    assertTrue("tmid options incorrectly set", listener.getOption('t').equals("140"));
  }

  public void testWithMistypedOption() throws CmdlineOptions.OptionsException {
    String[] argv = {"-tmid=140", "propfile", "tmfile.xtm"};
    setupRDBMS(argv);

    try {
      options.parse();
      fail("Incorrect options allowed");
    } catch (CmdlineOptions.OptionsException e) {
      assertTrue("Wrong problem reported" + e.getArgument(), e.getArgument().equals("-tmid=140"));
    }
  }

  public void testWithMistypedOption2() throws CmdlineOptions.OptionsException {
    String[] argv = {"-tmid", "propfile", "tmfile.xtm"};
    setupRDBMS(argv);

    try {
      options.parse();
      fail("Incorrect options allowed");
    } catch (CmdlineOptions.OptionsException e) {
      assertTrue("Wrong problem reported" + e.getArgument(), e.getArgument().equals("-tmid"));
    }
  }
  
  // --- Internal

  private void setupRDBMS(String[] argv) 
    throws CmdlineOptions.OptionsException {
    
    options = new CmdlineOptions("RDBMSImport", argv);
    listener = new Listener();
    CmdlineUtils.registerLoggingOptions(options);
    options.addLong(listener, "tmid", 't', true);
  }

  // --- Listener implementations

  class Listener implements CmdlineOptions.ListenerIF {
    private Map options;

    public Listener() {
      options = new HashMap();
    }
    
    public String getOption(char option) {
      return (String) options.get(new Character(option));
    }
    
    public void processOption(char option, String value)
      throws CmdlineOptions.OptionsException {

      options.put(new Character(option), value);
      
    }
  }
  
}
