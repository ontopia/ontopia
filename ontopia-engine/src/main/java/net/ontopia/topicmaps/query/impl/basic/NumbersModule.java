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

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import net.ontopia.topicmaps.impl.utils.Argument;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateSignature;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;
import net.ontopia.topicmaps.query.parser.ModuleIF;
import net.ontopia.topicmaps.query.parser.PredicateIF;
import net.ontopia.utils.OntopiaRuntimeException;

public class NumbersModule implements ModuleIF {

  public static final String MODULE_URI = 
    "http://psi.ontopia.net/tolog/numbers/";
  private static final String SHARED_SIGNATURE = "n n! n!+";

  private Map<String, PredicateIF> predicates;

  public NumbersModule() {
    predicates = new HashMap<String, PredicateIF>();
    add(new NumbersValuePredicate());
    add(new NumbersFormatPredicate());
    add(new NumbersAbsolutePredicate());
    add(new NumbersAddPredicate());
    add(new NumbersSubtractPredicate());
    add(new NumbersMultiplyPredicate());
    add(new NumbersDividePredicate());
    add(new NumbersMinimumPredicate());
    add(new NumbersMaximumPredicate());
  }

  private void add(PredicateIF predicate) {
    predicates.put(predicate.getName(), predicate);
  }

  @Override
  public PredicateIF getPredicate(String name) {
    return predicates.get(name);
  }


  // --- NumberSupport -- enum for supported Number types, currently Integer and Float
  public enum NumberSupport {

    INTEGER(32), FLOAT(320);

    private int precisionRanking;

    private NumberSupport(int precisionRanking) {
      this.precisionRanking = precisionRanking;
    }

    private static NumberSupport fromClass(Class klass) {
      if (Integer.class.isAssignableFrom(klass)) {
        return INTEGER;
      }
      if (Float.class.isAssignableFrom(klass)) {
        return FLOAT;
      }
      return null;
    }

    public static NumberSupport getHighestPrecision(Number[] values) throws InvalidQueryException {
      int highestPrecision = 0;
      NumberSupport highestPrecisionNumber = null;
      for (Number value : values) {
        NumberSupport currentNumber = NumberSupport.fromClass(value.getClass());
        int currentPrecision = currentNumber.precisionRanking;
        if (currentPrecision > highestPrecision) {
          highestPrecision = currentPrecision;
          highestPrecisionNumber = currentNumber;
        }
      }
      return highestPrecisionNumber;
    }

    public static Integer[] castToIntegers(Number[] values) {
      Integer[] result = new Integer[values.length];
      for (int i = 0; i < values.length; i++) {
        result[i] = values[i].intValue();
      }
      return result;
    }

    public static Float[] castToFloats(Number[] values) {
      Float[] result = new Float[values.length];
      for (int i = 0; i < values.length; i++) {
        result[i] = values[i].floatValue();
      }
      return result;
    }

    public static Number castToSupportedClass(Number value) {
      return (Integer.class.isAssignableFrom(value.getClass()))
        ? value
        : value.floatValue();
    }

  }


  // --- AbstractPredicate
  abstract class AbstractPredicate implements BasicPredicateIF {

    @Override
    public int getCost(boolean[] boundparams) {
      try {
        int signLenghth = -1;
        PredicateSignature sign = PredicateSignature.getSignature(this);
        for (int ix = 0; ix < boundparams.length; ix++) {
          Argument arg = sign.getArgument(ix);
          if (arg != null) {
            signLenghth = ix;
          } else {
            Argument lastArg = sign.getArgument(signLenghth);
            if (lastArg.isRepeatable()) {
              arg = lastArg;
            }
          }
          if (arg == null) {
            throw new InvalidQueryException("Cost for predicate '" + getName() + "' cannot be calculated. Argument " + ix + " is null.");
          }
          if (arg.mustBeBound() && !boundparams[ix]) {
            return PredicateDrivenCostEstimator.INFINITE_RESULT;
          }
        }
        return PredicateDrivenCostEstimator.FILTER_RESULT;
      } catch (InvalidQueryException e) {
        throw new OntopiaRuntimeException(e);
      }
    }

    protected void addMatch(QueryMatches result, Object[] row) {
      if (result.last + 1 == result.size) {
        result.increaseCapacity();
      }
      result.data[++result.last] = row;
    }

  }


