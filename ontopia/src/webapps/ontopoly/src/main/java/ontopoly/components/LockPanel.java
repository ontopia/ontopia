package ontopoly.components;

import java.util.Date;

import ontopoly.LockManager;
import ontopoly.OntopolyContext;
import ontopoly.OntopolySession;
import ontopoly.model.OntopolyTopicIF;

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
  
  public LockPanel(String id, IModel<? extends OntopolyTopicIF> topicModel, boolean shouldAcquireLock) {
    super(id, topicModel);
    
    // acquire lock unless read-only page
    if (!shouldAcquireLock)
      acquireLock();
    
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
        boolean gotlock = acquireLock();
        if ((hadlock && !gotlock)) {
          stop();
          onLockLost(target, (OntopolyTopicIF)getDefaultModelObject());
        } else if (!hadlock && gotlock) {
          onLockWon(target, (OntopolyTopicIF)getDefaultModelObject());
        }
      }
    };
    Button unlockButton = new Button("unlockButton");
    unlockButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        LockManager lockManager = OntopolyContext.getLockManager();
        OntopolyTopicIF topic = (OntopolyTopicIF)getDefaultModelObject();
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
    LockManager.Lock lock = session.lock((OntopolyTopicIF)getDefaultModelObject(), lockerId);
    this.lockedBy = lock.getLockedBy();
    this.lockedAt = new Date(lock.getLockTime()).toString();
    this.lockKey = lock.getLockKey();
    if (!lock.ownedBy(lockerId)) {
      this.lockedByOther = true;
      return false;
    } else {
      return true;
    }
  }
  
  public boolean isLockedByOther() {
    return lockedByOther;
  }
  
  /**
   * Called when the lock on the topic was lost.
   */
  protected abstract void onLockLost(AjaxRequestTarget target, OntopolyTopicIF topic);
  
  /**
   * Called when the lock on the topic was won.
   */
  protected abstract void onLockWon(AjaxRequestTarget target, OntopolyTopicIF topic);
  
}
