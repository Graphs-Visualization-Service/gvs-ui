package gvs.business.logic.physics.rules;

import gvs.business.logic.physics.helpers.AreaVector;
import gvs.business.logic.physics.helpers.Particle;

/**
 * Calculates the repulsive force between given particles
 * 
 * @author aegli
 *
 */
public class RepulsiveForce {

  private double linearity = 3;
  private double impact = 1500;

  /**
   * Builds an instance of a repulsive force
   *
   */
  public RepulsiveForce() {

  }

  /**
   * Calculates the repulsive force between particles
   * 
   * @param refP
   * @param relationP
   */
  public void compute(Particle refP, Particle relationP) {
    AreaVector force = new AreaVector(refP.getPointPosition(),
        relationP.getPointPosition());

    if (force.getLength() != 0) {
      double nDist = force.getLength();
      if (nDist <= 1.0) {
        nDist = 1.0;
      }

      nDist = Math.pow(nDist, linearity);

      double dev = -refP.getWeight() * relationP.getWeight() / nDist;
      force.reduceMultiplicator(dev * impact);

      if (force.getLength() > impact) {
        force.scaleTo(impact);
      }
      refP.accelerate(force);
      force.invertXYCoordinates();
      relationP.accelerate(force);
    }
  }
}