  // --- AbstractNumbersPredicate -- works with exactly one unbound argument
  abstract class AbstractNumbersPredicate extends AbstractPredicate {

    private Integer unboundArgument = null;

    protected void calculateUnboundArgument() throws InvalidQueryException {
      PredicateSignature sign = PredicateSignature.getSignature(this);
      int ix;
      for (Argument arg = sign.getArgument(ix = 0); arg != null; arg = sign.getArgument(++ix)) {
        if (!arg.mustBeBound()) {
          if (unboundArgument == null) {
            unboundArgument = ix;
          } else {
            throw new InvalidQueryException("AbstractNumbersPredicate works only with predicates with one unbound argument, " + 
              "predicate '" + getName() + "' has multiple unbound arguments in signature '" + getSignature() + "'");
          }
        }
      }
      if (unboundArgument == null) {
        throw new InvalidQueryException("AbstractNumbersPredicate works only with predicates with one unbound argument, " + 
          "predicate '" + getName() + "' has no unbound arguments in signature '" + getSignature() + "'");
      }
    }

    @Override
    public QueryMatches satisfy(QueryMatches matches, Object[] arguments) throws InvalidQueryException {
      if (unboundArgument == null) {
        calculateUnboundArgument();
      }

      PredicateSignature sign = PredicateSignature.getSignature(this);
      sign.verifyBound(matches, arguments, this);

      int[] colix = new int[arguments.length];
      for (int i = 0; i < arguments.length; i++) {
        colix[i] = matches.getIndex(arguments[i]);
      }
      int unboundColumn = colix[unboundArgument];

      QueryMatches result = new QueryMatches(matches);
      for (int ix = 0; ix <= matches.last; ix++) {
        Object[] row = matches.data[ix];
        List<Number> boundValues = new ArrayList<Number>();
        for (int i = 0; i < arguments.length; i++) {
          if (i != unboundArgument) {
            boundValues.add((Number) row[colix[i]]);
          }
        }
        Number[] boundValuesArray = boundValues.toArray(new Number[boundValues.size()]);
        Number resultValue = calculateResult(boundValuesArray);
        if (matches.bound(unboundColumn)) {
          if (resultValue.equals((Number) row[unboundColumn])) {
            addMatch(result, row);
          }
        } else {
          Object[] newRow = row.clone();
          newRow[unboundColumn] = resultValue;
          addMatch(result, newRow);
        }
      }
      return result;
    }

    protected Number calculateResult(Number[] values) throws InvalidQueryException {
      switch (NumberSupport.getHighestPrecision(values)) {
        case INTEGER: return calculateResult(NumberSupport.castToIntegers(values));
        case FLOAT:   return calculateResult(NumberSupport.castToFloats(values));
        default:      return null;
      }
    }

    protected abstract Number calculateResult(Integer[] values) throws InvalidQueryException;

    protected abstract Number calculateResult(Float[] values) throws InvalidQueryException;

  }

  // --- AbstractNumbersArrayPickerPredicate -- returns element from array, in stead of calculated result
  abstract class AbstractNumbersArrayPickerPredicate extends AbstractNumbersPredicate {

    @Override
    protected Number calculateResult(Number[] values) throws InvalidQueryException {
      switch (NumberSupport.getHighestPrecision(values)) {
        case INTEGER: return values[calculateResult(NumberSupport.castToIntegers(values)).intValue()];
        case FLOAT:   return values[calculateResult(NumberSupport.castToFloats(values)).intValue()];
        default:      return null;
      }
    }

  }

  // --- value(string, result, pattern?, locale?)
  class NumbersValuePredicate extends AbstractPredicate {

    @Override
    public String getName() {
      return "value";
    }

    @Override
    public String getSignature() {
      return "s! n s!? s!?";
    }

