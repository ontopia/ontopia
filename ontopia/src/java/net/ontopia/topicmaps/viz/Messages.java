
// $Id: Messages.java,v 1.6 2008/11/03 12:26:00 lars.garshol Exp $

package net.ontopia.topicmaps.viz;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * INTERNAL. Handles the localization.
 */
public class Messages {
  private static final String BUNDLE_NAME = 
      "net.ontopia.topicmaps.viz.messages";
  private static ResourceBundle resourceBundle = ResourceBundle
      .getBundle(BUNDLE_NAME);
  private static final MessageFormat formatter = new MessageFormat("");
    
  private Messages() {
    // It should not be possible to create an instance of this class
  }

  public static void setLanguage(String lang) {
    String bundleName;
    if (lang == null || lang.toLowerCase().equals("en"))
      bundleName = BUNDLE_NAME;
    else
      bundleName = BUNDLE_NAME + '_' + lang;
    resourceBundle = ResourceBundle.getBundle(bundleName);
  }

  public static String getString(String key) {
    try {
      return resourceBundle.getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }

  public static String getString(String key, Object[] args) {
    formatter.applyPattern(Messages.getString(key));
    return formatter.format(args);
  }

  public static String getString(String template, String aString) {
    return Messages.getString(template, new Object[]{aString});
  }
}
