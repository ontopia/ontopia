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

package net.ontopia.topicmaps.query.impl.basic;

import java.util.HashMap;
import java.util.Map;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.impl.utils.Argument;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateSignature;
import net.ontopia.topicmaps.query.parser.ModuleIF;
import net.ontopia.topicmaps.query.parser.PredicateIF;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

public class StringModule implements ModuleIF {
  public static final String MODULE_URI = 
      "http://psi.ontopia.net/tolog/string/";
  
  private Map predicates;
  
  public StringModule() {
    predicates = new HashMap();
    add(new StartsWithPredicate());
    add(new ContainsPredicate());
    add(new LengthPredicate());
    add(new ConcatPredicate());
    add(new TranslatePredicate());
    add(new SubstringPredicate());
    add(new SubstringAfterPredicate());
    add(new SubstringBeforePredicate());
    add(new EndsWithPredicate());
    add(new LastIndexOfPredicate());
    add(new IndexOfPredicate());
  }

  private void add(PredicateIF predicate) {
    predicates.put(predicate.getName(), predicate);
  }
  
  @Override
  public PredicateIF getPredicate(String name) {
    return (PredicateIF) predicates.get(name);
  }

  // --- abstract ---------------------------------------------------------
  abstract class AbstractPredicate implements BasicPredicateIF {
    @Override
    public int getCost(boolean[] boundparams) {
      try {
        PredicateSignature sign = PredicateSignature.getSignature(this);
        for (int ix = 0; ix < boundparams.length; ix++) {
          Argument arg = sign.getArgument(ix);
          if (arg == null)
            throw new OntopiaRuntimeException("INTERNAL ERROR");
          
          if (arg.mustBeBound() && !boundparams[ix])
            return PredicateDrivenCostEstimator.INFINITE_RESULT;
        }

        return PredicateDrivenCostEstimator.FILTER_RESULT;
      } catch (InvalidQueryException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  }

  // --- concat(string, string, string) -----------------------------------

  class ConcatPredicate extends AbstractPredicate {

    @Override
    public String getName() {
      return "concat";
    }

    @Override
    public String getSignature() {
      return "s s! s!";
    }
    
    @Override
    public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
      throws InvalidQueryException {

      PredicateSignature sign = PredicateSignature.getSignature(this);
      sign.verifyBound(matches, arguments, this);
      
      int colix1 = matches.getIndex(arguments[0]);
      int colix2 = matches.getIndex(arguments[1]);
      int colix3 = matches.getIndex(arguments[2]);
      
      if (matches.bound(colix1))
        return filter(matches, colix1, colix2, colix3);
      else
        return concat(matches, colix1, colix2, colix3);
    }

    private QueryMatches filter(QueryMatches matches, int ix1, int ix2,
                                int ix3) {
      QueryMatches result = new QueryMatches(matches);
    
      for (int ix = 0; ix <= matches.last; ix++) {
        // verify types of objects
        if (!(matches.data[ix][ix1] instanceof String &&
              matches.data[ix][ix2] instanceof String &&
              matches.data[ix][ix3] instanceof String))
          continue;

        // check value found against value given
        String str = (String) matches.data[ix][ix1];
        String str2 = ((String) matches.data[ix][ix2]) + matches.data[ix][ix3];
        if (str == null || !str.equals(str2))
          continue;

        // ok, add match
        if (result.last + 1 == result.size) 
          result.increaseCapacity();
        result.last++;
        result.data[result.last] = matches.data[ix];
      }

      return result;
    }

    private QueryMatches concat(QueryMatches matches, int ix1, int ix2,
                                int ix3) {
      QueryMatches result = new QueryMatches(matches);
      for (int ix = 0; ix <= matches.last; ix++) {
        if (!(matches.data[ix][ix2] instanceof String &&
              matches.data[ix][ix3] instanceof String))
          continue;
      
        Object[] newRow = (Object[]) matches.data[ix].clone();
        newRow[ix1] = ((String) newRow[ix2]) + newRow[ix3];

        if (result.last + 1 == result.size) 
          result.increaseCapacity();
        result.last++;
        result.data[result.last] = newRow;
      }

      return result;
    }
  }
  
  // --- starts-with(string, string) --------------------------------------

  // needs to be public so QueryOptimizer can access it
  public class StartsWithPredicate extends AbstractPredicate {
    @Override
    public String getName() {
      return "starts-with";
    }

    @Override
    public String getSignature() {
      return "s! s!";
    }
    
    @Override
    public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
      throws InvalidQueryException {

      PredicateSignature sign = PredicateSignature.getSignature(this);
      sign.verifyBound(matches, arguments, this);
      
      int colix1 = matches.getIndex(arguments[0]);
      int colix2 = matches.getIndex(arguments[1]);
      
      return PredicateUtils.filter(matches, colix1, colix2,
                                   String.class, String.class,
                                   PredicateUtils.FILTER_STR_STARTS_WITH);
    }
  }

  // --- ends-with(string, string) --------------------------------------

  public class EndsWithPredicate extends AbstractPredicate {
    @Override
    public String getName() {
      return "ends-with";
    }

    @Override
    public String getSignature() {
      return "s! s!";
    }
    
    @Override
    public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
      throws InvalidQueryException {

      PredicateSignature sign = PredicateSignature.getSignature(this);
      sign.verifyBound(matches, arguments, this);
      
      int colix1 = matches.getIndex(arguments[0]);
      int colix2 = matches.getIndex(arguments[1]);
      
      return PredicateUtils.filter(matches, colix1, colix2,
                                   String.class, String.class,
                                   PredicateUtils.FILTER_STR_ENDS_WITH);
    }
  }

  // --- contains(string, string) -----------------------------------------

  class ContainsPredicate extends AbstractPredicate {

    @Override
    public String getName() {
      return "contains";
    }

    @Override
    public String getSignature() {
      return "s! s!";
    }
    
    @Override
    public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
      throws InvalidQueryException {

      PredicateSignature sign = PredicateSignature.getSignature(this);
      sign.verifyBound(matches, arguments, this);
      
      int colix1 = matches.getIndex(arguments[0]);
      int colix2 = matches.getIndex(arguments[1]);
      
      return PredicateUtils.filter(matches, colix1, colix2,
                                   String.class, String.class,
                                   PredicateUtils.FILTER_STR_CONTAINS);
    }
  }

  // --- length(string, number) -------------------------------------------

  class LengthPredicate extends AbstractPredicate {

    @Override
    public String getName() {
      return "length";
    }

    @Override
    public String getSignature() {
      return "s! i";
    }
    
    @Override
    public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
      throws InvalidQueryException {

      PredicateSignature sign = PredicateSignature.getSignature(this);
      sign.verifyBound(matches, arguments, this);
      
      int strix = matches.getIndex(arguments[0]);
      int numix = matches.getIndex(arguments[1]);

      if (matches.bound(numix))
        return PredicateUtils.filter(matches, strix, numix,
                                     String.class, Number.class,
                                     PredicateUtils.FILTER_STR_LENGTH);
      else
        return PredicateUtils.objectToOne(matches, strix, numix, String.class,
                                          PredicateUtils.STR_TO_LENGTH);
    }
  }

  // --- translate(string, string, string, string) ------------------------

  class TranslatePredicate extends AbstractPredicate {

    @Override
    public String getName() {
      return "translate";
    }

    @Override
    public String getSignature() {
      return "s s! s! s! s!?";
    }
    
    @Override
    public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
      throws InvalidQueryException {

      PredicateSignature sign = PredicateSignature.getSignature(this);
      sign.verifyBound(matches, arguments, this);
      
      int str1ix = matches.getIndex(arguments[0]); // output
      int str2ix = matches.getIndex(arguments[1]); // input
      matches.getIndex(arguments[2]); // fromstr
      matches.getIndex(arguments[3]); // tostr
      int str5ix = -1;                             // deletestr
      if (arguments.length > 4)
        str5ix = matches.getIndex(arguments[4]); 

      // FIXME: code assumes 2-3-4 are literals
      
      // create translation table
      TranslationTable table =
        new TranslationTable((String) arguments[2], (String) arguments[3],
                             str5ix != -1 ? (String) arguments[4] : null);
      
      if (matches.bound(str1ix))
        return filter(matches, str1ix, str2ix, table);
      else
        return translate(matches, str1ix, str2ix, table);
    }

    private QueryMatches filter(QueryMatches matches, int ix1, int ix2,
                                TranslationTable table) {
      QueryMatches result = new QueryMatches(matches);
    
      for (int ix = 0; ix <= matches.last; ix++) {
        // verify types of objects
        if (!(matches.data[ix][ix1] instanceof String &&
              matches.data[ix][ix2] instanceof String))
          continue;

        // check value found against value given
        String str = (String) matches.data[ix][ix1];
        String str2 = table.translate((String) matches.data[ix][ix2]);
        if (str == null || !str.equals(str2))
          continue;

        // ok, add match
        if (result.last + 1 == result.size) 
          result.increaseCapacity();
        result.last++;
        result.data[result.last] = matches.data[ix];
      }

      return result;
    }

    private QueryMatches translate(QueryMatches matches, int ix1, int ix2,
                                   TranslationTable table) {
      QueryMatches result = new QueryMatches(matches);
      for (int ix = 0; ix <= matches.last; ix++) {
        if (!(matches.data[ix][ix2] instanceof String))
          continue;
      
        Object[] newRow = (Object[]) matches.data[ix].clone();
        newRow[ix1] = table.translate((String) newRow[ix2]);

        if (result.last + 1 == result.size) 
          result.increaseCapacity();
        result.last++;
        result.data[result.last] = newRow;
      }

      return result;
    }

    class TranslationTable {
      public char[] conversion;
      public char lowest;
      public char highest;
      public String delete;
      private static final char DELETE = 0;

      public TranslationTable(String from, String to, String delete) {
        this.delete = delete; // TODO: consider array for binary search instead
        
        // find character range
        lowest = 0xFFFF;
        highest = 0;
        for (int ix = 0; ix < from.length(); ix++) {
          char ch = from.charAt(ix);
          if (ch < lowest)
            lowest = ch;
          if (ch > highest)
            highest = ch;
        }

        // make conversion table
        conversion = new char[(highest - lowest) + 1];
        if (delete != null) {
          for (int ix = lowest; ix < highest; ix++)
            if (delete.indexOf(ix) != -1)
              conversion[ix - lowest] = DELETE;
            else
              conversion[ix - lowest] = (char) ix;
        }
        
        for (int ix = 0; ix < from.length(); ix++) {
          char fch = from.charAt(ix);
          char tch = fch;
          if (ix < to.length()) // FIXME: report error instead?
            tch = to.charAt(ix);
          
          conversion[fch - lowest] = tch;
        }
      }

      public String translate(String input) {
        char[] buf = new char[input.length()];

        int pos = 0;
        for (int ix = 0; ix < input.length(); ix++) {
          char ch = input.charAt(ix);
          
          if (ch < lowest || ch > highest) {
            // character outside table
            if (delete == null || // XPath behaviour: delete all unknowns
                delete.indexOf(ch) != -1) // Python behaviour: delete only listed
              ch = DELETE;
          } else
            // character in table
            ch = conversion[ch - lowest];

          if (ch != DELETE)
            buf[pos++] = ch;
        }

        return new String(buf, 0, pos);
      }
    }
  }

  // --- substring(s, s, number, number?) ---------------------------------

  class SubstringPredicate extends AbstractPredicate {

    @Override
    public String getName() {
      return "substring";
    }

    @Override
    public String getSignature() {
      return "s s! i! i!?";
    }
    
    @Override
    public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
      throws InvalidQueryException {

      PredicateSignature sign = PredicateSignature.getSignature(this);
      sign.verifyBound(matches, arguments, this);
      
      int resix   = matches.getIndex(arguments[0]);
      int strix   = matches.getIndex(arguments[1]);
      int startix = matches.getIndex(arguments[2]);
      int endix   = -1;
      if (arguments.length == 4)
        endix = matches.getIndex(arguments[3]);
      
      if (matches.bound(resix))
        return filter(matches, resix, strix, startix, endix);
      else
        return substr(matches, resix, strix, startix, endix);
    }

    private QueryMatches filter(QueryMatches matches, int ix1, int ix2, int ix3,
                                int ix4) throws InvalidQueryException {
      QueryMatches result = new QueryMatches(matches);
    
      for (int ix = 0; ix <= matches.last; ix++) {
        // verify types of objects
        if (!(matches.data[ix][ix1] instanceof String &&
              matches.data[ix][ix2] instanceof String &&
              matches.data[ix][ix3] instanceof Integer &&
              (ix4 == -1 || matches.data[ix][ix4] instanceof Integer)))
          continue;

        // check value found against value given
        String str = (String) matches.data[ix][ix1];
        String str2 = ((String) matches.data[ix][ix2]);
        Integer int1 = ((Integer) matches.data[ix][ix3]);
        String sub;

        if (ix4 == -1)
          sub = str2.substring(int1.intValue());
        else {
          Integer int2 = ((Integer) matches.data[ix][ix4]);

          if (int2.compareTo(int1) < 0)
            throw new InvalidQueryException("The 3rd and 4th parameters to " +
                    "the the '" + getName() + "' predicate must be in " +
                    "increasing order.");
          
          sub = str2.substring(int1.intValue(), int2.intValue());
        }

        if (str == null || !str.equals(sub))
          continue;
        
        // ok, add match
        if (result.last + 1 == result.size) 
          result.increaseCapacity();
        result.last++;
        result.data[result.last] = matches.data[ix];
      }

      return result;
    }

    private QueryMatches substr(QueryMatches matches, int ix1, int ix2, int ix3,
                                int ix4) throws InvalidQueryException {
      QueryMatches result = new QueryMatches(matches);
      for (int ix = 0; ix <= matches.last; ix++) {
        // verify types
        if (!(matches.data[ix][ix2] instanceof String &&
              matches.data[ix][ix3] instanceof Integer &&
              (ix4 == -1 || matches.data[ix][ix4] instanceof Integer)))
          continue;
      
        Object[] newRow = (Object[]) matches.data[ix].clone();
        String str = ((String) matches.data[ix][ix2]);
        Integer int1 = ((Integer) matches.data[ix][ix3]);
        String sub;

        if (ix4 == -1)
          sub = str.substring(int1.intValue());
        else {
          Integer int2 = ((Integer) matches.data[ix][ix4]);

          if (int2.compareTo(int1) < 0)
            throw new InvalidQueryException("The 3rd and 4th parameters to " +
                    "the the '" + getName() + "' predicate must be in " +
                    "increasing order.");
          
          sub = str.substring(int1.intValue(), int2.intValue());
        }
        newRow[ix1] = sub;

        if (result.last + 1 == result.size) 
          result.increaseCapacity();
        result.last++;
        result.data[result.last] = newRow;
      }

      return result;
    }
  }

  // --- substring-after(s, s, s) -----------------------------------------

  class SubstringAfterPredicate extends AbstractPredicate {

    @Override
    public String getName() {
      return "substring-after";
    }

    @Override
    public String getSignature() {
      return "s s! s!";
    }
    
    @Override
    public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
      throws InvalidQueryException {

      PredicateSignature sign = PredicateSignature.getSignature(this);
      sign.verifyBound(matches, arguments, this);
      
      int resix = matches.getIndex(arguments[0]);
      int strix = matches.getIndex(arguments[1]);
      int subix = matches.getIndex(arguments[2]);
      
      if (matches.bound(resix))
        return filter(matches, resix, strix, subix);
      else
        return substr(matches, resix, strix, subix);
    }

    private QueryMatches filter(QueryMatches matches, int resix, int strix,
                                int subix) {
      QueryMatches result = new QueryMatches(matches);
    
      for (int ix = 0; ix <= matches.last; ix++) {
        // verify types of objects
        if (!(matches.data[ix][resix] instanceof String &&
              matches.data[ix][strix] instanceof String &&
              matches.data[ix][subix] instanceof String))
          continue;

        // get set values
        String res = (String) matches.data[ix][resix];
        String str = (String) matches.data[ix][strix];
        String sub = (String) matches.data[ix][subix];

        // verify
        int pos = str.indexOf(sub);
        if (pos == -1 || res == null || !res.equals(str.substring(pos + 
            sub.length())))
          continue;

        // ok, add match
        if (result.last + 1 == result.size) 
          result.increaseCapacity();
        result.last++;
        result.data[result.last] = matches.data[ix];
      }

      return result;
    }

    private QueryMatches substr(QueryMatches matches, int resix, int strix,
                                int subix) {
      QueryMatches result = new QueryMatches(matches);
      for (int ix = 0; ix <= matches.last; ix++) {
        // verify types of objects
        if (!(matches.data[ix][strix] instanceof String &&
              matches.data[ix][subix] instanceof String))
          continue;

        // get set values
        String str = (String) matches.data[ix][strix];
        String sub = ((String) matches.data[ix][subix]);

        // compute
        int pos = str.indexOf(sub);
        if (pos == -1)
          continue;
        String res = str.substring(pos + sub.length());

        // make new match
        Object[] newRow = (Object[]) matches.data[ix].clone();
        newRow[resix] = res;

        if (result.last + 1 == result.size) 
          result.increaseCapacity();
        result.last++;
        result.data[result.last] = newRow;
      }

      return result;
    }
  }

  // --- substring-before(s, s, s) -----------------------------------------

  class SubstringBeforePredicate extends AbstractPredicate {

    @Override
    public String getName() {
      return "substring-before";
    }

    @Override
    public String getSignature() {
      return "s s! s!";
    }
    
    @Override
    public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
      throws InvalidQueryException {

      PredicateSignature sign = PredicateSignature.getSignature(this);
      sign.verifyBound(matches, arguments, this);
      
      int resix = matches.getIndex(arguments[0]);
      int strix = matches.getIndex(arguments[1]);
      int subix = matches.getIndex(arguments[2]);
      
      if (matches.bound(resix))
        return filter(matches, resix, strix, subix);
      else
        return substr(matches, resix, strix, subix);
    }

    private QueryMatches filter(QueryMatches matches, int resix, int strix,
                                int subix) {
      QueryMatches result = new QueryMatches(matches);
    
      for (int ix = 0; ix <= matches.last; ix++) {
        // verify types of objects
        if (!(matches.data[ix][resix] instanceof String &&
              matches.data[ix][strix] instanceof String &&
              matches.data[ix][subix] instanceof String))
          continue;

        // get set values
        String res = (String) matches.data[ix][resix];
        String str = (String) matches.data[ix][strix];
        String sub = (String) matches.data[ix][subix];

        // verify
        int pos = str.indexOf(sub);
        if (pos == -1 || res == null || !res.equals(str.substring(0, pos)))
          continue;

        // ok, add match
        if (result.last + 1 == result.size) 
          result.increaseCapacity();
        result.last++;
        result.data[result.last] = matches.data[ix];
      }

      return result;
    }

    private QueryMatches substr(QueryMatches matches, int resix, int strix,
                                int subix) {
      QueryMatches result = new QueryMatches(matches);
      for (int ix = 0; ix <= matches.last; ix++) {
        // verify types of objects
        if (!(matches.data[ix][strix] instanceof String &&
              matches.data[ix][subix] instanceof String))
          continue;

        // get set values
        String str = (String) matches.data[ix][strix];
        String sub = ((String) matches.data[ix][subix]);

        // compute
        int pos = str.indexOf(sub);
        if (pos == -1)
          continue;
        String res = str.substring(0, pos);

        // make new match
        Object[] newRow = (Object[]) matches.data[ix].clone();
        newRow[resix] = res;

        if (result.last + 1 == result.size) 
          result.increaseCapacity();
        result.last++;
        result.data[result.last] = newRow;
      }

      return result;
    }
  }
  
  // --- last-index-of(n, s!, s!) -----------------------------------------

  class LastIndexOfPredicate extends AbstractPredicate {

    @Override
    public String getName() {
      return "last-index-of";
    }

    @Override
    public String getSignature() {
      return "i s! s!";
    }
    
    @Override
    public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
      throws InvalidQueryException {

      PredicateSignature sign = PredicateSignature.getSignature(this);
      sign.verifyBound(matches, arguments, this);
      
      int indexix = matches.getIndex(arguments[0]);
      int sourceix = matches.getIndex(arguments[1]);
      int targetix = matches.getIndex(arguments[2]);
      
      if (matches.bound(indexix))
        return filter(matches, indexix, sourceix, targetix);
      else
        return index(matches, indexix, sourceix, targetix);
    }

    private QueryMatches filter(QueryMatches matches, int ix1, int ix2, int ix3)
        throws InvalidQueryException {
      QueryMatches result = new QueryMatches(matches);
    
      for (int ix = 0; ix <= matches.last; ix++) {
        // verify types of objects
        if (!(matches.data[ix][ix1] instanceof Integer &&
              matches.data[ix][ix2] instanceof String &&
              matches.data[ix][ix3] instanceof String))
          continue;

        Integer int1 = ((Integer) matches.data[ix][ix1]);
        String str1 = (String) matches.data[ix][ix2];
        String str2 = ((String) matches.data[ix][ix3]);

        int index = str1.lastIndexOf(str2);

        // If the predicate can't be found, this is not a match
        // check value found against value given
        if (index == -1 || int1.intValue() != index)
          continue;
        
        // ok, add match
        if (result.last + 1 == result.size) 
          result.increaseCapacity();
        result.last++;
        result.data[result.last] = matches.data[ix];
      }

      return result;
    }

    private QueryMatches index(QueryMatches matches, int ix1, int ix2, int ix3)
        throws InvalidQueryException {
      QueryMatches result = new QueryMatches(matches);
      for (int ix = 0; ix <= matches.last; ix++) {
        // verify types
        if (!(matches.data[ix][ix2] instanceof String &&
              matches.data[ix][ix3] instanceof String))
          continue;
      
        Object[] newRow = (Object[]) matches.data[ix].clone();
        String str1 = ((String) matches.data[ix][ix2]);
        String str2 = ((String) matches.data[ix][ix3]);

        int index = str1.lastIndexOf(str2);
        
        // If the predicate can't be found, this is not a match
        // check value found against value given
        if (index == -1)
          continue;
        
        newRow[ix1] = new Integer(index);

        if (result.last + 1 == result.size) 
          result.increaseCapacity();
        result.last++;
        result.data[result.last] = newRow;
      }

      return result;
    }
  }
  
  // --- index-of(n, s!, s!) -----------------------------------------

  class IndexOfPredicate extends AbstractPredicate {

    @Override
    public String getName() {
      return "index-of";
    }

    @Override
    public String getSignature() {
      return "i s! s!";
    }
    
    @Override
    public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
      throws InvalidQueryException {

      PredicateSignature sign = PredicateSignature.getSignature(this);
      sign.verifyBound(matches, arguments, this);
      
      int indexix = matches.getIndex(arguments[0]);
      int sourceix = matches.getIndex(arguments[1]);
      int targetix = matches.getIndex(arguments[2]);
      
      if (matches.bound(indexix))
        return filter(matches, indexix, sourceix, targetix);
      else
        return index(matches, indexix, sourceix, targetix);
    }

    private QueryMatches filter(QueryMatches matches, int ix1, int ix2, int ix3)
        throws InvalidQueryException {
      QueryMatches result = new QueryMatches(matches);
    
      for (int ix = 0; ix <= matches.last; ix++) {
        // verify types of objects
        if (!(matches.data[ix][ix1] instanceof Integer &&
              matches.data[ix][ix2] instanceof String &&
              matches.data[ix][ix3] instanceof String))
          continue;

        Integer int1 = ((Integer) matches.data[ix][ix1]);
        String str1 = (String) matches.data[ix][ix2];
        String str2 = ((String) matches.data[ix][ix3]);

        int index = str1.indexOf(str2);

        // If the predicate can't be found, this is not a match
        // check value found against value given
        if (index == -1 || int1.intValue() != index)
          continue;
        
        // ok, add match
        if (result.last + 1 == result.size) 
          result.increaseCapacity();
        result.last++;
        result.data[result.last] = matches.data[ix];
      }

      return result;
    }

    private QueryMatches index(QueryMatches matches, int ix1, int ix2, int ix3)
        throws InvalidQueryException {
      QueryMatches result = new QueryMatches(matches);
      for (int ix = 0; ix <= matches.last; ix++) {
        // verify types
        if (!(matches.data[ix][ix2] instanceof String &&
              matches.data[ix][ix3] instanceof String))
          continue;
      
        Object[] newRow = (Object[]) matches.data[ix].clone();
        String str1 = ((String) matches.data[ix][ix2]);
        String str2 = ((String) matches.data[ix][ix3]);

        int index = str1.indexOf(str2);
        
        // If the predicate can't be found, this is not a match
        // check value found against value given
        if (index == -1)
          continue;
        
        newRow[ix1] = new Integer(index);

        if (result.last + 1 == result.size) 
          result.increaseCapacity();
        result.last++;
        result.data[result.last] = newRow;
      }

      return result;
    }
  }
}
