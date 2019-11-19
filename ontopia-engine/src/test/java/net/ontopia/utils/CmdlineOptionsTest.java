/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.utils;

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class CmdlineOptionsTest {
  private CmdlineOptions options;
  private Listener listener;
  
  // --- Test cases

  @Test
  public void testSimple() throws CmdlineOptions.OptionsException {
    String[] argv = {"propfile", "tmfile.xtm"};
    setupRDBMS(argv);
    options.parse();

    String args[] = options.getArguments();
    Assert.assertTrue("Wrong number of arguments", args.length == 2);
    Assert.assertTrue("First argument is wrong", args[0].equals("propfile"));
    Assert.assertTrue("Second argument is wrong", args[1].equals("tmfile.xtm"));
    Assert.assertTrue("tmid options incorrectly set", listener.getOption('t') == null);
  }

  @Test
  public void testWithOption() throws CmdlineOptions.OptionsException {
    String[] argv = {"--tmid=140", "propfile", "tmfile.xtm"};
    setupRDBMS(argv);
    options.parse();

    String args[] = options.getArguments();
    Assert.assertTrue("Wrong number of arguments", args.length == 2);
    Assert.assertTrue("First argument is wrong", args[0].equals("propfile"));
    Assert.assertTrue("Second argument is wrong", args[1].equals("tmfile.xtm"));
    Assert.assertTrue("tmid options incorrectly set", listener.getOption('t').equals("140"));
  }

  @Test
  public void testWithMistypedOption() throws CmdlineOptions.OptionsException {
    String[] argv = {"-tmid=140", "propfile", "tmfile.xtm"};
    setupRDBMS(argv);

    try {
      options.parse();
      Assert.fail("Incorrect options allowed");
    } catch (CmdlineOptions.OptionsException e) {
      Assert.assertTrue("Wrong problem reported" + e.getArgument(), e.getArgument().equals("-tmid=140"));
    }
  }

  @Test
  public void testWithMistypedOption2() throws CmdlineOptions.OptionsException {
    String[] argv = {"-tmid", "propfile", "tmfile.xtm"};
    setupRDBMS(argv);

    try {
      options.parse();
      Assert.fail("Incorrect options allowed");
    } catch (CmdlineOptions.OptionsException e) {
      Assert.assertTrue("Wrong problem reported" + e.getArgument(), e.getArgument().equals("-tmid"));
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
    
    @Override
    public void processOption(char option, String value)
      throws CmdlineOptions.OptionsException {

      options.put(new Character(option), value);
      
    }
  }
  
}
