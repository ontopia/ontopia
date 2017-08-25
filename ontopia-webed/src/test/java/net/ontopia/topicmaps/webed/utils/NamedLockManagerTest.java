/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.utils;

import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;

import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.impl.basic.NavigatorConfiguration;
import net.ontopia.topicmaps.nav2.impl.framework.User;
import net.ontopia.topicmaps.webed.impl.utils.LockResult;
import net.ontopia.topicmaps.webed.impl.utils.NamedLockManager;
import net.ontopia.topicmaps.webed.impl.utils.TagUtils;
import net.ontopia.utils.ontojsp.FakeHttpSession;
import net.ontopia.utils.ontojsp.FakeServletContext;
import net.ontopia.topicmaps.webed.impl.utils.SessionListener;
import junit.framework.TestCase;

public class NamedLockManagerTest extends TestCase {
  private NamedLockManager lockMan;
  private FakeHttpSession session;
  private NavigatorConfiguration navConf;

  public NamedLockManagerTest(String name) {
    super(name);
  }

  @Override
  public void setUp() {
    session = new FakeHttpSession(new FakeServletContext());
    session.addSessionListener(new SessionListener());
    lockMan = TagUtils.getNamedLockManager(session.getServletContext());
    lockMan.clear();
    navConf = new NavigatorConfiguration();
  }

  @Override
  public void tearDown() {
    lockMan.clear();
  }
  
  public void testLockingType() {
    
    FakeServletContext servletContext = (FakeServletContext) session
        .getServletContext();
    servletContext.setVersion(2,2);
    assertTrue(NamedLockManager.usesTimedLockExpiry(session));
    
    servletContext.setVersion(2,3);
    assertFalse(NamedLockManager.usesTimedLockExpiry(session));
  }

  protected void assertNoUnlockables(LockResult result) {
    assertNoUnlockables(result.getUnlockable());
  }

  protected void assertNoUnlockables(Collection coll) {
    if (!coll.isEmpty())
      fail("There were objects that could not be locked: " + coll);
  }

  public void testLockPrimitiveObjs() {
    UserIF user1 = new User("user1", navConf);
    Collection objs = new HashSet();
    objs.add("first");
    objs.add("second");
    assertNoUnlockables(lockMan.attemptToLock(user1, objs, "gusto", session));
  }

  public void testLockPrimitiveObjs2() {
    UserIF user1 = new User("user1", navConf);
    UserIF user2 = new User("user2", navConf);
    Collection objs = new HashSet();
    objs.add("first");
    objs.add("second");
    assertNoUnlockables(lockMan.attemptToLock(user1, objs, "gusto", session));
    assertEquals(objs, lockMan.attemptToLock(user2, objs, "gusto", session).getUnlockable());
  }

  public void testUnlockObjs() {
    UserIF user1 = new User("user1", navConf);
    Collection objs = new HashSet();
    objs.add("first");
    objs.add("second");
    assertNoUnlockables(lockMan.attemptToLock(user1, objs, "binding-to-1", 
                        session));
    lockMan.unlock(user1, "binding-to-1", false);
    assertNoUnlockables(lockMan.attemptToLock(user1, objs, "binding-to-1", 
                        session));
  }

  public void testNotUnlockObjsDependentLocks() {
    UserIF user = new User("user1", navConf);
    Collection objs1 = new HashSet();
    objs1.add("first");
    objs1.add("second");
    objs1.add("third");
    Collection objs2 = new HashSet();
    objs2.add("second");
    objs2.add("fourth");
    Collection objs3 = new HashSet();
    objs3.add("fourth");
    objs3.add("sixth");
    Collection objs4 = new HashSet();
    objs4.add("first");
    objs1.add("second");
    objs1.add("third");
    objs4.add("fourth");

    LockResult res1 = lockMan.attemptToLock(user, objs1, "lock1", session);
    String name1 = res1.getName();
    LockResult res2 = lockMan.attemptToLock(user, objs1, "lock2", session);
    String name2 = res2.getName();
    LockResult res3 = lockMan.attemptToLock(user, objs1, "lock3", session);
    String name3 = res3.getName();
    LockResult res4 = lockMan.attemptToLock(user, objs1, "lock4", session);
    String name4 = res4.getName();

    assertNoUnlockables(res1);
    assertNoUnlockables(res2);
    assertNoUnlockables(res3);
    assertNoUnlockables(res4);
    
    lockMan.unlock(user, name1, false);
    
    assertFalse("Owns lock 1-a", lockMan.ownsLock(user, name1));
    assertTrue("Does not own lock 2-a", lockMan.ownsLock(user, name2));
    assertTrue("Does not own lock 3-a", lockMan.ownsLock(user, name3));
    assertTrue("Does not own lock 4-a", lockMan.ownsLock(user, name4));

    LockResult res5 = lockMan.attemptToLock(user, objs1, "lock1", session);
    LockResult res6 = lockMan.attemptToLock(user, objs2, "lock2", session);
    
    assertNoUnlockables(res5);
    String name5 = res5.getName();
    assertNoUnlockables(res6);
    String name6 = res6.getName();
    
    lockMan.unlock(user, name3, false);
    
    assertTrue("Does not own lock 1-b", lockMan.ownsLock(user, name5));
    assertTrue("Does not own lock 2-b", lockMan.ownsLock(user, name6));
    assertFalse("Owns lock 3-c", lockMan.ownsLock(user, name3));
    assertTrue("Does not own lock 4-c", lockMan.ownsLock(user, name4));

    lockMan.unlock(user, name5, false);
    assertFalse("Owns lock 1-c", lockMan.ownsLock(user, name5));
    lockMan.unlock(user, name6, false);
    lockMan.unlock(user, name2, false);
    lockMan.unlock(user, name4, false);
    assertTrue("Remaining locks != 0", lockMan.lockCountFor(user) == 0);
  }

