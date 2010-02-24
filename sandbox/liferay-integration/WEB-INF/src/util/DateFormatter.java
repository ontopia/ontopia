
package util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class provides a convenient way to tranform Date objects into strings that fit into ontopoly
 * @author mfi
 */

public class DateFormatter {

  private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd HH:mm:ss");

  public static String format(Date date){

    String result = null;
    if(date != null){
      result = simpleDateFormat.format(date);
    }
    return result;
  }
}
