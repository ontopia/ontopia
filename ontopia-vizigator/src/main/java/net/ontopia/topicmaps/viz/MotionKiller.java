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

import java.util.Timer;
import java.util.TimerTask;

import com.touchgraph.graphlayout.TGPanel;

/**
 * Stops the motion of the nodes in Vizigator at certain intervals.
 * Has methods for that lets the MotionKiller waits for a given amount of time
 * before stopping the motion, so that the nodes can be arranged reasonably
 * first.
 * Also has a counter that helps operations outside the OKS, which cannot call
 * the waitFor() operation, so that these operations are given a bit more time
 * (on average) to finish.
 * WEAKNESSES:
 * There are some weaknesses to this approach.
 * First of all, MotionKiller may run immediately after operations that are
 * outside the OKS, since these cannot ask MotionKiller to wait.
 *   - This would be easily solvable if we modified the TouchGraph code.
 * Another disadvantage is that MotionKiller compromised with the algorithm in
 * TouchGraph which makes sure nodes and edges are arranged optimally to avoid
 * clutter. For small graphs this is normally not a problem, but large graphs
 * which take time to arrange optimally may end up much more cluttered than
 * necessary. Possible solutions to this problem are:
 *   - Let the user control the parameters of this class (easy to implement)
 *   - Improve the layout algorithm of TouchGraph (probably hard)
 *   - Hope that TouchGraph has/will implement an improved layout algorithm.
 */
public class MotionKiller extends TimerTask {
  
  // Note: VizPanel uses this variable set the initial state of a menu.
  public static final boolean INITIALLY_ENABLED = false;
  
  protected TGPanel tgPanel;
  protected long waitUntil1;
  protected long waitUntil2;
  protected int cycle = 0;
  protected int maxCycle = 3;
  protected boolean enabled;
  
  /**
   * Create a MotionKiller for the given tgPanel, scheduled to run every
   * 'millis' number of milliseconds after creation.
   * @param tgPanel The TGPanel for which to stop the motion.
   * @param millis The milliseconds between every time the motion is stopped
   */
  public MotionKiller(TGPanel tgPanel, long millis) {
    this.tgPanel = tgPanel;
    waitUntil1 = 0;
    waitUntil2 = 0;
    Timer timer = new Timer();
    timer.scheduleAtFixedRate(this, millis, millis);
    
    // Note: VizPanel assumes this assignment when building menus.
    enabled = INITIALLY_ENABLED;
    setEnabled(enabled);
  }
  
  /**
   * Wait for 'duration1' milliseconds before slowing down the motion,
   * then wait for 'duration2' milliseconds before stopping the motion,
   */
  public void waitFor(long duration1, long duration2) {
    long currentTime = System.currentTimeMillis();
    waitUntil1 = currentTime  + duration1;
    waitUntil2 = currentTime  + duration2;
  }
  
  /**
   * This method is called on schedula by the timer.
   */
  @Override
  public void run() {
    if (!enabled) {
      return;
    }
    stopMotion();
  }
  
  /**
   * Enables/disables this motion killer.
   * Note: VizPanel uses the value of enabled to build menus, so this method
   *     should only be changed (indirectly) from there.
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    tgPanel.resetDamper();

    // The following code causes exception bacause one cannot cancel a task
    // and then reschedule it.
    // So currently, the task keeps running forever, even if MotionKiller is
    // disabled. Would be nice to turn it off completely, since it interrupts
    // once a second.
//    if (enabled) {
//      timer = new Timer();
//      timer.scheduleAtFixedRate(this, millis, millis);
//      tgPanel.resetDamper();
//    } else if (timer != null) {
//      timer.cancel();
//      timer = null;
//    }
  }
  
  public boolean getEnabled() {
    return enabled;
  }
  
  private void stopMotion() {
    if (waitUntil1 == 0 && waitUntil2 == 0) {
      // This execution is not soon after call to a waitFor(), but it
      // may be after some other procedure that requires
      // layouting. Wait a bit longer than usual by only executing
      // some of the times (frequency: 1 / maxCycle)
      cycle++;
      if (cycle >= maxCycle) {
        cycle = 0;
      } else {
        return;
      }
    }
    
    if (waitUntil1 != 0) {
      // This execution is soon after a call to a waitFor().
      // Check if the wait to slow down the motion has been long enough.
      if (System.currentTimeMillis() <  waitUntil1) {
        return;
      }
      waitUntil1 = 0;
    }
    tgPanel.stopMotion();
    
    if (waitUntil2 != 0) {
      // This execution is soon after a call to a waitFor().
      // Check if the wait to stop the motion has been long enough.
      if (System.currentTimeMillis() <  waitUntil2) {
        return;
      }
      waitUntil2 = 0;
    }
    tgPanel.stopMotion();
  }
  
  protected void setMaxCycle(int maxCycle) {
    this.maxCycle = maxCycle;
    VizDebugUtils.debug("MotionKiller - maxCycle: " + maxCycle);
  }
}
