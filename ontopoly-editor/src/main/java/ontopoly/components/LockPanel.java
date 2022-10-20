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
package ontopoly.components;

import java.util.Date;

import ontopoly.LockManager;
import ontopoly.OntopolyContext;
import ontopoly.OntopolySession;
import ontopoly.model.Topic;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.time.Duration;

public abstract class LockPanel extends Panel {
  
  protected boolean lockedByOther;
  protected String lockedBy;
  protected String lockedAt;
  protected String lockKey;
  
  public LockPanel(String id, IModel<? extends Topic> topicModel, boolean shouldAcquireLock) {
    super(id, topicModel);
    
    // acquire lock unless read-only page
    if (!shouldAcquireLock) {
      acquireLock();
    }
    
    WebMarkupContainer container = new WebMarkupContainer("lockPanelContainer") {
      @Override 
      public boolean isVisible() {
        return lockedByOther;
      }      
    };
    container.setOutputMarkupId(true);
    
    container.add(new Label("lockMessage", lockedByOther ? new ResourceModel("lockPanel.message") : null));
    
    container.add(new Label("lockedByLabel", new ResourceModel("lockPanel.lockedByLabel")));
    container.add(new Label("lockedByValue", lockedBy));
    container.add(new Label("lockedAtLabel", new ResourceModel("lockPanel.lockedAtLabel")));
    container.add(new Label("lockedAtValue", lockedAt));
     
    final AbstractAjaxTimerBehavior timerBehavior = 
      new AbstractAjaxTimerBehavior(Duration.minutes(LockManager.DEFAULT_LOCK_REACQUIRE_TIMESPAN_MINUTES)) {
      @Override
      protected void onTimer(AjaxRequestTarget target) {
        boolean hadlock = !lockedByOther;
        //! System.out.println("Attempting to " + (hadlock ? "re" : "") + "acquire lock on " + (AbstractTopic)getModelObject());
        boolean gotlock = acquireLock();
        //! System.out.println("Got lock: " + hadlock + " " + gotlock);
        if ((hadlock && !gotlock)) {
          stop();
          onLockLost(target, (Topic)getDefaultModelObject());
        } else if (!hadlock && gotlock) {
          onLockWon(target, (Topic)getDefaultModelObject());
        }
      }
    };
    Button unlockButton = new Button("unlockButton");
    unlockButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        LockManager lockManager = OntopolyContext.getLockManager();
        Topic topic = (Topic)getDefaultModelObject();
        lockManager.forcedUnlock(lockKey);
        timerBehavior.stop();
        onLockWon(target, topic);
      }
    });
    container.add(unlockButton);
    add(container);
   
    // add timer behaviour only if page was locked by us
    if (!lockedByOther) {
      // have page (re)acquire the lock just before it times out
      add(timerBehavior);
    }
  }
  
  @Override
  public boolean isVisible() {
    return lockedByOther;
  }
  
  protected boolean acquireLock() {
    // create lock id and lock key
    OntopolySession session = (OntopolySession)Session.get();
    String lockerId = session.getLockerId(getRequest());
    LockManager.Lock lock = session.lock((Topic)getDefaultModelObject(), lockerId);
    this.lockedBy = lock.getLockedBy();
    this.lockedAt = new Date(lock.getLockTime()).toString();
    this.lockKey = lock.getLockKey();
    if (!lock.ownedBy(lockerId)) {
      this.lockedByOther = true;
      //! System.out.println("Got lock: false: " + lock);
      return false;
    } else {
      //! System.out.println("Got lock: true" + lock);
      return true;
    }
  }
  
  public boolean isLockedByOther() {
    return lockedByOther;
  }
  
  /**
   * Called when the lock on the topic was lost.
   * @param target
   * @param topic
   */
  protected abstract void onLockLost(AjaxRequestTarget target, Topic topic);
  
  /**
   * Called when the lock on the topic was won.
   * @param target
   * @param topic
   */
  protected abstract void onLockWon(AjaxRequestTarget target, Topic topic);
  
}
