package tm;

import net.ontopia.utils.DeciderIF;

public class UserDecider implements DeciderIF{


  public boolean ok(Object arg0) {
    if(arg0.getClass().toString().equals(net.ontopia.topicmaps.impl.basic.Association.class.toString())){
      return false;
    }
    return true;
  }

}
