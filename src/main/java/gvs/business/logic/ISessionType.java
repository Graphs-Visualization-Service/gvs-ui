package gvs.business.logic;

import gvs.business.logic.layouter.ILayouter;

/**
 * Interface to label sessions with their respective types.
 * 
 * @author mtrentini
 *
 */
public interface ISessionType {

  ILayouter getLayouter();
}
