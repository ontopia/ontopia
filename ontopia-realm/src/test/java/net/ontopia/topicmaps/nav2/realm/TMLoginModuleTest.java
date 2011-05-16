
// $Id: TMLoginModuleTest.java,v 1.3 2005/03/20 13:41:11 grove Exp $

package net.ontopia.topicmaps.nav2.realm;


import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import java.security.Principal;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.junit.Test;
import junit.framework.TestCase;

/**
 * INTERNAL: Tests the TMLoginModule class.
 */
public class TMLoginModuleTest extends TestCase {

  public TMLoginModuleTest(String name) {
    super(name);
  }

  @Test
  public void testLoginModulePlaintextSucce() throws Exception{
    String[] tokens = new String[] {
      "plaintext", "feil",
      "plaintext", "hemmelig1",
      "plaintext", "hemmelig3",
      "plaintext", "hemmelig1"
    };
    String[] pnames = new String[] { "plaintext", "user", "Administrator" };

    Map options = new java.util.HashMap();
    options.put("hashmethod", "plaintext");
    options.put("topicmap", "tmloginmodule.ltm");
    
    doLoginTests(tokens, pnames, options);
  }

  @Test
  public void testLoginModuleBase64() throws Exception{
    String[] tokens = new String[] {
      "base64", "feil",
      "base64", "hemmelig2",
      "base64", "hemmelig1",
      "base64", "hemmelig2"
    };

	// user (small) = implicit role
	// User (capt)  = Role topic with "User" as topicname

    String[] pnames = new String[] { "base64", "user", "Administrator", "Janitor", "User" };

    Map options = new java.util.HashMap();
    options.put("hashmethod", "base64");
    options.put("topicmap", "tmloginmodule.ltm");
    
    doLoginTests(tokens, pnames, options);
  }

  @Test
  public void testLoginModuleMD5() throws Exception{
    String[] tokens = new String[] {
      "md5", "feil",
      "md5", "hemmelig3",
      "md5", "hemmelig2",
      "md5", "hemmelig3"
    };
    String[] pnames = new String[] { "md5", "user" };
   
    Map options = new java.util.HashMap();
    options.put("hashmethod", "md5");
    options.put("topicmap", "tmloginmodule.ltm");
    
    doLoginTests(tokens, pnames, options);
  }

  protected void doLoginTests(String[] tokens, String[] pnames, Map options) throws Exception {
    TestableTMLoginModule loginModule = new TestableTMLoginModule();    
    Subject subject = new Subject();    
    CallbackHandler callbackHandler = new CallbackHandlerImpl(tokens);
    
    Map _principals = new java.util.HashMap();
    
    loginModule.initialize(
              subject, 
              callbackHandler,
              _principals, options);

    // should fail with incorrect password
    assertFalse("Could log in with wrong password", loginModule.login());
    assertTrue("Could not log out (1)", loginModule.logout());

    // should succeed
    assertTrue("Could not log in with correct tokens (2)", loginModule.login());
    assertTrue("Could not log out (2)", loginModule.logout());

    // should fail with other user's password
    assertFalse("Could log in with other user's password", loginModule.login());
    assertTrue("Could not log out (3)", loginModule.logout());

    // should succeed
    assertTrue("Could not log in with correct tokens", loginModule.login());

    // accept last token
    assertTrue("Could not commit", loginModule.commit());

    // verify roles
    Collection principals = subject.getPrincipals();

    // ISSUE: all principals have just one role subject, 'user' for
    // the time being, totally two principals including the user
    // principal.

    //! assertTrue("Subject does not have correct number of principals", 
    //!            principals.size() == pnames.length);

    assertEquals("Subject does not have correct number of principals " + java.util.Arrays.asList(principals), pnames.length, principals.size());

    Iterator iter = principals.iterator();
    while (iter.hasNext()) {
      Principal principal = (Principal)iter.next();
      String pname = principal.getName();
      boolean ok = false;
      for (int i=0; i < pnames.length; i++) {
	if (pnames[i].equals(pname)) {
	  ok = true;
	  break;
	}
      }
      if (!ok) fail("User did not have proper principals: " + java.util.Arrays.asList(pnames));	
    }
  }
  
  static class CallbackHandlerImpl implements CallbackHandler {

    protected String[] tokens;
    protected int index;

    public CallbackHandlerImpl(String[] tokens) {
      this.tokens = tokens;
    }
    
    public void handle(Callback[] callbacks)
      throws IOException, UnsupportedCallbackException {
    
      for (int i = 0; i < callbacks.length; i++) {
	if (callbacks[i] instanceof NameCallback) {
          // prompt the user for a username
          NameCallback nc = (NameCallback)callbacks[i];
	  String username = tokens[index++];
          nc.setName(username);

        } else if (callbacks[i] instanceof PasswordCallback) {      
          // prompt the user for sensitive information
          PasswordCallback pc = (PasswordCallback)callbacks[i];
	  String password = tokens[index++];
          pc.setPassword(password.toCharArray());

	} else if (callbacks[i] instanceof TextOutputCallback) {
	  // ignore
        
        } else {
          throw new UnsupportedCallbackException(callbacks[i], 
						 "Unrecognized Callback");
        }
      }
    }

  }

}
