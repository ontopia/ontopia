/*
 * #!
 * Ontopia Vizigator
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

package net.ontopia.topicmaps.viz;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * INTERNAL: A generalized error dialog which displays an exception by
 * showing the error message, but with support for showing the
 * traceback if necessary.
 */
public class ErrorDialog {
  /**
   * INTERNAL: Shows dialog for an exception.
   */
  public static void showError(Component parent, Exception e) {
    StringWriter tmp = new StringWriter();
    e.printStackTrace(new PrintWriter(tmp));
    JOptionPane.showMessageDialog(parent, tmp.toString(), 
                                  Messages.getString("Viz.ErrorTitle"),
                                  JOptionPane.ERROR_MESSAGE);
//    showError(parent, e.toString());
  }

  /**
   * INTERNAL: Shows dialog for an exception.
   */
  public static void showMessage(Component parent, Exception e) {
    StringWriter tmp = new StringWriter();
    e.printStackTrace(new PrintWriter(tmp));
    showError(parent, e.toString());
  }

  /**
   * INTERNAL: Shows dialog for an exception.
   */
  public static void showError(Component parent, String msg, Exception e) {
    e.printStackTrace();
    showError(parent, msg + ": " + e.getMessage());
  }
  
  /**
   * INTERNAL: Shows a straight error message.
   */
  public static void showError(Component parent, String message) {
    JOptionPane.showMessageDialog(parent, message,
                                  Messages.getString("Viz.ErrorTitle"),
                                  JOptionPane.ERROR_MESSAGE);
  }
}
