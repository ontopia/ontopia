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

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Class that contains useful stuff for command line utilities.
 */
public class CmdlineUtils {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(CmdlineUtils.class.getName());

  private static boolean isLog4JAvailable() {
    return log.getClass().getName().equals("org.slf4j.impl.Log4jLoggerAdapter");
  }

  /**
   * Returns if SLF4J uses Log4J
   */
  public static void initializeLogging() {
    if (!isLog4JAvailable()) {
        log.warn("Log4J is not available, logging configuration will be ignored");
        return;
    }
    String propfile = null;
    try {
      propfile = System.getProperty("log4j.configuration");
    } catch (SecurityException e) {
      log.warn(e.toString());
    }
      
    if (propfile == null) {
      // Reset and initialize logging configuration
      resetLoggingConfiguration();
      try {
        String priority = System.getProperty("net.ontopia.utils.CmdlineUtils.priority");
        setLoggingPriority((priority == null ? "INFO" : priority));
      } catch (SecurityException e) {
        setLoggingPriority("INFO");
      }
    } else {
      // Use the specified log4j property file
      configureByFile(propfile);
    }
  }

  private static void resetLoggingConfiguration() {
    try {
      Class<?> basicConfiguratorClass = Class.forName("org.apache.log4j.BasicConfigurator");
      basicConfiguratorClass.getDeclaredMethod("resetConfiguration").invoke(null);
      basicConfiguratorClass.getDeclaredMethod("configure").invoke(null);
    }
    catch (Exception ex) {
      log.warn("Resetting basic configuration failed: {}", ex);
    }
  }

  private static void configureByFile(String propfile) {
    try {
      Class<?> propertyConfiguratorClass = Class.forName("org.apache.log4j.PropertyConfigurator");
      propertyConfiguratorClass.getDeclaredMethod("configure", String.class).invoke(null, propfile);
    }
    catch (Exception ex) {
      log.warn("Configuring Log4J with property file '{}' failed: {}", propfile, ex);
    }
  }

  public static void setLoggingPriority(String priority) {
    if (!isLog4JAvailable()) {
        log.warn("Log4J is not available, logging configuration will be ignored");
        return;
    }
    if (priority == null) {
      priority = "INFO";
    }
    priority = priority.toUpperCase();
    // Must be done to translate NONE to the correct Log4J level OFF
    if ("NONE".equals(priority)) {
      priority = "OFF";
    }
    try {
      Class<?> log4jManagerClass = Class.forName("org.apache.log4j.LogManager");
      Class<?> levelClass = Class.forName("org.apache.log4j.Level");
      Object loggerRepository = log4jManagerClass.getDeclaredMethod("getLoggerRepository").invoke(null);
      Method setThreshold = loggerRepository.getClass().getDeclaredMethod("setThreshold", levelClass);
      Method toLevel = levelClass.getDeclaredMethod("toLevel", String.class);
      setThreshold.invoke(loggerRepository, toLevel.invoke(null, priority));
    }
    catch (Exception ex) {
      log.warn("Configuring Log4J through reflection failed: {}", ex);
    }
  }

  
  public static void registerLoggingOptions(CmdlineOptions options) {
    // Configure option listeners
    LoggingOptionsListener listener = new LoggingOptionsListener();
    options.addLong(listener, "logargs", '9', true);
    options.addLong(listener, "loglevel", '8', true);
    //options.addLong(listener, "logfile", '7', true);
  }

  static class LoggingOptionsListener implements CmdlineOptions.ListenerIF {
    
    @Override
    public void processOption(char option, String value) throws CmdlineOptions.OptionsException {

      switch (option)
        {
        case '9':
          configureByFile(value);
          break;
        case '8':
          if (value == null) {
            break;
      }
          setLoggingPriority(value);
          break;
        }
    }
  }

  public static void printLoggingOptionsUsage(java.io.PrintStream stream) {
    stream.println("    --logargs=<propfile>  : log4j properties config file");
    stream.println("    --loglevel=[DEBUG|INFO|WARN|ERROR|FATAL|NONE]  : the log level to use (verbosity threshold)");
  }
  
}
