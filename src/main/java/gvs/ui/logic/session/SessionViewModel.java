package gvs.ui.logic.session;

import java.util.Observable;
import java.util.Observer;

import gvs.interfaces.ISessionController;

/**
 * Represents the ViewModel for the Session.
 * @author muriele
 *
 */
public class SessionViewModel implements Observer{
  private ISessionController sessionController;
  
  public SessionViewModel(ISessionController controller) {
    sessionController = controller;
    
  }

  public void changeCurrentGraphToNext() {
    // TODO Auto-generated method stub
    
  }

  public void changeCurrentGraphToPrevious() {
    // TODO Auto-generated method stub
    
  }

  public void changeCurrentGraphToFirst() {
    // TODO Auto-generated method stub
    
  }

  public void changeCurrentGraphToLast() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void update(Observable o, Object arg) {
    // TODO Auto-generated method stub
    
  }

}
