package gvs.mock;

import gvs.business.logic.ISessionType;
import gvs.business.logic.layouter.ILayouter;

public class SessionTypeMock implements ISessionType {

  @Override
  public ILayouter getLayouter() {
    return new LayouterMock();
  }

  @Override
  public String toString() {
    return "GVS Mock of SessionType";
  }
}
