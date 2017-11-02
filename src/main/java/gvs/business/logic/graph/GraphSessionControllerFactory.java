package gvs.business.logic.graph;

import java.util.Collection;
import java.util.List;

import com.google.inject.assistedinject.Assisted;

import gvs.business.model.graph.Graph;

/**
 * Factory interface for Guice
 * 
 * @see https://github.com/google/guice/wiki/AssistedInject
 * @author Michi
 *
 */
public interface GraphSessionControllerFactory {

  GraphSessionController create(long sessionId, String sessionName,
      List<Graph> pGraphModels);
}
