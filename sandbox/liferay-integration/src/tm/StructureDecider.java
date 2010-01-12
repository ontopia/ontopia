package tm;

import net.ontopia.utils.DeciderIF;

public class StructureDecider implements DeciderIF{

  public boolean ok(Object arg0) {
    if(arg0.getClass().toString().equals(net.ontopia.topicmaps.impl.basic.Association.class.toString())){
      return false; // do not update any associations
    }
    return true; // update everything else
  }

}
