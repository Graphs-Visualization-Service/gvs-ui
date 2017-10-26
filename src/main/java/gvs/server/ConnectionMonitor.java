package gvs.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * Everyone who wants to send data, must reserve this service. This class is the
 * central entrypoint for every data transfer.
 * 
 * @author mkoller
 */
@Singleton
public class ConnectionMonitor {

  private String owner = "";
  private long lastUse;
  private static final Logger logger = LoggerFactory
      .getLogger(ConnectionMonitor.class);

  public ConnectionMonitor() {
    lastUse = System.currentTimeMillis();
  }

  /**
   * The caller can reserve the service
   * 
   * @param pOwner
   *          the caller
   * @return isFree
   */
  public synchronized int reserveService(String pOwner) {
    if (owner.equals("") || owner == "") {
      lastUse = System.currentTimeMillis();
      this.owner = pOwner;
      logger.debug(owner + "reserved service");
      return 0;
    } else {
      logger.debug("Service is in use");
      return -1;
    }
  }

  /**
   * Check if the caller is owner
   * 
   * @param pOwner
   *          the caller
   * @return isOwner
   */
  public synchronized boolean isOwner(String pOwner) {
    if (owner.equals(pOwner) || owner == pOwner) {
      logger.debug(owner + "is owner");
      lastUse = System.currentTimeMillis();
      return true;
    } else {
      logger.debug(owner + "is not owner");
      return false;
    }
  }

  /**
   * The owner can release the service
   * 
   * @param pOwner
   */
  public synchronized void releaseService(String pOwner) {
    if (owner.equals(pOwner) || owner == pOwner) {
      owner = "";
      logger.debug("Serivce will be released");
    }
  }

  /**
   * If a timeout occurs the service can be rested.
   *
   */
  public synchronized void resetConnection() {
    this.owner = "";
    lastUse = System.currentTimeMillis();
  }

  /**
   * Return the last time a registerd client has used the connection.
   * 
   * @return lastusetime
   */
  public long getLastUse() {
    return lastUse;
  }

}
