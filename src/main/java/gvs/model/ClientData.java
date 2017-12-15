package gvs.model;

/**
 * Represents data from the client. Used to transfer the data over Observer
 * connection to upper layer.
 * 
 * @author mwieland
 */
public class ClientData {

  private final long sessionId;
  private final String sessionName;
  private final ISessionType sessionType;
  private final Graph graph;

  /**
   * Client data
   * 
   * @param sessionId
   *          id of the session
   * @param sessionName
   *          name of the session
   * @param sessionType
   *          type of the session
   * @param newGraph
   *          graph or tree in the session
   */
  public ClientData(long sessionId, String sessionName,
      ISessionType sessionType, Graph newGraph) {
    this.sessionId = sessionId;
    this.sessionName = sessionName;
    this.sessionType = sessionType;
    this.graph = newGraph;
  }

  public long getSessionId() {
    return sessionId;
  }

  public String getSessionName() {
    return sessionName;
  }

  public ISessionType getSessionType() {
    return sessionType;
  }

  public Graph getGraph() {
    return graph;
  }
}
