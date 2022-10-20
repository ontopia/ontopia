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
    if (lang == null || lang.toLowerCase().equals("en")) {
      bundleName = BUNDLE_NAME;
    } else {
      bundleName = BUNDLE_NAME + '_' + lang;
    }
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
