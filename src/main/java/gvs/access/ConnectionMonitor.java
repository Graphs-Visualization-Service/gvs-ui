package gvs.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * Guarantees that only one client can communicate with the service at a time.
 * 
 * @author mwieland
 */
@Singleton
public class ConnectionMonitor {

  private String currentOwnerAddress;

  private static final Logger logger = LoggerFactory
      .getLogger(ConnectionMonitor.class);

  private static final String WATCHDOG = "watchdog";

  /**
   * Reserves the service for a client address.
   * 
   * @param clientAddress
   *          client address
   * @throws InterruptedException
   *           if any thread interrupted the current thread
   */
  public synchronized void reserveService(String clientAddress)
      throws InterruptedException {

    while (currentOwnerAddress != null) {
      wait();
    }

    currentOwnerAddress = clientAddress;
    logger.info("{} reserved the service.", currentOwnerAddress);
  }

  /**
   * Release the service. <br>
   * Only the current owner of the service can release it for other clients.
   * 
   * @param clientAddress
   *          client address
   */
  public synchronized void releaseService(String clientAddress) {
    if (clientAddress != null) {
      if (clientAddress.equals(currentOwnerAddress) || clientAddress.equals(WATCHDOG)) {
        currentOwnerAddress = null;
        notifyAll();
        logger.info("Service released: " + clientAddress);
      }
    }
  }

  public synchronized boolean isReserved() {
    return currentOwnerAddress != null;
  }
}
