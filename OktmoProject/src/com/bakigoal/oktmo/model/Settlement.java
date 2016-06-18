package com.bakigoal.oktmo.model;

/**
 *
 * @author ilmir
 */
public class Settlement extends Partition<Place> {

  private String capital;

  public Settlement(long code) {
    super(code);
  }

  public Settlement(Long code, String capital) {
    super(code);
    this.capital = capital;
  }

  public String getCapital() {
    return capital;
  }

  public void setCapital(String capital) {
    this.capital = capital;
  }
}
