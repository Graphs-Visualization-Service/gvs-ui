package gvs.business.logic.physics.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.interfaces.IVertex;

/**
 * Represents an element in the layouting area
 * 
 * @author aegli
 *
 */
public class Particle {

  private double weight;
  private IVertex relatedVertex;

  private AreaVector speed;
  private AreaVector acceleration;
  private AreaPoint particlePosition;

  private static final Logger logger = LoggerFactory.getLogger(Particle.class);

  /**
   * Builds an instance of a particle.
   * 
   * @param position
   *          position
   * @param relatedVertex
   *          related vertex
   * @param weight
   *          particle weight
   */
  public Particle(AreaPoint position, IVertex relatedVertex, double weight) {
    this.particlePosition = position;
    this.relatedVertex = relatedVertex;
    this.weight = weight;

    this.speed = new AreaVector(0, 0);
    this.acceleration = new AreaVector(0, 0);
  }

  public AreaPoint getPointPosition() {
    return particlePosition;
  }

  /**
   * Accelerates a particle.
   * 
   * @param vector
   *          acceleration vector
   */
  public void accelerate(AreaVector vector) {
    if (!relatedVertex.isLayouted()) {
      double scale = 1;
      if (weight >= 0) {
        scale = 1.0 / weight;
      }
      acceleration.add(vector, scale);
    }
  }

  /**
   * Each time a ticker impuls happens, the newly calculated acceleration and
   * speed Vector are updated and the new particle position will be set
   * accordingly. If the speed of a particle is lower than a given value, its
   * position is marked as is layouted.
   */
  public void update() {
    if (!relatedVertex.isLayouted()) {
      speed.add(acceleration);
      particlePosition.addVector(speed);
      if (Math.abs(speed.getSpeedSummary()) < 0.2) {
        relatedVertex.setIsLayouted(true);
      }
    }
    updateRelatedVertex();
  }

  public AreaVector getSpeed() {
    return speed;
  }

  public AreaVector getAcceleration() {
    return acceleration;
  }

  public double getWeight() {
    return weight;
  }

  /**
   * Returns the related vertex
   * 
   * @return related vertex
   */
  public IVertex getRelatedVertex() {
    return relatedVertex;
  }

  /**
   * Updates the X/Y positions of the business vertex with the calculated
   * values.
   * 
   * The new values are propagated via observers to the view model
   * representation.
   * 
   * The ViewModel representation is bound to the current Ellipse object and
   * updated in real time.
   *
   */
  public void updateRelatedVertex() {
    relatedVertex.setXPosition(particlePosition.getX() / 10);
    relatedVertex.setYPosition(particlePosition.getY() / 10);
  }

}
