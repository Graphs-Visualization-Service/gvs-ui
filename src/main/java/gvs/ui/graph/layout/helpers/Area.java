package gvs.ui.graph.layout.helpers;

import gvs.ui.graph.layout.rules.RepulsiveForce;
import gvs.ui.graph.layout.rules.Traction;

import java.util.Iterator;
import java.util.Observable;
import java.util.Vector;

/**
 * Layout area where the elements are set to their positions
 * 
 * @author aegli
 *
 */
public class Area extends Observable {

  private final int dimensions = 2;
  private Vector<Particle> particles = new Vector<Particle>();
  private Vector<Traction> traction = new Vector<Traction>();

  private AreaDimension dimension = null;
  private RepulsiveForce repulsiveForce = new RepulsiveForce();

  private double viscosity;
  private int fixedParticles = 0;
  private boolean isAreaStable = false;

  /**
   * Builds new default area where particle positions are calculated Default
   * dimension: 1000*1000
   *
   */
  public Area() {
    this(new AreaDimension(1000, 1000));
  }

  /**
   * Builds new area where particle positions are calculated. Used for
   * overwriting the default dimension
   * 
   * @param dimension
   */
  public Area(AreaDimension dimension) {
    this(dimension, 0.15);
  }

  /**
   * Builds new area where particle positions are calcluated. Used for
   * overwriting default dimension and viscosity. Viscosity has an influence on
   * how fast accelerated particles slow down
   * 
   * @param dimension
   * @param viscosity
   */
  public Area(AreaDimension dimension, double viscosity) {
    this.dimension = dimension;
    this.viscosity = viscosity;
  }

  /**
   * Returns dimension of actual area
   * 
   * @return
   */
  public AreaDimension getUniverseDimension() {
    return dimension;
  }

  /**
   * Sets a new viscosity. Viscosity has an influence on how fast accelerated
   * particles slow down
   * 
   * @param d
   */
  public void setViscosity(double d) {
    viscosity = d;
  }

  /**
   * Places new particles in area, so their positions can be calculated
   * 
   * @param p
   */
  public synchronized void addParticles(Particle p) {
    particles.add(p);
  }

  /**
   * Returns centre of area. Default: 500*500
   * 
   * @return
   */
  public AreaPoint getAreaCenter() {
    return dimension.getCenter();
  }

  /**
   * Adds tractions. Used by particles which are connected to each other.
   * 
   * @param t
   */
  public synchronized void addTraction(Traction t) {
    traction.add(t);
  }

  /**
   * Returns particle with requested id
   * 
   * @param id
   * @return
   */
  public Particle getParticleWithID(long id) {
    Iterator it = particles.iterator();
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
   */
  public void setAreaState(boolean pState) {
    isAreaStable = pState;
  }

  /**
   * Returns if all available particles in area are fixed
   * 
   * @return
   */
  public boolean getAreaState() {
    return isAreaStable;
  }

  /**
   * Each time the are ticker sends a pulse, updateAll method will be called. So
   * particles are accelerated into claimed direction until they are stable
   *
   */
  public synchronized void updateAll() {
    Iterator it1 = particles.iterator();
    while (it1.hasNext()) {
      Particle p = (Particle) it1.next();
      p.getAcceleration().resetAcc();
    }
    Iterator it9 = traction.iterator();
    while (it9.hasNext()) {
      Traction t = (Traction) it9.next();
      t.compute();
    }

    Iterator it3 = particles.iterator();
    Iterator it4;
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

    Iterator it = particles.iterator();
    while (it.hasNext()) {
      Particle p = (Particle) it.next();
      AreaVector vectorToCentre = new AreaVector(p.getPointPosition(),
          getAreaCenter());
      vectorToCentre.scaleTo(2);

      p.accelerate(vectorToCentre);
      p.getSpeed().reduceMultiplicator(1 - viscosity);
      p.update();
    }

    Iterator it11 = particles.iterator();
    while (it11.hasNext()) {
      Particle p = (Particle) it11.next();
      p.update();
      if (p.getSpeed().getDistance() > 20) {
        p.getSpeed().scaleTo(20);
      }
      checkAreaBounds(p);
    }

    Iterator it0 = particles.iterator();
    fixedParticles = 0;
    while (it0.hasNext()) {
      Particle p = (Particle) it0.next();
      if (p.positionFixed()) {
        fixedParticles += 1;
      }

      if (fixedParticles == particles.size()) {
        setAreaState(true);
      }
    }
  }

  // Check if particles are leaving area boundarys.
  // If true acclerate them in opposite direction
  private void checkAreaBounds(Particle p) {

    AreaPoint pos = p.getPointPosition();
    AreaVector vel = p.getSpeed();

    int offx = 30;
    int offy = 10;

    for (int i = 0; i < dimensions; i++) {
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
        vel.setField(i, -0.9 * vel.getField(i));
      } else if (ub > uc) {
        pos.setField(i, uc);
        vel.setField(i, -0.9 * vel.getField(i));
      }

    }
  }
}
