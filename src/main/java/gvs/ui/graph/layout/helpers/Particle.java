package gvs.ui.graph.layout.helpers;

import gvs.interfaces.IVertex;

/**
 * Represents an element in the layouting area
 * 
 * @author aegli
 *
 */
public class Particle {

	private AreaVector speed, acceleration;
	private AreaPoint particlePosition = null;
	private boolean positionFixed = false;
	private double weight = 0;
	private long particleId = 0;
	private IVertex myNode = null;

	/**
	 * Builds an instance of a particle.
	 * 
	 * @param position
	 * @param particleId
	 * @param myNode
	 * @param isfixed
	 * @param weight
	 * @param radius
	 */
	public Particle(AreaPoint position, long particleId, IVertex myNode, boolean isfixed, double weight,
			double radius) {
		this.particlePosition = position;
		this.particleId = particleId;
		this.myNode = myNode;
		this.weight = 10;

		this.speed = new AreaVector(0, 0);
		this.acceleration = new AreaVector(0, 0);
		this.positionFixed = isfixed;
	}

	/**
	 * Returns position of a particle
	 * 
	 * @return
	 */
	public AreaPoint getPointPosition() {
		return particlePosition;
	}

	/**
	 * Accelerates a particle
	 * 
	 * @param vec
	 */
	public void accelerate(AreaVector vec) {
		if (positionFixed == false) {
			double scale = 1;
			if (weight >= 0) {
				scale = 1.0 / weight;
			}
			acceleration.add(vec, scale);
		}
	}

	/**
	 * Each time a ticker impuls happens, the newly calculated acceleration and
	 * speed Vector are updated and the new particle position will be set
	 * accordingly If speed of a particle is lower than a given value, mark
	 * position as fixed
	 */
	public void update() {
		if (positionFixed == false) {
			speed.add(acceleration);
			particlePosition.addVector(speed);
			if (Math.abs(speed.getSpeedSummary()) < 0.2) {
				this.positionFixed = true;
			}
		}
		updateMyNode();
	}

	/**
	 * Returns speed of a particle
	 * 
	 * @return
	 */
	public AreaVector getSpeed() {
		return speed;
	}

	/**
	 * Returns acceleration of a particle
	 * 
	 * @return
	 */
	public AreaVector getAcceleration() {
		return acceleration;
	}

	/**
	 * Returns mass of an particle
	 * 
	 * @return
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * Sets mass of a particle
	 * 
	 * @param weight
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * Returns particle id
	 * 
	 * @return
	 */
	public long getParticleId() {
		return particleId;
	}

	/**
	 * Returns if actual particle is fixed
	 * 
	 * @return
	 */
	public boolean positionFixed() {
		return this.positionFixed;
	}

	/**
	 * When particle is fixed, sets position(percent) of the appropriate vertex
	 *
	 */
	public void updateMyNode() {
		myNode.setXPosition(particlePosition.getX() / 10);
		myNode.setYPosition(particlePosition.getY() / 10);
	}

}
