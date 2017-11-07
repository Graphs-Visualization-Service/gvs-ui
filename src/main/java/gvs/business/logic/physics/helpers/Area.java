package gvs.business.logic.physics.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.business.logic.physics.rules.RepulsiveForce;
import gvs.business.logic.physics.rules.Traction;

/**
 * Layout area where the elements are set to their positions.
 * 
 * @author aegli
 *
 */
public class Area extends Observable {

  private AreaDimension dimension;
  private RepulsiveForce repulsiveForce;

  private double viscosity;
  private boolean isStable = false;

  private final List<Particle> particles = new ArrayList<>();
  private final List<Traction> tractions = new ArrayList<>();

  // how fast accelerated particles slow down
  private static final double DEFAULT_VISCOSITY = 0.15;

  private static final int DIMENSIONS = 2;
  private static final int DEFAULT_DISTANCE = 20;
  private static final int OFFSET_Y = 10;
  private static final int OFFSET_X = 30;
  private static final double DEFAULT_PERCENTAGE = 0.9;

  private static final Logger logger = LoggerFactory.getLogger(Area.class);

  /**
   * Builds new area where particle positions are calculated. Used for
   * overwriting the default dimension
   * 
   * @param dimension
   *          dimension
   */
  public Area(AreaDimension dimension) {
    this.dimension = dimension;
    this.viscosity = DEFAULT_VISCOSITY;
    this.repulsiveForce = new RepulsiveForce();
  }

  /**
   * Returns dimension of actual area.
   * 
   * @return area dimension
   */
  public AreaDimension getUniverseDimension() {
    return dimension;
  }

  /**
   * Sets a new viscosity. Viscosity has an influence on how fast accelerated
   * particles slow down.
   * 
   * @param d
   *          viscosity
   */
  public void setViscosity(double d) {
    viscosity = d;
  }

  /**
   * Places new particles in area, so their positions can be calculated.
   * 
   * @param p
   *          particle
   */
  public synchronized void addParticles(Particle p) {
    particles.add(p);
  }

  /**
   * Reset the calculated area
   */
  public void resetArea() {
    tractions.clear();
    particles.clear();
  }

  /**
   * Return the center of a area. Default: 500*500
   * 
   * @return Area Point
   */
  public AreaPoint getAreaCenter() {
    return dimension.getCenter();
  }

  /**
   * Adds tractions. Used by particles which are connected to each other.
   * 
   * @param t
   *          traction
   */
  public synchronized void addTraction(Traction t) {
    tractions.add(t);
  }

  /**
   * Returns particle with requested id.
   * 
   * @param id
   *          particle id
   * @return Particle
   */
  public Particle getParticleWithID(long id) {
    Iterator<Particle> it = particles.iterator();
    while (it.hasNext()) {
      Particle p = (Particle) it.next();
      if (id == p.getParticleId()) {
        return p;
      }
    }
    return null;
  }

  /**
   * Sets area as stable, if all particle positions are fixed. Layout Controller
   * will then return positions to Session controller for drawing
   * 
   * @param pState
   *          particle state
   */
  public void setIsStable(boolean state) {
    isStable = state;
  }

  /**
   * Returns if all available particles in area are fixed.
   * 
   * @return is area stable
   */
  public boolean isStable() {
    return isStable;
  }

  /**
   * Each time the are ticker sends a pulse, updateAll method will be called. So
   * particles are accelerated into claimed direction until they are stable
   *
   */
  public synchronized void updateAll() {
    logger.info("Update particle positions");

    particles.forEach(p -> {
      p.getAcceleration().resetAcc();
    });

    tractions.forEach(t -> {
      t.compute();
    });

    particles.forEach(p1 -> {
      particles.forEach(p2 -> {
        if (p1 != p2) {
          repulsiveForce.compute(p1, p2);
        }
      });
    });

    particles.forEach(p -> {
      AreaVector vectorToCentre = new AreaVector(p.getPointPosition(),
          getAreaCenter());
      vectorToCentre.scaleTo(2);

      p.accelerate(vectorToCentre);
      p.getSpeed().reduceMultiplicator(1 - viscosity);
      p.update();
    });

    particles.forEach(p -> {
      p.update();
      if (p.getSpeed().getDistance() > DEFAULT_DISTANCE) {
        p.getSpeed().scaleTo(DEFAULT_DISTANCE);
      }
      checkAreaBounds(p);
    });

    // set is stable
    boolean areaStable = particles.stream().allMatch(p -> p.positionFixed());
    setIsStable(areaStable);
  }

  /**
   * Check if particles are leaving area boundarys. If true acclerate them in
   * opposite direction.
   * 
   * @param p
   *          particle
   */
  private void checkAreaBounds(Particle p) {

    AreaPoint pos = p.getPointPosition();
    AreaVector vel = p.getSpeed();

    int offx = OFFSET_X;
    int offy = OFFSET_Y;

    for (int i = 0; i < DIMENSIONS; i++) {
      int off = 0;

      if (i == 0) {
        off = offx;
      } else if (i == 1) {
        off = offy;
      }

      double ua = dimension.getStart(i) + off;
      double uc = dimension.getEnd(i) - off;
      double ub = pos.getField(i);

      if (ua > uc) {
        return;
      }

      if (ub < ua) {
        pos.setField(i, ua);
        vel.setField(i, -DEFAULT_PERCENTAGE * vel.getField(i));
      } else if (ub > uc) {
        pos.setField(i, uc);
        vel.setField(i, -DEFAULT_PERCENTAGE * vel.getField(i));
      }

    }
  }
}
