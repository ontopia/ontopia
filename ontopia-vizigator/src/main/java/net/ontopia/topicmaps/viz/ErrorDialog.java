
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
