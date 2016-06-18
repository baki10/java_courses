package com.bakigoal.oktmo;

import java.util.List;

/**
 *
 * @author ilmir
 */
public class OktmoMain {

  private static final String FILE_NAME = "tom5_oktmo_1.csv";
  private static final String FILE_NAME_2 = "tom5_oktmo_2.csv";

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    OktmoReader reader = new OktmoReader();
    OktmoData data = new OktmoData();

    reader.readPlacesExp(FILE_NAME_2, data);
//    System.out.println("=========== PLACES ===========");
//    data.displayPlaces();
//    System.out.println("=========== STATUSES ===========");
//    data.displayStatuses();
//    System.out.println("=========== SORTED PLACES ===========");
//    data.displaySortedPlaces();
//    System.out.println("=========== <= 7 letters -ово ===========");
//    String regExp = "^[А-яа-я]{0,4}ово$";
//    data.displayPlaces(data.getMatchedPlaces(regExp));

    reader.readPartitions(FILE_NAME, data);
    data.associatePlaces();
    System.out.println("----Абзелиловский район----");
    data.displaySettlementsWithDistrictCode(80601000L);
    
    String maxName = OktmoAnalyzer.findMostPopularPlaceName(80000000L, data);
    System.out.println(maxName);
    System.out.println(OktmoAnalyzer.findDistrictWithMaxPlaces(80000000L, data));
    System.out.println(OktmoAnalyzer.displaySummary(80000000L, data));
  }

}
