package gvs.business.logic;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import gvs.util.Action;

/**
 * TimerTask, responsible for showing replays with a defined timeout
 * 
 * @author mwieland
 *
 */
public class SessionReplay extends TimerTask {

  private boolean isCanceled;
  private int replayIteration;
  private Action finishedCallback;

  private final Session session;

  private static final Logger logger = LoggerFactory
      .getLogger(SessionReplay.class);

  /**
   * GraphSessionReplay.
   * 
   * @param session
   *          session
   * @param finishedCallback
   *          callback executed if thread is terminated
   * @param startGraphId
   *          graph id to start with
   */
  @Inject
  public SessionReplay(@Assisted Session session,
      @Assisted Action finishedCallback, @Assisted int startGraphId) {

    this.replayIteration = startGraphId;
    this.finishedCallback = finishedCallback;
    this.session = session;
  }

  /**
   * Executes the timer task.
   */
  public void run() {
    logger.info("Session replay task started...");
    if (replayIteration < session.getGraphs().size()) {
      replayIteration++;
      logger.debug("Show graph with id {}", replayIteration);
      session.changeCurrentGraphToNext();
    } else {
      logger.info("Replay finished");
      isCanceled = cancelReplay();
    }
  }

  /**
   * Cancel current thread and execute callback.
   * 
   * @return returns true if it prevents one or more scheduled executions from
   *         taking place.
   */
  public boolean cancelReplay() {
    if (finishedCallback != null) {
      finishedCallback.execute();
    }
    return super.cancel();
  }

  public boolean isCanceled() {
    return isCanceled;
  }
}
