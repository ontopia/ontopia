package net.ontopia.topicmaps.webed.impl.utils;

import java.util.Collection;
import java.util.ResourceBundle;

import net.ontopia.utils.OntopiaRuntimeException;

public class LockResult {
  private Collection unlockable;
  private NamedLock namedLock;
  private String name;
  private ResourceBundle bundle;
  
  public LockResult(Collection unlockable, NamedLock namedLock, String name,
                    ResourceBundle bundle) {
    this.unlockable = unlockable;
    this.namedLock = namedLock;
    this.name = name;
    this.bundle = bundle;
  }
  
  public Collection getUnlockable() {
    return unlockable;
  }
  
  public NamedLock getNamedLock() {
    return namedLock;
  }
  
  public String getLockUserIdLocalized() {    
    if (namedLock == null)
      // throw new OntopiaRuntimeException("Attempted to get name of null NamedLock.");
      return bundle.getString("unknown");
    if (namedLock.getUser() == null)
      throw new OntopiaRuntimeException("No user found on NamedLock");

    String lockName = namedLock.getUser().getId();
    if (lockName.equals("defaultUser"))
      lockName = bundle.getString("unknown");
    return lockName;
  }
  
  public String getName() {
    return name;
  }
}