  public void testLockTimedExpiry() {
    UserIF user1 = new User("user1", navConf);
    UserIF user2 = new User("user2", navConf);
    Collection objs = new HashSet();
    objs.add("first");
    objs.add("second");

    ((FakeServletContext) session.getServletContext()).setVersion(2, 2);
    session.setMaxInactiveInterval(0);

    assertNoUnlockables(lockMan.attemptToLock(user1, objs, "binding-to-1", 
                        session));
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {}
    lockMan.attemptToLock(user1, Collections.EMPTY_LIST, "b2", session);

    // lock should now have expired, and this should go through
    assertNoUnlockables(lockMan.attemptToLock(user2, objs, "binding-to-1", 
                        session));
  }

  public void testLockSessionExpiry() {
    Collection objs = new HashSet();
    objs.add("first");
    objs.add("second");

    UserIF user1 = new User("user1", navConf);
    UserIF user2 = new User("user2", navConf);
    
    session.setAttribute(NavigatorApplicationIF.USER_KEY, user1);
    
    assertNoUnlockables(lockMan.attemptToLock(user1, objs, "binding-to-1",
                        session));

    session.expire();

    // lock should now have expired, and this should go through
    assertNoUnlockables(lockMan.attemptToLock(user2, objs, "binding-to-1",
                        session));
  }

  public void testLockFight() {
    UserIF user1 = new User("user1", navConf);
    UserIF user2 = new User("user2", navConf);
    Collection objs = new HashSet();
    objs.add("first");
    objs.add("second");
    
    String lockName1 = "lock1";

    // User 1 locks all the objects.
    LockResult res1 = lockMan.attemptToLock(user1, objs, lockName1, session);
    assertNoUnlockables(res1);
    
    // User 2 attempts to lock the objects, but fails.
    assertEquals(objs, lockMan.attemptToLock(user2, objs, lockName1, session)
        .getUnlockable());
    
    // User 2 unlocks the objects and then locks them
    lockMan.unlock(user2, lockName1, true);
    LockResult res2 = lockMan.attemptToLock(user2, objs, lockName1, session);
    assertNoUnlockables(res2);
    
    // User 1 attempts to lock the objects, but fails.
    assertEquals(objs, lockMan.attemptToLock(user1, objs, lockName1, session)
        .getUnlockable());
    
    // User 1 unlocks the objects and then locks them
    lockMan.unlock(user1, lockName1, true);
    LockResult res3 = lockMan.attemptToLock(user1, objs, lockName1, session);
    assertNoUnlockables(res3);

    // User 2 attempts to lock the objects twice, but fails.
    assertEquals(objs, lockMan.attemptToLock(user2, objs, lockName1, session)
        .getUnlockable());
    assertEquals(objs, lockMan.attemptToLock(user2, objs, lockName1, session)
        .getUnlockable());
  }


  public void testUnlocking() {
    UserIF user1 = new User("user1", navConf);
    UserIF user2 = new User("user2", navConf);
    Collection objs = new HashSet();
    objs.add("first");
    objs.add("second");
    
    String lockName1 = "lock1";

    // User 1 locks all the objects.
    LockResult res1 = lockMan.attemptToLock(user1, objs, lockName1, session);
    assertNoUnlockables(res1);
    
    // User 2 attempts to lock the objects, but fails.
    assertEquals(objs, lockMan.attemptToLock(user2, objs, lockName1, session)
        .getUnlockable());
    
    // User 1 unlocks the objects.
    lockMan.unlock(user1, res1.getName(), false);
    
    // User 2 locks the objects.
    LockResult res2 = lockMan.attemptToLock(user2, objs, lockName1, session);
    assertNoUnlockables(res2);
    
    // User 1 attempts to lock the objects, but fails.
    assertEquals(objs, lockMan.attemptToLock(user1, objs, lockName1, session)
        .getUnlockable());
    
    // User 2 unlocks the objects
    lockMan.unlock(user2, res2.getName(), false);
    
    // User 1 locks the objects.
    LockResult res3 = lockMan.attemptToLock(user1, objs, lockName1, session);
    assertNoUnlockables(res3);
  }
}
