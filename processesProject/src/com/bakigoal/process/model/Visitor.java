package com.bakigoal.process.model;

import com.bakigoal.process.BusinessCenter;

/**
 *
 * @author ilmir
 */
public class Visitor implements Runnable {

  private final BusinessCenter place;
  private static int totalCount;        //счётчик посетителей
  private final int num;                //номер посетителя
  private final int floor;              //на какой этаж идет посетитель

  public Visitor(BusinessCenter place) {
    this.place = place;
    totalCount++;
    num = totalCount;
    floor = (int) (Math.random() * 9) + 1;
  }

  @Override
  public void run() {
    passControl();
    goUp();
    doSomeWork();
    goDown();
  }

  public void passControl() {
    place.enterControl(this);
    place.exitFromControl(this);
  }

  public void goUp() {
    place.enterLift(this);
    place.moveLift(floor, this);
    place.exitFromLift(this);
  }

  public void doSomeWork() {
    System.out.println(this + ": doing some work");
    try {
      Thread.sleep(3000);
    } catch (InterruptedException ex) {
    }
    System.out.println(this + ": all works are done");
  }

  public void goDown() {
    place.enterLift(this);
    place.moveLift(1, this);
    place.exitFromLift(this);
  }

  @Override
  public String toString() {
    return "Visitor[" + num + ']';
  }

}
