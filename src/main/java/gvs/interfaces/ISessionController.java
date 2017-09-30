package gvs.interfaces;

import gvs.ui.application.view.ControlPanel;

public interface ISessionController {
	
	public abstract ControlPanel getControlPanel();
	
	public abstract String getSessionName();
	
	public abstract long getSessionId();
	
	public abstract void setVisualModel();
	
	public abstract void getFirstModel();
	
	public abstract void getPreviousModel();
	
	public abstract void getNextModel();
	
	public abstract void getLastModel();
	
	public abstract void replay();
	
	public abstract void speed(int picsPerSecond);
	
	public abstract void autoLayout();
	
	public abstract boolean validateNavigation(long requestedModelId);
	
}
