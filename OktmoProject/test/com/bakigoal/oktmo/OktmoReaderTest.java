/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bakigoal.oktmo;

import com.bakigoal.oktmo.model.Place;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ilmir
 */
public class OktmoReaderTest {

  private static final String FILE_NAME = "tom5_oktmo_2.csv";

  public OktmoReaderTest() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  /**
   * Test of readPlaces method, of class OktmoReader.
   */
  @Test
  public void testReadPlaces() {
    System.out.println("readPlaces");
    String fileName = FILE_NAME;
    OktmoData data = new OktmoData();
    OktmoReader instance = new OktmoReader();
    instance.readPlaces(fileName, data);

    int size = data.size();
    assertEquals(16, size);
    System.out.println(size + " places");

    //навание/статус/код 10-го в списке НП
    Place tenthPlace = new Place(80601407101L, "Амангильдино", "с");
    assertEquals(tenthPlace, data.getPlace(9));
    System.out.println(data.getPlace(9));

    //навание/статус/код последнего в списке НП
    Place lastPlace = new Place(80601410106L, "Даутово", "д");
    assertEquals(lastPlace, data.getPlace(size - 1));
    System.out.println(data.getPlace(size - 1));

  }

  @Test
  public void testReadPlacesExp() {
    System.out.println("readPlacesExp");
    String fileName = FILE_NAME;
    OktmoData data = new OktmoData();
    OktmoReader instance = new OktmoReader();
    instance.readPlacesExp(fileName, data);

    //навание/статус/код 10-го в списке НП
    Place tenthPlace = new Place(80601407101L, "Амангильдино", "с");
    assertEquals(tenthPlace, data.getPlace(9));
    System.out.println(data.getPlace(9));

    //навание/статус/код последнего в списке НП
    Place lastPlace = new Place(80601410106L, "Даутово", "д");
    assertEquals(lastPlace, data.getPlace(15));
    System.out.println(data.getPlace(15));

  }
}