    @Override
    public QueryMatches satisfy(QueryMatches matches, Object[] arguments) throws InvalidQueryException {
      PredicateSignature sign = PredicateSignature.getSignature(this);
      sign.verifyBound(matches, arguments, this);

      if (arguments.length == 3) {
        throw new InvalidQueryException("Variable 'locale' must be provided" +
                                        " when 'pattern' is used in predicate " +
                                        this.getName());
      }

      int colix1 = matches.getIndex(arguments[0]);
      int colix2 = matches.getIndex(arguments[1]);
      int colix3 = (arguments.length > 3) ? matches.getIndex(arguments[2]) : -1;
      int colix4 = (arguments.length > 3) ? matches.getIndex(arguments[3]) : -1;

      QueryMatches result = new QueryMatches(matches);
      for (int ix = 0; ix <= matches.last; ix++) {
        Object[] row = matches.data[ix];
        Number resultValue = 
          (arguments.length > 3) ? calculateResult((String) row[colix1], (String) row[colix3], (String) row[colix4]) :
                                   calculateResult((String) row[colix1]);
        if (matches.bound(colix2)) {
          if (resultValue.equals((Number) row[colix2])) {
            addMatch(result, row);
          }
        } else {
              Object[] newRow = row.clone();
              newRow[colix2] = resultValue;
          addMatch(result, newRow);
        }
      }
      return result;
    }

    private Number calculateResult(String value) throws InvalidQueryException {
      try {
        return Integer.parseInt(value);
        } catch (NumberFormatException fallthrough) {
        try {
          return Float.parseFloat(value);
        } catch (NumberFormatException e) {
          throw new InvalidQueryException(e);
        }
        }
    }

    private Number calculateResult(String value, String pattern, String locale) throws InvalidQueryException {
      try {
        Locale l = new Locale(locale);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(l);
        DecimalFormat formatter = new DecimalFormat(pattern, symbols);
        return calculateResult(value, formatter);
      } catch (IllegalArgumentException e) {
        throw new InvalidQueryException(e);
      }
    }

    private Number calculateResult(String value, DecimalFormat formatter) throws InvalidQueryException {
      try {
        Number result = formatter.parse(value);
        return NumberSupport.castToSupportedClass(result);
      } catch (ParseException e) {
        throw new InvalidQueryException(e);
      }
    }

  }

  // --- format(number, result, pattern?, locale?)
  class NumbersFormatPredicate extends AbstractPredicate {

    @Override
    public String getName() {
      return "format";
    }

    @Override
    public String getSignature() {
      return "n! s s!? s!?";
    }

    @Override
    public QueryMatches satisfy(QueryMatches matches, Object[] arguments) throws InvalidQueryException {
      PredicateSignature sign = PredicateSignature.getSignature(this);
      sign.verifyBound(matches, arguments, this);

      if (arguments.length == 3) {
        throw new InvalidQueryException("Variable 'locale' must be provided" +
                                        " when 'pattern' is used in predicate " +
                                        this.getName());
      }

      int colix1 = matches.getIndex(arguments[0]);
      int colix2 = matches.getIndex(arguments[1]);
      int colix3 = (arguments.length > 3) ? matches.getIndex(arguments[2]) : -1;
      int colix4 = (arguments.length > 3) ? matches.getIndex(arguments[3]) : -1;

      try {
        QueryMatches result = new QueryMatches(matches);
        for (int ix = 0; ix <= matches.last; ix++) {
          Object[] row = matches.data[ix];
          String resultValue = 
            (arguments.length > 3) ? calculateResult((Number) row[colix1], (String) row[colix3], (String) row[colix4]) :
                                     calculateResult((Number) row[colix1]);
          if (matches.bound(colix2)) {
            if (resultValue.equals((String) row[colix2])) {
              addMatch(result, row);
            }
          } else {
                Object[] newRow = row.clone();
                newRow[colix2] = resultValue;
            addMatch(result, newRow);
          }
        }
        return result;
      } catch (IllegalArgumentException e) {
        throw new InvalidQueryException(e);
      }
    }

    private String calculateResult(Number value) {
      return value.toString();
    }

    private String calculateResult(Number value, String pattern, String locale) throws InvalidQueryException {
      try {
        Locale l = new Locale(locale);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(l);
        DecimalFormat formatter = new DecimalFormat(pattern, symbols);
        return formatter.format(value);
      } catch (IllegalArgumentException e) {
        throw new InvalidQueryException(e);
      }
    }

  }

  // --- absolute(number, result)
  class NumbersAbsolutePredicate extends AbstractNumbersPredicate {

    @Override
    public String getName() {
      return "absolute";
    }

    @Override
    public String getSignature() {
      return "n! n";
    }

    @Override
    protected Number calculateResult(Integer[] values) {
      return Math.abs(values[0]);
    }

    @Override
    protected Number calculateResult(Float[] values) {
      return Math.abs(values[0]);
    }

  }

  // --- add(result, number, number+)
  class NumbersAddPredicate extends AbstractNumbersPredicate {

