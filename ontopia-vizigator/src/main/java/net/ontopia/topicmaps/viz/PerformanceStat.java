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

public class PerformanceStat {
  protected long sum;
  protected long sumOfSquares;
  protected long count;
  protected long min;
  protected long max;
  
  protected long initTime;
  protected long startTime;
  
  protected String id;
  
  protected boolean showIndividuals;
  
  public PerformanceStat(String id) {
    // NB! Put this test at the top of all dynamic methods in this class.
    if (!VizDebugUtils.isDebugEnabled()) {
      return;
    }

    this.id = id;
  }
  
  public void init() {
    // NB! Put this test at the top of all dynamic methods in this class.
    if (!VizDebugUtils.isDebugEnabled()) {
      return;
    }

    sum = 0;
    sumOfSquares = 0;
    count = 0;
    showIndividuals = false;
    
    initTime = System.currentTimeMillis();
  }
  
  public void setShowIndividuals(boolean showIndividuals) {
    // NB! Put this test at the top of all dynamic methods in this class.
    if (!VizDebugUtils.isDebugEnabled()) {
      return;
    }

    this.showIndividuals = showIndividuals;
  }
  
  public void startOp() {
    // NB! Put this test at the top of all dynamic methods in this class.
    if (!VizDebugUtils.isDebugEnabled()) {
      return;
    }

    startTime = System.currentTimeMillis();
  }
  
  public void stopOp() {
    // NB! Put this test at the top of all dynamic methods in this class.
    if (!VizDebugUtils.isDebugEnabled()) {
      return;
    }

    long currentTime = System.currentTimeMillis() - startTime;
    
    count++;
    sum += currentTime;
    sumOfSquares += currentTime * currentTime;
    
    if (count == 1 || currentTime < min) {
      min = currentTime;
    }
    if (count == 1 || currentTime > max) {
      max = currentTime;
    }
    
    if (showIndividuals) {
      VizDebugUtils.debug("Value(" + id + "): " + String.valueOf(currentTime));
    }
  }
  
  public void report() {
    // NB! Put this test at the top of all dynamic methods in this class.
    if (!VizDebugUtils.isDebugEnabled()) {
      return;
    }

    long reportMakingStartTime = System.currentTimeMillis();
    VizDebugUtils.debug("--------------------------------------------------" +
    "--------------------------------------------------");
    VizDebugUtils.debug("PerformanceStat - Report (" + id + ")");
    VizDebugUtils.debug("--------------------------------------------------" +
                        "--------------------------------------------------");
    if (count == 0) {
      VizDebugUtils.debug("  No iterations executed.");
      return;
    }
    long overallTime = System.currentTimeMillis() - initTime;
    long average = sum / count;
    
    double standardDeviation = standardDev(sum, sumOfSquares, count);
    int sdPercentageAvg = (int)(standardDeviation * 100 / average);

    int percentage = (int)(((double)sum) * 100 / overallTime);
    
    long reportMakingTime = System.currentTimeMillis() - reportMakingStartTime;
    int reportMakingPercentage = (int)(((double)reportMakingTime) * 100 /
        overallTime);
    
    VizDebugUtils.debug("  Overall time: " + 
        VizDebugUtils.formatTimeDeltaValue(overallTime));
    VizDebugUtils.debug("  Report time: " + 
        VizDebugUtils.formatTimeDeltaValue(reportMakingTime) + "(" +
        reportMakingPercentage + "%)");
    VizDebugUtils.debug("  Sum times: " + 
        VizDebugUtils.formatTimeDeltaValue(sum) + "(" + percentage + "%)");
    VizDebugUtils.debug("  Count times: " + count);
    VizDebugUtils.debug("  Average time: " + 
        VizDebugUtils.formatTimeDeltaValue(average));
    VizDebugUtils.debug("  Minimum time: " + 
        VizDebugUtils.formatTimeDeltaValue(min));
    VizDebugUtils.debug("  Maximum time: " + 
        VizDebugUtils.formatTimeDeltaValue(max));
    VizDebugUtils.debug("  Standard deviation: " +
        VizDebugUtils.formatTimeDeltaValue((long)standardDeviation) +
        "(" + sdPercentageAvg + "% of average)");
  }

  /**
   * Given the sum of the values, the sum of the squares of the values and the
   * number of values in a collection numbers, calculates the standard deviation
   * @param sum The sum of the values.
   * @param sumOfSquares The sum of the squares of the values.
   * @param count The number of values.
   * @return The standard deviation of the values.
   */
  public static double standardDev(long sum, long sumOfSquares, long count) {
    /*
     * The variance is equal to the sum of the squares of each data item minus the
     * mean value. All of this is devided by the number of data items. I.e.:
     *   variance = sum[i in 1 to n](sq(xi - mean)) / n
     *            = sum[i in 1 to n](sq(xi) - 2 * xi * mean + square(mean)) / n
     *            = (sum[i in 1 to n](sq(xi))
     *               - 2 * mean * sum[i in 1 to n](xi)
     *               + n * square(mean)) / n
     *            = (sum[i in 1 to n](sq(xi))
     *               + mean * (n * mean - 2 * sum[i in 1 to n](xi))) / n
     *            = (sum[i in 1 to n](sq(xi))
     *               + mean * (sum[i in 1 to n](xi) - 2 * sum[i in 1 to n](xi))
     *              ) / n
     *            = (sum[i in 1 to n](sq(xi)) - mean * sum[i in 1 to n](xi)) / n
     *            = sumOfSquares / count - square(mean)
     * Having preprocessed all the sums of the data, the variance, and thus the
     * standard deviation can be calculated without traversing the raw data,
     * and hence without building up a collection.
     * This can save a lot of memory when monitoring large topic maps.
     */
    double mean = ((double)sum) / count;
    double variance = sumOfSquares / count - mean * mean;
    double sd = Math.sqrt(variance);
    return sd;
  }
}
