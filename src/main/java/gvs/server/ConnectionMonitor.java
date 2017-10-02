package gvs.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.ui.application.controller.Main;

/**
 * Everyone who wants to send data, must reserve this service. This class is the
 * central entrypoint for every data transfer.
 * 
 * @author mkoller
 */
public class ConnectionMonitor {

  private static ConnectionMonitor connectionMonitor = null;
  private String owner = "";
  private long lastUse;
  private static final Logger serverLogger = LoggerFactory
      .getLogger(Main.class);
  // private Logger serverLogger = null;

  private ConnectionMonitor() {
    lastUse = System.currentTimeMillis();
  }

  public static synchronized ConnectionMonitor getInstance() {
    if (connectionMonitor == null) {
      connectionMonitor = new ConnectionMonitor();
    }

    return connectionMonitor;
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
      serverLogger.debug(owner + "reserved service");
      return 0;
    } else {
      serverLogger.debug("Service is in use");
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
      serverLogger.debug(owner + "is owner");
      lastUse = System.currentTimeMillis();
      return true;
    } else {
      serverLogger.debug(owner + "is not owner");
      return false;
    }
  }

  /**
   * The owner can release the service
   * 
   * @param pOwner
   */
  public synchronized void releaseServer(String pOwner) {
    if (owner.equals(pOwner) || owner == pOwner) {
      owner = "";
      serverLogger.debug("Serivce will be released");
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
