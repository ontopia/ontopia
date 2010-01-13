
package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import tm.OntopiaAdapter;


public class DateFormater {

  private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd HH:mm:ss");

  public static String format(Date date){
    return notNullDates(date);
  }

  private static String notNullDates(Date date){
    String result = date != null ? simpleDateFormat.format(date) : OntopiaAdapter.NULL;
    return result;
  }

}
