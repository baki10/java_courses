package com.bakigoal.process;

import com.bakigoal.process.model.Visitor;

/**
 *
 * @author ilmir
 */
public class ProcessesMain {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    BusinessCenter center = new BusinessCenter();
    for (int i = 0; i < 4; i++) {
      new Thread(new Visitor(center)).start();
    }
  }
  
}
