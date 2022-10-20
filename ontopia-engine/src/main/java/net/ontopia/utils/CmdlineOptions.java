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

import gnu.getopt.LongOpt;
import gnu.getopt.Getopt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * INTERNAL: A class that parses command line options.
 */
public class CmdlineOptions {

  protected String application;
  protected String[] argv;
  
  protected StringBuilder sargs = new StringBuilder();
  protected List<LongOpt> largs = new ArrayList<LongOpt>();

  protected Map<Integer, ListenerIF> listeners = new HashMap<Integer, ListenerIF>();
  protected List<String> arguments = new ArrayList<String>();

  public CmdlineOptions(String application, String[] argv) {
    this.argv = argv;
  }

  /**
   * Add a short argumentless option with the specified listener.
   */    
  public void addShort(ListenerIF listener, char c) {
    // No argument
    sargs.append("" + c);
    // Register listener
    listeners.put((int) c, listener);
  }
  
  /**
   * Add a short option with argument with the specified listener.
   */    
  public void addShort(ListenerIF listener, char c, boolean req_arg) {    
    if (req_arg) {
      // Required argument
      sargs.append("" + c + ":");
    } else {
      // Optional argument
      sargs.append("" + c + "::");
    }
    // Register listener
    listeners.put((int) c, listener);
  }
  /**
   * Add a long argumentless option with the specified listener.
   */    
  public void addLong(ListenerIF listener, String name, char c) {
    // No argument
    largs.add(new LongOpt(name, LongOpt.NO_ARGUMENT, null, c));
    // Register listener
    listeners.put((int) c, listener);    
  }

  /**
   * Add a long option with argument with the specified listener.
   */    
  public void addLong(ListenerIF listener, String name, char c, boolean req_arg) {
    if (req_arg) {
      // Required argument
      largs.add(new LongOpt(name, LongOpt.REQUIRED_ARGUMENT, null, c));
    } else {
      // Optional argument
      largs.add(new LongOpt(name, LongOpt.OPTIONAL_ARGUMENT, null, c));
    }
    // Register listener
    listeners.put((int) c, listener);    
  }

  /**
   * Parse the command line arguments and notify option listeners.
   */    
  public void parse() throws OptionsException {
    Getopt g = new Getopt(application, argv, sargs.toString(), largs.toArray(new LongOpt[] {}));
    g.setOpterr(false); // We'll do our own error handling
    
    int c;
    while ((c = g.getopt()) != -1) {
      switch (c) {
      case '?':
        // Get invalid option
        int ix = g.getOptind();
        String option = argv[(ix == 0 ? 0 : ix-1)];
        throw new OptionsException(option, g.getOptarg());
      default:
        ListenerIF listener = listeners.get(c);
        if (listener != null) {
          listener.processOption((char)c, g.getOptarg());
      } else {
          System.err.println ("Warning: option '" + (char)c + "' ignored");
      }
        break;
      }
    }
    
    // Get non-option arguments
    for (int i = g.getOptind(); i < argv.length ; i++) {
      arguments.add(argv[i]);
    }
  }

  
  /**
   * Return non-option arguments that are remaining after parsing the
   * command line arguments.
   */    
  public String[] getArguments() {
    return arguments.toArray(new String[] {});
  }

  /**
   * INTERNAL: A listener interface that must be implemented by object
   * that are interested in options found by the CmdlineOptions
   * instance.</p>
   */

  public interface ListenerIF {

    /**
     * Method that is called by the command line option parser when an
     * option registered for the listener is found.
     */    
    void processOption(char option, String value) throws OptionsException;

  }

  /**
   * INTERNAL: An exception that is thrown when there are problems
   * with the options specified on the command line.</p>
   */

  public static class OptionsException extends Exception {

    protected String argument;
    protected String value;
    
    public OptionsException(String argument, String value) {
      this.argument = argument;
      this.value = value;
    }

    /**
     * Returns the name of the invalid argument.
     */    
    public String getArgument() {
      return argument;
    }

    /**
     * Returns the value of the invalid argument.
     */    
    public String getValue() {
      return value;
    }

    @Override
    public String getMessage() {
      if (value == null) {
        return "Invalid option '" + getArgument() + "'.";
      } else {
        return "Invalid option '" + getArgument() + "=" + getValue() + "'.";
      }    
    }
  }

}
