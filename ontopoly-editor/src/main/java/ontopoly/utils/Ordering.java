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

package ontopoly.utils;

import org.apache.commons.lang3.StringUtils;

public final class Ordering {

  public static final int ORDER_INCREMENTS = 1000;
  public static final int MAX_ORDER = Integer.MAX_VALUE - 1;

  public static int stringToOrder(String order) {
    return (order == null ? Integer.MAX_VALUE : Integer.parseInt(order));
  }

  public static String orderToString(int order) {
    return StringUtils.leftPad(Integer.toString(order), 9, '0');
  }

}
