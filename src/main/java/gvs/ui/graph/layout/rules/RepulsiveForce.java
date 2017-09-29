package gvs.ui.graph.layout.rules;

import gvs.ui.graph.layout.helpers.AreaVector;
import gvs.ui.graph.layout.helpers.Particle;

/**
 * Calculates the repulsive force between given particles
 * @author aegli
 *
 */
public class RepulsiveForce {
		
	private double linearity=3; 
	private double impact=1500;

	/**
	 * Builds an instance of a repulsive force
	 *
	 */
	public RepulsiveForce() {

	}
	
	/**
	 * Calculates the repulsive force between particles
	 * @param refP
	 * @param relationP
	 */
	public void compute(Particle refP,Particle relationP){
		AreaVector force = new AreaVector(refP.getPointPosition(),relationP.getPointPosition());	
		
		if(force.length!=0){	
			double nDist = force.length;
	    if ( nDist <=1.0 ) {
	        nDist = 1.0;
	    }
	
	    nDist = Math.pow(nDist, linearity);
	    
	    double dev = -refP.getWeight() * relationP.getWeight() /  nDist;
	    force.reduceMultiplicator(dev*impact);
	    
	    if ( force.length > impact ) {
	        force.scaleTo(impact);
	    }
	    refP.accelerate(force);
	    force.changeOfSign();
	    relationP.accelerate(force);
		}
	}
}
