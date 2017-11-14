package gvs.business.logic.graph;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import gvs.interfaces.Action;

/**
 * TimerTask, responsible for showing replays with a defined timeout
 * 
 * @author mwieland
 *
 */
public class SessionReplay extends TimerTask {

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
      cancel();
    }
  }

  /**
   * Cancel current thread and execute callback.
   * 
   * @return true if this task is scheduled for one-time execution and has not
   *         yet run, or this task is scheduled for repeated execution. Returns
   *         false if the task was scheduled for one-time execution and has
   *         already run, or if the task was never scheduled, or if the task was
   *         already cancelled. (Loosely speaking, this method returns true if
   *         it prevents one or more scheduled executions from taking place.)
   */
  public boolean cancel() {
    if (finishedCallback != null) {
      finishedCallback.execute();
    }
    return super.cancel();
  }
}
