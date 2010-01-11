package tm;

import net.ontopia.utils.DeciderIF;

public class WebContentDecider implements DeciderIF {

  public boolean ok(Object arg0) {
    // TODO Check what associations must not be updated and return "false" for those.
    return true;
  }

}
