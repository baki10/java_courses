package com.bakigoal.oktmo.model;

/**
 *
 * @author ilmir
 */
public class District extends Partition<Settlement> {

  private String capital;

  public District(long code) {
    super(code);
  }

  public District(Long code, String capital) {
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
