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

  /**
   * Reserves the service for a client address.
   * 
   * @param clientAddress
   *          client address
   * @return 0 if service was reserved <br>
   *         -1 if service is busy
   */
  public synchronized int reserveService(String clientAddress) {
    if (currentOwnerAddress == null) {
      currentOwnerAddress = clientAddress;
      logger.info("{} reserved the service.", currentOwnerAddress);
      return 0;
    } else {
      logger.info("Service is busy");
      return -1;
    }
  }

  /**
   * Release the service. <br>
   * Only the current owner of the service can release it for other clients.
   * 
   * @param clientAddress
   *          client address
   */
  public synchronized void releaseService(String clientAddress) {
    if (currentOwnerAddress == null) {
      logger.debug("Service already released.");
    }
    if (currentOwnerAddress.equals(clientAddress)) {
      currentOwnerAddress = null;
      logger.info("Serivce will be released...");
    }
  }
}
