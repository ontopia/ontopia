/*
 * #!
 * Ontopia Engine
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
