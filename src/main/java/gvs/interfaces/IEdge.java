package gvs.interfaces;

import gvs.business.model.graph.NodeStyle;

public interface IEdge {

  IVertex getEndVertex();

  boolean isDirected();

  String getLabel();

  NodeStyle getStyle();

  IVertex getStartVertex();

}
