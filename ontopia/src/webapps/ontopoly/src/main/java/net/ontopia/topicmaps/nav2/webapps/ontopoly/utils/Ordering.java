// $Id: Ordering.java,v 1.1 2008/10/23 05:18:38 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.utils;

import net.ontopia.utils.StringUtils;

public class Ordering {

  public static final int ORDER_INCREMENTS = 1000;
  public static final int MAX_ORDER = Integer.MAX_VALUE - 1;

  public static int stringToOrder(String order) {
    return (order == null ? Integer.MAX_VALUE : Integer.parseInt(order));
  }

  public static String orderToString(int order) {
    return StringUtils.pad(order, '0', 9);
  }

}
