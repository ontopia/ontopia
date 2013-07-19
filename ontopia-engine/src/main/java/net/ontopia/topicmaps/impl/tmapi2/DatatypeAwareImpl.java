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

package net.ontopia.topicmaps.impl.tmapi2;

import java.math.BigDecimal;
import java.math.BigInteger;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.DataTypes;

import org.tmapi.core.DatatypeAware;
import org.tmapi.core.Locator;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public abstract class DatatypeAwareImpl extends ScopedImpl implements
    DatatypeAware {

  private static final LocatorIF XSD_INT = URILocator.create("http://www.w3.org/2001/XMLSchema#int");

  public DatatypeAwareImpl(TopicMapImpl topicMap) {
    super(topicMap);
  }

  /**
   * Sets the value / datatype pair.
   * 
   * Methods which invoke this method have to ensure that value and datatype
   * is never null.
   */
  protected abstract void setValue(String value, LocatorIF datatype);

  /* (non-Javadoc)
   * @see org.tmapi.core.DatatypeAware#decimalValue()
   */
  
  public BigDecimal decimalValue() {
    return new BigDecimal(getValue());
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.DatatypeAware#floatValue()
   */
  
  public float floatValue() {
    return decimalValue().floatValue();
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.DatatypeAware#intValue()
   */
  
  public int intValue() {
    return decimalValue().intValue();
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.DatatypeAware#integerValue()
   */
  
  public BigInteger integerValue() {
    return decimalValue().toBigInteger();
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.DatatypeAware#longValue()
   */
  
  public long longValue() {
    return decimalValue().longValue();
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.DatatypeAware#setValue(java.math.BigDecimal)
   */
  
  public void setValue(BigDecimal value) {
    Check.valueNotNull(this, value);
    setValue(value.toString(), DataTypes.TYPE_DECIMAL);
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.DatatypeAware#setValue(java.math.BigInteger)
   */
  
  public void setValue(BigInteger value) {
    Check.valueNotNull(this, value);
    setValue(value.toString(), DataTypes.TYPE_INTEGER);
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.DatatypeAware#setValue(long)
   */
  
  public void setValue(long value) {
    setValue(Long.toString(value), DataTypes.TYPE_LONG);
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.DatatypeAware#setValue(float)
   */
  
  public void setValue(float value) {
    setValue(Float.toString(value), DataTypes.TYPE_FLOAT);
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.DatatypeAware#setValue(int)
   */
  
  public void setValue(int value) {
	  // the TMAPI  demands a xsd:int for int
    setValue(Integer.toString(value), XSD_INT);
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.DatatypeAware#setValue(java.lang.String, org.tmapi.core.Locator)
   */
  
  public void setValue(String value, Locator datatype) {
    Check.valueNotNull(this, value, datatype);
    setValue(value, topicMap.unwrapLocator(datatype));
  }

}
