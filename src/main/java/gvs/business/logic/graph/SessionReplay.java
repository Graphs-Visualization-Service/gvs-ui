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

  private final Session session;
  private final Action finishedCallback;

  private static final Logger logger = LoggerFactory
      .getLogger(SessionReplay.class);

  /**
   * GraphSessionReplay.
   * 
   * @param session
   *          session
   * 
   * @param callback
   *          replay callback which updates the ui
   */
  @Inject
  public SessionReplay(@Assisted Session session, @Assisted Action callback) {
    this.replayIteration = 1;
    this.session = session;
    this.finishedCallback = callback;

    session.changeCurrentGraphToFirst();
  }

  /**
   * Executes the timer task.
   */
  public void run() {
    logger.info("Session replay task started...");
    if (replayIteration <= session.getGraphs().size()) {
      logger.debug("Replay graph {}", replayIteration);
      session.changeCurrentGraphToNext();
      replayIteration++;
    } else {
      logger.info("Replay finished");
      finishedCallback.execute();
      super.cancel();
    }
  }
}
