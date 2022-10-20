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

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import net.ontopia.utils.OntopiaRuntimeException;


/**
 * Manages probability values for given keys, so that all probability values
 * always add up to 1. If a new probability value is added, the other values
 * are added. This can be used to build up a probability distribution
 * incrementally while ensuring that the probabilities never add up to more
 * than 1, i.e. 100%.
 * Currently not used anywhere.
 * Should add a method that guesses a 0.0 ... 1.0 number and returns a
 * corresponding key.
 */
public class ProbabilityManager {
  private Map probabilities;
  private Random random;

  public ProbabilityManager(Object key) {
    probabilities = new TreeMap();
    probabilities.put(key, 1.0);
    random = new Random();
  }
  
  /**
   * Set key to be compKey * compFactor and adjust all values so that they still
   * add up to 1 and at the same time, the value of compKey remains the same
   * relative to other values.
   * @param key The key to add (may be there already, in which case it's changed
   * @param compKey Use the value of 'compKey' to create the value for 'key'
   * @param compFactor Let 'key' = 'compFactor' * 'compKey'.
   */
  public void addProbability(Object key, Object compKey, double compFactor) {
    if (compFactor < 0) {
      throw new OntopiaRuntimeException("Internal error, received negative" +
          " probability factor.");
    }

    Double compValue = (Double)probabilities.get(compKey);
    double compVal = compValue.doubleValue();

    // The new value of key before adjustment.
    double newVal = compVal * compFactor;
    
    // The old value of key.
    double oldVal = 0;
    Double oldValue = (Double)probabilities.get(key);
    if (oldValue != null) {
      oldVal = oldValue.doubleValue();
    }
    
    // The temporarily new sum before adjusting.
    double sum = 1 + newVal - oldVal;
    
    // Factor that all old values should be multiplied by.
    double factor = 1 / sum;

    // Set this probability.
    probabilities.put(key, newVal);
    
    // Update all other probability values so they add up to 1.
    Iterator keysIt = probabilities.keySet().iterator();
    while (keysIt.hasNext()) {
      Object currentKey = keysIt.next();
      Double currentValue = (Double)probabilities.get(currentKey);
      probabilities.put(currentKey, currentValue.doubleValue() * factor);
    }
  }
  
  public Object guessKey() {
    double valueGuess = random.nextDouble();
    return guessKey(valueGuess);
  }
  
  public Object guessKey(double valueGuess) {
    double cummulator = 0;
    Iterator keysIt = probabilities.keySet().iterator();
    Object currentKey = null;
    while (keysIt.hasNext()) {
      currentKey = keysIt.next();
      Double currentValue = (Double)probabilities.get(currentKey);
      cummulator += currentValue.doubleValue();
      if (valueGuess < cummulator) {
        return currentKey;
      }
    }
    return currentKey;
  }
  
  public Double getProbability(Object key) {
    return (Double)probabilities.get(key);
  }
  
  public double getProbabilityValue(Object key) {
    Double probability = getProbability(key);
    if (probability == null) {
      return 0;
    }
    return probability.doubleValue();
  }
  
  public static void test() {
    System.out.println("Testing ProbabilityMnager");
    String key1 = "key1: ";
    String key2 = "key2: ";
    String key3 = "key3: ";
    String key4 = "key4: ";
    ProbabilityManager man = new ProbabilityManager(key1);
    System.out.println("-----------------------------");
    System.out.println(key1 + man.getProbabilityValue(key1));
    System.out.println(key2 + man.getProbabilityValue(key2));
    System.out.println(key3 + man.getProbabilityValue(key3));
    System.out.println(key4 + man.getProbabilityValue(key4));
    man.addProbability(key2, key1, 3);
    System.out.println("-----------------------------");
    System.out.println(key1 + man.getProbabilityValue(key1));
    System.out.println(key2 + man.getProbabilityValue(key2));
    System.out.println(key3 + man.getProbabilityValue(key3));
    System.out.println(key4 + man.getProbabilityValue(key4));
    man.addProbability(key2, key1, 2);
    System.out.println("-----------------------------");
    System.out.println(key1 + man.getProbabilityValue(key1));
    System.out.println(key2 + man.getProbabilityValue(key2));
    System.out.println(key3 + man.getProbabilityValue(key3));
    System.out.println(key4 + man.getProbabilityValue(key4));
    man.addProbability(key3, key2, 2);
    System.out.println("-----------------------------");
    System.out.println(key1 + man.getProbabilityValue(key1));
    System.out.println(key2 + man.getProbabilityValue(key2));
    System.out.println(key3 + man.getProbabilityValue(key3));
    System.out.println(key4 + man.getProbabilityValue(key4));
    
    System.out.println("0.0: " + man.guessKey(0.0));
    System.out.println("0.1: " + man.guessKey(0.1));
    System.out.println("0.2: " + man.guessKey(0.2));
    System.out.println("0.3: " + man.guessKey(0.3));
    System.out.println("0.4: " + man.guessKey(0.4));
    System.out.println("0.5: " + man.guessKey(0.5));
    System.out.println("0.6: " + man.guessKey(0.6));
    System.out.println("0.7: " + man.guessKey(0.7));
    System.out.println("0.8: " + man.guessKey(0.8));
    System.out.println("0.9: " + man.guessKey(0.9));
    System.out.println("1: " + man.guessKey(1));
    
    System.out.println("0.0: " + man.guessKey(0.0));
    System.out.println("0.1: " + man.guessKey(0.1));
    System.out.println("0.2: " + man.guessKey(0.2));
    System.out.println("0.3: " + man.guessKey(0.3));
    System.out.println("0.4: " + man.guessKey(0.4));
    System.out.println("0.5: " + man.guessKey(0.5));
    System.out.println("0.6: " + man.guessKey(0.6));
    System.out.println("0.7: " + man.guessKey(0.7));
    System.out.println("0.8: " + man.guessKey(0.8));
    System.out.println("0.9: " + man.guessKey(0.9));
    System.out.println("1: " + man.guessKey(1));
    
    int key1Count = 0;
    int key2Count = 0;
    int key3Count = 0;
    int iterations = 1000000;
    for (int i = 0 ; i < iterations; i++) {
      if (man.guessKey() == key1) {
        key1Count++;
      }
      if (man.guessKey() == key2) {
        key2Count++;
      }
      if (man.guessKey() == key3) {
        key3Count++;
      }
    }
    System.out.println("Key1Count: " + key1Count + ", " + 
        ((double)key1Count)/iterations);
    System.out.println("Key2Count: " + key2Count + ", " + 
        ((double)key2Count)/iterations);
    System.out.println("Key3Count: " + key3Count + ", " + 
        ((double)key3Count)/iterations);
  }
}
