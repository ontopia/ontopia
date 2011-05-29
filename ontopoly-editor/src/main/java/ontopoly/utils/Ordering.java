
package ontopoly.utils;

import net.ontopia.utils.StringUtils;

public final class Ordering {

  public static final int ORDER_INCREMENTS = 1000;
  public static final int MAX_ORDER = Integer.MAX_VALUE - 1;

  public static int stringToOrder(String order) {
    return (order == null ? Integer.MAX_VALUE : Integer.parseInt(order));
  }

  public static String orderToString(int order) {
    return StringUtils.pad(order, '0', 9);
  }

}
