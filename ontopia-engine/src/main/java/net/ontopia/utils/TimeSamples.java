
// $Id: TimeSamples.java,v 1.5 2002/11/29 09:51:37 larsga Exp $

package net.ontopia.utils;

import java.util.ArrayList;

/**
 * INTERNAL: Store a set a time samples for easier calculation of
 * minimum, maximum and average times.
 */
public class TimeSamples {

  private ArrayList timeSample = new ArrayList();

  /**
   * INTERNAL: Add sample time to set.
   */
  public void addTime(double timeInSecs) {
    if (timeInSecs < 0.0)
      throw new IllegalArgumentException("Can't add negative amount of time to set!");
    
    timeSample.add(new Double(timeInSecs));
  }

  /**
   * INTERNAL: Get average sample time in seconds.
   */
  public double getAverageTime() {
    double sum = 0.0;
    if (timeSample.size() <= 0)
      return 0.0;
    
    for (int i=0; i < timeSample.size(); i++) {
      sum += ((Double)timeSample.get(i)).doubleValue();
    }
    double avg = sum / timeSample.size();
    // Limit result to 3 digits after commata (which represents precision)
    return Math.round(avg * 1000.0) / 1000.0;
  }

  /**
   * INTERNAL: Get maximum sampled time in seconds.
   */
  public double getMinimumTime() {
    double min = Double.MAX_VALUE;
    for (int i=0; i < timeSample.size(); i++) {
      double actVal = ((Double)timeSample.get(i)).doubleValue();
      if (actVal < min) 
        min = actVal;
    }
    return min;    
  }

  /**
   * INTERNAL: Get minimum sampled time in seconds.
   */
  public double getMaximumTime() {
    double max = 0.0;
    for (int i=0; i < timeSample.size(); i++) {
      double actVal = ((Double)timeSample.get(i)).doubleValue();
      if (actVal > max) 
        max = actVal;
    }
    return max;    
  }
  
}
