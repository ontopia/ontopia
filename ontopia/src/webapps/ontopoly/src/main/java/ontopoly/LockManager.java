package ontopoly;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.utils.ObjectUtils;

public class LockManager {

  public static final long DEFAULT_LOCK_TIMESPAN_MINUTES = 5; // 5 minutes 
  public static final long DEFAULT_LOCK_REACQUIRE_TIMESPAN_MINUTES = DEFAULT_LOCK_TIMESPAN_MINUTES - 1; // 4 minutes 
  
  private final Map<String,Lock> locks = new HashMap<String,Lock>();
  
  private long lockTimespan; 
  private int accessCount;
  private long nextPrune;
  
  public LockManager() {
    this(DEFAULT_LOCK_TIMESPAN_MINUTES * 1000 * 60);
  }
  
  public LockManager(long lockTimespan) {
    this.lockTimespan = lockTimespan;
    nextPrune = System.currentTimeMillis() + lockTimespan;
  }
  
  public long getLockTimeSpan() {
    return lockTimespan;
  }
  
  public Lock lock(String lockKey, String lockerId) {
    synchronized (locks) {
      long expiryTime = System.currentTimeMillis(); 
      if (expiryTime >= nextPrune && (++accessCount % 100 == 0))
        pruneLocks(nextPrune);
      Lock lock = (Lock)locks.get(lockKey);
      // return existing lock, if not expired
      if (lock != null && !lock.expired(expiryTime, getLockTimeSpan())) {
        // if the existing lock was owned by us, then update the expiry
        if (lock.ownedBy(lockerId))
          lock.setLockTime(expiryTime);
        return lock;
      } else {
        // create new lock
        lock = new Lock(lockerId, lockKey);
        locks.put(lockKey, lock);
      }
      return lock;
    }
  }
  
  public LockManager.Lock forcedUnlock(String lockKey) {
    synchronized (locks) {
      long expiryTime = System.currentTimeMillis(); 
      if (expiryTime >= nextPrune && (++accessCount % 100 == 0))
        pruneLocks(nextPrune);
      return (Lock)locks.remove(lockKey);
    }    
  }
  
  public LockManager.Lock unlock(String lockKey, String lockerId) {
    synchronized (locks) {
      long expiryTime = System.currentTimeMillis(); 
      if (expiryTime >= nextPrune && (+accessCount % 100 == 0))
        pruneLocks(nextPrune);
      Lock lock = (Lock)locks.get(lockKey);
      if (lock != null && lock.ownedBy(lockerId))
        return (LockManager.Lock)locks.remove(lockKey);
      else
        return null;
    }    
  }

  public void expireLocksForOwner(String lockerId) {
    synchronized (locks) {
      Iterator iter = locks.keySet().iterator();
      while (iter.hasNext()) {
        String lockKey = (String)iter.next();
        Lock lock = (Lock)locks.get(lockKey);
        if (lock.ownedBy(lockerId))
          iter.remove();
      }
    }
  }

  private void pruneLocks(long expiryTime) {
    // don't prune locks if there are few of them
    if (locks.size() < 200) {
      Iterator iter = locks.keySet().iterator();
      while (iter.hasNext()) {
        String lockKey = (String)iter.next();
        Lock lock = (Lock)locks.get(lockKey);
        if (lock.expired(expiryTime, getLockTimeSpan()))
          iter.remove();
      }
    }
    accessCount = 0;
    nextPrune = System.currentTimeMillis() + lockTimespan;
  }
  
  public static class Lock {

    private String lockedBy;
    private String lockKey;
    private long lockedTime;

    public Lock(String lockedBy, String lockKey) {
      this.lockedBy = lockedBy;
      this.lockKey = lockKey;
      this.lockedTime = System.currentTimeMillis();
    }
    
    public String getLockedBy() {
      return lockedBy;
    }
    
    public long getLockTime() {
      return lockedTime;
    }

    public void setLockTime(long lockedTime) {
      this.lockedTime = lockedTime;
    }

    public String getLockKey() {
      return lockKey;
    }
    
    public boolean expired(long expiryTime, long lockTimeSpan) {
      return (lockedTime + lockTimeSpan) < expiryTime;
    }
    
    public boolean ownedBy(String ownerId) {
      return ObjectUtils.equals(lockedBy, ownerId);
    }
  }  
}
