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

  private AreaDimension dimension = null;
  private RepulsiveForce repulsiveForce = new RepulsiveForce();

  private double viscosity;
  private int fixedParticles = 0;
  private boolean isStable = false;

  private final List<Particle> particles = new ArrayList<>();
  private final List<Traction> tractions = new ArrayList<>();

  private static final int DIMENSIONS = 2;
  private static final double DEFAULT_VISCOSITY = 0.15;
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
    this(dimension, DEFAULT_VISCOSITY);
  }

  /**
   * Builds new area where particle positions are calcluated. Used for
   * overwriting default dimension and viscosity. Viscosity has an influence on
   * how fast accelerated particles slow down
   * 
   * @param dimension
   *          dimension
   * @param viscosity
   *          viscosity
   */
  public Area(AreaDimension dimension, double viscosity) {
    this.dimension = dimension;
    this.viscosity = viscosity;
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

  public void clearParticles() {
    this.particles.clear();
  }

  /**
   * Returns centre of area. Default: 500*500
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
   * will then return postions to Session controller for drawing
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

    Iterator<Particle> it3 = particles.iterator();
    Iterator<Particle> it4;
    while (it3.hasNext()) {
      Particle refP = (Particle) it3.next();
      it4 = particles.iterator();
      while (it4.hasNext()) {
        Particle relationP = (Particle) it4.next();
        if (refP != relationP) {
          repulsiveForce.compute(refP, relationP);
        }
      }
    }

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
    this.fixedParticles = 0;
    particles.forEach(p -> {
      if (p.positionFixed()) {
        fixedParticles += 1;
      }
      if (fixedParticles == particles.size()) {
        setIsStable(true);
      }
    });
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
