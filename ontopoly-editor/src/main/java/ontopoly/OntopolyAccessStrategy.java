/*
 * #!
 * Ontopoly Editor
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
package ontopoly;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import ontopoly.model.FieldInstance;
import ontopoly.model.Topic;

/**
 * Default access strategy implementation used by OntopolyApplication.
 * Subclasses can extend this functionality to enable authentication and/or
 * authorization for their ontopoly instance. To activate your own
 * implementation, create an extension of {@link OntopolyApplication} and
 * override the {@link OntopolyApplication#newAccessStrategy()} method.
 *
 * @see <a href="https://github.com/ontopia/ontopia/wiki/OntopolyHowTo">
 *      Ontopia github - Ontopoly tips</a>
 * @see OntopolyApplication
 * @see OntopolyApplication#newAccessStrategy()
 */
public abstract class OntopolyAccessStrategy implements Serializable {

  /**
   * The set of Privileges that a user can have.
   */
  public enum Privilege { EDIT, READ_ONLY, NONE }

  /**
   * Indicates if this strategy is enabled. An enabled strategy will cause the
   * login page to be used. The default implementation always returns true.
   * @return True if the strategy is enabled, false if disabled.
   */
  public boolean isEnabled() {
    return true;
  }

  /**
   * Automatically authenticates a user based on a request made. Subclasses can
   * implement a remember-me functionality by using this method, for example by
   * checking the presence of a cookie. Default implementation always returns
   * null.
   * @param request The request made that requires automatic authentication
   * checks
   * @return A User object when the automated authentication has succeeded, or
   * null.
   */
  public User autoAuthenticate(HttpServletRequest request) {
    return null;
  }

  /**
   * Attempts to authenticate a user with given name and password. Subclasses
   * should override this method to include their user verification. The default
   * implementation makes a new user with username as name, and ignores the
   * provided password. Note that subclasses should not throw exceptions when
   * authentication fails, but instead return null.
   * @param username The username to authenticate
   * @param password The password in plain text used by the user attempting to
   * login
   * @return A User object when authentication succeeded, or null
   */
  public User authenticate(String username, String password) {
    return new User(username, false);
  }

  /**
   * Resolves the privilege of a user for the provided topic. In this context,
   * the privileges mean:
   * <ul><li>Privilege.EDIT: The user is allowed to change this topic</li>
   * <li>Privilege.READ_ONLY: The user is allowed to view this topic, but not
   * to change it.</li>
   * <li>Privilege.NONE: The user is not allowed to view or change this topic.
   * </li></ul>
   * @param user The user to retrieve the privilege for
   * @param topic The topic the user is trying to access
   * @return The privilege of the user regarding the provided topic. Must not
   * be null.
   */
  public Privilege getPrivilege(User user, Topic topic) {
    return Privilege.EDIT;
  }

  /**
   * Resolves the privilege of a user for the provided field. In this context,
   * the privileges mean:
   * <ul><li>Privilege.EDIT: The user is allowed to change this field</li>
   * <li>Privilege.READ_ONLY: The user is allowed to view this field, but not
   * to change it.</li>
   * <li>Privilege.NONE: The user is not allowed to view or change this field.
   * </li></ul>
   * The field privilege has precedence over the topic privilege provided by
   * {@link #getPrivilege(User, Topic)}.
   * @param user The user to retrieve the privilege for
   * @param fieldInstance The fieldinstance the user is trying to access
   * @return The privilege of the user regarding the provided fieldinstance.
   * Must not be null.
   */
  public Privilege getPrivilege(User user, FieldInstance fieldInstance) {
    return Privilege.EDIT;
  }

  /**
   * Returns the message displayed on the login page.
   * @return The message displayed on the login page
   */
  public String getSignInMessage() {
    return "Please sign in.";
  }
}
