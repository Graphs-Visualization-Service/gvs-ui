package gvs.business.logic.physics.rules;

import gvs.business.logic.physics.helpers.AreaVector;
import gvs.business.logic.physics.helpers.Particle;

/**
 * Calculates the traction and its influence on the system
 * 
 * @author aegli
 *
 */
public class Traction {

  private double impact;
  private double distance;
  private Particle from;
  private Particle to;

  /**
   * Creates traction instance. Representing edges
   * 
   * @param from
   * @param to
   * @param impact
   * @param distance
   */
  public Traction(Particle from, Particle to, double impact, double distance) {
    this.from = from;
    this.to = to;
    this.impact = impact;
    this.distance = distance;
  }

  /**
   * Computes traction forces between given particles
   *
   */
  public void compute() {
    AreaVector force = new AreaVector(from.getPointPosition(),
        to.getPointPosition());

    if (force.getLength() != 0) {
      double dev = (force.getDistance() - distance) / distance;
      force.scaleTo(dev * impact);
      from.accelerate(force);
      force.changeOfSign();
      to.accelerate(force);
    }
  }

}
