package com.bakigoal.process;

import com.bakigoal.process.model.Visitor;

/**
 *
 * @author ilmir
 */
public class BusinessCenter {

  private volatile boolean liftFree = true;
  private volatile boolean controlFree = true;    //проходная свободна
  private volatile int liftFloor;

  public BusinessCenter() {
  }

  public void enterLift(Visitor v) {
    synchronized (this) {
      while (!liftFree) {
        try {
          System.out.println(v + ": waiting for lift from floor " + liftFloor);
          wait();
        } catch (InterruptedException e) {
        }
      }
      System.out.println(v + ": entering lift");
      liftFree = false;
    }
  }

  public void exitFromLift(Visitor v) {
    synchronized (this) {
      System.out.println(v+ ": exit from lift");
      liftFree = true;
      notifyAll();
    }
  }

  public void moveLift(int targetFloor, Visitor v) {
    System.out.println(v+": start moving to " + targetFloor);
    waitForSeconds(targetFloor % 4);
    System.out.println(v+": end moving to " + targetFloor);
    liftFloor = targetFloor;
  }

  public void enterControl(Visitor v) {
    synchronized (this) {
      while (!controlFree) {
        try {
          System.out.println(v + ": waiting for control queue");
          wait();
        } catch (InterruptedException e) {
        }
      }
      System.out.println(v + ": passing control");
      waitForSeconds(1);
      controlFree = false;
    }
  }

  public synchronized void exitFromControl(Visitor v) {
    synchronized (this) {
      System.out.println(v+ ": control passed");
      controlFree = true;
      notifyAll();
    }
  }

  private void waitForSeconds(int i) {
    try {
      Thread.sleep(i * 1000);
    } catch (InterruptedException ex) {
    }
  }

}
