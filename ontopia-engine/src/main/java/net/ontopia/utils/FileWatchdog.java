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

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Check every now and then that a certain file has not
 * changed. If it has, then call the {@link #doOnChange} method.
 *
 * @since 1.4
 */
public abstract class FileWatchdog extends Thread {

  // initialization of logging facility
  private static final Logger log = LoggerFactory.getLogger(FileWatchdog.class.getName());
  
  /**
   * The default delay between every file modification check, set to 6
   * seconds.
   */
  static final public long DEFAULT_DELAY = 6000;
  
  /**
   * The name of the file to observe for changes.
   */
  protected String filename;
  
  /**
   * The delay to observe between every check. By default set {@link
   * #DEFAULT_DELAY}.
   */
  protected long delay = DEFAULT_DELAY; 
  
  private File file;
  private long lastModified = 0; 
  private boolean warnedAlready = false;
  private boolean interrupted = false;

  protected FileWatchdog() {
    super();
  }
  
  protected FileWatchdog(String filename) {
    initialize(filename);
  }
  
  protected void initialize(String filename) {
    this.filename = filename;
    file = new File(filename);
    setDaemon(true);
    try {
      log.debug("Doing initial check");
      checkAndConfigure();
    } catch (Exception e) {
      log.warn("Error checking file", e);
    }
  }

  /**
   * Sets the delay to observe between each check of the file changes.
   *
   * @param delay - The delay in milliseconds, in the case of a
   *        negative value the further execution is interrupted.
   */
  public void setDelay(long delay) {
    this.delay = delay;
    if (delay < 0)
      this.interrupted = true;
  }

  abstract protected void doOnChange();

  protected void checkAndConfigure() {
    boolean fileExists;
    try {
      fileExists = file.exists();
    } catch (SecurityException e) {
      log.warn("Was not allowed to check existence of file: {}", filename);
      // there is no point in continuing
      interrupted = true;
      return;
    }
    if (fileExists) {
      long l = file.lastModified();
      log.debug("{} last modified: {}; previously: {}", new Object[] {filename, Long.toString(l), Long.toString(lastModified)} );
      if (l > lastModified) {
        log.debug("loading file {}", filename);
        lastModified = l;
        doOnChange();
        warnedAlready = false;
      }
    } else {
      if (!warnedAlready) {
        log.debug("[{}] does not exist.", filename);
        warnedAlready = true;
      }
    }
  }

  @Override
  public void run() {
    while (!interrupted) {
      try {
        Thread.currentThread().sleep(delay);
      } catch (InterruptedException e) {
        // no interruption expected
      }
      try {
        log.debug("Checking and configuring");
        checkAndConfigure();
      } catch (Exception e) {
        log.warn("Error checking file", e);
      }
    }
  }

}