    @Override
    public String getName() {
      return "add";
    }

    @Override
    public String getSignature() {
      return SHARED_SIGNATURE;
    }

    @Override
    protected Number calculateResult(Integer[] values) {
      int result = 0;
      for (int value : values) {
        result += value;
      }
      return result;
    }

    @Override
    protected Number calculateResult(Float[] values) {
      float result = 0.0f;
      for (float value : values) {
        result += value;
      }
      return result;
    }

  }

  // --- subtract(result, number, number+)
  class NumbersSubtractPredicate extends AbstractNumbersPredicate {

    @Override
    public String getName() {
      return "subtract";
    }

    @Override
    public String getSignature() {
      return SHARED_SIGNATURE;
    }

    @Override
    protected Number calculateResult(Integer[] values) {
      int result = 0;
      int first = -1;
      for (int value : values) {
        result -= (first * value);
        first = 1;
      }
      return result;
    }

    @Override
    protected Number calculateResult(Float[] values) {
      float result = 0.0f;
      int first = -1;
      for (float value : values) {
        result -= (first * value);
        first = 1;
      }
      return result;
    }

  }

  // --- multiply(result, number, number+)
  class NumbersMultiplyPredicate extends AbstractNumbersPredicate {

    @Override
    public String getName() {
      return "multiply";
    }

    @Override
    public String getSignature() {
      return SHARED_SIGNATURE;
    }

    @Override
    protected Number calculateResult(Integer[] values) {
      int result = 1;
      for (int value : values) {
        result *= value;
      }
      return result;
    }

    @Override
    protected Number calculateResult(Float[] values) {
      float result = 1.0f;
      for (float value : values) {
        result *= value;
      }
      return result;
    }

  }

  // --- divide(result, number, number+)
  class NumbersDividePredicate extends AbstractNumbersPredicate {

    @Override
    public String getName() {
      return "divide";
    }

    @Override
    public String getSignature() {
      return SHARED_SIGNATURE;
    }

    @Override
    protected Number calculateResult(Integer[] values) throws InvalidQueryException {
      try {
        int result = 1;
        boolean first = true;
        for (int value : values) {
          result = (first)
            ? (result * value)
            : (result / value);
          first = false;
        }
        return result;
      } catch (ArithmeticException e) { // e.g. devide by 0
        throw new InvalidQueryException(e);
      }
    }

    @Override
    protected Number calculateResult(Float[] values) throws InvalidQueryException {
      try {
        float result = 1.0f;
        boolean first = true;
        for (float value : values) {
          result = (first)
            ? (result * value)
            : (result / value);
          first = false;
        }
        return result;
      } catch (ArithmeticException e) { // e.g. devide by 0
        throw new InvalidQueryException(e);
      }
    }

  }

  // --- min(result, number, number+)
  class NumbersMinimumPredicate extends AbstractNumbersArrayPickerPredicate {

    @Override
    public String getName() {
      return "min";
    }

    @Override
    public String getSignature() {
      return SHARED_SIGNATURE;
    }

    @Override
    protected Number calculateResult(Integer[] values) {
      Integer minimum = values[0];
      int index = 0;
      int counter = 0;
      for (int value : values) {
        if (value < minimum) {
          minimum = value;
          index = counter;
        }
        counter++;
      }
      return index;
    }

    @Override
    protected Number calculateResult(Float[] values) {
      Float minimum = values[0];
      int index = 0;
      int counter = 0;
      for (float value : values) {
        if (value < minimum) {
          minimum = value;
          index = counter;
        }
        counter++;
      }
      return index;
    }

  }

  // --- max(result, number, number+)
  class NumbersMaximumPredicate extends AbstractNumbersArrayPickerPredicate {

    @Override
    public String getName() {
      return "max";
    }

    @Override
    public String getSignature() {
      return SHARED_SIGNATURE;
    }

    @Override
    protected Number calculateResult(Integer[] values) {
      Integer maximum = values[0];
      int index = 0;
      int counter = 0;
      for (int value : values) {
        if (value > maximum) {
          maximum = value;
          index = counter;
        }
        counter++;
      }
      return index;
    }

    @Override
    protected Number calculateResult(Float[] values) {
      Float maximum = values[0];
      int index = 0;
      int counter = 0;
      for (float value : values) {
        if (value > maximum) {
          maximum = value;
          index = counter;
        }
        counter++;
      }
      return index;
    }

  }

}
