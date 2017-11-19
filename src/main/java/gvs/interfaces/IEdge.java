package gvs.interfaces;

import gvs.business.model.styles.GVSStyle;

public interface IEdge {

  IVertex getEndVertex();

  boolean isDirected();

  String getLabel();

  GVSStyle getStyle();

  IVertex getStartVertex();

}
